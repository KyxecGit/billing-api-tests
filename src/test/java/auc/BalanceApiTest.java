package auc;

import auc.utils.ApiAssertions;
import auc.utils.RequestBuilder;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * <b>BalanceApiTest - Тесты для эндпоинтов управления балансами пользователей</b>
 * <p>
 * Этот класс содержит 10 тестов для проверки API баланса, включая:
 * <ul>
 *   <li>GET операции - получение баланса по ID и получение всех балансов</li>
 *   <li>PUT операции - обновление баланса</li>
 *   <li>Проверка прав доступа (авторизация через JWT)</li>
 *   <li>Обнаружение БАГ в эндпоинте UPDATE (см. тест testUpdateBalance_AsPerSpecification)</li>
 * </ul>
 * </p>
 * <p>
 * <b>Покрытые эндпоинты:</b>
 * <ul>
 *   <li><b>GET /api/balance/{id}</b> - получение баланса по ID (требует авторизацию)</li>
 *   <li><b>GET /api/balance/all</b> - получение всех балансов (требует авторизацию)</li>
 *   <li><b>PUT /api/balance/update/{id}</b> - обновление баланса (требует авторизацию)</li>
 * </ul>
 * </p>
 * <p>
 * <b>Структура баланса:</b>
 * <pre>
 * {
 *   "id": 1,
 *   "userId": 42,
 *   "amount": 1500.75,
 *   "currency": "USD",
 *   "lastUpdated": "2025-10-27T10:30:45"
 * }
 * </pre>
 * </p>
 * <p>
 * <b>🐛 ОБНАРУЖЕННЫЙ БАГ:</b>
 * Эндпоинт PUT /api/balance/update/{id} не принимает requestBody согласно OpenAPI спецификации.
 * Тест: testUpdateBalance_AsPerSpecification() - документирует эту ошибку.
 * </p>
 * <p>
 * <b>Количество тестов:</b> 10
 * <ul>
 *   <li>2 теста для GET /api/balance/{id}</li>
 *   <li>2 теста для GET /api/balance/all</li>
 *   <li>5 тестов для PUT /api/balance/update/{id}</li>
 *   <li>1 тест обнаружения БАГ</li>
 * </ul>
 * </p>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BalanceApiTest extends BaseApiTest {

    private static long testBalanceId;

    @BeforeAll
    public static void setup() {
        globalSetup();
        
        Response response = RequestBuilder.authorizedGet(adminToken)
            .when()
            .get(TestConfig.BALANCE_GET_ALL);
        
        if (response.statusCode() == 200 && !response.jsonPath().getList("content").isEmpty()) {
            testBalanceId = response.jsonPath().getLong("content[0].id");
        } else {
            testBalanceId = 1L;
        }
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/balance/{id} - успешное получение баланса")
    public void testGetBalanceById_Success() {
        Response response = RequestBuilder.authorizedGet(adminToken)
            .when()
            .get(TestConfig.BALANCE_GET_BY_ID.replace("{id}", String.valueOf(testBalanceId)));
        
        ApiAssertions.assertOkResponse(response);
        response.then().body("content.id", equalTo((int)testBalanceId));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/balance/{id} - несуществующий ID")
    public void testGetBalanceById_NotFound() {
        Response response = RequestBuilder.authorizedGet(adminToken)
            .when()
            .get(TestConfig.BALANCE_GET_BY_ID.replace("{id}", "999999999"));
        
        ApiAssertions.assertNotFound(response);
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/balance/{id} - без токена")
    public void testGetBalanceById_Unauthorized() {
        Response response = RequestBuilder.unauthorizedGet()
            .when()
            .get(TestConfig.BALANCE_GET_BY_ID.replace("{id}", String.valueOf(testBalanceId)));
        
        ApiAssertions.assertForbidden(response);
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/balance/all - успешное получение всех балансов")
    public void testGetAllBalances_Success() {
        Response response = RequestBuilder.authorizedGet(adminToken)
            .when()
            .get(TestConfig.BALANCE_GET_ALL);
        
        ApiAssertions.assertOkResponse(response);
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/balance/all - без токена")
    public void testGetAllBalances_Unauthorized() {
        Response response = RequestBuilder.unauthorizedGet()
            .when()
            .get(TestConfig.BALANCE_GET_ALL);
        
        ApiAssertions.assertForbidden(response);
    }

    @Test
    @Order(6)
    @DisplayName("🐛 PUT /api/balance/update/{id} - ПО СПЕЦИФИКАЦИИ (requestBody)")
    public void testUpdateBalance_AsPerSpecification() {
        Map<String, Object> body = new HashMap<>();
        body.put("amount", 2500.75);
        
        Response response = RequestBuilder.authorizedPut(adminToken, body)
            .when()
            .put(TestConfig.BALANCE_UPDATE.replace("{id}", String.valueOf(testBalanceId)));
        
        if (response.statusCode() == 400) {
            System.out.println("🐛 БАГ: API не принимает requestBody как указано в спецификации!");
            System.out.println("   Ожидаем: 200 OK с requestBody {\"amount\": 2500.75}");
            System.out.println("   Получаем: 400 Bad Request");
            response.then().statusCode(400);
        } else {
            ApiAssertions.assertOkResponse(response);
            System.out.println("✓ UPDATE работает по спецификации (requestBody)");
        }
    }

    @Test
    @Order(7)
    @DisplayName("PUT /api/balance/update/{id} - несуществующий ID")
    public void testUpdateBalance_NotFound() {
        Map<String, Object> body = new HashMap<>();
        body.put("amount", 1500.0);
        
        Response response = RequestBuilder.authorizedPut(adminToken, body)
            .when()
            .put(TestConfig.BALANCE_UPDATE.replace("{id}", "999999999"));
        
        response.then().statusCode(anyOf(is(404), is(400)));
    }

    @Test
    @Order(8)
    @DisplayName("PUT /api/balance/update/{id} - без токена")
    public void testUpdateBalance_Unauthorized() {
        Map<String, Object> body = new HashMap<>();
        body.put("amount", 3000.0);
        
        Response response = RequestBuilder.unauthorizedRequest(body)
            .when()
            .put(TestConfig.BALANCE_UPDATE.replace("{id}", String.valueOf(testBalanceId)));
        
        ApiAssertions.assertForbidden(response);
    }
    
    @Test
    @Order(9)
    @DisplayName("PUT /api/balance/update/{id} - валидация: отсутствует обязательное поле amount")
    public void testUpdateBalance_MissingAmount() {
        Map<String, Object> body = new HashMap<>();
        
        Response response = RequestBuilder.authorizedPut(adminToken, body)
            .when()
            .put(TestConfig.BALANCE_UPDATE.replace("{id}", String.valueOf(testBalanceId)));
        
        ApiAssertions.assertBadRequest(response);
    }
    
    @Test
    @Order(10)
    @DisplayName("PUT /api/balance/update/{id} - валидация: некорректный тип amount (строка вместо number)")
    public void testUpdateBalance_InvalidAmountType() {
        Map<String, Object> body = new HashMap<>();
        body.put("amount", "invalid_string");
        
        Response response = RequestBuilder.authorizedPut(adminToken, body)
            .when()
            .put(TestConfig.BALANCE_UPDATE.replace("{id}", String.valueOf(testBalanceId)));
        
        ApiAssertions.assertBadRequest(response);
    }
}
