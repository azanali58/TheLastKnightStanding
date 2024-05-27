package Boardgame.ResultModel;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface ResultManager {
    List<Result> add(Result result) throws IOException;

    List<Result> getAll() throws IOException;

    default List<Stats> getBestPlayers(int limit) throws IOException {
        var winnerMap = getAll()
                .stream()
                .collect(Collectors.groupingBy(Result::getWinner, Collectors.counting()));
        return winnerMap.entrySet()
                .stream()
                .map(entry -> new Stats(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingLong(Stats::getWinCount).reversed())
                .limit(limit)
                .toList();
    }
}
