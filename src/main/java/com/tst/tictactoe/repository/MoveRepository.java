package com.tst.tictactoe.repository;

import com.tst.tictactoe.model.entity.Game;
import com.tst.tictactoe.model.entity.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MoveRepository extends JpaRepository<Move, UUID> {
}
