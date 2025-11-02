package auc;

import auc.dto.request.AuthSignInRequest;
import auc.dto.request.AuthSignUpRequest;
import auc.utils.RequestBuilder;
import auc.utils.ResponseExtractor;
import auc.utils.TestDataGenerator;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;

public abstract class BaseApiTest {

    protected static String adminToken;
    
    @BeforeClass
    public void globalSetup() {
        RestAssured.baseURI = TestConfig.BASE_URL;
        adminToken = getAdminToken();
    }
    
    protected String url(String template, Object... params) {
        String result = template;
        for (Object param : params) {
            result = result.replaceFirst("\\{[^}]+}", String.valueOf(param));
        }
        return result;
    }
    
    private String getAdminToken() {
        // Try sign in first to avoid re-registering the same user every run
        AuthSignInRequest signIn = AuthSignInRequest.builder()
            .username(TestConfig.ADMIN_USERNAME)
            .password(TestConfig.ADMIN_PASSWORD)
            .build();

        Response signInResponse = RequestBuilder.unauthorized().body(signIn)
            .post(TestConfig.AUTH_SIGN_IN);

        if (signInResponse.getStatusCode() == 200) {
            return ResponseExtractor.extractToken(signInResponse);
        }

        // If sign in failed (e.g., user not found), register and then sign in
        AuthSignUpRequest signUp = AuthSignUpRequest.builder()
            .username(TestConfig.ADMIN_USERNAME)
            .password(TestConfig.ADMIN_PASSWORD)
            .firstName(TestDataGenerator.generateFirstName())
            .lastName(TestDataGenerator.generateLastName())
            .telegramChatId(TestDataGenerator.generateTelegramChatId())
            .build();

        RequestBuilder.unauthorized().body(signUp)
            .post(TestConfig.AUTH_REGISTER);

        Response secondSignIn = RequestBuilder.unauthorized().body(signIn)
            .post(TestConfig.AUTH_SIGN_IN);

        return ResponseExtractor.extractToken(secondSignIn);
    }
}
