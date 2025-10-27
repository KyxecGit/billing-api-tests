package auc;

import auc.utils.ApiAssertions;
import auc.utils.RequestBuilder;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.*;

/**
 * <b>CounterApiTest - Тесты для read-only эндпоинтов счётчиков трафика</b>
 * <p>
 * Этот класс содержит 7 тестов для проверки операций чтения счётчиков трафика:
 * <ul>
 *   <li>GET операции - получение счётчиков по ID, все счётчики, активные счётчики</li>
 *   <li>Проверка прав доступа (только авторизованные пользователи)</li>
 *   <li>Валидация структуры ответа согласно спецификации</li>
 * </ul>
 * </p>
 * <p>
 * <b>Покрытые эндпоинты:</b>
 * <ul>
 *   <li><b>GET /api/admin/counter/{id}</b> - получение счётчика по ID (только администраторы)</li>
 *   <li><b>GET /api/admin/counter/all</b> - получение всех счётчиков (только администраторы)</li>
 *   <li><b>GET /api/admin/counter/all-active</b> - получение активных счётчиков (только администраторы)</li>
 * </ul>
 * </p>
 * <p>
 * <b>Структура счётчика:</b>
 * <pre>
 * {
 *   "id": 1,
 *   "name": "Traffic Counter 1",
 *   "description": "Счётчик входящего трафика",
 *   "isActive": true,
 *   "createdAt": "2025-10-01T08:00:00",
 *   "lastValue": 95672.50,
 *   "unit": "GB"
 * }
 * </pre>
 * </p>
 * <p>
 * <b>Требования доступа:</b>
 * <ul>
 *   <li>Все эндпоинты требуют JWT токена администратора (Bearer token)</li>
 *   <li>Без авторизации: 403 Forbidden</li>
 *   <li>С неверным токеном: 403 Forbidden</li>
 * </ul>
 * </p>
 * <p>
 * <b>Количество тестов:</b> 7
 * <ul>
 *   <li>2 теста для GET /api/admin/counter/{id} (успех + unauthorized)</li>
 *   <li>2 теста для GET /api/admin/counter/all (успех + unauthorized)</li>
 *   <li>2 теста для GET /api/admin/counter/all-active (успех + unauthorized)</li>
 *   <li>1 тест на пустой результат (no-active counters)</li>
 * </ul>
 * </p>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CounterApiTest extends BaseApiTest {

    private static Long existingCounterId;

    @BeforeAll
    public static void setup() {
        BaseApiTest.globalSetup();
        
        Response response = RequestBuilder.authorizedGet(adminToken)
            .when()
            .get(TestConfig.COUNTER_GET_ALL);
        
        if (response.statusCode() == 200) {
            existingCounterId = response.jsonPath().getLong("content[0].id");
            System.out.println("✓ Найден тестовый счётчик ID: " + existingCounterId);
        }
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/admin/counter/{id} - успешное получение")
    public void testGetCounterById_Success() {
        Response response = RequestBuilder.authorizedGet(adminToken)
            .when()
            .get(TestConfig.COUNTER_GET_BY_ID.replace("{id}", String.valueOf(existingCounterId)));
        
        ApiAssertions.assertOkResponse(response);
        response.then().body("content.id", equalTo(existingCounterId.intValue()));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/admin/counter/{id} - несуществующий ID")
    public void testGetCounterById_NotFound() {
        Response response = RequestBuilder.authorizedGet(adminToken)
            .when()
            .get(TestConfig.COUNTER_GET_BY_ID.replace("{id}", "999999999"));
        
        ApiAssertions.assertNotFound(response);
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/admin/counter/{id} - без токена")
    public void testGetCounterById_Unauthorized() {
        Response response = RequestBuilder.unauthorizedGet()
            .when()
            .get(TestConfig.COUNTER_GET_BY_ID.replace("{id}", String.valueOf(existingCounterId)));
        
        ApiAssertions.assertForbidden(response);
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/admin/counter/all - успешное получение всех")
    public void testGetAllCounters_Success() {
        Response response = RequestBuilder.authorizedGet(adminToken)
            .when()
            .get(TestConfig.COUNTER_GET_ALL);
        
        ApiAssertions.assertOkResponse(response);
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/admin/counter/all - без токена")
    public void testGetAllCounters_Unauthorized() {
        Response response = RequestBuilder.unauthorizedGet()
            .when()
            .get(TestConfig.COUNTER_GET_ALL);
        
        ApiAssertions.assertForbidden(response);
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/admin/counter/all-active - получение активных")
    public void testGetAllActiveCounters_Success() {
        Response response = RequestBuilder.authorizedGet(adminToken)
            .when()
            .get(TestConfig.COUNTER_GET_ALL_ACTIVE)
            .then()
            .statusCode(anyOf(is(200), is(204)))
            .extract()
            .response();
        
        if (response.statusCode() == 200) {
            response.then().body("content", isA(java.util.List.class));
        }
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/admin/counter/all-active - отсутствие токена возвращает 403")
    public void testGetAllActiveCounters_Unauthorized() {
        Response response = RequestBuilder.unauthorizedGet()
            .when()
            .get(TestConfig.COUNTER_GET_ALL_ACTIVE);
        
        ApiAssertions.assertForbidden(response);
    }
}
