package com.tst.tictactoe.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "Player")
@Data
@NoArgsConstructor
public class Player {

    @Id
    private UUID playerId = UUID.randomUUID();
    private String name;
    private Character symbol;

    public Player(String name, Character symbol) {
        this.name = name;
        this.symbol = symbol;
    }
}
