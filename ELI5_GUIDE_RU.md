# Объяснение на пальцах (ELI5): что делает каждый файл и каждая строка

Цель: объяснить, как будто вы вообще не из ИТ. Представьте большой супермаркет.

- API — это супермаркет с дверями (эндпоинты) и охраной (авторизация).
- Наши тесты — проверяющие: приходят, показывают паспорт (токен), совершают действие (запрос), смотрят чек (ответ).
- Если чек странный — значит проблема у магазина, не у проверяющих.

Сначала дадим словарик, потом пошагово разберём каждый файл, вставим реальные куски кода и расшифруем каждую строчку простыми словами. В конце — частые ошибки и как их чинить.

---

## Мини-словарик для «не айтишников»

- Эндпоинт — конкретная дверь в магазин (URL), например «/api/admin/profile/create».
- Метод HTTP — тип действия у двери: GET (посмотреть), POST (создать), PUT (изменить), DELETE (удалить).
- JSON — форма-заявка/чек в текстовом виде. Пример: `{ "msisdn": "996801234567" }`.
- Токен — пропуск от охраны. Без него многие двери не откроются.
- Статус-код — ответ кассы/охраны на попытку: 200 (всё ок), 400 (кривая заявка), 403 (без пропуска), 404 (дверь/товар не найден), 500 (внутренняя ошибка магазина).

---

## Как запустить проверки и что «за кадром» происходит

1) Настраиваем адрес магазина и логин/пароль админа в `TestConfig.java` (по умолчанию уже заполнено).
2) Запускаем все проверки:

```powershell
mvn -q test
```

3) Maven найдёт тесты и запустит их. Сначала выполнится общая подготовка (получим токен), затем — конкретные проверки.
4) Результаты появятся в папке `target/surefire-reports` (там как «протокол проверки»).

Дополнительно: можно переопределить адрес и креды из командной строки, без правки файлов:

```powershell
mvn -q -DbaseUrl=http://127.0.0.1:8080 -DadminUsername=admin -DadminPassword=pass test
```

---

## Файл 1: `TestConfig.java` — «таблички на входе»

Показываем полный код и разбираем каждую строку.

```java
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
```

Разбор простыми словами:
- `BASE_URL` — адрес магазина. Можно переопределить через консольный параметр `-DbaseUrl=...`. Если не указали — возьмём значение по умолчанию.
- `ADMIN_USERNAME`, `ADMIN_PASSWORD` — логин/пароль админа. Тоже можно передать параметрами `-DadminUsername=... -DadminPassword=...`.
- Остальные строки — это таблички с названиями дверей (URL-шаблоны). `{id}` и `{msisdn}` — дырки, в которые мы подставим числа/строки когда будет надо.

Полезно: Подстановку делает метод `url()` в `BaseApiTest` (разберём ниже).

---

## Файл 2: `BaseApiTest.java` — «общая подготовка и вспомогалки»

Полный код:

```java
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
```

Разбор по пунктам:
- `protected static String adminToken;` — «пропуск админа», хранится один раз и доступен всем тестам.
- `@BeforeClass globalSetup()` — делаем перед запуском тестов: включаем «навигатор» на `BASE_URL` и получаем токен.
- `url(template, params...)` — берёт шаблон с дырками `{}` и подставляет параметры по очереди. Пример: `url("/api/x/{id}/y/{msisdn}", 5, "99680...")`.
- `getAdminToken()` — сначала пробуем войти (логин-пароль). Если получилось (код 200) — вытаскиваем токен и всё. Если нет (например, пользователь ещё не создан) — регистрируем, потом снова логинимся, берём токен.

На что обратить внимание:
- Если сервер недоступен — тут же упадём: дальше смысла нет, без токена многие проверки нельзя выполнить.
- Если поменяли логин/пароль — укажите новые через параметры запуска `-DadminUsername -DadminPassword`.

---

## Файл 3: `utils/RequestBuilder.java` — «как зайти в магазин»

```java
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
```

Просто и ясно:
- `authorized(token)` — начинаем собирать запрос, сразу говорим «мы общаемся JSON-ом» и показываем паспорт: `Authorization: Bearer ...`.
- `unauthorized()` — тоже самое, только без паспорта (для тестов, где и должен быть отказ).

---

## Файл 4: `utils/ApiAssertions.java` — «как выглядит правильный чек»

```java
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
```

Человеческим языком:
- `assertOkResponse` — чек должен быть со статусом 200, полем `code == "OK"` и с непустым разделом `content`.
- Остальные `assert*` — быстрые проверки для типовых ошибок: нет доступа (403), не найдено (404), кривая заявка (400).
- `safeBody` — аккуратно достаёт тело ответа для сообщения об ошибке, чтобы легче было понять, что пошло не так.

---

## Файл 5: `utils/ResponseExtractor.java` — «как вытащить нужное из чека»

```java
package auc.utils;

import io.restassured.response.Response;
import java.util.List;

public class ResponseExtractor {

    public static <T> T extractContent(Response response, Class<T> type) {
        return response.jsonPath().getObject("content", type);
    }

    public static <T> List<T> extractContentList(Response response, Class<T> type) {
        return response.jsonPath().getList("content", type);
    }

    public static Long extractId(Response response) {
        return response.jsonPath().getLong("content.id");
    }

    public static String extractToken(Response response) {
        return response.jsonPath().getString("content.token");
    }
}
```

Пояснения:
- `extractContent` — «дай мне весь раздел content как объект такого-то вида».
- `extractContentList` — «дай список объектов из content».
- `extractId`, `extractToken` — быстрые геттеры по популярным полям.

---

## Файл 6: `utils/TestDataGenerator.java` — «как придумываем правдоподобные данные»

```java
package auc.utils;

import net.datafaker.Faker;

public class TestDataGenerator {

    private static final Faker faker = new Faker();

    public static String generateMsisdn() {
        return "99680" + faker.number().digits(7);
    }

    public static String generateFirstName() {
        return faker.name().firstName();
    }

    public static String generateLastName() {
        return faker.name().lastName();
    }

    public static String generateTelegramChatId() {
        return String.valueOf(faker.number().numberBetween(100000000, 999999999));
    }

    public static Double generateBalanceAmount() {
        return faker.number().randomDouble(2, 100, 5000);
    }
}
```

Логика:
- Префикс `99680` + 7 цифр — выглядит как реальный номер, и почти всегда уникален.
- Имена/фамилии — рандомные, чтобы не плодить дубликаты.
- Суммы — в адекватном диапазоне (100…5000), с 2 знаками после запятой.

---

## Файл 7: DTO-заявки (request) — «как мы заполняем форму»

Покажем одну — `ProfileCreateRequest.java` (остальные по аналогии):

```java
package auc.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfileCreateRequest {
    @JsonProperty("msisdn")
    private String msisdn;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("pricePlanId")
    private Long pricePlanId;

    public static Builder builder() {
        return new Builder();
    }

    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getPricePlanId() { return pricePlanId; }
    public void setPricePlanId(Long pricePlanId) { this.pricePlanId = pricePlanId; }

    public static class Builder {
        private String msisdn;
        private Long userId;
        private Long pricePlanId;

        public Builder msisdn(String msisdn) { 
            this.msisdn = msisdn; 
            return this; 
        }

        public Builder userId(Long userId) { 
            this.userId = userId; 
            return this; 
        }

        public Builder pricePlanId(Long pricePlanId) { 
            this.pricePlanId = pricePlanId; 
            return this; 
        }

        public ProfileCreateRequest build() {
            ProfileCreateRequest request = new ProfileCreateRequest();
            request.msisdn = this.msisdn;
            request.userId = this.userId;
            request.pricePlanId = this.pricePlanId;
            return request;
        }
    }
}
```

Что к чему:
- `@JsonProperty("msisdn")` — как поле называется в JSON-чеке. Благодаря этому, при отправке/чтении поле правильно сериализуется.
- Паттерн Builder — удобный способ «пошагово» заполнить форму: `.msisdn(...).userId(...).pricePlanId(...).build()`.

---

## Файл 8: DTO-ответы (response) — «как прочитать чек как объект»

Пример: `ProfileDto.java` (другие по смыслу такие же):

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDto {
    @JsonProperty("id")
    private Long id;
    ...
}
```

Смысл:
- Поля размечены `@JsonProperty`, чтобы при чтении JSON мы легко получали Java-объект с нужными данными.
- `@JsonIgnoreProperties(ignoreUnknown = true)` — если в JSON прилетит ещё куча полей, которые нам не нужны — игнорируем, не ругаемся.

---

## Файл 9: Пример с реальным тестом — `tests/ProfileApiTest.java`

Покажем ключевые части и разжуем их.

```java
@BeforeClass
public void setup() {
    testMsisdn = TestDataGenerator.generateMsisdn();
    testUserId = 1L;
    testPricePlanId = 1L;

    ProfileCreateRequest body = ProfileCreateRequest.builder()
        .msisdn(testMsisdn)
        .userId(testUserId)
        .pricePlanId(testPricePlanId)
        .build();

    Response createResponse = RequestBuilder.authorized(adminToken).body(body)
        .post(TestConfig.PROFILE_CREATE);

    ApiAssertions.assertOkResponse(createResponse);
    testProfileId = ResponseExtractor.extractId(createResponse);
}
```

Простыми словами:
- Перед всеми тестами в этом файле мы создаём один тестовый профиль. Сохраняем его `id` — будем использовать дальше.
- Если создание профиля не прошло (не 200 / нет `OK` / пустой `content`) — завалим тест сразу, дальше нет смысла идти.

Ещё один пример — сам тест создания, но уже с очисткой за собой:

```java
@Test(priority = 1, description = "POST /api/admin/profile/create - успешное создание")
public void testCreateProfile_Success() {
    ProfileCreateRequest body = ProfileCreateRequest.builder()
        .msisdn(TestDataGenerator.generateMsisdn())
        .userId(testUserId)
        .pricePlanId(testPricePlanId)
        .build();

    Response response = RequestBuilder.authorized(adminToken).body(body)
        .post(TestConfig.PROFILE_CREATE);
    
    ApiAssertions.assertOkResponse(response);
    
    Long createdId = ResponseExtractor.extractId(response);
    RequestBuilder.authorized(adminToken)
        .delete(url(TestConfig.PROFILE_DELETE, createdId));
}
```

По шагам:
1) Собрали валидную форму.
2) Зашли с паспортом и отправили на дверь «создать профиль».
3) Убедились, что чек нормальный (200, OK, есть content).
4) Достали `id` из чека.
5) Удалили созданное — чтобы после проверки магазин не засорялся тестовыми данными.

Аналогично устроены тесты на чтение, обновление, удаление и проверки «без паспорта → 403».

---

## Как читать ошибки и что делать

Типовые случаи и «перевод на человеческий»:

- 403 Forbidden — «охрана не пустила». Причины: нет/протух токен, неправильные креды. Действия: проверьте `ADMIN_USERNAME/ADMIN_PASSWORD`, доступность API.
- 404 Not Found — «дверь/товар не найден». Причины: передали несуществующий `id` или URL. В тестах это ожидаемо в отдельных сценариях.
- 400 Bad Request — «кривая заявка». Причины: забыли обязательное поле, неверный формат. Пример — обновление баланса без `amount`.
- 500 Internal Server Error — «сломалось внутри магазина». Это точно проблема API. Приложите тело ответа из сообщения об ошибке.
- 201/204 вместо 200 — «магазин выдаёт неожиданный тип чека». Это несоответствие спецификации: тест падает специально, чтобы API-команда поправила поведение.

Где смотреть детали: отчёты `target/surefire-reports` и сообщения ассертов (мы специально подсовываем кусок тела ответа в текст ошибки).

---

## Частые вопросы новичков

- Что такое Maven? — «Менеджер запусков и зависимостей». Он скачает нужные библиотеки и запустит тесты.
- Что такое TestNG? — «Движок тестов». Он понимает `@Test`, `@BeforeClass`, задаёт порядок, считает результаты.
- Что такое REST Assured? — «Инструмент общаться с дверями магазина по HTTP». Делает запросы, читает ответы, парсит JSON.
- А если у меня другой адрес/логин? — Передайте их флагами `-DbaseUrl=... -DadminUsername=... -DadminPassword=...` при запуске.
- Где итог? — В отчётах Maven и в консоли. Зелёные — хорошо, красные — несоответствие спецификации/ошибки API.

---

## Шпаргалка команд (PowerShell, Windows)

- Все тесты:
```powershell
mvn -q test
```

- Один набор (например, только профили):
```powershell
mvn -q -Dtest=ProfileApiTest test
```

- С другим адресом и кредами:
```powershell
mvn -q -DbaseUrl=http://localhost:7173 -DadminUsername=admin -DadminPassword=pass test
```

---

## Главное, что нужно запомнить

1) Перед тестами — настраиваем адрес и берём токен (автоматически в `BaseApiTest`).
2) Каждый тест — это: подготовил данные → отправил запрос → проверил чек → при необходимости подчистил.
3) Если статус/структура ответа не такие, как в спецификации — это не проблема теста, это сигнал, что API нужно поправить.

Если нужна ещё более детальная «пошаговая раскраска» конкретного теста (строка за строкой) — скажите какой именно (из `tests/`), и я распишу построчно каждую переменную и вызов.
