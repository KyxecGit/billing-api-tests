package auc;

/**
 * <b>TestConfig - Централизованное хранилище конфигурационных констант</b>
 * <p>
 * Этот класс служит единственным источником истины (Single Source of Truth) для всех
 * конфигурационных данных, используемых в тестах. Это позволяет:
 * </p>
 * <ul>
 *   <li><b>Упрощать поддержку:</b> Изменение URL или учётных данных требует изменения в одном месте</li>
 *   <li><b>Избежать дублирования:</b> Все эндпоинты определены в одном классе</li>
 *   <li><b>Избежать опечаток:</b> Использование констант вместо строк найдёт ошибки на этапе компиляции</li>
 *   <li><b>Самодокументировать код:</b> Названия констант сами объясняют их назначение</li>
 * </ul>
 * <p>
 * <b>Структура:</b> Содержит три группы констант:
 * <ol>
 *   <li><b>Базовые настройки:</b> BASE_URL, учётные данные администратора</li>
 *   <li><b>Группы эндпоинтов по функциям:</b> Auth, Balance, Profile, Counter</li>
 *   <li><b>Пути к конкретным операциям:</b> GET, POST, PUT, DELETE для каждого ресурса</li>
 * </ol>
 * </p>
 * <p>
 * <b>Пример использования:</b>
 * <pre>
 * // Вместо hardcode-а строки:
 * .get("/api/balance/{id}");
 * 
 * // Используем:
 * .get(TestConfig.BALANCE_GET_BY_ID.replace("{id}", "123"));
 * </pre>
 * </p>
 */
public class TestConfig {
    
    // ========================= БАЗОВЫЕ НАСТРОЙКИ =========================
    
    /**
     * <b>Базовый URL API-сервера</b>
     * <p>
     * Используется REST Assured для установки базового пути всех запросов.
     * Инициализируется в методе BaseApiTest.globalSetup().
     * </p>
     * <p>
     * <b>Текущий сервер:</b> http://195.38.164.168:7173
     * Это тестовый сервер (песочница) для обучения и автотестирования.
     * </p>
     * <p>
     * <b>Для изменения окружения:</b>
     * <pre>
     * // Development
     * public static final String BASE_URL = "http://localhost:8080";
     * 
     * // Staging
     * public static final String BASE_URL = "http://staging.example.com:7173";
     * 
     * // Production
     * public static final String BASE_URL = "http://api.example.com";
     * </pre>
     * </p>
     */
    public static final String BASE_URL = "http://195.38.164.168:7173";
    
    /**
     * <b>Имя пользователя администратора</b>
     * <p>
     * Используется для аутентификации в методе BaseApiTest.getAdminToken().
     * Этот пользователь должен иметь роль ROLE_ADMIN для доступа ко всем защищённым эндпоинтам.
     * </p>
     * <p>
     * <b>Требования к паролю администратора:</b>
     * <ul>
     *   <li>Минимум 8 символов</li>
     *   <li>Содержит прописные буквы (A-Z)</li>
     *   <li>Содержит строчные буквы (a-z)</li>
     *   <li>Содержит цифры (0-9)</li>
     *   <li>Содержит специальные символы (!@#$%)</li>
     * </ul>
     * </p>
     */
    public static final String ADMIN_USERNAME = "superuser";
    
    /**
     * <b>Пароль администратора</b>
     * <p>
     * Соответствует политике безопасности API и используется вместе с ADMIN_USERNAME
     * для получения JWT токена через эндпоинт /api/auth/sign_in.
     * </p>
     * <p>
     * <b>Важно:</b> В production окружении этот пароль должен быть переменной окружения,
     * а не жёстко закодирован в исходном коде!
     * </p>
     * <p>
     * <b>Пример с переменной окружения:</b>
     * <pre>
     * public static final String ADMIN_PASSWORD = System.getenv("ADMIN_PASSWORD");
     * </pre>
     * </p>
     */
    public static final String ADMIN_PASSWORD = "Admin123!@#";
    
    // ========================= AUTHENTICATION ENDPOINTS =========================
    
    /**
     * <b>Регистрация нового пользователя</b>
     * <p>
     * HTTP Method: POST
     * Content-Type: application/json
     * Authentication: Не требуется (публичный эндпоинт)
     * </p>
     * <p>
     * <b>Структура запроса:</b>
     * <pre>
     * {
     *   "username": "unique_username",
     *   "password": "SecurePass123!",
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "telegramChatId": "123456789"
     * }
     * </pre>
     * </p>
     * <p>
     * <b>Возможные статус-коды:</b>
     * <ul>
     *   <li>200 OK - успешная регистрация</li>
     *   <li>201 Created - пользователь создан (возможно в зависимости от реализации)</li>
     *   <li>400 Bad Request - некорректные данные или дублирующийся username</li>
     *   <li>409 Conflict - пользователь с таким username уже существует</li>
     * </ul>
     * </p>
     */
    public static final String AUTH_REGISTER = "/api/auth/sign_up";
    
    /**
     * <b>Авторизация пользователя и получение JWT токена</b>
     * <p>
     * HTTP Method: POST
     * Content-Type: application/json
     * Authentication: Не требуется (публичный эндпоинт)
     * </p>
     * <p>
     * <b>Структура запроса:</b>
     * <pre>
     * {
     *   "username": "user_username",
     *   "password": "user_password"
     * }
     * </pre>
     * </p>
     * <p>
     * <b>Структура успешного ответа (200 OK):</b>
     * <pre>
     * {
     *   "code": "OK",
     *   "message": "Sign in successful",
     *   "content": {
     *     "id": 1,
     *     "username": "user_username",
     *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     *   }
     * }
     * </pre>
     * </p>
     * <p>
     * <b>Возможные статус-коды:</b>
     * <ul>
     *   <li>200 OK - успешная авторизация, токен получен</li>
     *   <li>400 Bad Request - отсутствуют username или password</li>
     *   <li>401 Unauthorized - неверные учётные данные</li>
     * </ul>
     * </p>
     */
    public static final String AUTH_SIGN_IN = "/api/auth/sign_in";
    
    // ========================= BALANCE ENDPOINTS =========================
    
    /**
     * <b>Получение баланса пользователя по ID</b>
     * <p>
     * HTTP Method: GET
     * Content-Type: application/json
     * Authentication: Required (Bearer Token)
     * Authorization: Только администраторы (ROLE_ADMIN)
     * </p>
     * <p>
     * <b>Параметры:</b>
     * <ul>
     *   <li>{id} - Long, ID баланса (path parameter)</li>
     * </ul>
     * </p>
     * <p>
     * <b>Пример URL:</b> /api/balance/123
     * </p>
     * <p>
     * <b>Возвращаемые данные:</b>
     * <pre>
     * {
     *   "code": "OK",
     *   "content": {
     *     "id": 123,
     *     "amount": 1500.75,
     *     "currency": "TJS",
     *     "userId": 45,
     *     "updatedAt": "2025-10-27T10:30:00"
     *   }
     * }
     * </pre>
     * </p>
     */
    public static final String BALANCE_GET_BY_ID = "/api/balance/{id}";
    
    /**
     * <b>Получение всех балансов в системе</b>
     * <p>
     * HTTP Method: GET
     * Content-Type: application/json
     * Authentication: Required (Bearer Token)
     * Authorization: Только администраторы (ROLE_ADMIN)
     * </p>
     * <p>
     * <b>Возвращаемые данные (list):</b>
     * <pre>
     * {
     *   "code": "OK",
     *   "content": [
     *     {
     *       "id": 1,
     *       "amount": 500.00,
     *       "userId": 10
     *     },
     *     {
     *       "id": 2,
     *       "amount": 1000.50,
     *       "userId": 11
     *     }
     *   ]
     * }
     * </pre>
     * </p>
     */
    public static final String BALANCE_GET_ALL = "/api/balance/all";
    
    /**
     * <b>Обновление баланса пользователя</b>
     * <p>
     * HTTP Method: PUT
     * Content-Type: application/json
     * Authentication: Required (Bearer Token)
     * Authorization: Только администраторы (ROLE_ADMIN)
     * </p>
     * <p>
     * <b>Параметры:</b>
     * <ul>
     *   <li>{id} - Long, ID баланса (path parameter)</li>
     * </ul>
     * </p>
     * <p>
     * <b>Структура запроса:</b>
     * <pre>
     * {
     *   "amount": 2500.75
     * }
     * </pre>
     * </p>
     * <p>
     * <b>⚠️ ИЗВЕСТНЫЙ БАГ:</b> Эндпоинт возвращает 400 Bad Request при передаче requestBody
     * как указано в OpenAPI спецификации. API не принимает JSON body для этого запроса.
     * Тест: BalanceApiTest.testUpdateBalance_AsPerSpecification()
     * </p>
     */
    public static final String BALANCE_UPDATE = "/api/balance/update/{id}";
    
    // ========================= PROFILE ENDPOINTS =========================
    
    /**
     * <b>Создание нового профиля абонента</b>
     * <p>
     * HTTP Method: POST
     * Content-Type: application/json
     * Authentication: Required (Bearer Token)
     * Authorization: Только администраторы (ROLE_ADMIN)
     * </p>
     * <p>
     * <b>Структура запроса:</b>
     * <pre>
     * {
     *   "msisdn": "996800123456",      // pattern: ^99680\d{7}$ (обязательно!)
     *   "userId": 1,                   // ID пользователя
     *   "pricePlanId": 1              // ID тарифного плана
     * }
     * </pre>
     * </p>
     * <p>
     * <b>Валидация MSISDN:</b> Номер телефона должен соответствовать паттерну:
     * <ul>
     *   <li>Начинаться с префикса: 99680</li>
     *   <li>Содержать ровно 7 цифр после префикса</li>
     *   <li>Общая длина: ровно 12 символов</li>
     *   <li>Пример валидных номеров: 996800000001, 996809999999, 996805555555</li>
     * </ul>
     * </p>
     * <p>
     * <b>⚠️ ИЗВЕСТНЫЙ БАГ:</b> API возвращает 201 Created вместо 200 OK
     * как указано в OpenAPI спецификации.
     * Тест: ProfileApiTest.testCreateProfile_Success()
     * </p>
     */
    public static final String PROFILE_CREATE = "/api/admin/profile/create";
    
    /**
     * <b>Обновление существующего профиля</b>
     * <p>
     * HTTP Method: PUT
     * Content-Type: application/json
     * Authentication: Required (Bearer Token)
     * Authorization: Только администраторы (ROLE_ADMIN)
     * </p>
     * <p>
     * <b>Параметры:</b>
     * <ul>
     *   <li>{id} - Long, ID профиля (path parameter)</li>
     * </ul>
     * </p>
     * <p>
     * <b>Структура запроса:</b>
     * <pre>
     * {
     *   "msisdn": "996801234567",     // Новый номер телефона (pattern: ^99680\d{7}$)
     *   "userId": 1,                  // ID пользователя
     *   "pricePlanId": 2             // Новый ID тарифного плана
     * }
     * </pre>
     * </p>
     * <p>
     * <b>Возвращаемые данные:</b>
     * <pre>
     * {
     *   "code": "OK",
     *   "content": {
     *     "id": 5,
     *     "msisdn": "996801234567",
     *     "userId": 1,
     *     "pricePlanId": 2,
     *     "status": "ACTIVE",
     *     "createdAt": "2025-10-25T12:00:00",
     *     "updatedAt": "2025-10-27T14:30:00"
     *   }
     * }
     * </pre>
     * </p>
     */
    public static final String PROFILE_UPDATE = "/api/admin/profile/update/{id}";
    
    /**
     * <b>Получение профиля абонента по ID</b>
     * <p>
     * HTTP Method: GET
     * Content-Type: application/json
     * Authentication: Required (Bearer Token)
     * Authorization: Только администраторы (ROLE_ADMIN)
     * </p>
     * <p>
     * <b>Параметры:</b>
     * <ul>
     *   <li>{id} - Long, ID профиля (path parameter)</li>
     * </ul>
     * </p>
     * <p>
     * <b>Пример URL:</b> /api/admin/profile/5
     * </p>
     */
    public static final String PROFILE_GET_BY_ID = "/api/admin/profile/{id}";
    
    /**
     * <b>Получение профиля по номеру телефона (MSISDN)</b>
     * <p>
     * HTTP Method: GET
     * Content-Type: application/json
     * Authentication: Required (Bearer Token)
     * Authorization: Только администраторы (ROLE_ADMIN)
     * </p>
     * <p>
     * <b>Параметры:</b>
     * <ul>
     *   <li>{msisdn} - String, Номер телефона в формате 99680XXXXXXX (path parameter)</li>
     * </ul>
     * </p>
     * <p>
     * <b>Пример URL:</b> /api/admin/profile/getByMsisdn/996800123456
     * </p>
     */
    public static final String PROFILE_GET_BY_MSISDN = "/api/admin/profile/getByMsisdn/{msisdn}";
    
    /**
     * <b>Получение всех профилей абонентов</b>
     * <p>
     * HTTP Method: GET
     * Content-Type: application/json
     * Authentication: Required (Bearer Token)
     * Authorization: Только администраторы (ROLE_ADMIN)
     * </p>
     * <p>
     * <b>Возвращаемые данные (list всех активных профилей)</b>
     * </p>
     */
    public static final String PROFILE_GET_ALL = "/api/admin/profile/all";
    
    /**
     * <b>Получение всех удалённых профилей</b>
     * <p>
     * HTTP Method: GET
     * Content-Type: application/json
     * Authentication: Required (Bearer Token)
     * Authorization: Только администраторы (ROLE_ADMIN)
     * </p>
     * <p>
     * Возвращает список профилей, которые были пометки как удалённые (soft delete).
     * Фактически данные остаются в БД, но помечаются как неактивные.
     * </p>
     */
    public static final String PROFILE_GET_ALL_REMOVED = "/api/admin/profile/all-removed";
    
    /**
     * <b>Удаление профиля абонента</b>
     * <p>
     * HTTP Method: DELETE
     * Content-Type: application/json
     * Authentication: Required (Bearer Token)
     * Authorization: Только администраторы (ROLE_ADMIN)
     * </p>
     * <p>
     * <b>Параметры:</b>
     * <ul>
     *   <li>{id} - Long, ID профиля (path parameter)</li>
     * </ul>
     * </p>
     * <p>
     * <b>⚠️ ИЗВЕСТНЫЙ БАГ:</b> API возвращает 204 No Content вместо 200 OK
     * как указано в OpenAPI спецификации.
     * Тест: ProfileApiTest.testDeleteProfile_StatusCode()
     * </p>
     */
    public static final String PROFILE_DELETE = "/api/admin/profile/delete/{id}";
    
    // ========================= COUNTER ENDPOINTS =========================
    
    /**
     * <b>Получение счётчика трафика по ID</b>
     * <p>
     * HTTP Method: GET
     * Content-Type: application/json
     * Authentication: Required (Bearer Token)
     * Authorization: Только администраторы (ROLE_ADMIN)
     * </p>
     * <p>
     * <b>Параметры:</b>
     * <ul>
     *   <li>{id} - Long, ID счётчика (path parameter)</li>
     * </ul>
     * </p>
     * <p>
     * <b>Счётчик содержит:</b> Информацию о используемом трафике (SMS, секунды, Мб)
     * </p>
     */
    public static final String COUNTER_GET_BY_ID = "/api/admin/counter/{id}";
    
    /**
     * <b>Получение всех счётчиков</b>
     * <p>
     * HTTP Method: GET
     * Content-Type: application/json
     * Authentication: Required (Bearer Token)
     * Authorization: Только администраторы (ROLE_ADMIN)
     * </p>
     * <p>
     * Возвращает список всех счётчиков (включая неактивные)
     * </p>
     */
    public static final String COUNTER_GET_ALL = "/api/admin/counter/all";
    
    /**
     * <b>Получение всех активных счётчиков</b>
     * <p>
     * HTTP Method: GET
     * Content-Type: application/json
     * Authentication: Required (Bearer Token)
     * Authorization: Только администраторы (ROLE_ADMIN)
     * </p>
     * <p>
     * Возвращает только счётчики в статусе ACTIVE (активные)
     * </p>
     */
    public static final String COUNTER_GET_ALL_ACTIVE = "/api/admin/counter/all-active";
}
