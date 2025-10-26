package auc.utils;

import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;

/**
 * Утилитный класс для стандартизированных проверок HTTP-ответов API.
 * <p>
 * Предоставляет удобные методы для валидации статус-кодов ответов
 * без дублирования кода проверок в тестах.
 * </p>
 */
public class ApiAssertions {
    
    /**
     * Проверяет, что ответ имеет статус-код 200 OK.
     *
     * @param response HTTP-ответ для проверки
     */
    public static void assertOkResponse(Response response) {
        response.then()
            .statusCode(200)
            .body("code", equalTo("OK"))
            .body("content", notNullValue());
    }
    
    /**
     * Проверяет, что ответ имеет статус-код 201 Created.
     *
     * @param response HTTP-ответ для проверки
     */
    public static void assertCreatedResponse(Response response) {
        response.then()
            .statusCode(201)
            .body("code", equalTo("CREATED"))
            .body("content", notNullValue());
    }
    
    /**
     * Проверяет, что ответ имеет статус-код 403 Forbidden.
     *
     * @param response HTTP-ответ для проверки
     */
    public static void assertForbidden(Response response) {
        response.then().statusCode(403);
    }
    
    /**
     * Проверяет, что ответ имеет статус-код 404 Not Found.
     *
     * @param response HTTP-ответ для проверки
     */
    public static void assertNotFound(Response response) {
        response.then().statusCode(404);
    }
    
    /**
     * Проверяет, что ответ имеет статус-код 400 Bad Request.
     *
     * @param response HTTP-ответ для проверки
     */
    public static void assertBadRequest(Response response) {
        response.then().statusCode(400);
    }
}
