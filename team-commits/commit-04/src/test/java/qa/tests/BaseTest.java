package qa.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import qa.config.Config;
import qa.helpers.AuthHelper;

/**
 * Базовый класс для всех тестов.
 * Содержит простые методы для работы с API.
 */
public class BaseTest {
    
    // ========== HTTP МЕТОДЫ ==========
    
    /** Отправить GET запрос */
    protected Response get(String path) {
        return RestAssured.given()
            .header("Authorization", "Bearer " + AuthHelper.getToken())
            .get(Config.BASE_URL + path);
    }
    
    /** Отправить POST запрос с JSON */
    protected Response post(String path, String jsonBody) {
        return RestAssured.given()
            .header("Authorization", "Bearer " + AuthHelper.getToken())
            .contentType("application/json")
            .body(jsonBody)
            .post(Config.BASE_URL + path);
    }
    
    /** Отправить PUT запрос с JSON */
    protected Response put(String path, String jsonBody) {
        return RestAssured.given()
            .header("Authorization", "Bearer " + AuthHelper.getToken())
            .contentType("application/json")
            .body(jsonBody)
            .put(Config.BASE_URL + path);
    }
    
    /** Отправить DELETE запрос */
    protected Response delete(String path) {
        return RestAssured.given()
            .header("Authorization", "Bearer " + AuthHelper.getToken())
            .delete(Config.BASE_URL + path);
    }
}