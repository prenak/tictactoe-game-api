package com.tst.tictactoe.service;

import com.tst.tictactoe.constant.GameStatus;
import com.tst.tictactoe.constant.PlayerSymbol;
import com.tst.tictactoe.model.GameInfo;
import com.tst.tictactoe.model.entity.Game;
import com.tst.tictactoe.model.entity.Move;
import com.tst.tictactoe.model.entity.Player;
import com.tst.tictactoe.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GamePlayService {

    private static final Integer BOARD_SIZE = 3;

    @Autowired
    private GameRepository gameRepository;


    public UUID createNewGame(GameInfo newGameInfo){
        Assert.notNull(newGameInfo, "GameInfo passed is null");
        Assert.hasText(newGameInfo.getPlayer_x(), "Pass a valid name for Player_X");
        Assert.hasText(newGameInfo.getPlayer_o(), "Pass a valid name for Player_O");

        log.debug("Creating a new game with player_x {} and player_o {}", newGameInfo.getPlayer_x(), newGameInfo.getPlayer_o());
        Game game = new Game();
        game.setFirstPlayer(new Player(newGameInfo.getPlayer_x(), PlayerSymbol.X.getSymbol()));
        game.setSecondPlayer(new Player(newGameInfo.getPlayer_o(), PlayerSymbol.O.getSymbol()));
        game.setNextPlayer(game.getFirstPlayer());
        UUID gameId = gameRepository.save(game).getGameId();
        log.info("New game created. GameId: {}", gameId);
        return gameId;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GameInfo makeAMove(String gameId, Integer row, Integer column) throws Exception {
        Assert.hasText(gameId, "GameId passed is null or empty");
        Assert.notNull(row, "Row passed is null");
        Assert.notNull(column, "Column passed is null");

        if (! isValidIndices(row, column)) throw new Exception("Invalid indices passed");

        log.debug("Fetching the game with id {}", gameId);
        Game game = gameRepository.findById(UUID.fromString(gameId)).orElseThrow(() -> new Exception("Invalid game id"));
        log.debug("Game fetched: {}", game);
        Player player = game.getNextPlayer();
        Move move = new Move(row, column, game, player);
        game.getMoves().add(move);

        Character[][] board = convertMovesToGameBoard(game.getMoves());
        GameInfo gameInfo = new GameInfo();
        gameInfo.setGameId(gameId);
        gameInfo.setBoard(board);
        gameInfo.setPlayer_x(game.getFirstPlayer().getName());
        gameInfo.setPlayer_o(game.getSecondPlayer().getName());

        if(checkIfPlayerWon(board, row, column, player.getSymbol()) || (game.getMoves().size() == Math.pow(BOARD_SIZE, 2))) {
            log.info("Game completed!");
            game.setGameStatus(GameStatus.COMPLETED);
            game.setNextPlayer(null);
            gameInfo.setNextPlayer('\0');
            gameInfo.setMovesInOrder(game.getMoves().stream()
                    .sorted(Comparator.comparing(Move::getCreatedTimestamp))
                    .collect(Collectors.toList())
            );

        } else {
            log.debug("Game ongoing...");
            Character firstPlayerSymbol = game.getFirstPlayer().getSymbol();
            game.setNextPlayer(player.getSymbol() == firstPlayerSymbol ? game.getSecondPlayer() : game.getFirstPlayer());
            gameInfo.setNextPlayer(game.getNextPlayer().getSymbol());
        }

        gameRepository.save(game);
        log.info("Returning {}", gameInfo);
        return gameInfo;
    }


    public List<GameInfo> fetchAllGames() {
        log.debug("Fetching the list of all games");
        List<GameInfo> gameInfoList = new ArrayList<>();
        List<Game> games = gameRepository.findAll();
        if (CollectionUtils.isEmpty(games)){
            log.debug("No games available yet");
            return new ArrayList<>();
        }

        log.info("Found {} games", games.size());
        games.forEach(game -> {
            GameInfo gameInfo = new GameInfo();
            gameInfo.setGameId(game.getGameId().toString());
            gameInfo.setPlayer_x(game.getFirstPlayer().getName());
            gameInfo.setPlayer_o(game.getSecondPlayer().getName());
            gameInfo.setMovesInOrder(game.getMoves().stream()
                    .sorted(Comparator.comparing(Move::getCreatedTimestamp))
                    .collect(Collectors.toList())
            );
            gameInfoList.add(gameInfo);
        });
        log.debug("Returning the list of games: {}", gameInfoList);
        return gameInfoList;
    }


    private boolean checkIfPlayerWon(Character[][] board, int row, int col, char symbol) {
        log.debug("Checking if the current player won");
        for(int i = 0; i < BOARD_SIZE; i++){
            if(board[i][col] != symbol)
                break;
            if(i == BOARD_SIZE-1) return true;
        }

        // Check entire row for selected row
        for(int i = 0; i < BOARD_SIZE; i++){
            if(board[row][i] != symbol)
                break;
            if(i == BOARD_SIZE-1) return true;
        }

        // Check for diagonal
        if (row == col) {
            for(int i = 0; i < BOARD_SIZE; i++){
                if(board[i][i] != symbol)
                    break;
                if(i == BOARD_SIZE-1) return true;
            }
        }

        // Check for reverse diagonal
        if (row + col == BOARD_SIZE-1) {
            for(int i = 0; i < BOARD_SIZE; i++){
                if(board[i][(BOARD_SIZE-1)-i] != symbol)
                    break;
                if(i == BOARD_SIZE-1) return true;
            }
        }

        return false;
    }


    private Character[][] convertMovesToGameBoard(List<Move> moves) {
        log.debug("Converting the moves to board array");
        Character[][] board = new Character[BOARD_SIZE][BOARD_SIZE];
        for (Character[] row : board){
            Arrays.fill(row, '\0');
        }
        if (CollectionUtils.isEmpty(moves)) return board;

        moves.forEach(move -> {
            board[move.getBoardRow()][move.getBoardColumn()] = move.getPlayer().getSymbol();
        });
        log.debug("Board: {}", board);
        return board;
    }


    private boolean isValidIndices(int row, int column) {
        return  row >=0 && row < BOARD_SIZE && column >= 0 && column < BOARD_SIZE;
    }
}
