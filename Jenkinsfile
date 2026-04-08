pipeline {
    agent any

    stages {

        stage('Build') {
            steps {
                sh 'chmod +x mvnw || true'
                sh './mvnw clean package'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t codeexec:v1 .'
            }
        }

        stage('Run Container') {
            steps {
                sh 'docker stop codeexec || true'
                sh 'docker rm codeexec || true'
                sh 'docker run -d -p 8082:8080 --name codeexec codeexec:v1'
            }
        }
    }
}
