package auc;

import auc.utils.ApiAssertions;
import auc.utils.RequestBuilder;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.*;

/**
 * Тесты для read-only эндпоинтов счётчиков трафика.
 * <p>
 * Проверяет получение счётчиков по ID, получение всех счётчиков и активных счётчиков.
 * Включает валидацию прав доступа и структуры ответов согласно OpenAPI спецификации.
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
