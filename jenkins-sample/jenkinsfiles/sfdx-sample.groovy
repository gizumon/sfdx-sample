// SFDX-sample jenkins pipeline files
pipeline {
    agent any
    parameters {
        choice(name: 'STAGE', choices: ['PROD', 'PPD', 'DEV'], description: 'リリース先環境')
        string(name: 'SFDX_USERNAME', defaultValue: 'jenkins@service.dev.com', description: 'デプロイを実行するSalesforceするユーザー名')
        string(name: 'GITLAB_URL', defaultValue: 'https://code-repo.develop.devcond-test.net/user.tomoatsu.sekikawa/sfdx-sample.git', description: 'SFDXプロジェクト Gitlab URL')
        string(name: 'DEPLOY_BRANCH', defaultValue: 'develop/jenkins', description: 'デプロイ対象のブランチ')
        choice(name: 'IS_RUN_APEX_TEST', choices: ['TRUE', 'FALSE'], description: 'Apexテストを実行するか')
        choice(name: 'IS_RUN_STATIC_ANALYSIS', choices: ['FALSE', 'TRUE'], description: '静的解析を実行するか')
        // If codescan project do not need to be specified, then the followings can move in environments 
        string(name: 'SONARQUBE_PRJ_KEY', defaultValue: 'sfdx-sample', description: 'Code.scan(sonar qube)のプロジェクトキー')
        string(name: 'SONARQUBE_TOKEN', defaultValue: 'db9967908b639654e526c31a794da2b718eac0dd', description: 'code.scanのプロジェクトアクセストークン (デフォルトは開発環境のSonarQubeのプロジェクトアクセストークン)')
    }
    environment {
        // Need to implement NodeJS plugin with installing sfdx-cli global
        NODEJS_HOME = "${tool 'NodeJS_SFDX'}"
        PATH = "${env.NODEJS_HOME}/bin:${env.PATH}"
        // Need to set the following environments and credentials
        SONARQUBE_SERVER_URL = "${env.SONARQUBE_SERVER_URL}" // Need to set sonarqube server url as 'SONARQUBE_SERVER_URL' key
        SERVER_KEY = credentials('SFDX_SERVER_KEY') // Need to set server key as 'SFDX_SERVER_KEY' key 
        CONSUMER_KEY = credentials('SFDX_CONSUMER_KEY') // Need to set consumer key as 'SFDX_CONSUMER_KEY' key
    }
    stages {
        stage('Prepare') {
            steps {
                // Setup gitlab
                echo "INFO: Fetch gitlab source: url ${GITLAB_URL}"
                // Need to set gitlab access user credential as 'gitlab-integrator' key
                git credentialsId: 'gitlab-integrator',
                    url: "${GITLAB_URL}"
                sh "git checkout -B deploy origin/${DEPLOY_BRANCH}"
                echo 'INFO: Show build envionments and parameters of this pipeline'
                sh '''
                    mkdir -p ./release
                    node -v > ./release/build-environment.txt
                    npm -v >> ./release/build-environment.txt
                    sfdx -v >> ./release/build-environment.txt
                '''
                sh """
                    echo STAGE = ${STAGE} > ./release/build-parameters.txt
                    echo SFDX_USERNAME = ${SFDX_USERNAME} >> ./release/build-parameters.txt
                    echo GITLAB_URL = ${GITLAB_URL} >> ./release/build-parameters.txt
                    echo DEPLOY_BRANCH = ${DEPLOY_BRANCH} >> ./release/build-parameters.txt
                    echo IS_RUN_APEX_TEST = ${IS_RUN_APEX_TEST} >> ./release/build-parameters.txt
                    echo IS_RUN_STATIC_ANALYSIS = ${IS_RUN_STATIC_ANALYSIS} >> ./release/build-parameters.txt
                    echo SONARQUBE_PRJ_KEY = ${SONARQUBE_PRJ_KEY} >> ./release/build-parameters.txt
                    echo SONARQUBE_TOKEN = ${SONARQUBE_TOKEN} >> ./release/build-parameters.txt
                """
                // Login Salesforce Org
                echo "INFO: Login ${STAGE} envionments of pipeline as ${SFDX_USERNAME} user"
                sh """
                    sfdx auth:logout -p --targetusername sfdx || echo 'Already logout'
                    if [ "${STAGE}" = "PROD" ]; then
                        # Login PROD org
                        sfdx force:auth:jwt:grant -i ${CONSUMER_KEY} -u ${SFDX_USERNAME} -f ${SERVER_KEY} -a sfdx
                    else
                        # Login TEST org
                        # TODO: Check actual sandbox org because there is no Sandbox in DeveloperEditon.
                        sfdx force:auth:jwt:grant -i ${CONSUMER_KEY} -u ${SFDX_USERNAME} -f ${SERVER_KEY} -a sfdx --instanceurl https://test.salesforce.com
                    fi
                """
            }
        }
        stage('Build') {
            steps {
                // Build source
                // INFO: If use force:source:release, then don't need to convert source
                echo 'INFO: Convert source'
                sh '''
                    sfdx force:source:convert -d ./release
                    tar -cvf package.tar ./release
                '''
            }
        }
        stage('Test') {
            steps {
                // INFO: The Apex test command will be executed on the connected org and cannot be tested prior to this release
                // Apex test
                // sh """
                //     # sfdx force:apex:test:run --synchronous -w "-1" -c -v -r human --testlevel=RunLocalTests -u ${SFDX_USERNAME}
                // """
                // Static analysis (Code.scan)
                sh """
                    if [ "${IS_RUN_STATIC_ANALYSIS}" = "TRUE" ]; then
                        echo y | sfdx plugins:install sfdx-codescan-plugin
                        sfdx codescan:run --token ${SONARQUBE_TOKEN} --projectkey ${SONARQUBE_PRJ_KEY} --server ${SONARQUBE_SERVER_URL}
                    fi
                """
            }
        }
        stage('Deploy') {
            steps {
                sh """
                    # Depoly converted metadata
                    if [ '${IS_RUN_APEX_TEST}' = 'TRUE' ]; then
                        sfdx force:mdapi:deploy -w '-1' -d ./release -u sfdx --testlevel=RunLocalTests
                    else
                        sfdx force:mdapi:deploy -w '-1' -d ./release -u sfdx --testlevel=NoTestRun
                    fi
                """
            }
        }
    }
    post {
        success {
            archiveArtifacts artifacts: 'package.tar', followSymlinks: false
        }
    }
}
