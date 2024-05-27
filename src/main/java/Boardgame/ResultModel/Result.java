package Boardgame.ResultModel;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import lombok.*;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {


    private String whitePlayer;
    private String blackPlayer;
    private String winner;
    private int numberOfMovesWhite;
    private int numberOfMovesBlack;
    private LocalDateTime startDateTime;


}
