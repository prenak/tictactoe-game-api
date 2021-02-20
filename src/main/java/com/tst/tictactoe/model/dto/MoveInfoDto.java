package com.tst.tictactoe.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveInfoDto {

    private Character symbol;
    private Integer row;
    private Integer column;
    private Date moveTimestamp;
}
