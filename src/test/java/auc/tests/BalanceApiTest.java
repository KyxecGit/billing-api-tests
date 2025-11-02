package auc.tests;

import auc.BaseApiTest;
import auc.TestConfig;
import auc.dto.request.BalanceUpdateRequest;
import auc.dto.response.BalanceDto;
import auc.utils.ApiAssertions;
import auc.utils.RequestBuilder;
import auc.utils.ResponseExtractor;
import auc.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BalanceApiTest extends BaseApiTest {

    private static long testBalanceId;

    @BeforeClass
    public void setup() {
        Response response = RequestBuilder.authorized(adminToken)
            .get(TestConfig.BALANCE_GET_ALL);

        ApiAssertions.assertOkResponse(response);
        testBalanceId = response.jsonPath().getLong("content[0].id");
    }

    @Test(priority = 1, description = "GET /api/balance/{id} - успешное получение баланса")
    public void testGetBalanceById_Success() {
        Response response = RequestBuilder.authorized(adminToken)
            .get(url(TestConfig.BALANCE_GET_BY_ID, testBalanceId));
        
        ApiAssertions.assertOkResponse(response);
        BalanceDto balance = ResponseExtractor.extractContent(response, BalanceDto.class);
        Assert.assertEquals(balance.getId(), testBalanceId);
    }

    @Test(priority = 2, description = "GET /api/balance/{id} - несуществующий ID")
    public void testGetBalanceById_NotFound() {
        Response response = RequestBuilder.authorized(adminToken)
            .get(url(TestConfig.BALANCE_GET_BY_ID, 999999999));
        
        ApiAssertions.assertNotFound(response);
    }

    @Test(priority = 3, description = "GET /api/balance/{id} - без токена")
    public void testGetBalanceById_Unauthorized() {
        Response response = RequestBuilder.unauthorized()
            .get(url(TestConfig.BALANCE_GET_BY_ID, testBalanceId));
        
        ApiAssertions.assertForbidden(response);
    }

    @Test(priority = 4, description = "GET /api/balance/all - успешное получение всех балансов")
    public void testGetAllBalances_Success() {
        Response response = RequestBuilder.authorized(adminToken)
            .get(TestConfig.BALANCE_GET_ALL);
        
        ApiAssertions.assertOkResponse(response);
    }

    @Test(priority = 5, description = "GET /api/balance/all - без токена")
    public void testGetAllBalances_Unauthorized() {
        Response response = RequestBuilder.unauthorized()
            .get(TestConfig.BALANCE_GET_ALL);
        
        ApiAssertions.assertForbidden(response);
    }

    @Test(priority = 6, description = "PUT /api/balance/update/{id} - успешное обновление по спецификации")
    public void testUpdateBalance_AsPerSpecification() {
        BalanceUpdateRequest body = BalanceUpdateRequest.builder()
            .amount(TestDataGenerator.generateBalanceAmount())
            .build();

        Response response = RequestBuilder.authorized(adminToken).body(body)
            .put(url(TestConfig.BALANCE_UPDATE, testBalanceId));

        ApiAssertions.assertOkResponse(response);
    }

    @Test(priority = 7, description = "PUT /api/balance/update/{id} - несуществующий ID")
    public void testUpdateBalance_NotFound() {
        BalanceUpdateRequest body = BalanceUpdateRequest.builder()
            .amount(TestDataGenerator.generateBalanceAmount())
            .build();
        
        Response response = RequestBuilder.authorized(adminToken).body(body)
            .put(url(TestConfig.BALANCE_UPDATE, 999999999));
        
        ApiAssertions.assertNotFound(response);
    }

    @Test(priority = 8, description = "PUT /api/balance/update/{id} - без токена")
    public void testUpdateBalance_Unauthorized() {
        BalanceUpdateRequest body = BalanceUpdateRequest.builder()
            .amount(TestDataGenerator.generateBalanceAmount())
            .build();
        
        Response response = RequestBuilder.unauthorized().body(body)
            .put(url(TestConfig.BALANCE_UPDATE, testBalanceId));
        
        ApiAssertions.assertForbidden(response);
    }
    
    @Test(priority = 9, description = "PUT /api/balance/update/{id} - валидация: отсутствует обязательное поле amount")
    public void testUpdateBalance_MissingAmount() {
        BalanceUpdateRequest body = new BalanceUpdateRequest();
        
        Response response = RequestBuilder.authorized(adminToken).body(body)
            .put(url(TestConfig.BALANCE_UPDATE, testBalanceId));
        
        ApiAssertions.assertBadRequest(response);
    }
}
