pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        echo 'Building...'
        sleep 5
        echo 'Build complete!'
      }
    }

    stage('Test') {
      steps {
        echo 'Testing...'
        sleep 5
        echo 'Test complete!'
      }
    }

    stage('Deploy') {
      parallel {
        stage('Deploy') {
          steps {
            echo 'Deploying...'
            sleep 5
            echo 'Deploy complete!'
          }
        }

        stage('') {
          steps {
            echo 'kk'
          }
        }

      }
    }

  }
}