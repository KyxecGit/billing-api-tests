package auc.utils;

import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

/**
 * Утилитный класс для построения HTTP-запросов с типовыми настройками.
 * <p>
 * Предоставляет методы для создания авторизованных и неавторизованных запросов
 * с корректными заголовками и форматами данных.
 * </p>
 */
public class RequestBuilder {
    
    /**
     * Создаёт авторизованный GET-запрос с JWT токеном.
     *
     * @param token JWT токен для авторизации
     * @return спецификация запроса для дальнейшей конфигурации
     */
    public static RequestSpecification authorizedGet(String token) {
        return given().header("Authorization", "Bearer " + token).contentType(JSON);
    }
    
    /**
     * Создаёт авторизованный POST-запрос с JWT токеном и телом запроса.
     *
     * @param token JWT токен для авторизации
     * @param body тело запроса в формате JSON
     * @return спецификация запроса для дальнейшей конфигурации
     */
    public static RequestSpecification authorizedPost(String token, Object body) {
        return authorizedGet(token).body(body);
    }
    
    /**
     * Создаёт авторизованный PUT-запрос с JWT токеном и телом запроса.
     *
     * @param token JWT токен для авторизации
     * @param body тело запроса в формате JSON
     * @return спецификация запроса для дальнейшей конфигурации
     */
    public static RequestSpecification authorizedPut(String token, Object body) {
        return authorizedGet(token).body(body);
    }
    
    /**
     * Создаёт авторизованный DELETE-запрос с JWT токеном.
     *
     * @param token JWT токен для авторизации
     * @return спецификация запроса для дальнейшей конфигурации
     */
    public static RequestSpecification authorizedDelete(String token) {
        return authorizedGet(token);
    }
    
    /**
     * Создаёт неавторизованный GET-запрос без токена.
     *
     * @return спецификация запроса для дальнейшей конфигурации
     */
    public static RequestSpecification unauthorizedGet() {
        return given().contentType(JSON);
    }
    
    /**
     * Создаёт неавторизованный запрос с телом для публичных эндпоинтов.
     *
     * @param body тело запроса в формате JSON
     * @return спецификация запроса для дальнейшей конфигурации
     */
    public static RequestSpecification unauthorizedRequest(Object body) {
        return unauthorizedGet().body(body);
    }
}
