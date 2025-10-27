package auc;

import auc.utils.ApiAssertions;
import auc.utils.RequestBuilder;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * <b>AuthApiTest - Тесты для эндпоинтов аутентификации и авторизации пользователей</b>
 * <p>
 * Этот класс содержит набор тестов для проверки функциональности регистрации и авторизации.
 * Включает как позитивные сценарии (успешные операции), так и негативные (обработка ошибок).
 * </p>
 * <p>
 * <b>Покрытые эндпоинты:</b>
 * <ul>
 *   <li><b>POST /api/auth/sign_up</b> - регистрация нового пользователя (публичный эндпоинт)</li>
 *   <li><b>POST /api/auth/sign_in</b> - авторизация пользователя и получение JWT (публичный эндпоинт)</li>
 * </ul>
 * </p>
 * <p>
 * <b>Количество тестов:</b> 4 теста
 * <ul>
 *   <li>2 теста для sign_up (успех + дубликат)</li>
 *   <li>2 теста для sign_in (успех + отсутствие полей)</li>
 * </ul>
 * </p>
 * <p>
 * <b>Особенности:</b>
 * <ul>
 *   <li>Использует System.currentTimeMillis() для генерации уникальных username</li>
 *   <li>Не требует предварительных данных в БД (работает с любым состоянием)</li>
 *   <li>Все тесты независимы и могут выполняться в любом порядке</li>
 * </ul>
 * </p>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthApiTest extends BaseApiTest {
    
    /**
     * <b>Тест 1: Успешная регистрация нового пользователя</b>
     * <p>
     * <b>Сценарий:</b> Пользователь отправляет корректные данные для регистрации.
     * </p>
     * <p>
     * <b>Шаги:</b>
     * <ol>
     *   <li>Генерируем уникальный username, используя текущее время в миллисекундах</li>
     *   <li>Формируем Map с данными: username, password, firstName, lastName, telegramChatId</li>
     *   <li>Отправляем POST запрос на /api/auth/sign_up без авторизации (публичный эндпоинт)</li>
     *   <li>Проверяем, что статус-код 200 или 201 (API может вернуть любой из них)</li>
     * </ol>
     * </p>
     * <p>
     * <b>Ожидаемый результат:</b> Пользователь успешно зарегистрирован, получен статус 200/201
     * </p>
     * <p>
     * <b>Требования безопасности пароля:</b>
     * <ul>
     *   <li>Минимум 8 символов</li>
     *   <li>Содержит прописные буквы (Password123!)</li>
     *   <li>Содержит строчные буквы</li>
     *   <li>Содержит цифры</li>
     *   <li>Содержит специальные символы</li>
     * </ul>
     * </p>
     * <p>
     * <b>Требования к другим полям:</b>
     * <ul>
     *   <li>username - уникален в системе (генерируем с timestamp для гарантии уникальности)</li>
     *   <li>firstName, lastName - строки (описание: имя, фамилия пользователя)</li>
     *   <li>telegramChatId - ID чата в Telegram для уведомлений (опционально, но передаём)</li>
     * </ul>
     * </p>
     * <p>
     * <b>Почему используем System.currentTimeMillis():</b>
     * <pre>
     * String uniqueUsername = "user_" + System.currentTimeMillis();
     * // Результат: user_1735489234567
     * // Гарантирует уникальность за счёт использования текущего времени в миллисекундах
     * // Во время разработки - тесты можно запускать тысячи раз без конфликтов
     * </pre>
     * </p>
     *
     * @see RequestBuilder#unauthorizedRequest(Object) отправка POST без авторизации
     */
    @Test
    @Order(1)
    @DisplayName("POST /api/auth/sign_up - успешная регистрация")
    public void testSignUp_Success() {
        // Генерируем уникальный username используя текущее время
        String uniqueUsername = "user_" + System.currentTimeMillis();
        
        // Подготавливаем данные для регистрации (Arrange)
        Map<String, Object> signUpData = new HashMap<>();
        signUpData.put("username", uniqueUsername);
        signUpData.put("password", "Password123!");
        signUpData.put("firstName", "Test");
        signUpData.put("lastName", "User");
        signUpData.put("telegramChatId", "123456789");
        
        // Отправляем запрос на регистрацию (Act)
        // Используем unauthorizedRequest так как эндпоинт публичный
        RequestBuilder.unauthorizedRequest(signUpData)
            .when()
            .post(TestConfig.AUTH_REGISTER)
            .then()
            .statusCode(anyOf(is(200), is(201))); // API может вернуть оба варианта
    }
    
    /**
     * <b>Тест 2: Попытка регистрации с дублирующимся username</b>
     * <p>
     * <b>Сценарий:</b> Два пользователя пытаются зарегистрироваться с одинаковым username.
     * </p>
     * <p>
     * <b>Шаги:</b>
     * <ol>
     *   <li>Генерируем уникальный username</li>
     *   <li>Регистрируем первого пользователя - должна вернуться 200/201</li>
     *   <li>Пытаемся зарегистрировать второго с тем же username</li>
     *   <li>Проверяем, что API вернёт 400 Bad Request или 409 Conflict</li>
     * </ol>
     * </p>
     * <p>
     * <b>Ожидаемый результат:</b> Вторая попытка завершается ошибкой 400/409
     * </p>
     * <p>
     * <b>Проверяемое требование:</b> Уникальность username в системе
     * </p>
     * <p>
     * <b>Статус-коды:</b>
     * <ul>
     *   <li>400 Bad Request - общая ошибка валидации (username уже используется)</li>
     *   <li>409 Conflict - более специфичный код для конфликта уникальности</li>
     * </ul>
     * </p>
     */
    @Test
    @Order(2)
    @DisplayName("POST /api/auth/sign_up - дублирующийся username")
    public void testSignUp_DuplicateUsername() {
        // Генерируем уникальный username
        String duplicateUsername = "duplicate_user_" + System.currentTimeMillis();
        
        // Подготавливаем данные
        Map<String, Object> signUpData = new HashMap<>();
        signUpData.put("username", duplicateUsername);
        signUpData.put("password", "Password123!");
        signUpData.put("firstName", "Test");
        signUpData.put("lastName", "User");
        signUpData.put("telegramChatId", "123456789");
        
        // Первая регистрация - должна успеть (Arrange)
        RequestBuilder.unauthorizedRequest(signUpData)
            .when()
            .post(TestConfig.AUTH_REGISTER);
        
        // Вторая регистрация с тем же username - должна вернуть ошибку (Act & Assert)
        RequestBuilder.unauthorizedRequest(signUpData)
            .when()
            .post(TestConfig.AUTH_REGISTER)
            .then()
            .statusCode(anyOf(is(400), is(409))); // Конфликт дублирования
    }
    
    /**
     * <b>Тест 3: Успешная авторизация и получение JWT токена</b>
     * <p>
     * <b>Сценарий:</b> Пользователь авторизуется с корректными credentials и получает JWT токен.
     * </p>
     * <p>
     * <b>Шаги:</b>
     * <ol>
     *   <li>Генерируем уникальные username и password</li>
     *   <li>Регистрируем пользователя (создаём аккаунт)</li>
     *   <li>Авторизуемся с этими же credentials через /api/auth/sign_in</li>
     *   <li>Проверяем статус 200 OK и структуру ответа (валидность JWT)</li>
     * </ol>
     * </p>
     * <p>
     * <b>Ожидаемый результат:</b>
     * <ul>
     *   <li>Статус-код 200 OK</li>
     *   <li>Ответ содержит поле code="OK"</li>
     *   <li>В content находится JWT токен</li>
     * </ul>
     * </p>
     * <p>
     * <b>Структура успешного ответа:</b>
     * <pre>
     * {
     *   "code": "OK",
     *   "message": "Sign in successful",
     *   "content": {
     *     "id": 42,
     *     "username": "signin_test_1735489234567",
     *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI0MiIs..."
     *   }
     * }
     * </pre>
     * </p>
     * <p>
     * <b>Использование JWT токена в дальнейших запросах:</b>
     * <pre>
     * String token = response.jsonPath().getString("content.token");
     * RequestBuilder.authorizedGet(token)  // Используем token для авторизованных запросов
     *     .when().get("/api/admin/profile/all");
     * </pre>
     * </p>
     *
     * @see ApiAssertions#assertOkResponse(Response) стандартная проверка 200 OK ответа
     */
    @Test
    @Order(3)
    @DisplayName("POST /api/auth/sign_in - успешная авторизация (получение JWT)")
    public void testSignIn_Success() {
        // Генерируем уникальные credentials
        String username = "signin_test_" + System.currentTimeMillis();
        String password = "TestPassword123!";
        
        // Подготавливаем данные для регистрации
        Map<String, Object> signUpData = new HashMap<>();
        signUpData.put("username", username);
        signUpData.put("password", password);
        signUpData.put("firstName", "SignIn");
        signUpData.put("lastName", "Test");
        signUpData.put("telegramChatId", "123456789");
        
        // Регистрируем пользователя (Arrange)
        RequestBuilder.unauthorizedRequest(signUpData)
            .when()
            .post(TestConfig.AUTH_REGISTER);
        
        // Подготавливаем данные для авторизации
        Map<String, Object> signInData = new HashMap<>();
        signInData.put("username", username);
        signInData.put("password", password);
        
        // Авторизуемся и получаем JWT токен (Act)
        Response response = RequestBuilder.unauthorizedRequest(signInData)
            .when()
            .post(TestConfig.AUTH_SIGN_IN);
        
        // Проверяем ответ (Assert)
        ApiAssertions.assertOkResponse(response); // Проверяем: 200 OK, code="OK", content не null
        
        // Дополнительно можем проверить наличие токена
        response.then().body("content.token", notNullValue());
    }
    
    /**
     * <b>Тест 4: Попытка регистрации с отсутствующими обязательными полями</b>
     * <p>
     * <b>Сценарий:</b> Пользователь отправляет неполные данные для регистрации.
     * </p>
     * <p>
     * <b>Шаги:</b>
     * <ol>
     *   <li>Формируем Map только с username (остальные поля пропускаем)</li>
     *   <li>Отправляем запрос на регистрацию</li>
     *   <li>Проверяем, что API вернёт 400 Bad Request</li>
     * </ol>
     * </p>
     * <p>
     * <b>Ожидаемый результат:</b> Статус-код 400 Bad Request
     * </p>
     * <p>
     * <b>Обязательные поля (согласно OpenAPI спецификации):</b>
     * <ul>
     *   <li>username (required)</li>
     *   <li>password (required)</li>
     *   <li>firstName (required)</li>
     *   <li>lastName (required)</li>
     *   <li>telegramChatId (required)</li>
     * </ul>
     * </p>
     * <p>
     * <b>Проверяемое требование:</b> Валидация входных данных и обязательных полей
     * </p>
     */
    @Test
    @Order(4)
    @DisplayName("POST /api/auth/sign_up - отсутствуют обязательные поля")
    public void testSignUp_MissingRequiredFields() {
        // Создаём Map с отсутствующими полями (только username, остальное пропускаем)
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("username", "test");
        // password, firstName, lastName, telegramChatId - НЕ заполняем
        
        // Отправляем некорректный запрос (Act)
        RequestBuilder.unauthorizedRequest(invalidData)
            .when()
            .post(TestConfig.AUTH_REGISTER)
            .then()
            .statusCode(400); // Ожидаем 400 Bad Request из-за отсутствия обязательных полей
    }
}
