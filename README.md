# TicTacToe Game Api
#### TicTacToe Game Api is a Spring Boot application that provides REST interfaces to create and play Tic-Tac-Toe game. It supports the following functionalities:
- Create a new game for 2 players.
- Fetch the list of all games.
- Play the next move of a specific game.

## How to  Run

### Starting the application:
Run the below commands to bring up the application.
```bash  
cd tictactoe-game-api  
mvn spring-boot:run  
```    
The application would be up and running on 8282 port.

### Application monitoring:
Application health and info can be monitored via actuator management context.
- http://localhost:8282/actuator/health
- http://localhost:8282/actuator/info

## REST Endpoints

### 1. POST  /v1/games
Creates a new game.    
Example: http://localhost:8282/v1/games
```bash  
{  
  "player_x": "P1",  
  "player_o": "P2"  
}  
```  
### 2. POST  /v1/games/{gameId}/moves
Play a move for a specific game id.   
Example: http://localhost:8282/v1/games/3aa939e9-216b-4394-8184-73529729f1de/moves
```bash  
{  
  "row":2,  
  "column": 2  
}  
```  
Response:
```bash  
{  
  "gameId": "3aa939e9-216b-4394-8184-73529729f1de",  
  "player_x": "P1",  
  "player_o": "P2",  
  "nextPlayer": "O",  
  "board": [  
 ["","","" ], ["","","" ], ["","", "X" ] ],  "moves": []  
}  
```  
### 3. GET  /v1/games Fetch all the games.
Example: http://localhost:8282/v1/games      
Response:
```bash  
[  
 {  "gameId": "3aa939e9-216b-4394-8184-73529729f1de",  
  "player_x": "P1",  
  "player_o": "P2",  
  "moves": [  
 {  "symbol": "X",  
  "row": 2,  
  "column": 2,  
  "moveTimestamp": "2021-02-23T17:01:50.107+00:00"  
 } ] }]  
```  

## CI/CD Support
>  DockerFile, JenkinFile and Kubernetes configurations are included.


###  NOTE:
>  Since this application uses H2 in-memory database to store the data, the functionalities might not work very well when deployed as a multi instance cluster.
