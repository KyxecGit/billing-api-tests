package qa.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import qa.config.Config;
import qa.helpers.AuthHelper;
import org.testng.Assert;

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
    
    // ========== ПРОВЕРКИ ==========
    
    /** Проверить что статус = ожидаемому */
    protected void checkStatus(Response response, int expected) {
        int actual = response.statusCode();
        Assert.assertEquals(actual, expected, 
            String.format("Ожидался статус %d, получен %d", expected, actual));
    }
    
    /** Проверить что статус один из списка */
    protected void checkStatusOneOf(Response response, int... expected) {
        int actual = response.statusCode();
        for (int status : expected) {
            if (actual == status) return;
        }
        Assert.fail(String.format("Ожидался один из %s, получен %d", 
            java.util.Arrays.toString(expected), actual));
    }
    
    // ========== ПОЛУЧЕНИЕ ДАННЫХ ИЗ JSON ==========
    
    /** Получить текст из JSON ответа */
    protected String text(Response response, String path) {
        return response.jsonPath().getString(path);
    }
    
    /** Получить число из JSON ответа */
    protected Integer number(Response response, String path) {
        return response.jsonPath().getInt(path);
    }
    
    /** Получить первый ID из списка */
    protected Integer firstId(String path) {
        Response response = get(path);
        var list = response.jsonPath().getList("content");
        return list.isEmpty() ? null : number(response, "content[0].id");
    }
}