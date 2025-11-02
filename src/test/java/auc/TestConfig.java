package auc;

public class TestConfig {

    public static final String BASE_URL = System.getProperty("baseUrl", "http://195.38.164.168:7173");

    public static final String ADMIN_USERNAME = System.getProperty("adminUsername", "superuser");
    public static final String ADMIN_PASSWORD = System.getProperty("adminPassword", "Admin123!@#");

    public static final String AUTH_REGISTER = "/api/auth/sign_up";
    public static final String AUTH_SIGN_IN = "/api/auth/sign_in";

    public static final String BALANCE_GET_BY_ID = "/api/balance/{id}";
    public static final String BALANCE_GET_ALL = "/api/balance/all";
    public static final String BALANCE_UPDATE = "/api/balance/update/{id}";

    public static final String PROFILE_CREATE = "/api/admin/profile/create";
    public static final String PROFILE_UPDATE = "/api/admin/profile/update/{id}";
    public static final String PROFILE_GET_BY_ID = "/api/admin/profile/{id}";
    public static final String PROFILE_GET_BY_MSISDN = "/api/admin/profile/getByMsisdn/{msisdn}";
    public static final String PROFILE_GET_ALL = "/api/admin/profile/all";
    public static final String PROFILE_GET_ALL_REMOVED = "/api/admin/profile/all-removed";
    public static final String PROFILE_DELETE = "/api/admin/profile/delete/{id}";
    public static final String PROFILE_DELETE_ALL = "/api/admin/profile/delete/all";

    public static final String COUNTER_GET_BY_ID = "/api/admin/counter/{id}";
    public static final String COUNTER_GET_ALL = "/api/admin/counter/all";
    public static final String COUNTER_GET_ALL_ACTIVE = "/api/admin/counter/all-active";
}
