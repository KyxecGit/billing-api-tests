package auc.tests;

import auc.BaseApiTest;
import auc.TestConfig;
import auc.dto.request.ProfileCreateRequest;
import auc.dto.response.ProfileDto;
import auc.utils.ApiAssertions;
import auc.utils.RequestBuilder;
import auc.utils.ResponseExtractor;
import auc.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ProfileApiTest extends BaseApiTest {

    private static Long testProfileId;
    private static String testMsisdn;
    private static Long testUserId;
    private static Long testPricePlanId;

    @BeforeClass
    public void setup() {
        testMsisdn = TestDataGenerator.generateMsisdn();
        testUserId = 1L;
        testPricePlanId = 1L;

        ProfileCreateRequest body = ProfileCreateRequest.builder()
            .msisdn(testMsisdn)
            .userId(testUserId)
            .pricePlanId(testPricePlanId)
            .build();

        Response createResponse = RequestBuilder.authorized(adminToken).body(body)
            .post(TestConfig.PROFILE_CREATE);

        ApiAssertions.assertOkResponse(createResponse);
        testProfileId = ResponseExtractor.extractId(createResponse);
    }
    
    @Test(priority = 1, description = "POST /api/admin/profile/create - успешное создание")
    public void testCreateProfile_Success() {
        ProfileCreateRequest body = ProfileCreateRequest.builder()
            .msisdn(TestDataGenerator.generateMsisdn())
            .userId(testUserId)
            .pricePlanId(testPricePlanId)
            .build();

        Response response = RequestBuilder.authorized(adminToken).body(body)
            .post(TestConfig.PROFILE_CREATE);
        
        ApiAssertions.assertOkResponse(response);
        
        Long createdId = ResponseExtractor.extractId(response);
        RequestBuilder.authorized(adminToken)
            .delete(url(TestConfig.PROFILE_DELETE, createdId));
    }
    
    @Test(priority = 2, description = "POST /api/admin/profile/create - дубликат MSISDN")
    public void testCreateProfile_DuplicateMsisdn() {
        ProfileCreateRequest body = ProfileCreateRequest.builder()
            .msisdn(testMsisdn)
            .userId(testUserId)
            .pricePlanId(testPricePlanId)
            .build();

        Response response = RequestBuilder.authorized(adminToken).body(body)
            .post(TestConfig.PROFILE_CREATE);
        
        ApiAssertions.assertBadRequest(response);
    }
    
    @Test(priority = 3, description = "GET /api/admin/profile/{id} - успешное получение")
    public void testGetProfileById_Success() {
        Response response = RequestBuilder.authorized(adminToken)
            .get(url(TestConfig.PROFILE_GET_BY_ID, testProfileId));
        
        ApiAssertions.assertOkResponse(response);
        ProfileDto profile = ResponseExtractor.extractContent(response, ProfileDto.class);
        Assert.assertEquals(profile.getId(), testProfileId);
    }
    
    @Test(priority = 4, description = "GET /api/admin/profile/{id} - несуществующий ID")
    public void testGetProfileById_NotFound() {
        Response response = RequestBuilder.authorized(adminToken)
            .get(url(TestConfig.PROFILE_GET_BY_ID, 999999999));
        
        ApiAssertions.assertNotFound(response);
    }
    
    @Test(priority = 5, description = "GET /api/admin/profile/all - получение всех профилей")
    public void testGetAllProfiles_Success() {
        Response response = RequestBuilder.authorized(adminToken)
            .get(TestConfig.PROFILE_GET_ALL);
        
        ApiAssertions.assertOkResponse(response);
    }
    
    @Test(priority = 6, description = "PUT /api/admin/profile/update/{id} - успешное обновление")
    public void testUpdateProfile_Success() {
        String updatedMsisdn = TestDataGenerator.generateMsisdn();

        ProfileCreateRequest body = ProfileCreateRequest.builder()
            .msisdn(updatedMsisdn)
            .userId(testUserId)
            .pricePlanId(testPricePlanId)
            .build();

        Response response = RequestBuilder.authorized(adminToken).body(body)
            .put(url(TestConfig.PROFILE_UPDATE, testProfileId));
        
        ApiAssertions.assertOkResponse(response);
        ProfileDto profile = ResponseExtractor.extractContent(response, ProfileDto.class);
        Assert.assertEquals(profile.getMsisdn(), updatedMsisdn);
    }
    
    @Test(priority = 7, description = "DELETE /api/admin/profile/delete/{id} - проверка статус-кода")
    public void testDeleteProfile_StatusCode() {
        ProfileCreateRequest body = ProfileCreateRequest.builder()
            .msisdn(TestDataGenerator.generateMsisdn())
            .userId(testUserId)
            .pricePlanId(testPricePlanId)
            .build();

        Response createResponse = RequestBuilder.authorized(adminToken).body(body)
            .post(TestConfig.PROFILE_CREATE);
        
        Long createdId = ResponseExtractor.extractId(createResponse);
        
        Response deleteResponse = RequestBuilder.authorized(adminToken)
            .delete(url(TestConfig.PROFILE_DELETE, createdId));

        ApiAssertions.assertOkResponse(deleteResponse);
    }

    @Test(priority = 8, description = "GET /api/admin/profile/all-removed - получение списка удалённых профилей")
    public void testGetAllRemovedProfiles_Success() {
        Response response = RequestBuilder.authorized(adminToken)
            .get(TestConfig.PROFILE_GET_ALL_REMOVED);

        ApiAssertions.assertOkResponse(response);
    }

    @Test(priority = 9, description = "POST /api/admin/profile/create - без токена")
    public void testCreateProfile_Unauthorized() {
        ProfileCreateRequest body = ProfileCreateRequest.builder()
            .msisdn(TestDataGenerator.generateMsisdn())
            .userId(testUserId)
            .pricePlanId(testPricePlanId)
            .build();

        Response response = RequestBuilder.unauthorized().body(body)
            .post(TestConfig.PROFILE_CREATE);
        
        ApiAssertions.assertForbidden(response);
    }

    @Test(priority = 11, description = "GET /api/admin/profile/{id} - без токена")
    public void testGetProfileById_Unauthorized() {
        Response response = RequestBuilder.unauthorized()
            .get(url(TestConfig.PROFILE_GET_BY_ID, testProfileId));
        
        ApiAssertions.assertForbidden(response);
    }

    @Test(priority = 12, description = "GET /api/admin/profile/all - без токена")
    public void testGetAllProfiles_Unauthorized() {
        Response response = RequestBuilder.unauthorized()
            .get(TestConfig.PROFILE_GET_ALL);
        
        ApiAssertions.assertForbidden(response);
    }

    @Test(priority = 13, description = "PUT /api/admin/profile/update/{id} - без токена")
    public void testUpdateProfile_Unauthorized() {
        ProfileCreateRequest body = ProfileCreateRequest.builder()
            .msisdn(TestDataGenerator.generateMsisdn())
            .userId(testUserId)
            .pricePlanId(testPricePlanId)
            .build();

        Response response = RequestBuilder.unauthorized().body(body)
            .put(url(TestConfig.PROFILE_UPDATE, testProfileId));
        
        ApiAssertions.assertForbidden(response);
    }

    @Test(priority = 14, description = "DELETE /api/admin/profile/delete/{id} - без токена")
    public void testDeleteProfile_Unauthorized() {
        Response response = RequestBuilder.unauthorized()
            .delete(url(TestConfig.PROFILE_DELETE, testProfileId));
        
        ApiAssertions.assertForbidden(response);
    }
}
