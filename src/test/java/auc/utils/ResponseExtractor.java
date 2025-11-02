package auc.utils;

import io.restassured.response.Response;
import java.util.List;

public class ResponseExtractor {

    public static <T> T extractContent(Response response, Class<T> type) {
        return response.jsonPath().getObject("content", type);
    }

    public static <T> List<T> extractContentList(Response response, Class<T> type) {
        return response.jsonPath().getList("content", type);
    }

    public static Long extractId(Response response) {
        return response.jsonPath().getLong("content.id");
    }

    public static String extractToken(Response response) {
        return response.jsonPath().getString("content.token");
    }
}
