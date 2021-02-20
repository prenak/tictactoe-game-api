FROM openjdk:11
ADD target/tictactoe-game-api.jar tictactoe-game-api.jar
EXPOSE 8282
ENTRYPOINT ["java","-jar","tictactoe-game-api.jar"]