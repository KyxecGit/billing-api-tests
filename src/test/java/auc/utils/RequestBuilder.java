package auc.utils;

import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

/**
 * <b>RequestBuilder - Утилитный класс для построения HTTP-запросов с типовыми настройками</b>
 * <p>
 * Этот класс реализует паттерн <b>Builder</b> и содержит набор статических фабрик для создания
 * предконфигурированных HTTP запросов. Использование этого класса обеспечивает:
 * </p>
 * <ul>
 *   <li><b>Консистентность:</b> Все запросы одного типа (GET, POST и т.д.) конфигурируются одинаково</li>
 *   <li><b>Централизация логики авторизации:</b> Добавление JWT токена в заголовок происходит в одном месте</li>
 *   <li><b>Читаемость:</b> Код тестов становится более понятным (authorizedGet vs given().header()...)</li>
 *   <li><b>Легкость изменения:</b> Если логика авторизации изменится, нужно обновить только этот класс</li>
 * </ul>
 * <p>
 * <b>Архитектурная роль:</b> Выступает промежуточным слоем между REST Assured и тестами,
 * абстрагируя детали конфигурации запроса от тестовых методов.
 * </p>
 * <p>
 * <b>Типовой паттерн использования:</b>
 * <pre>
 * // Перед: прямое использование given()
 * given()
 *     .header("Authorization", "Bearer " + token)
 *     .contentType("application/json")
 *     .when()
 *     .get("/api/endpoint");
 * 
 * // После: использование RequestBuilder
 * RequestBuilder.authorizedGet(token)
 *     .when()
 *     .get("/api/endpoint");
 * </pre>
 * </p>
 * <p>
 * <b>Структура методов:</b>
 * <ol>
 *   <li><b>Авторизованные методы:</b> authorizedGet(), authorizedPost(), authorizedPut(), authorizedDelete()</li>
 *   <li><b>Неавторизованные методы:</b> unauthorizedGet(), unauthorizedRequest()</li>
 * </ol>
 * </p>
 */
public class RequestBuilder {
    
    /**
     * <b>Создаёт авторизованный GET-запрос с JWT токеном в заголовке</b>
     * <p>
     * Этот метод формирует базовую RequestSpecification для GET запроса с авторизацией.
     * </p>
     * <p>
     * <b>Выполняемые конфигурации:</b>
     * <ol>
     *   <li>Устанавливает заголовок Authorization с Bearer токеном</li>
     *   <li>Устанавливает Content-Type в application/json</li>
     *   <li>Возвращает RequestSpecification для дальнейшей конфигурации</li>
     * </ol>
     * </p>
     * <p>
     * <b>Структура Authorization заголовка:</b>
     * <pre>
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0...
     * </pre>
     * </p>
     * <p>
     * <b>Примеры использования:</b>
     * <pre>
     * // Получение всех балансов
     * RequestBuilder.authorizedGet(adminToken)
     *     .when()
     *     .get("/api/balance/all");
     * 
     * // Получение баланса по ID
     * RequestBuilder.authorizedGet(adminToken)
     *     .when()
     *     .get("/api/balance/123");
     * 
     * // С дополнительными параметрами (query params, etc.)
     * RequestBuilder.authorizedGet(adminToken)
     *     .queryParam("status", "ACTIVE")
     *     .when()
     *     .get("/api/counter/all");
     * </pre>
     * </p>
     * <p>
     * <b>Возвращаемый тип:</b> RequestSpecification от REST Assured, что позволяет
     * использовать метод в цепочке вызовов (method chaining).
     * </p>
     * <p>
     * <b>Типичная цепочка вызовов:</b>
     * <pre>
     * RequestBuilder.authorizedGet(token)      // Создание RequestSpec
     *     .when()                               // Переход в фазу выполнения
     *     .get("/api/endpoint")                 // Выполнение запроса
     *     .then()                               // Переход в фазу проверки
     *     .statusCode(200);                     // Проверка ответа
     * </pre>
     * </p>
     *
     * @param token JWT токен для авторизации (формат: Bearer token)
     * @return RequestSpecification с установленными заголовками авторизации и типа контента
     * @see #authorizedPost(String, Object) для POST запросов с авторизацией
     * @see #unauthorizedGet() для публичных GET запросов без авторизации
     */
    public static RequestSpecification authorizedGet(String token) {
        // Используем REST Assured для создания базовой спецификации
        // Добавляем заголовок Authorization с Bearer token
        // Устанавливаем тип контента JSON для всех запросов
        return given()
            .header("Authorization", "Bearer " + token)
            .contentType(JSON);
    }
    
    /**
     * <b>Создаёт авторизованный POST-запрос с JWT токеном и телом запроса</b>
     * <p>
     * Этот метод используется для создания новых ресурсов (CREATE операции). Базируется на
     * authorizedGet(), добавляя тело запроса с данными для создания.
     * </p>
     * <p>
     * <b>Выполняемые конфигурации:</b>
     * <ol>
     *   <li>Вызывает authorizedGet() для установления авторизации и типа контента</li>
     *   <li>Добавляет тело запроса (requestBody) с данными</li>
     *   <li>Возвращает полностью сконфигурированную RequestSpecification</li>
     * </ol>
     * </p>
     * <p>
     * <b>Примеры использования:</b>
     * <pre>
     * // Создание профиля абонента
     * Map<String, Object> profileData = new HashMap<>();
     * profileData.put("msisdn", "996800123456");
     * profileData.put("userId", 1);
     * profileData.put("pricePlanId", 1);
     * 
     * Response response = RequestBuilder.authorizedPost(adminToken, profileData)
     *     .when()
     *     .post("/api/admin/profile/create");
     * 
     * // Регистрация пользователя (неавторизованный запрос)
     * Map<String, Object> signUpData = new HashMap<>();
     * signUpData.put("username", "newuser");
     * signUpData.put("password", "SecurePass123!");
     * signUpData.put("firstName", "John");
     * signUpData.put("lastName", "Doe");
     * signUpData.put("telegramChatId", "123456789");
     * 
     * RequestBuilder.unauthorizedRequest(signUpData)
     *     .when()
     *     .post("/api/auth/sign_up");
     * </pre>
     * </p>
     * <p>
     * <b>Типы тел запроса (Object body):</b>
     * <ul>
     *   <li><b>Map:</b> Map<String, Object> - самый гибкий способ</li>
     *   <li><b>POJO:</b> Любой Java объект - для сложных структур</li>
     *   <li><b>String:</b> JSON строка (обычно не рекомендуется)</li>
     * </ul>
     * </p>
     * <p>
     * <b>REST Assured автоматически:</b>
     * <ul>
     *   <li>Сериализует Object в JSON (используя Jackson)</li>
     *   <li>Отправляет JSON с Content-Type: application/json</li>
     * </ul>
     * </p>
     *
     * @param token JWT токен для авторизации
     * @param body тело запроса (обычно Map или POJO) для отправки на сервер
     * @return RequestSpecification с заголовками и телом запроса
     * @see #authorizedPut(String, Object) для PUT запросов с телом
     * @see #unauthorizedRequest(Object) для POST без авторизации
     */
    public static RequestSpecification authorizedPost(String token, Object body) {
        // Получаем базовую спецификацию с авторизацией
        // Добавляем тело запроса для отправки данных на сервер
        return authorizedGet(token).body(body);
    }
    
    /**
     * <b>Создаёт авторизованный PUT-запрос с JWT токеном и телом запроса</b>
     * <p>
     * Этот метод используется для обновления существующих ресурсов (UPDATE операции).
     * По сути эквивалентен authorizedPost(), но предназначен для HTTP метода PUT.
     * </p>
     * <p>
     * <b>Выполняемые конфигурации:</b>
     * <ol>
     *   <li>Вызывает authorizedGet() для установления авторизации и типа контента</li>
     *   <li>Добавляет тело запроса с новыми данными для обновления</li>
     *   <li>Возвращает сконфигурированную RequestSpecification</li>
     * </ol>
     * </p>
     * <p>
     * <b>Примеры использования:</b>
     * <pre>
     * // Обновление профиля абонента
     * Map<String, Object> updatedData = new HashMap<>();
     * updatedData.put("msisdn", "996801234567");
     * updatedData.put("userId", 1);
     * updatedData.put("pricePlanId", 2);
     * 
     * RequestBuilder.authorizedPut(adminToken, updatedData)
     *     .when()
     *     .put("/api/admin/profile/update/5");
     * 
     * // Обновление баланса
     * Map<String, Object> balanceUpdate = new HashMap<>();
     * balanceUpdate.put("amount", 2500.75);
     * 
     * RequestBuilder.authorizedPut(adminToken, balanceUpdate)
     *     .when()
     *     .put("/api/balance/update/123");
     * </pre>
     * </p>
     * <p>
     * <b>Различие между POST и PUT:</b>
     * <ul>
     *   <li><b>POST:</b> Создание нового ресурса (обычно возвращает 201 Created)</li>
     *   <li><b>PUT:</b> Полное обновление существующего ресурса (возвращает 200 OK или 204 No Content)</li>
     * </ul>
     * </p>
     * <p>
     * <b>⚠️ ИЗВЕСТНЫЙ БАГ в API:</b> Эндпоинт /api/balance/update/{id} возвращает 400 Bad Request
     * вместо обработки requestBody как указано в спецификации.
     * </p>
     *
     * @param token JWT токен для авторизации
     * @param body тело запроса с новыми данными для обновления ресурса
     * @return RequestSpecification с заголовками и телом запроса
     * @see #authorizedPost(String, Object) для POST запросов
     * @see #authorizedDelete(String) для DELETE запросов без тела
     */
    public static RequestSpecification authorizedPut(String token, Object body) {
        // Аналогично POST, но для HTTP метода PUT (обновление данных)
        return authorizedGet(token).body(body);
    }
    
    /**
     * <b>Создаёт авторизованный DELETE-запрос с JWT токеном</b>
     * <p>
     * Этот метод используется для удаления ресурсов (DELETE операции).
     * DELETE запросы обычно не содержат тело (requestBody), только параметры пути.
     * </p>
     * <p>
     * <b>Выполняемые конфигурации:</b>
     * <ol>
     *   <li>Вызывает authorizedGet() для установления авторизации</li>
     *   <li>Возвращает RequestSpecification без добавления тела</li>
     * </ol>
     * </p>
     * <p>
     * <b>Примеры использования:</b>
     * <pre>
     * // Удаление профиля абонента
     * RequestBuilder.authorizedDelete(adminToken)
     *     .when()
     *     .delete("/api/admin/profile/delete/5");
     * 
     * // Удаление пользователя
     * RequestBuilder.authorizedDelete(adminToken)
     *     .when()
     *     .delete("/api/admin/users/delete/123");
     * </pre>
     * </p>
     * <p>
     * <b>⚠️ ИЗВЕСТНЫЙ БАГ в API:</b> Эндпоинт DELETE /api/admin/profile/delete/{id} возвращает
     * 204 No Content вместо 200 OK как указано в спецификации.
     * </p>
     * <p>
     * <b>Возможные статус-коды ответов:</b>
     * <ul>
     *   <li>200 OK - ресурс успешно удалён (как в спецификации)</li>
     *   <li>204 No Content - ресурс удалён, нет содержимого в ответе (текущее поведение API)</li>
     *   <li>404 Not Found - ресурс не найден</li>
     * </ul>
     * </p>
     *
     * @param token JWT токен для авторизации
     * @return RequestSpecification с заголовком авторизации и типом контента
     * @see #authorizedPost(String, Object) для POST запросов
     * @see #authorizedPut(String, Object) для PUT запросов
     */
    public static RequestSpecification authorizedDelete(String token) {
        // DELETE запросы обычно не содержат тело, поэтому просто возвращаем авторизованную спецификацию
        return authorizedGet(token);
    }
    
    /**
     * <b>Создаёт неавторизованный GET-запрос без JWT токена</b>
     * <p>
     * Этот метод используется для тестирования публичных эндпоинтов, которые не требуют авторизации
     * (например, /api/auth/sign_up, /api/auth/sign_in), а также для проверки механизма контроля доступа
     * (валидация случаев отсутствия токена).
     * </p>
     * <p>
     * <b>Выполняемые конфигурации:</b>
     * <ol>
     *   <li>Создаёт базовую RequestSpecification через given()</li>
     *   <li>Устанавливает Content-Type в application/json</li>
     *   <li><b>НЕ</b> добавляет заголовок Authorization</li>
     * </ol>
     * </p>
     * <p>
     * <b>Примеры использования:</b>
     * <pre>
     * // Попытка GET защищённого эндпоинта без токена - должен вернуться 403
     * Response response = RequestBuilder.unauthorizedGet()
     *     .when()
     *     .get("/api/balance/all");
     * 
     * ApiAssertions.assertForbidden(response); // Ожидаем 403 Forbidden
     * 
     * // Публичный эндпоинт регистрации (работает без авторизации)
     * RequestBuilder.unauthorizedGet()
     *     .when()
     *     .get("/api/auth/sign_in");
     * </pre>
     * </p>
     * <p>
     * <b>Когда использовать:</b>
     * <ul>
     *   <li>Тестирование защищённых эндпоинтов без авторизации (должны вернуть 403)</li>
     *   <li>Тестирование публичных эндпоинтов (регистрация, авторизация)</li>
     *   <li>Проверка валидности механизма контроля доступа</li>
     * </ul>
     * </p>
     *
     * @return RequestSpecification без заголовка авторизации
     * @see #authorizedGet(String) для авторизованных GET запросов
     * @see #unauthorizedRequest(Object) для неавторизованных POST/PUT запросов с телом
     */
    public static RequestSpecification unauthorizedGet() {
        // Создаём спецификацию БЕЗ заголовка Authorization
        // Это используется для тестирования публичных эндпоинтов и проверки контроля доступа
        return given().contentType(JSON);
    }
    
    /**
     * <b>Создаёт неавторизованный запрос с телом для публичных эндпоинтов</b>
     * <p>
     * Этот метод используется для отправки POST/PUT запросов без авторизации.
     * Базируется на unauthorizedGet(), добавляя тело запроса.
     * </p>
     * <p>
     * <b>Выполняемые конфигурации:</b>
     * <ol>
     *   <li>Вызывает unauthorizedGet() для базовой конфигурации</li>
     *   <li>Добавляет тело запроса (requestBody)</li>
     *   <li>Возвращает полностью сконфигурированную RequestSpecification</li>
     * </ol>
     * </p>
     * <p>
     * <b>Примеры использования:</b>
     * <pre>
     * // Регистрация пользователя
     * Map<String, Object> signUpData = new HashMap<>();
     * signUpData.put("username", "newuser123");
     * signUpData.put("password", "SecurePass123!");
     * signUpData.put("firstName", "John");
     * signUpData.put("lastName", "Doe");
     * signUpData.put("telegramChatId", "123456789");
     * 
     * RequestBuilder.unauthorizedRequest(signUpData)
     *     .when()
     *     .post("/api/auth/sign_up");
     * 
     * // Авторизация пользователя
     * Map<String, Object> signInData = new HashMap<>();
     * signInData.put("username", "newuser123");
     * signInData.put("password", "SecurePass123!");
     * 
     * Response response = RequestBuilder.unauthorizedRequest(signInData)
     *     .when()
     *     .post("/api/auth/sign_in");
     * 
     * String token = response.jsonPath().getString("content.token");
     * </pre>
     * </p>
     * <p>
     * <b>Когда использовать:</b>
     * <ul>
     *   <li>Публичные эндпоинты аутентификации (sign_up, sign_in)</li>
     *   <li>Попытка отправить данные на защищённый эндпоинт без токена (для тестирования безопасности)</li>
     * </ul>
     * </p>
     *
     * @param body тело запроса (обычно Map с данными для регистрации/авторизации)
     * @return RequestSpecification с телом но БЕЗ заголовка авторизации
     * @see #unauthorizedGet() для GET запросов без авторизации
     * @see #authorizedPost(String, Object) для авторизованных POST запросов
     */
    public static RequestSpecification unauthorizedRequest(Object body) {
        // Аналогично authorizedPost, но БЕЗ заголовка Authorization
        // Используется для публичных эндпоинтов и тестирования контроля доступа
        return unauthorizedGet().body(body);
    }
}
