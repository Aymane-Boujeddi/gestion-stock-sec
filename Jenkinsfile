pipeline {
  agent any

  environment {
    // SonarQube is reachable from Jenkins via the docker-compose service name.
    SONAR_HOST_URL = 'http://sonarqube:9000'

    // Project key in SonarQube.
    SONAR_PROJECT_KEY = 'gestion-stock'

    // Jenkins Credentials ID (Secret text) containing the Sonar token.
    SONAR_TOKEN_CRED_ID = 'sonar-token'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        sh 'chmod +x mvnw || true'
        sh './mvnw -B clean verify'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withCredentials([string(credentialsId: env.SONAR_TOKEN_CRED_ID, variable: 'SONAR_TOKEN')]) {
          sh '''
            ./mvnw -B sonar:sonar \
              -Dsonar.projectKey=$SONAR_PROJECT_KEY \
              -Dsonar.host.url=$SONAR_HOST_URL \
              -Dsonar.token=$SONAR_TOKEN
          '''
        }
      }
    }
  }
}
