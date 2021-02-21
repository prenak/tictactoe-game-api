package com.tst.tictactoe.service;


import com.tst.tictactoe.model.GameInfo;
import com.tst.tictactoe.repository.GameRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

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

}
