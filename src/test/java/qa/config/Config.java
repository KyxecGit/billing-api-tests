package qa.config;

/**
 * Настройки для подключения к API.
 * Здесь хранятся все важные параметры для тестов.
 */
public class Config {
    
    // ========== API СЕРВЕР ==========
    
    /** Адрес API сервера */
    public static final String BASE_URL = "http://195.38.164.168:7173";
    
    // ========== АВТОРИЗАЦИЯ ==========
    
    /** Имя администратора */
    public static final String ADMIN_USERNAME = "superuser";
    
    /** Пароль администратора */
    public static final String ADMIN_PASSWORD = "Admin123!@#";
    
    // ========== ТЕСТОВЫЕ ДАННЫЕ ==========
    
    /** ID тестового тарифного плана */
    public static final int TEST_PRICE_PLAN = 2;
    
    /** ID тестового пользователя */
    public static final int TEST_USER_ID = 1;
    
    /** Префикс для номеров телефонов */
    public static final String PHONE_PREFIX = "99680";
}
