package com.tst.tictactoe.api;


import com.tst.tictactoe.model.GameInfo;
import com.tst.tictactoe.model.dto.GameInfoDto;
import com.tst.tictactoe.model.dto.MoveInfoDto;
import com.tst.tictactoe.service.GamePlayService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/games")
public class GamePlayController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GamePlayService gamePlayService;


    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String createNewGame(@RequestBody GameInfoDto gameInfoDto) {
        log.info("Received a request to create a new game: {}", gameInfoDto);
        String gameId;
        try{
            GameInfo gameInfo = modelMapper.map(gameInfoDto, GameInfo.class);
            gameId = gamePlayService.createNewGame(gameInfo).toString();
            log.info("Created a new game with id {}", gameId);

        } catch (Exception ex){
            log.error("Exception - {}", ex.getMessage(), ex);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
            log.info("Returning ResponseStatusException: ", responseStatusException);
            throw responseStatusException;
        }
        return gameId;
    }


    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.FOUND)
    public List<GameInfoDto> fetchAllGames() {
        log.info("Received a request to fetch all games");
        List<GameInfoDto> gameInfoDtos = new ArrayList<>();
        try {
            List<GameInfo> gameInfoList = gamePlayService.fetchAllGames();
            gameInfoList.forEach(gameInfo -> {
                GameInfoDto gameInfoDto = modelMapper.map(gameInfo, GameInfoDto.class);
                gameInfoDto.getMoves().clear();
                gameInfo.getMovesInOrder().forEach(move -> {
                    gameInfoDto.getMoves()
                            .add(new MoveInfoDto(move.getPlayer().getSymbol(), move.getBoardRow(),
                                    move.getBoardColumn(), move.getCreatedTimestamp()));
                });
                gameInfoDtos.add(gameInfoDto);
            });

        } catch (Exception ex) {
            log.error("Exception - {}", ex.getMessage(), ex);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
            log.info("Returning ResponseStatusException: ", responseStatusException);
            throw responseStatusException;
        }
        return gameInfoDtos;
    }


    @PostMapping(value = "/{game-id}/moves", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public GameInfoDto makeAMove(@PathVariable("game-id") String gameId, @RequestBody MoveInfoDto moveInfoDto) {
        log.info("Received a request to make a move: {}", moveInfoDto);
        GameInfoDto gameInfoDto;
        try{
            GameInfo gameInfo = gamePlayService.makeAMove(gameId, moveInfoDto.getRow(), moveInfoDto.getColumn());
            gameInfoDto = modelMapper.map(gameInfo, GameInfoDto.class);
            gameInfoDto.getMoves().clear();
            gameInfo.getMovesInOrder().forEach(move -> {
                gameInfoDto.getMoves()
                        .add(new MoveInfoDto(move.getPlayer().getSymbol(), move.getBoardRow(),
                                move.getBoardColumn(), move.getCreatedTimestamp()));
            });

        } catch (Exception ex){
            log.error("Exception - {}", ex.getMessage(), ex);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
            log.info("Returning ResponseStatusException: ", responseStatusException);
            throw responseStatusException;
        }
        return gameInfoDto;
    }
}
