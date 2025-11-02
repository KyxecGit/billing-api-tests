package auc.utils;

import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class RequestBuilder {

    public static RequestSpecification authorized(String token) {
        return given().contentType(JSON).header("Authorization", "Bearer " + token);
    }

    public static RequestSpecification unauthorized() {
        return given().contentType(JSON);
    }
}
