package auc;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

/**
 * Базовый абстрактный класс для всех автотестов API.
 * <p>
 * Предоставляет общую конфигурацию REST Assured и управление аутентификацией.
 * Все тестовые классы должны наследоваться от этого базового класса.
 * </p>
 */
public abstract class BaseApiTest {
    
    /**
     * JWT токен аутентифицированного администратора, используемый во всех тестах.
     */
    protected static String adminToken;
    
    /**
     * Глобальная настройка REST Assured перед запуском всех тестов.
     * Конфигурирует базовый URI и выполняет аутентификацию администратора.
     */
    @BeforeAll
    public static void globalSetup() {
        RestAssured.baseURI = TestConfig.BASE_URL;
        adminToken = getAdminToken();
        System.out.println("✓ Получен JWT токен администратора");
    }
    
    /**
     * Получает JWT токен администратора через эндпоинт аутентификации.
     *
     * @return JWT токен для использования в авторизованных запросах
     */
    private static String getAdminToken() {
        given()
            .contentType(ContentType.JSON)
            .body(String.format("{\"username\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\",\"telegramChatId\":\"%s\"}",
                TestConfig.ADMIN_USERNAME, TestConfig.ADMIN_PASSWORD, "Admin", "User", "123456789"))
            .post("/api/auth/sign_up");
        
        Response response = given()
            .contentType(ContentType.JSON)
            .body(String.format("{\"username\":\"%s\",\"password\":\"%s\"}", 
                TestConfig.ADMIN_USERNAME, TestConfig.ADMIN_PASSWORD))
            .post("/api/auth/sign_in");
        
        return response.jsonPath().getString("content.token");
    }
}
