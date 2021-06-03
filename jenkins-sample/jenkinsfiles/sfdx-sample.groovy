// SFDX-sample jenkins pipeline files
pipeline {
    agent any
    parameters {
        string(name: 'SFDX_USERNAME', defaultValue: 'jenkins@service.dev.com', description: 'Salesforceへアクセスするユーザー名')
        string(name: 'CONSUMER_KEY', defaultValue: "${env.CONSUMER_KEY}", description: 'Salesforceへアクセスするコンシューマーキー')
        string(name: 'GITLAB_URL', defaultValue: 'https://code-repo.develop.devcond-test.net/user.tomoatsu.sekikawa/sfdx-sample.git', description: 'SFDXプロジェクト Gitlab URL')
        // credentials(name: 'SFDX_CREDENTIALS', credentialType: 'com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl', defaultValue: 'SFDX_DEV', description: 'SFDX用のJWTキー', required: true)
        // credentials(name: 'SFDX_CREDENTIALS', credentialType: 'org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl', defaultValue: 'SFDX_SEVER_KEY', description: 'Salesforce組織への認証キー', required: true)
    }
    environment {
        NODEJS_HOME = "${tool 'NodeJS_SFDX'}"
        PATH = "${env.NODEJS_HOME}/bin:${env.PATH}"
        SFDX_CREDENTIALS = credentials('SFDX_SEVER_KEY')
    }
    stages {
        stage('Prepare') {
            steps {
                // Set git
                echo 'INFO: Preparing stage start...'
                echo 'INFO: Access to gitlab project...'
                git credentialsId: 'GITLAB_USER',
                    url: "${GITLAB_URL}"
                echo 'INFO: Success to access gitlab project...'
                // Set node path
                echo 'INFO: Setting node path...'
                sh 'npm --version'
                sh '''
                    ls -l
                    node -v
                    npm -v
                    sfdx -v
                    printenv
                '''
            }
        }
        stage('Login') {
            steps {
                echo 'INFO: Login stage start...'
                sh """
                    sfdx force:auth:jwt:grant -i ${CONSUMER_KEY} -u ${SFDX_USERNAME} -f ${SFDX_CREDENTIALS}
                """
            }
        }
        // stage('Test') {
        //     steps {
        //         echo 'Testing..'
        //     }
        // }
        stage('Deploy') {
            steps {
                echo 'INFO: Deploying stage start....'
            }
        }
    }
}
