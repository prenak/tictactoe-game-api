pipeline{

    agent any

    tools{
        maven "3.6.3"
    }

    stages{
        stage("Compile, Test and Package") {
            steps {
                echo "Testing and Packaging the application..."
                sh "mvn clean package"
                echo "Testing and Packaging the application successfully completed"
            }
        }
        stage("Build Docker Image") {
            steps {
                echo "Building the docker image version ${env.BUILD_NUMBER}"
                sh "docker build -t prenak/tictactoe-game-api:${env.BUILD_NUMBER} ."
                echo "Docker image built successfully"
            }
        }
        stage("Push Docker Image to Repository") {
            steps {
                echo "Pushing the docker image to repository"
                withCredentials([string(credentialsId: 'dockerHubPassword', variable: 'dockerHubPass')]) {
                    sh "docker login -u prenak -p ${dockerHubPass}"
                }
                sh "docker push prenak/tictactoe-game-api:${env.BUILD_NUMBER}"
                echo "Docker image is pushed to repository successfully"
            }
        }
    }

    post{
        always{
            cleanWs()
        }
    }
}