// SFDX-sample jenkins pipeline files
pipeline {
    agent any
    environment {
        SFDX_CERT_KEY = credentials('SFDX-DEV')
        SFDX_USERNAME = env.SF_USERNAME ?: 'jenkins@service.dev.com'
        CONSUMER_KEY = env.CONSUMER_KEY
        GITLAB_URL = env.GITLAB_URL ?: 'https://code-repo.develop.devcond-test.net/user.tomoatsu.sekikawa/sfdx-sample.git'
    }
    stages {
        stage('Prepare') {
            steps {
                echo '[Preparing stage start...]'
                echo 'INFO: Access to gitlab project...'
                git credentialsId: 'GITLAB_USER',
                    url: "${GITLAB_URL}"
                echo 'INFO: Success to access gitlab project...'
                sh '''
                    node -v
                    npm -v
                    sfdx -v
                '''
            }
        }
        stage('Build') {
            steps {
                echo '[Building stage start...]'
            }
        }
        // stage('Test') {
        //     steps {
        //         echo 'Testing..'
        //     }
        // }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}