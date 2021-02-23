package com.tst.tictactoe.service;


import com.tst.tictactoe.model.GameInfo;
import com.tst.tictactoe.model.entity.Game;
import com.tst.tictactoe.model.entity.Move;
import com.tst.tictactoe.model.entity.Player;
import com.tst.tictactoe.repository.GameRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {GamePlayService.class})
public class GamePlayServiceTests {

    @Autowired
    private GamePlayService gamePlayService;

    @MockBean
    private GameRepository gameRepository;


    @Before
    public void setUp(){
        Mockito.when(gameRepository.save(Mockito.any())).thenAnswer(invocationOnMock -> {
           return invocationOnMock.getArgument(0);
        });
    }

    @Test
    public void test_CreateNewGame_ForIllegalArguments() {
        assertThat(catchThrowable(() -> {
            gamePlayService.createNewGame(null);
        })).isInstanceOf(IllegalArgumentException.class).hasMessage("GameInfo passed is null");

        assertThat(catchThrowable(() -> {
            gamePlayService.createNewGame(new GameInfo());
        })).isInstanceOf(IllegalArgumentException.class).hasMessage("Pass a valid name for Player_X");

        GameInfo gameInfo = new GameInfo();
        gameInfo.setPlayer_x("test_x");
        assertThat(catchThrowable(() -> {
            gamePlayService.createNewGame(gameInfo);
        })).isInstanceOf(IllegalArgumentException.class).hasMessage("Pass a valid name for Player_O");
    }

    @Test
    public void test_CreateNewGame_WithValidArguments() {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setPlayer_x("P1");
        gameInfo.setPlayer_o("P2");

        UUID gameId = gamePlayService.createNewGame(gameInfo);
        assertThat(gameId).isNotNull();

        ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);
        Mockito.verify(gameRepository, Mockito.atMostOnce()).save(gameArgumentCaptor.capture());
        Game createdGame = gameArgumentCaptor.getValue();
        assertThat(createdGame).isNotNull();
        assertThat(createdGame.getFirstPlayer().getName()).isEqualTo("P1");
        assertThat(createdGame.getFirstPlayer().getSymbol() == 'X').isTrue();
        assertThat(createdGame.getSecondPlayer().getName()).isEqualTo("P2");
        assertThat(createdGame.getSecondPlayer().getSymbol() == 'O').isTrue();
        assertThat(createdGame.getNextPlayer()).isEqualTo(createdGame.getFirstPlayer());
    }

    @Test
    public void test_MakeAMove_ForIllegalArguments() {
        assertThat(catchThrowable(() -> {
            gamePlayService.makeAMove(null, null, null);
        })).isInstanceOf(IllegalArgumentException.class).hasMessage("GameId passed is null or empty");

        assertThat(catchThrowable(() -> {
            gamePlayService.makeAMove("test-game-id", null, null);
        })).isInstanceOf(IllegalArgumentException.class).hasMessage("Row passed is null");

        assertThat(catchThrowable(() -> {
            gamePlayService.makeAMove("test-game-id", 1, null);
        })).isInstanceOf(IllegalArgumentException.class).hasMessage("Column passed is null");
    }

    @Test
    public void test_MakeAMove_WhenInvalidIndexIsPassed() {
        assertThat(catchThrowable(() -> {
            gamePlayService.makeAMove("test-game-id", -1, 0);
        })).isInstanceOf(Exception.class).hasMessage("Invalid indices passed");

        assertThat(catchThrowable(() -> {
            gamePlayService.makeAMove("test-game-id", 3, 2);
        })).isInstanceOf(Exception.class).hasMessage("Invalid indices passed");

        assertThat(catchThrowable(() -> {
            gamePlayService.makeAMove("test-game-id", 0, -1);
        })).isInstanceOf(Exception.class).hasMessage("Invalid indices passed");

        assertThat(catchThrowable(() -> {
            gamePlayService.makeAMove("test-game-id", 2, 3);
        })).isInstanceOf(Exception.class).hasMessage("Invalid indices passed");
    }

    @Test
    public void test_MakeAMove_WhenGameIdIsInvalid() {
        Mockito.when(gameRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        assertThat(catchThrowable(() -> {
            gamePlayService.makeAMove("e9226e4e-d45d-4e36-b807-d04516d385a9", 0, 0);
        })).isInstanceOf(Exception.class).hasMessage("Invalid game id");
    }

    @Test
    public void test_MakeAMove_WhenDuplicateRowAndColumnPassed() {
        Game game = new Game();
        game.getMoves().add(new Move(1, 2, game, new Player()));
        Mockito.when(gameRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(game));

        assertThat(catchThrowable(() -> {
            gamePlayService.makeAMove("e9226e4e-d45d-4e36-b807-d04516d385a9", 1, 2);
        })).isInstanceOf(Exception.class).hasMessage("Row 1 and Column 2 are already selected");
    }

    @Test
    public void test_MakeAMove_WhenTheGameIsOngoing() throws Exception {
        Game game = new Game();
        game.setFirstPlayer(new Player("P1",'X'));
        game.setSecondPlayer(new Player("P2",'O'));
        game.setNextPlayer(game.getSecondPlayer());
        game.getMoves().add(new Move(1, 2, game, game.getFirstPlayer()));
        Mockito.when(gameRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(game));

        GameInfo gameInfo = gamePlayService.makeAMove("e9226e4e-d45d-4e36-b807-d04516d385a9", 0, 2);
        ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);
        Mockito.verify(gameRepository, Mockito.atMostOnce()).save(gameArgumentCaptor.capture());

        assertThat(gameInfo.getPlayer_x()).isEqualTo("P1");
        assertThat(gameInfo.getPlayer_o()).isEqualTo("P2");
        assertThat(gameInfo.getNextPlayer() == 'X').isTrue();
        assertThat(CollectionUtils.isEmpty(gameInfo.getMovesInOrder())).isTrue();
    }

    @Test
    public void test_MakeAMove_WhenTheGameIsWon() throws Exception {
        Game game = new Game();
        game.setFirstPlayer(new Player("P1",'X'));
        game.setSecondPlayer(new Player("P2",'O'));
        game.setNextPlayer(game.getFirstPlayer());
        game.getMoves().add(new Move(0, 2, game, game.getFirstPlayer()));
        game.getMoves().add(new Move(1, 2, game, game.getSecondPlayer()));
        game.getMoves().add(new Move(0, 1, game, game.getFirstPlayer()));
        game.getMoves().add(new Move(2, 1, game, game.getSecondPlayer()));
        Mockito.when(gameRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(game));

        GameInfo gameInfo = gamePlayService.makeAMove("e9226e4e-d45d-4e36-b807-d04516d385a9", 0, 0);
        ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);
        Mockito.verify(gameRepository, Mockito.atMostOnce()).save(gameArgumentCaptor.capture());

        assertThat(gameInfo.getPlayer_x()).isEqualTo("P1");
        assertThat(gameInfo.getPlayer_o()).isEqualTo("P2");
        assertThat(gameInfo.getNextPlayer() == '\0').isTrue();
        assertThat(gameInfo.getMovesInOrder().size() == 5).isTrue();
    }

    @Test
    public void test_MakeAMove_WhenTheGameIsATie() throws Exception {
        Game game = new Game();
        game.setFirstPlayer(new Player("P1",'X'));
        game.setSecondPlayer(new Player("P2",'O'));
        game.setNextPlayer(game.getFirstPlayer());
        game.getMoves().add(new Move(0, 0, game, game.getFirstPlayer()));
        game.getMoves().add(new Move(0, 1, game, game.getSecondPlayer()));
        game.getMoves().add(new Move(0, 2, game, game.getFirstPlayer()));
        game.getMoves().add(new Move(1, 0, game, game.getSecondPlayer()));
        game.getMoves().add(new Move(1, 1, game, game.getFirstPlayer()));
        game.getMoves().add(new Move(1, 2, game, game.getSecondPlayer()));
        game.getMoves().add(new Move(2, 1, game, game.getFirstPlayer()));
        game.getMoves().add(new Move(2, 0, game, game.getSecondPlayer()));
        Mockito.when(gameRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(game));

        GameInfo gameInfo = gamePlayService.makeAMove("e9226e4e-d45d-4e36-b807-d04516d385a9", 2, 2);
        ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);
        Mockito.verify(gameRepository, Mockito.atMostOnce()).save(gameArgumentCaptor.capture());

        assertThat(gameInfo.getPlayer_x()).isEqualTo("P1");
        assertThat(gameInfo.getPlayer_o()).isEqualTo("P2");
        assertThat(gameInfo.getNextPlayer() == '\0').isTrue();
        assertThat(gameInfo.getMovesInOrder().size() == 9).isTrue();
    }
}
