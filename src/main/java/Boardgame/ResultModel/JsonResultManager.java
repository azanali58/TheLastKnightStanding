package Boardgame.ResultModel;
import lombok.NonNull;
import Boardgame.util.JacksonHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonResultManager implements ResultManager {
    private Path filePath;

    public JsonResultManager(@NonNull Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Result> add(@NonNull Result result) throws IOException {
        var results = getAll();
        results.add(result);
        try (var out = Files.newOutputStream(filePath)) {
            JacksonHelper.writeList(out, results);
        }
        return results;
    }

    public List<Result> getAll() throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<Result>();
        }
        try (var in = Files.newInputStream(filePath)) {
            return JacksonHelper.readList(in, Result.class);
        }
    }

}

