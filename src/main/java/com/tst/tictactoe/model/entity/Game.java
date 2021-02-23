package com.tst.tictactoe.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tst.tictactoe.constant.GameStatus;
import com.tst.tictactoe.constant.PlayerSymbol;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Game")
@Data
@ToString(exclude = "moves")
@EqualsAndHashCode(exclude = "moves")
public class Game {

    @Id
    private UUID gameId = UUID.randomUUID();

    private boolean isCompleted;

    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus = GameStatus.IN_PROGRESS;

    @ManyToOne(cascade = CascadeType.ALL)
    private Player nextPlayer;

    @ManyToOne(cascade = CascadeType.ALL)
    private Player firstPlayer;

    @ManyToOne(cascade = CascadeType.ALL)
    private Player secondPlayer;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("game")
    private List<Move> moves = new ArrayList<>();
}
