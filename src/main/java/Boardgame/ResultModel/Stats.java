package Boardgame.ResultModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Stats{
    private String name;
    private Long winCount;

}
