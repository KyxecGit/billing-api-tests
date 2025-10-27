package auc.utils;

import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;

/**
 * <b>ApiAssertions - Утилитный класс для стандартизированных проверок HTTP-ответов</b>
 * <p>
 * Этот класс содержит коллекцию статических методов для проверки типичных HTTP ответов.
 * Использование этого класса обеспечивает:
 * </p>
 * <ul>
 *   <li><b>Консистентность проверок:</b> Все тесты используют одинаковую логику валидации</li>
 *   <li><b>Читаемость:</b> Вместо длинных цепочек .then().statusCode().body() используются понятные методы</li>
 *   <li><b>Переиспользование:</b> Одна логика проверки используется во множестве тестов</li>
 *   <li><b>Лёгкость изменения:</b> Если API изменит структуру ответа, изменение в одном методе отразится везде</li>
 * </ul>
 * <p>
 * <b>Архитектурный паттерн:</b> Это реализация паттерна <b>Strategy</b>, где каждый метод
 * представляет стратегию проверки для конкретного типа ответа.
 * </p>
 * <p>
 * <b>Типовая структура ответа API (успешный запрос):</b>
 * <pre>
 * {
 *   "code": "OK",                    // Или "CREATED", "ERROR" и т.д.
 *   "message": "Operation successful",
 *   "content": { ... }               // Данные ответа
 * }
 * </pre>
 * </p>
 * <p>
 * <b>Пример использования:</b>
 * <pre>
 * Response response = RequestBuilder.authorizedGet(token)
 *     .when().get("/api/endpoint");
 * 
 * // Вместо:
 * response.then().statusCode(200)
 *     .body("code", equalTo("OK"))
 *     .body("content", notNullValue());
 * 
 * // Используем:
 * ApiAssertions.assertOkResponse(response);
 * </pre>
 * </p>
 * <p>
 * <b>Расширение функциональности:</b> Для добавления новых типов проверок добавьте новый метод:
 * <pre>
 * public static void assertUnprocessableEntity(Response response) {
 *     response.then().statusCode(422);
 * }
 * </pre>
 * </p>
 */
public class ApiAssertions {
    
    /**
     * <b>Проверяет, что ответ имеет статус-код 200 OK с типовой структурой успешного ответа</b>
     * <p>
     * Этот метод проверяет следующие условия:
     * <ol>
     *   <li><b>Статус-код = 200:</b> Запрос выполнен успешно</li>
     *   <li><b>code = "OK":</b> Поле code содержит значение "OK"</li>
     *   <li><b>content != null:</b> Возвращаемые данные не пусты</li>
     * </ol>
     * </p>
     * <p>
     * <b>Используется для:</b> Получение существующих данных (GET), успешные обновления (PUT)
     * </p>
     * <p>
     * <b>Ожидаемая структура ответа:</b>
     * <pre>
     * HTTP/1.1 200 OK
     * {
     *   "code": "OK",
     *   "message": "Success",
     *   "content": {
     *     "id": 123,
     *     "field1": "value1",
     *     "field2": "value2"
     *   }
     * }
     * </pre>
     * </p>
     * <p>
     * <b>Примеры использования:</b>
     * <pre>
     * // Проверка успешного GET запроса
     * Response response = RequestBuilder.authorizedGet(token)
     *     .when().get("/api/balance/123");
     * ApiAssertions.assertOkResponse(response);
     * 
     * // Можно добавить дополнительные проверки после
     * response.then().body("content.id", equalTo(123));
     * </pre>
     * </p>
     *
     * @param response HTTP ответ API для проверки
     * @throws AssertionError если статус-код не 200, code не "OK" или content null
     * @see #assertCreatedResponse(Response) для проверки 201 Created
     * @see #assertBadRequest(Response) для проверки 400 Bad Request
     */
    public static void assertOkResponse(Response response) {
        response.then()
            .statusCode(200)
            .body("code", equalTo("OK"))
            .body("content", notNullValue());
    }
    
    /**
     * <b>Проверяет, что ответ имеет статус-код 201 Created с типовой структурой успешного создания</b>
     * <p>
     * Этот метод проверяет следующие условия:
     * <ol>
     *   <li><b>Статус-код = 201:</b> Новый ресурс был создан</li>
     *   <li><b>code = "CREATED":</b> Поле code содержит значение "CREATED"</li>
     *   <li><b>content != null:</b> Возвращаемые данные (включая ID созданного ресурса) не пусты</li>
     * </ol>
     * </p>
     * <p>
     * <b>Используется для:</b> Создание новых ресурсов (POST), когда API строго следует REST соглашениям
     * </p>
     * <p>
     * <b>Ожидаемая структура ответа:</b>
     * <pre>
     * HTTP/1.1 201 Created
     * Location: /api/profile/15
     * {
     *   "code": "CREATED",
     *   "message": "Resource created",
     *   "content": {
     *     "id": 15,
     *     "msisdn": "996800123456",
     *     "userId": 1,
     *     "createdAt": "2025-10-27T14:30:00"
     *   }
     * }
     * </pre>
     * </p>
     * <p>
     * <b>Примеры использования:</b>
     * <pre>
     * Map<String, Object> newProfile = new HashMap<>();
     * newProfile.put("msisdn", "996800123456");
     * newProfile.put("userId", 1);
     * newProfile.put("pricePlanId", 1);
     * 
     * Response response = RequestBuilder.authorizedPost(token, newProfile)
     *     .when().post("/api/admin/profile/create");
     * 
     * ApiAssertions.assertCreatedResponse(response);
     * Long newId = response.jsonPath().getLong("content.id");
     * </pre>
     * </p>
     * <p>
     * <b>⚠️ Особенность:</b> В текущей реализации API для POST /api/admin/profile/create
     * возвращает 201 вместо 200 как указано в спецификации (это известный БАГ).
     * </p>
     *
     * @param response HTTP ответ API для проверки
     * @throws AssertionError если статус-код не 201, code не "CREATED" или content null
     * @see #assertOkResponse(Response) для проверки 200 OK
     */
    public static void assertCreatedResponse(Response response) {
        response.then()
            .statusCode(201)
            .body("code", equalTo("CREATED"))
            .body("content", notNullValue());
    }
    
    /**
     * <b>Проверяет, что ответ имеет статус-код 403 Forbidden (нет доступа)</b>
     * <p>
     * Этот статус-код указывает, что сервер понял запрос, но отказывает в доступе
     * из-за недостаточных прав или отсутствия аутентификации.
     * </p>
     * <p>
     * <b>Типичные причины 403:</b>
     * <ul>
     *   <li>Отсутствует заголовок Authorization с JWT токеном</li>
     *   <li>JWT токен невалиден или истёк (expired)</li>
     *   <li>Пользователь не имеет необходимой роли (например, попытка доступа к админским эндпоинтам обычным пользователем)</li>
     *   <li>Ресурс принадлежит другому пользователю и не может быть изменён</li>
     * </ul>
     * </p>
     * <p>
     * <b>Используется для:</b> Проверка механизма авторизации и контроля прав доступа
     * </p>
     * <p>
     * <b>Примеры использования:</b>
     * <pre>
     * // Попытка GET без токена - должен вернуться 403
     * Response response = RequestBuilder.unauthorizedGet()
     *     .when().get("/api/balance/all");
     * 
     * ApiAssertions.assertForbidden(response);
     * </pre>
     * </p>
     * <p>
     * <b>REST соглашения:</b>
     * <ul>
     *   <li>403 Forbidden - аутентифицирован, но нет доступа</li>
     *   <li>401 Unauthorized - не аутентифицирован (отсутствует токен)</li>
     * </ul>
     * </p>
     *
     * @param response HTTP ответ API для проверки
     * @throws AssertionError если статус-код не 403
     * @see #assertBadRequest(Response) для проверки 400 Bad Request
     * @see #assertNotFound(Response) для проверки 404 Not Found
     */
    public static void assertForbidden(Response response) {
        response.then().statusCode(403);
    }
    
    /**
     * <b>Проверяет, что ответ имеет статус-код 404 Not Found (ресурс не найден)</b>
     * <p>
     * Этот статус-код указывает, что запрошенный ресурс не существует на сервере.
     * </p>
     * <p>
     * <b>Типичные причины 404:</b>
     * <ul>
     *   <li>Попытка получить ресурс с несуществующим ID</li>
     *   <li>Эндпоинт не найден (опечатка в URL)</li>
     *   <li>Ресурс был удалён, но тест пытается его получить</li>
     * </ul>
     * </p>
     * <p>
     * <b>Используется для:</b> Проверка обработки несуществующих ресурсов и обработки ошибок
     * </p>
     * <p>
     * <b>Примеры использования:</b>
     * <pre>
     * // Попытка GET с несуществующим ID
     * Response response = RequestBuilder.authorizedGet(token)
     *     .when().get("/api/balance/999999999");
     * 
     * ApiAssertions.assertNotFound(response);
     * </pre>
     * </p>
     * <p>
     * <b>Тесты в проекте, использующие эту проверку:</b>
     * <ul>
     *   <li>BalanceApiTest.testGetBalanceById_NotFound()</li>
     *   <li>CounterApiTest.testGetCounterById_NotFound()</li>
     *   <li>ProfileApiTest.testGetProfileById_NotFound()</li>
     * </ul>
     * </p>
     *
     * @param response HTTP ответ API для проверки
     * @throws AssertionError если статус-код не 404
     * @see #assertBadRequest(Response) для проверки 400 Bad Request
     * @see #assertForbidden(Response) для проверки 403 Forbidden
     */
    public static void assertNotFound(Response response) {
        response.then().statusCode(404);
    }
    
    /**
     * <b>Проверяет, что ответ имеет статус-код 400 Bad Request (некорректные данные)</b>
     * <p>
     * Этот статус-код указывает, что сервер не смог обработать запрос из-за ошибки на стороне клиента.
     * Это может означать некорректный формат данных, отсутствие обязательных полей или невалидные значения.
     * </p>
     * <p>
     * <b>Типичные причины 400:</b>
     * <ul>
     *   <li>Отсутствуют обязательные поля в requestBody</li>
     *   <li>Тип данных не совпадает (например, строка вместо числа)</li>
     *   <li>Значение не соответствует валидационным правилам (например, MSISDN не совпадает с pattern)</li>
     *   <li>Дублирующееся значение уникального поля (например, уже существующий username)</li>
     * </ul>
     * </p>
     * <p>
     * <b>Используется для:</b> Проверка валидации входных данных и обработки ошибок
     * </p>
     * <p>
     * <b>Примеры использования:</b>
     * <pre>
     * // Попытка обновить баланс без обязательного поля amount
     * Map<String, Object> emptyBody = new HashMap<>();
     * Response response = RequestBuilder.authorizedPut(token, emptyBody)
     *     .when().put("/api/balance/update/123");
     * 
     * ApiAssertions.assertBadRequest(response);
     * </pre>
     * </p>
     * <p>
     * <b>Тесты в проекте, использующие эту проверку:</b>
     * <ul>
     *   <li>AuthApiTest.testSignUp_MissingRequiredFields()</li>
     *   <li>BalanceApiTest.testUpdateBalance_MissingAmount()</li>
     *   <li>ProfileApiTest.testCreateProfile_DuplicateMsisdn()</li>
     * </ul>
     * </p>
     *
     * @param response HTTP ответ API для проверки
     * @throws AssertionError если статус-код не 400
     * @see #assertNotFound(Response) для проверки 404 Not Found
     * @see #assertForbidden(Response) для проверки 403 Forbidden
     */
    public static void assertBadRequest(Response response) {
        response.then().statusCode(400);
    }
}
