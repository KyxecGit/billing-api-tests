package auc.utils;

import io.restassured.response.Response;
import org.testng.Assert;

public class ApiAssertions {

    public static void assertOkResponse(Response response) {
        assertStatus(response, 200);
        String body = safeBody(response);
        Assert.assertEquals(response.jsonPath().getString("code"), "OK", "code != OK. Body: " + body);
        Assert.assertNotNull(response.jsonPath().get("content"), "content is null. Body: " + body);
    }

    public static void assertForbidden(Response response) {
        assertStatus(response, 403);
    }

    public static void assertNotFound(Response response) {
        assertStatus(response, 404);
    }

    public static void assertBadRequest(Response response) {
        assertStatus(response, 400);
    }

    private static void assertStatus(Response response, int expected) {
        Assert.assertEquals(response.getStatusCode(), expected, "Unexpected status. Body: " + safeBody(response));
    }

    private static String safeBody(Response response) {
        try {
            String s = response.asString();
            return s == null ? "<null>" : (s.length() > 1000 ? s.substring(0, 1000) + "..." : s);
        } catch (Exception e) {
            return "<unavailable: " + e.getMessage() + ">";
        }
    }
}
