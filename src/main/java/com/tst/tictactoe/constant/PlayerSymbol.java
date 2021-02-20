package com.tst.tictactoe.constant;

import lombok.Getter;

@Getter
public enum PlayerSymbol {

    X ('X'),
    O ('O')
    ;

    private Character symbol;

    PlayerSymbol(Character symbol) {
        this.symbol = symbol;
    }
}
