package auc;

import auc.utils.ApiAssertions;
import auc.utils.RequestBuilder;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * Тесты для эндпоинтов аутентификации пользователей.
 * <p>
 * Проверяет функциональность регистрации (sign_up) и авторизации (sign_in),
 * включая валидацию дублирующихся пользователей и обязательных полей.
 * </p>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthApiTest extends BaseApiTest {
    
    @Test
    @Order(1)
    @DisplayName("POST /api/auth/sign_up - успешная регистрация")
    public void testSignUp_Success() {
        String uniqueUsername = "user_" + System.currentTimeMillis();
        
        Map<String, Object> signUpData = new HashMap<>();
        signUpData.put("username", uniqueUsername);
        signUpData.put("password", "Password123!");
        signUpData.put("firstName", "Test");
        signUpData.put("lastName", "User");
        signUpData.put("telegramChatId", "123456789");
        
        RequestBuilder.unauthorizedRequest(signUpData)
            .when()
            .post(TestConfig.AUTH_REGISTER)
            .then()
            .statusCode(anyOf(is(200), is(201)));
    }
    
    @Test
    @Order(2)
    @DisplayName("POST /api/auth/sign_up - дублирующийся username")
    public void testSignUp_DuplicateUsername() {
        String duplicateUsername = "duplicate_user_" + System.currentTimeMillis();
        
        Map<String, Object> signUpData = new HashMap<>();
        signUpData.put("username", duplicateUsername);
        signUpData.put("password", "Password123!");
        signUpData.put("firstName", "Test");
        signUpData.put("lastName", "User");
        signUpData.put("telegramChatId", "123456789");
        
        RequestBuilder.unauthorizedRequest(signUpData)
            .when()
            .post(TestConfig.AUTH_REGISTER);
        
        RequestBuilder.unauthorizedRequest(signUpData)
            .when()
            .post(TestConfig.AUTH_REGISTER)
            .then()
            .statusCode(anyOf(is(400), is(409)));
    }
    
    @Test
    @Order(3)
    @DisplayName("POST /api/auth/sign_in - успешная авторизация (получение JWT)")
    public void testSignIn_Success() {
        String username = "signin_test_" + System.currentTimeMillis();
        String password = "TestPassword123!";
        
        Map<String, Object> signUpData = new HashMap<>();
        signUpData.put("username", username);
        signUpData.put("password", password);
        signUpData.put("firstName", "SignIn");
        signUpData.put("lastName", "Test");
        signUpData.put("telegramChatId", "123456789");
        
        RequestBuilder.unauthorizedRequest(signUpData)
            .when()
            .post(TestConfig.AUTH_REGISTER);
        
        Map<String, Object> signInData = new HashMap<>();
        signInData.put("username", username);
        signInData.put("password", password);
        
        Response response = RequestBuilder.unauthorizedRequest(signInData)
            .when()
            .post(TestConfig.AUTH_SIGN_IN);
        
        ApiAssertions.assertOkResponse(response);
    }
    
    @Test
    @Order(4)
    @DisplayName("POST /api/auth/sign_up - отсутствуют обязательные поля")
    public void testSignUp_MissingRequiredFields() {
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("username", "test");
        
        RequestBuilder.unauthorizedRequest(invalidData)
            .when()
            .post(TestConfig.AUTH_REGISTER)
            .then()
            .statusCode(400);
    }
}
