// SFDX-sample jenkins pipeline files
pipeline {
    agent any
    parameters {
        choice(name: 'STAGE', choices: ['PROD', 'PPD', 'DEV'], description: 'リリース先環境')
        string(name: 'SFDX_USERNAME', defaultValue: 'jenkins@service.dev.com', description: 'デプロイを実行するSalesforceするユーザー名')
        string(name: 'GITLAB_URL', defaultValue: 'https://code-repo.develop.devcond-test.net/user.tomoatsu.sekikawa/sfdx-sample.git', description: 'SFDXプロジェクト Gitlab URL')
        string(name: 'DEPLOY_BRANCH', defaultValue: 'develop/jenkins', description: 'デプロイ対象のブランチ')
        choice(name: 'IS_RUN_TEST', choices: ['TRUE', 'FALSE'], description: 'テストを実行するか')
        // If set this key in each build
        // string(name: 'CONSUMER_KEY', defaultValue: "${env.CONSUMER_KEY}", description: 'Salesforceへアクセスするコンシューマーキー')
    }
    environment {
        NODEJS_HOME = "${tool 'NodeJS_SFDX'}"
        PATH = "${env.NODEJS_HOME}/bin:${env.PATH}"
        // Assuming that devcond can use same server key in different salesforce org
        SEVER_KEY = credentials('SFDX_SEVER_KEY')
        CONSUMER_KEY = credentials('SFDX_CONSUMER_KEY')
    }
    stages {
        stage('Prepare') {
            steps {
                // Setup gitlab
                git credentialsId: 'gitlab-integrator',
                    url: "${GITLAB_URL}"
                echo 'INFO: Build envionments of pipeline'
                sh "git checkout -B deploy origin/${DEPLOY_BRANCH}"
                sh '''
                    mkdir -p ./release
                    node -v > ./release/build-environment.txt
                    npm -v >> ./release/build-environment.txt
                    sfdx -v >> ./release/build-environment.txt
                '''
                // Login Salesforce Org
                echo "INFO: Login ${STAGE} envionments of pipeline as ${SFDX_USERNAME} user"
                sh """
                    if [ "${STAGE}" = "PROD" ]; then
                        # Login PROD org
                        sfdx force:auth:jwt:grant -i ${CONSUMER_KEY} -u ${SFDX_USERNAME} -f ${SEVER_KEY} -a sfdx
                    else
                        # Login TEST org
                        # TODO: Check actual sandbox org because there is no Sandbox in DeveloperEditon.
                        sfdx force:auth:jwt:grant -i ${CONSUMER_KEY} -u ${SFDX_USERNAME} -f ${SEVER_KEY} -a sfdx --instanceurl https://test.salesforce.com
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
                sh """
                    # echo y | sfdx plugins:install sfdx-codescan-plugin
                    ## INFO: The following command will be executed on the connected org and cannot be tested prior to this release
                    # sfdx force:apex:test:run --synchronous -w "-1" -c -v -r human --testlevel=RunLocalTests -u ${SFDX_USERNAME}
                """
            }
        }
        stage('Deploy') {
            steps {
                sh """
                    # Depoly converted metadata
                    if [ '${IS_RUN_TEST}' = 'TRUE' ]; then
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
