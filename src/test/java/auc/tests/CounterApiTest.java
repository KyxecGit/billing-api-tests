package auc.tests;

import auc.BaseApiTest;
import auc.TestConfig;
import auc.dto.response.CounterDto;
import auc.utils.ApiAssertions;
import auc.utils.RequestBuilder;
import auc.utils.ResponseExtractor;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CounterApiTest extends BaseApiTest {

    private static Long existingCounterId;

    @BeforeClass
    public void setup() {
        Response response = RequestBuilder.authorized(adminToken)
            .get(TestConfig.COUNTER_GET_ALL);

        ApiAssertions.assertOkResponse(response);
        existingCounterId = response.jsonPath().getLong("content[0].id");
    }

    @Test(priority = 1, description = "GET /api/admin/counter/{id} - успешное получение")
    public void testGetCounterById_Success() {
        Response response = RequestBuilder.authorized(adminToken)
            .get(url(TestConfig.COUNTER_GET_BY_ID, existingCounterId));
        
        ApiAssertions.assertOkResponse(response);
        CounterDto counter = ResponseExtractor.extractContent(response, CounterDto.class);
        Assert.assertEquals(counter.getId(), existingCounterId);
    }

    @Test(priority = 2, description = "GET /api/admin/counter/{id} - несуществующий ID")
    public void testGetCounterById_NotFound() {
        Response response = RequestBuilder.authorized(adminToken)
            .get(url(TestConfig.COUNTER_GET_BY_ID, 999999999));
        
        ApiAssertions.assertNotFound(response);
    }

    @Test(priority = 3, description = "GET /api/admin/counter/{id} - без токена")
    public void testGetCounterById_Unauthorized() {
        Response response = RequestBuilder.unauthorized()
            .get(url(TestConfig.COUNTER_GET_BY_ID, existingCounterId));
        
        ApiAssertions.assertForbidden(response);
    }

    @Test(priority = 4, description = "GET /api/admin/counter/all - успешное получение всех")
    public void testGetAllCounters_Success() {
        Response response = RequestBuilder.authorized(adminToken)
            .get(TestConfig.COUNTER_GET_ALL);
        
        ApiAssertions.assertOkResponse(response);
    }

    @Test(priority = 5, description = "GET /api/admin/counter/all - без токена")
    public void testGetAllCounters_Unauthorized() {
        Response response = RequestBuilder.unauthorized()
            .get(TestConfig.COUNTER_GET_ALL);
        
        ApiAssertions.assertForbidden(response);
    }

    @Test(priority = 6, description = "GET /api/admin/counter/all-active - получение активных")
    public void testGetAllActiveCounters_Success() {
        Response response = RequestBuilder.authorized(adminToken)
            .get(TestConfig.COUNTER_GET_ALL_ACTIVE);

        ApiAssertions.assertOkResponse(response);
    }

    @Test(priority = 7, description = "GET /api/admin/counter/all-active - отсутствие токена возвращает 403")
    public void testGetAllActiveCounters_Unauthorized() {
        Response response = RequestBuilder.unauthorized()
            .get(TestConfig.COUNTER_GET_ALL_ACTIVE);
        
        ApiAssertions.assertForbidden(response);
    }
}
