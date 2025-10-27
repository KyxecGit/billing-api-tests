package auc;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

/**
 * <b>BaseApiTest - Базовый абстрактный класс для всех автотестов API</b>
 * <p>
 * Этот класс является корневым для всех тестовых классов проекта и предоставляет:
 * <ul>
 *   <li><b>Централизованную конфигурацию REST Assured</b> - установка базового URL</li>
 *   <li><b>Управление аутентификацией администратора</b> - получение и сохранение JWT токена</li>
 *   <li><b>Единую точку входа</b> для инициализации тестового окружения</li>
 * </ul>
 * </p>
 * <p>
 * <b>Архитектурная роль:</b> Как фундамент архитектуры, этот класс обеспечивает:
 * <ul>
 *   <li>Однократное выполнение @BeforeAll методов перед каждым тестовым классом</li>
 *   <li>Наследование adminToken во все подклассы (AuthApiTest, BalanceApiTest и т.д.)</li>
 *   <li>Предотвращение дублирования кода инициализации в каждом тестовом классе</li>
 * </ul>
 * </p>
 * <p>
 * <b>Пример использования:</b>
 * <pre>
 * public class MyApiTest extends BaseApiTest {
 *     @Test
 *     public void testSomething() {
 *         // adminToken уже инициализирован и доступен
 *         RequestBuilder.authorizedGet(adminToken)
 *             .when().get("/api/endpoint");
 *     }
 * }
 * </pre>
 * </p>
 */
public abstract class BaseApiTest {
    
    /**
     * <b>JWT токен администратора для авторизованных запросов</b>
     * <p>
     * Инициализируется один раз в методе globalSetup() перед запуском всех тестов класса.
     * Используется всеми наследниками этого класса для авторизации на защищённые эндпоинты.
     * </p>
     * <p>
     * <b>Формат:</b> JWT токен (Bearer token) вида:
     * {@code eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkFkbWluIn0...}
     * </p>
     * <p>
     * <b>Область видимости:</b> protected static - доступен всем наследникам пакета
     * </p>
     */
    protected static String adminToken;
    
    /**
     * <b>Глобальная инициализация тестового окружения перед всеми тестами</b>
     * <p>
     * Выполняется аннотацией @BeforeAll <b>один раз</b> перед запуском всех тестовых методов
     * в каждом классе, наследующемся от BaseApiTest. Выполняет следующие шаги:
     * </p>
     * <ol>
     *   <li>Устанавливает базовый URL для REST Assured из конфигурации (TestConfig.BASE_URL)</li>
     *   <li>Вызывает getAdminToken() для получения JWT токена администратора</li>
     *   <li>Сохраняет токен в переменную класса adminToken для использования в тестах</li>
     *   <li>Выводит сообщение об успешной инициализации в консоль</li>
     * </ol>
     * <p>
     * <b>Последовательность выполнения в тестовом классе:</b>
     * <pre>
     * 1. BaseApiTest.globalSetup() @BeforeAll (инициализация базовой инфраструктуры)
     * 2. XxxApiTest.setup()         @BeforeAll (подготовка специфичных для теста данных)
     * 3. @Test методы              (выполнение тестов)
     * </pre>
     * </p>
     * @see TestConfig#BASE_URL базовый URL из конфигурации
     * @see #getAdminToken() метод получения JWT токена
     */
    @BeforeAll
    public static void globalSetup() {
        // Устанавливаем базовый URL для всех запросов REST Assured
        RestAssured.baseURI = TestConfig.BASE_URL;
        
        // Получаем и сохраняем JWT токен администратора
        adminToken = getAdminToken();
        
        // Информационный вывод в консоль для отслеживания инициализации
        System.out.println("✓ Получен JWT токен администратора");
    }
    
    /**
     * <b>Получает JWT токен администратора через эндпоинты аутентификации</b>
     * <p>
     * Этот приватный метод выполняет следующий процесс аутентификации:
     * </p>
     * <ol>
     *   <li><b>Регистрация администратора:</b> POST запрос на /api/auth/sign_up
     *       <ul>
     *         <li>Использует учётные данные из TestConfig.ADMIN_USERNAME и TestConfig.ADMIN_PASSWORD</li>
     *         <li>Ошибка, если админ уже существует, игнорируется (не прерывает процесс)</li>
     *       </ul>
     *   </li>
     *   <li><b>Авторизация администратора:</b> POST запрос на /api/auth/sign_in
     *       <ul>
     *         <li>Отправляет username и password администратора</li>
     *         <li>API возвращает ответ с JWT токеном в поле content.token</li>
     *       </ul>
     *   </li>
     *   <li><b>Извлечение токена:</b> Используется JsonPath для получения значения token из ответа</li>
     * </ol>
     * <p>
     * <b>Структура ответа API (200 OK):</b>
     * <pre>
     * {
     *   "code": "OK",
     *   "message": "Sign in successful",
     *   "content": {
     *     "id": 1,
     *     "username": "superuser",
     *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     *   }
     * }
     * </pre>
     * </p>
     * <p>
     * <b>Процесс регистрации:</b>
     * <pre>
     * JSON body = {
     *   "username": "superuser",
     *   "password": "Admin123!@#",
     *   "firstName": "Admin",
     *   "lastName": "User",
     *   "telegramChatId": "123456789"
     * }
     * </pre>
     * </p>
     * <p>
     * <b>Обработка ошибок:</b> Если администратор уже существует в системе, ошибка игнорируется
     * и процесс продолжается к авторизации. Это обеспечивает идемпотентность метода.
     * </p>
     * <p>
     * <b>Возвращаемое значение:</b> JWT токен в формате Bearer token, который используется
     * для авторизации всех последующих запросов. Токен передаётся в заголовке:
     * {@code Authorization: Bearer <token>}
     * </p>
     *
     * @return JWT токен администратора для использования в авторизованных запросах API
     * @see TestConfig#ADMIN_USERNAME имя пользователя администратора
     * @see TestConfig#ADMIN_PASSWORD пароль администратора
     * @see RequestBuilder#authorizedGet(String) использование токена в запросах
     */
    private static String getAdminToken() {
        // Шаг 1: Попытка регистрации админа (если он уже существует, API вернёт ошибку, которую мы игнорируем)
        given()
            .contentType(ContentType.JSON)
            .body(String.format("{\"username\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\",\"telegramChatId\":\"%s\"}",
                TestConfig.ADMIN_USERNAME, TestConfig.ADMIN_PASSWORD, "Admin", "User", "123456789"))
            .post("/api/auth/sign_up");
        
        // Шаг 2: Авторизация админа и получение JWT токена
        Response response = given()
            .contentType(ContentType.JSON)
            .body(String.format("{\"username\":\"%s\",\"password\":\"%s\"}", 
                TestConfig.ADMIN_USERNAME, TestConfig.ADMIN_PASSWORD))
            .post("/api/auth/sign_in");
        
        // Шаг 3: Извлечение токена из поля content.token ответа и возврат
        return response.jsonPath().getString("content.token");
    }
}
