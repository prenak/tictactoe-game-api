pipeline{

    agent any

    tools{
        maven "3.6.3"
    }

    environment{
        DOCKER_HUB_USER = "prenak"
        DOCKER_IMG_NAME = "prenak/tictactoe-game-api"
        CLOUD_USER_NAME = "ec2-user"
        CLOUD_MAC_IP    = "52:66:70:21"
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
                echo "Pushing the docker image to repository..."
                withCredentials([string(credentialsId: 'dockerHubPassword', variable: 'dockerHubPass')]) {
                    sh "docker login -u ${DOCKER_HUB_USER} -p ${dockerHubPass}"
                }
                sh "docker push ${DOCKER_IMG_NAME} --all-tags"
                echo "Docker image is pushed to repository successfully"
            }
        }
        stage("Delete Local Docker Image") {
            steps {
                echo "Deleting the local copy of docker images..."
                sh "docker image rm ${DOCKER_IMG_NAME}:latest"
                sh "docker image rm ${DOCKER_IMG_NAME}:${env.BUILD_NUMBER}"
                echo "Deleted local Docker images successfully"
            }
        }
        stage("Rollout to K8s Cluster") {
            steps {
                echo "Rolling out to K8s cluster..."
                sh "chmod +x updateTag.sh"
                sh "./updateTag.sh ${env.BUILD_NUMBER}"
                sh "cat deployment.yaml"
                sh "cat service.yaml"

                /*sshagent(['k8s-cluster']) {
                    sh "scp -o StrictHostKeyChecking=no service.yaml deployment.yaml ${CLOUD_USER_NAME}@${CLOUD_MAC_IP}:/home/${CLOUD_USER_NAME}"
                    script{
                        sh "ssh ${CLOUD_USER_NAME}@${CLOUD_MAC_IP} kubectl apply -f ."
                    }
                }*/
                echo "Rollout succeeded!"
            }
        }
    }

    post{
        always{
            cleanWs()
        }
    }
}