pipeline{

    agent any

    tools{
        maven "3.6.3"
    }

    environment{
        DOCKER_HUB_USER = "prenak"
        DOCKER_IMG_NAME = "prenak/tictactoe-game-api"

        AWS_USER_NAME   = ""
        AWS_EC2_IP      = ""
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
                sh "docker build -t ${DOCKER_IMG_NAME}:latest -t ${DOCKER_IMG_NAME}:${env.BUILD_NUMBER} ."
                echo "Docker image built successfully"
            }
        }
        stage("Push Docker Image to Repository") {
            steps {
                echo "Pushing the docker image to repository"
                withCredentials([string(credentialsId: 'dockerHubPassword', variable: 'dockerHubPass')]) {
                    sh "docker login -u ${DOCKER_HUB_USER} -p ${dockerHubPass}"
                }
                //sh "docker push ${DOCKER_IMG_NAME} --all-tags"
                echo "Docker image is pushed to repository successfully"
            }
        }
        stage("Delete Local Docker Image") {
            steps {
                echo "Deleting the local copy of docker images"
                sh "docker image rm ${DOCKER_IMG_NAME}:latest"
                sh "docker image rm ${DOCKER_IMG_NAME}:${env.BUILD_NUMBER}"
                echo "Deleted local Docker images successfully"
            }
        }
        stage("Deploying the images on AWS") {
            steps {
                DOCKER_RUN_CMD = "docker run -d -p 8282:8282 --name tictactoe-game-api ${DOCKER_IMG_NAME}:latest"
                echo "dockerRunCmd is ${DOCKER_RUN_CMD}"
                //sshagent(['dev-deploy-creds']) {
                //    sh "ssh ${AWS_USER_NAME}@${AWS_EC2_IP} ${dockerRunCmd}"
                //}
            }
        }
    }

    post{
        always{
            cleanWs()
        }
    }
}