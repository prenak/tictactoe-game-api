package com.tst.tictactoe.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GameInfoDto {

    private String gameId;
    private String player_x;
    private String player_o;
    private Character nextPlayer;
    Character[][] board;
    List<MoveInfoDto> moves = new ArrayList<>();
}
