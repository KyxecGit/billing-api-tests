package auc;

/**
 * Централизованная конфигурация для всех автотестов API.
 * <p>
 * Содержит базовые URL, учётные данные администратора и константы эндпоинтов.
 * Все пути к API должны быть определены здесь для удобства поддержки.
 * </p>
 */
public class TestConfig {
    
    /**
     * Базовый URL API-сервера для всех тестовых запросов.
     */
    public static final String BASE_URL = "http://195.38.164.168:7173";
    
    /**
     * Имя пользователя администратора для тестов.
     */
    public static final String ADMIN_USERNAME = "superuser";
    
    /**
     * Пароль администратора для тестов.
     */
    public static final String ADMIN_PASSWORD = "Admin123!@#";
    
    // === Authentication Endpoints ===
    public static final String AUTH_REGISTER = "/api/auth/sign_up";
    public static final String AUTH_SIGN_IN = "/api/auth/sign_in";
    
    // === Balance Endpoints ===
    public static final String BALANCE_GET_BY_ID = "/api/balance/{id}";
    public static final String BALANCE_GET_ALL = "/api/balance/all";
    public static final String BALANCE_UPDATE = "/api/balance/update/{id}";
    
    // === Profile Endpoints ===
    public static final String PROFILE_CREATE = "/api/admin/profile/create";
    public static final String PROFILE_UPDATE = "/api/admin/profile/update/{id}";
    public static final String PROFILE_GET_BY_ID = "/api/admin/profile/{id}";
    public static final String PROFILE_GET_BY_MSISDN = "/api/admin/profile/getByMsisdn/{msisdn}";
    public static final String PROFILE_GET_ALL = "/api/admin/profile/all";
    public static final String PROFILE_GET_ALL_REMOVED = "/api/admin/profile/all-removed";
    public static final String PROFILE_DELETE = "/api/admin/profile/delete/{id}";
    
    // === Counter Endpoints ===
    public static final String COUNTER_GET_BY_ID = "/api/admin/counter/{id}";
    public static final String COUNTER_GET_ALL = "/api/admin/counter/all";
    public static final String COUNTER_GET_ALL_ACTIVE = "/api/admin/counter/all-active";
}
