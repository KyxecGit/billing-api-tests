package qa.helpers;

import io.restassured.RestAssured;
import qa.config.Config;

/**
 * Помощник для авторизации в API.
 * Получает и кэширует JWT токен для всех тестов.
 */
public class AuthHelper {
    
    private static String token;

    /**
     * Получить токен авторизации.
     * Токен получается один раз и используется во всех тестах.
     */
    public static String getToken() {
        if (token == null) {
            token = login();
        }
        return token;
    }
    
    /**
     * Выполнить вход в систему и получить токен.
     */
    private static String login() {
        String body = String.format(
            "{\"username\":\"%s\",\"password\":\"%s\"}", 
            Config.ADMIN_USERNAME, 
            Config.ADMIN_PASSWORD
        );
        
        return RestAssured.given()
            .contentType("application/json")
            .body(body)
            .post(Config.BASE_URL + "/api/auth/sign_in")
            .jsonPath()
            .getString("content.token");
    }
    
    /** 
     * Сбросить токен (для повторного входа).
     */
    public static void resetToken() {
        token = null;
    }
}