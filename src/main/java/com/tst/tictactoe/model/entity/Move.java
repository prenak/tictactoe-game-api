package com.tst.tictactoe.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@ToString(exclude = {"game"})
@EqualsAndHashCode(exclude = {"game","player"})
@Entity
@Table(name = "Move")
@NoArgsConstructor
public class Move {

    @Id
    private UUID moveId = UUID.randomUUID();
    private Integer boardRow;
    private Integer boardColumn;
    private Date createdTimestamp;

    @ManyToOne
    @JoinColumn(name = "gameId")
    @JsonIgnoreProperties("moves")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "playerId")
    private Player player;

    public Move(Integer boardRow, Integer boardColumn, Game game, Player player) {
        this.boardRow = boardRow;
        this.boardColumn = boardColumn;
        this.game = game;
        this.player = player;
        this.createdTimestamp = new Date();
    }
}
