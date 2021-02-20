package com.tst.tictactoe.model;

import com.tst.tictactoe.model.entity.Move;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GameInfo {

    private String gameId;
    private String player_x;
    private String player_o;
    private Character nextPlayer;
    Character[][] board;
    List<Move> movesInOrder = new ArrayList<>();

}
