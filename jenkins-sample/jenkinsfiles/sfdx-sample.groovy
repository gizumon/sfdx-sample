// SFDX-sample jenkins pipeline files
pipeline {
    agent any
    parameters {
        choice(name: 'STAGE', choices: ['PROD', 'PPD', 'DEV'], description: 'リリース先環境')
        string(name: 'SFDX_USERNAME', defaultValue: 'jenkins@service.dev.com', description: 'デプロイを実行するSalesforceするユーザー名')
        string(name: 'GITLAB_URL', defaultValue: 'https://code-repo.develop.devcond-test.net/user.tomoatsu.sekikawa/sfdx-sample.git', description: 'SFDXプロジェクト Gitlab URL')
        string(name: 'DEPLOY_BRANCH', defaultValue: 'master', description: 'デプロイ対象のブランチ')
        // If set this key in each build
        // string(name: 'CONSUMER_KEY', defaultValue: "${env.CONSUMER_KEY}", description: 'Salesforceへアクセスするコンシューマーキー')
    }
    environment {
        NODEJS_HOME = "${tool 'NodeJS_SFDX'}"
        PATH = "${env.NODEJS_HOME}/bin:${env.PATH}"
        // Assuming that devcond can use same server key in different salesforce org
        SFDX_SEVER_KEY = credentials('SFDX_SEVER_KEY')
        CONSUMER_KEY = "${env.CONSUMER_KEY}"
    }
    stages {
        stage('Prepare') {
            steps {
                // Setup gitlab
                git credentialsId: 'GITLAB_USER',
                    url: "${GITLAB_URL}"
                echo 'INFO: Build envionments of pipeline'
                sh "git checkout -B deploy origin/${DEPLOY_BRANCH}"
                sh '''
                    node -v > ./release/build-environment.txt
                    npm -v >> ./release/build-environment.txt
                    sfdx -v >> ./release/build-environment.txt
                '''
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
        // stage('Test') {
        //     steps {
        //         // INFO: If you would like to test Apex then define the followigns...
        //         sh '''
        //             sfdx force:apex:test:run
        //             sfdx force:apex:test:report
        //         '''
        //     }
        // }
        stage('Deploy') {
            steps {
                sh """
                    if [ "${STAGE}" = "PROD" ]; then
                        # Login PROD org
                        sfdx force:auth:jwt:grant -i ${CONSUMER_KEY} -u ${SFDX_USERNAME} -f ${SFDX_SEVER_KEY} -a sfdx
                    else
                        # Login TEST org
                        # TODO: Check actual sandbox org because there is no Sandbox in DeveloperEditon.
                        sfdx force:auth:jwt:grant -i ${CONSUMER_KEY} -u ${SFDX_USERNAME} -f ${SFDX_SEVER_KEY} -a sfdx --instanceurl https://test.salesforce.com
                    fi
                    # Depoly converted metadata
                    sfdx force:mdapi:deploy -w "-1" -d ./release -u sfdx
                    # sfdx force:mdapi:deploy -d ./release -u sfdx
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
