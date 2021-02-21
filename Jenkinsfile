pipeline{

    agent any

    tools{
        maven "3.6.0"
    }

    stages{
        stage("Maven compile") {
            steps {
                sh "mvn -version"
                sh "mvn clean compile"
            }
        }
    }

    post{
        always{
            cleanWs()
        }
    }
}