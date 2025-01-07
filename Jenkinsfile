pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building...'
                sleep 5 // 5초 대기
                echo 'Build complete!'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing...'
                sleep 5 // 5초 대기
                echo 'Test complete!'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying...'
                sleep 5 // 5초 대기
                echo 'Deploy complete!'
            }
        }
    }
}