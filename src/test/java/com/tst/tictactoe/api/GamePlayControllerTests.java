package com.tst.tictactoe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tst.tictactoe.config.CommonBeanConfiguration;
import com.tst.tictactoe.model.dto.GameInfoDto;
import com.tst.tictactoe.service.GamePlayService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

@RunWith(SpringRunner.class)
@WebMvcTest(GamePlayController.class)
@ContextConfiguration(classes = {GamePlayController.class, CommonBeanConfiguration.class})
public class GamePlayControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GamePlayService gamePlayService;

    @Test
    public void test_CreateNewGame_ForBadRequests() throws Exception {
        Mockito.when(gamePlayService.createNewGame(Mockito.any())).thenCallRealMethod();

        GameInfoDto gameInfoDto = new GameInfoDto();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/games")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(gameInfoDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResolvedException())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Pass a valid name for Player_X");
    }

    @Test
    public void test_CreateNewGame() throws Exception {
        UUID uuid = UUID.randomUUID();
        Mockito.when(gamePlayService.createNewGame(Mockito.any())).thenReturn(uuid);

        GameInfoDto gameInfoDto = new GameInfoDto();
        gameInfoDto.setPlayer_x("P1");
        gameInfoDto.setPlayer_o("P2");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/games")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(gameInfoDto)))
                .andExpect(status().isCreated())
                .andReturn();

        Assertions.assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(uuid.toString());
    }

    //TODO: Rest of the test cases to be written in the similar manner

    private static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
