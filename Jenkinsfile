pipeline {
    agent any

    stages {

        stage('Build') {
            steps {
                bat '.\\mvnw.cmd clean package'
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t codeexec:v1 .'
            }
        }

        stage('Run Container') {
            steps {
                bat 'docker stop codeexec || exit 0'
                bat 'docker rm codeexec || exit 0'
                bat 'docker run -d -p 8080:8080 --name codeexec codeexec:v1'
            }
        }
    }
}
