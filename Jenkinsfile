pipeline{

    agent any

    tools{
        maven "3.6.3"
    }

    stages{
        stage("Compile and Test") {
            steps {
                echo "Compiling and testing the application..."
                sh "mvn clean test"
                echo "Compiling and testing the application successfully completed"
            }
        }
        stage("Package") {
            steps {
                echo "Packaging the application..."
                sh "mvn clean package"
                echo "Packaging the application successfully completed"
            }
        }
        stage("Build Docker Image") {
            steps {
                echo "Building the docker image version ${env.BUILD_NUMBER}"
                sh "docker build -t prenak/tictactoe-game-api:${env.BUILD_NUMBER} ."
                echo "Building the docker image successfully completed"
            }
        }
    }

    post{
        always{
            //cleanWs()
        }
    }
}