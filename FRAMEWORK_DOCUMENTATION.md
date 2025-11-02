# Billing API Test Framework - Technical Documentation

Полная техническая документация фреймворка для автоматизированного тестирования Billing API.

## 📚 Содержание

1. [Полный воркфлоу теста](#полный-воркфлоу-теста)
2. [Архитектура фреймворка](#архитектура-фреймворка)
3. [Базовые классы](#базовые-классы)
4. [Утилиты](#утилиты)
5. [DTOs](#dtos)
6. [Тесты](#тесты)
7. [Тест-кейсы](#тест-кейсы)
8. [Баг-репорты](#баг-репорты)
9. [Best Practices](#best-practices)

---

## Полный воркфлоу теста

### Пример: testCreateProfile_Success

Разберём пошагово, как выполняется тест создания профиля и какие файлы участвуют в процессе.

#### 🎬 Шаг 0: Подготовка (Before Class)

**Файл:** `ProfileApiTest.java`

```java
public class ProfileApiTest extends BaseApiTest {
    
    @BeforeClass
    public void setup() {
        // Вызывается BaseApiTest.globalSetup()
    }
}
```

**Что происходит:**

1. **BaseApiTest.globalSetup()** ← `BaseApiTest.java`
   ```java
   @BeforeClass
   public void globalSetup() {
       RestAssured.baseURI = TestConfig.BASE_URL; // ← TestConfig.java
       adminToken = getAdminToken(); // ← BaseApiTest.java
   }
   ```

2. **getAdminToken()** ← `BaseApiTest.java`
   - Создаёт `AuthSignInRequest` ← `dto/request/AuthSignInRequest.java`
   - Вызывает `RequestBuilder.unauthorized()` ← `utils/RequestBuilder.java`
   - Отправляет POST на `TestConfig.AUTH_SIGN_IN` ← `TestConfig.java`
   - Если 401, создаёт `AuthSignUpRequest` ← `dto/request/AuthSignUpRequest.java`
   - Генерирует данные через `TestDataGenerator` ← `utils/TestDataGenerator.java`
   - Извлекает токен через `ResponseExtractor.extractToken()` ← `utils/ResponseExtractor.java`

**Файлы задействованы:**
- ✅ `BaseApiTest.java`
- ✅ `TestConfig.java`
- ✅ `RequestBuilder.java`
- ✅ `AuthSignInRequest.java`
- ✅ `AuthSignUpRequest.java`
- ✅ `TestDataGenerator.java`
- ✅ `ResponseExtractor.java`

#### 🎬 Шаг 1: Запуск теста

**Файл:** `ProfileApiTest.java`

```java
@Test(priority = 1, description = "POST /api/admin/profile/create - успешное создание профиля")
public void testCreateProfile_Success() {
    // Начало теста
```

**Что происходит:**

TestNG видит аннотацию `@Test` и запускает метод.

**Файлы задействованы:**
- ✅ `ProfileApiTest.java`
- ✅ `pom.xml` (зависимость TestNG 7.10.2)

#### 🎬 Шаг 2: Генерация тестовых данных

```java
String msisdn = TestDataGenerator.generateMsisdn();
```

**Файл:** `utils/TestDataGenerator.java`

```java
public class TestDataGenerator {
    private static final Faker faker = new Faker();
    
    public static String generateMsisdn() {
        return "99680" + faker.number().digits(7);
        // Возвращает: "996801234567"
    }
}
```

**Что происходит:**

1. `Faker` из библиотеки Datafaker генерирует случайные 7 цифр
2. Добавляется префикс "99680"
3. Результат: уникальный MSISDN `"996801234567"`

**Файлы задействованы:**
- ✅ `TestDataGenerator.java`
- ✅ `pom.xml` (зависимость Datafaker 2.4.2)

#### 🎬 Шаг 3: Построение Request DTO

```java
ProfileCreateRequest body = ProfileCreateRequest.builder()
    .msisdn(msisdn)
    .userId(1L)
    .pricePlanId(1L)
    .build();
```

**Файл:** `dto/request/ProfileCreateRequest.java`

```java
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
    
    public static class Builder {
        private final ProfileCreateRequest request = new ProfileCreateRequest();
        
        public Builder msisdn(String msisdn) {
            request.msisdn = msisdn;
            return this;
        }
        
        public Builder userId(Long userId) {
            request.userId = userId;
            return this;
        }
        
        public Builder pricePlanId(Long pricePlanId) {
            request.pricePlanId = pricePlanId;
            return this;
        }
        
        public ProfileCreateRequest build() {
            return request;
        }
    }
}
```

**Что происходит:**

1. Builder создаёт новый объект `ProfileCreateRequest`
2. Заполняет поля через цепочку вызовов
3. Возвращает готовый объект

**Результат:** Объект ready для сериализации в JSON

**Файлы задействованы:**
- ✅ `ProfileCreateRequest.java`

#### 🎬 Шаг 4: Построение HTTP запроса

```java
Response response = RequestBuilder.authorized(adminToken)
    .body(body)
    .post(TestConfig.PROFILE_CREATE);
```

**Файл:** `utils/RequestBuilder.java`

```java
public class RequestBuilder {
    public static RequestSpecification authorized(String token) {
        return given()
            .contentType(JSON)
            .header("Authorization", "Bearer " + token);
    }
}
```

**Что происходит:**

1. **RequestBuilder.authorized()** создаёт REST Assured `RequestSpecification`
2. Устанавливает `Content-Type: application/json`
3. Добавляет header `Authorization: Bearer eyJhbGc...`
4. `.body(body)` ← Jackson сериализует `ProfileCreateRequest` в JSON
5. `.post()` получает URL из `TestConfig.PROFILE_CREATE`

**Файл:** `TestConfig.java`

```java
public static final String PROFILE_CREATE = "/api/admin/profile/create";
```

**REST Assured формирует запрос:**

```http
POST http://195.38.164.168:7173/api/admin/profile/create
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "msisdn": "996801234567",
  "userId": 1,
  "pricePlanId": 1
}
```

**Файлы задействованы:**
- ✅ `RequestBuilder.java`
- ✅ `TestConfig.java`
- ✅ `ProfileCreateRequest.java` (сериализация)
- ✅ `pom.xml` (REST Assured 5.5.0, Jackson 2.18.2)

#### 🎬 Шаг 5: Отправка запроса и получение ответа

**Что происходит:**

1. REST Assured отправляет HTTP POST запрос на сервер
2. Сервер обрабатывает запрос
3. Возвращается HTTP ответ:

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "code": "OK",
  "content": {
    "id": 42,
    "msisdn": "996801234567",
    "userId": 1,
    "pricePlanId": 1,
    "status": "ACTIVE",
    "createdAt": "2025-11-02T10:30:00",
    "updatedAt": "2025-11-02T10:30:00"
  }
}
```

4. REST Assured парсит ответ в объект `Response`

**Файлы задействованы:**
- ✅ `pom.xml` (REST Assured HTTP клиент)

#### 🎬 Шаг 6: Проверка статус-кода

```java
ApiAssertions.assertOkResponse(response);
```

**Файл:** `utils/ApiAssertions.java`

```java
public class ApiAssertions {
    public static void assertOkResponse(Response response) {
        assertStatus(response, 200); // ← Ожидаем 200
        
        String body = safeBody(response);
        
        Assert.assertEquals(
            response.jsonPath().getString("code"), 
            "OK", 
            "code != OK. Body: " + body
        );
        
        Assert.assertNotNull(
            response.jsonPath().get("content"), 
            "content is null. Body: " + body
        );
    }
    
    private static void assertStatus(Response response, int expected) {
        Assert.assertEquals(
            response.getStatusCode(), 
            expected, 
            "Unexpected status. Body: " + safeBody(response)
        );
    }
}
```

**Что происходит:**

1. **assertStatus(200)** проверяет код ответа
   - Ожидается: 200
   - Реально: 201 ❌
   - **ТЕСТ ПАДАЕТ** → найден БАГ API!

2. TestNG выбрасывает `AssertionError`:
   ```
   java.lang.AssertionError: Unexpected status. Expected: 200, Actual: 201
   ```

**Файлы задействованы:**
- ✅ `ApiAssertions.java`
- ✅ `pom.xml` (TestNG assertions)

#### 🎬 Шаг 7: Извлечение данных (если тест прошёл бы)

```java
Long createdId = ResponseExtractor.extractId(response);
```

**Файл:** `utils/ResponseExtractor.java`

```java
public class ResponseExtractor {
    public static Long extractId(Response response) {
        return response.jsonPath().getLong("content.id");
        // Вернёт: 42L
    }
}
```

**Что происходит:**

1. JsonPath парсит JSON ответа
2. Извлекает поле `content.id`
3. Конвертирует в `Long`

**Файлы задействованы:**
- ✅ `ResponseExtractor.java`
- ✅ `pom.xml` (REST Assured JsonPath)

#### 🎬 Шаг 8: Cleanup (если тест прошёл бы)

```java
RequestBuilder.authorized(adminToken)
    .delete(url(TestConfig.PROFILE_DELETE, createdId));
```

**Файл:** `BaseApiTest.java`

```java
protected String url(String template, Object... params) {
    String result = template;
    for (Object param : params) {
        result = result.replaceFirst("\\{[^}]+}", String.valueOf(param));
    }
    return result;
    // "/api/admin/profile/delete/{id}" → "/api/admin/profile/delete/42"
}
```

**Что происходит:**

1. `url()` подставляет ID в шаблон URL
2. `RequestBuilder.authorized()` создаёт DELETE запрос
3. Отправляется DELETE для очистки тестовых данных

**Файлы задействованы:**
- ✅ `BaseApiTest.java`
- ✅ `TestConfig.java`
- ✅ `RequestBuilder.java`

---

### 📊 Визуализация полного воркфлоу

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. ПОДГОТОВКА (@BeforeClass)                                    │
└─────────────────────────────────────────────────────────────────┘
        │
        ├─→ ProfileApiTest.setup()
        │   └─→ BaseApiTest.globalSetup()
        │       ├─→ TestConfig.BASE_URL ────────────────┐
        │       └─→ getAdminToken()                     │
        │           ├─→ AuthSignInRequest.builder() ────┤
        │           ├─→ RequestBuilder.unauthorized() ──┤
        │           ├─→ TestConfig.AUTH_SIGN_IN ────────┤
        │           ├─→ ResponseExtractor.extractToken() │
        │           │   (если 401):                      │
        │           ├─→ AuthSignUpRequest.builder() ────┤
        │           ├─→ TestDataGenerator.* ────────────┤
        │           └─→ POST /api/auth/sign_up ─────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────────┐
│ 2. ЗАПУСК ТЕСТА (@Test)                                         │
└─────────────────────────────────────────────────────────────────┘
        │
        ├─→ testCreateProfile_Success()
        │
        ▼
┌─────────────────────────────────────────────────────────────────┐
│ 3. ГЕНЕРАЦИЯ ДАННЫХ                                             │
└─────────────────────────────────────────────────────────────────┘
        │
        ├─→ TestDataGenerator.generateMsisdn()
        │   └─→ Faker.number().digits(7) ───────────────┐
        │       └─→ return "99680" + "1234567" ─────────┤
        │                                                │
        ▼                                                │
┌─────────────────────────────────────────────────────────────────┐
│ 4. ПОСТРОЕНИЕ REQUEST DTO                                       │
└─────────────────────────────────────────────────────────────────┘
        │
        ├─→ ProfileCreateRequest.builder()
        │   ├─→ .msisdn("996801234567") ────────────────┤
        │   ├─→ .userId(1L) ────────────────────────────┤
        │   ├─→ .pricePlanId(1L) ───────────────────────┤
        │   └─→ .build() ───────────────────────────────┤
        │                                                │
        ▼                                                │
┌─────────────────────────────────────────────────────────────────┐
│ 5. ПОСТРОЕНИЕ HTTP ЗАПРОСА                                      │
└─────────────────────────────────────────────────────────────────┘
        │
        ├─→ RequestBuilder.authorized(adminToken)
        │   ├─→ given() ────────────────────────────────┤ REST
        │   ├─→ .contentType(JSON) ─────────────────────┤ Assured
        │   ├─→ .header("Authorization", "Bearer ...") ─┤ 5.5.0
        │   └─→ .body(body) ────────────────────────────┤
        │       └─→ Jackson сериализует DTO в JSON ─────┤ Jackson
        │                                                │ 2.18.2
        ├─→ .post(TestConfig.PROFILE_CREATE)            │
        │   └─→ URL: "/api/admin/profile/create" ───────┤
        │                                                │
        ▼                                                │
┌─────────────────────────────────────────────────────────────────┐
│ 6. ОТПРАВКА НА СЕРВЕР                                           │
└─────────────────────────────────────────────────────────────────┘
        │
        │   HTTP POST →  http://195.38.164.168:7173     │
        │                /api/admin/profile/create       │
        │                                                │
        │   Headers:                                     │
        │   - Content-Type: application/json             │
        │   - Authorization: Bearer eyJhbG...            │
        │                                                │
        │   Body:                                        │
        │   {                                            │
        │     "msisdn": "996801234567",                  │
        │     "userId": 1,                               │
        │     "pricePlanId": 1                           │
        │   }                                            │
        │                                                │
        │   ← HTTP 201 Created                           │
        │   {                                            │
        │     "code": "OK",                              │
        │     "content": {                               │
        │       "id": 42,                                │
        │       "msisdn": "996801234567",                │
        │       "userId": 1,                             │
        │       "pricePlanId": 1,                        │
        │       "status": "ACTIVE",                      │
        │       "createdAt": "2025-11-02T10:30:00",      │
        │       "updatedAt": "2025-11-02T10:30:00"       │
        │     }                                          │
        │   }                                            │
        │                                                │
        ▼                                                │
┌─────────────────────────────────────────────────────────────────┐
│ 7. ВАЛИДАЦИЯ ОТВЕТА                                             │
└─────────────────────────────────────────────────────────────────┘
        │
        ├─→ ApiAssertions.assertOkResponse(response)
        │   ├─→ assertStatus(response, 200) ────────────┤
        │   │   ├─→ actual: 201 ❌                       │ TestNG
        │   │   └─→ expected: 200 (по спеке)            │ 7.10.2
        │   │       └─→ AssertionError! ─────────────────┤
        │   │           БАГ API НАЙДЕН! 🐛               │
        │   │                                            │
        │   ├─→ assertEquals("code", "OK") ──────────────┤
        │   └─→ assertNotNull("content") ────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────────┐
│ 8. ИЗВЛЕЧЕНИЕ ДАННЫХ (если бы прошёл)                          │
└─────────────────────────────────────────────────────────────────┘
        │
        ├─→ ResponseExtractor.extractId(response)
        │   └─→ jsonPath().getLong("content.id")
        │       └─→ return 42L
        │
        ▼
┌─────────────────────────────────────────────────────────────────┐
│ 9. CLEANUP (если бы прошёл)                                     │
└─────────────────────────────────────────────────────────────────┘
        │
        ├─→ url(TestConfig.PROFILE_DELETE, createdId)
        │   └─→ "/api/admin/profile/delete/42"
        │
        └─→ RequestBuilder.authorized(adminToken)
            └─→ .delete(url)
                └─→ DELETE /api/admin/profile/delete/42
```

---

### 📦 Полная карта зависимостей файлов

```
testCreateProfile_Success()
│
├── ProfileApiTest.java ──────────────────────┐ (тест)
│   └── extends BaseApiTest.java              │
│                                              │
├── BaseApiTest.java ─────────────────────────┤ (базовый класс)
│   ├── globalSetup()                         │
│   ├── getAdminToken()                       │
│   └── url()                                 │
│                                              │
├── TestConfig.java ──────────────────────────┤ (конфигурация)
│   ├── BASE_URL                              │
│   ├── ADMIN_USERNAME                        │
│   ├── ADMIN_PASSWORD                        │
│   ├── AUTH_SIGN_IN                          │
│   ├── AUTH_REGISTER                         │
│   ├── PROFILE_CREATE                        │
│   └── PROFILE_DELETE                        │
│                                              │
├── utils/RequestBuilder.java ────────────────┤ (HTTP запросы)
│   ├── authorized(token)                     │
│   └── unauthorized()                        │
│                                              │
├── utils/ApiAssertions.java ─────────────────┤ (проверки)
│   ├── assertOkResponse()                    │
│   ├── assertStatus()                        │
│   └── safeBody()                            │
│                                              │
├── utils/ResponseExtractor.java ─────────────┤ (извлечение данных)
│   ├── extractId()                           │
│   ├── extractToken()                        │
│   ├── extractContent()                      │
│   └── extractContentList()                  │
│                                              │
├── utils/TestDataGenerator.java ─────────────┤ (генерация данных)
│   ├── generateMsisdn()                      │
│   ├── generateFirstName()                   │
│   ├── generateLastName()                    │
│   └── generateTelegramChatId()              │
│                                              │
├── dto/request/ProfileCreateRequest.java ────┤ (request DTO)
│   ├── @JsonProperty fields                  │
│   ├── getters/setters                       │
│   └── Builder pattern                       │
│                                              │
├── dto/request/AuthSignInRequest.java ───────┤ (auth request)
│   └── Builder pattern                       │
│                                              │
├── dto/request/AuthSignUpRequest.java ───────┤ (register request)
│   └── Builder pattern                       │
│                                              │
├── dto/response/ProfileDto.java ─────────────┤ (response DTO)
│   ├── @JsonProperty fields                  │
│   └── getters/setters                       │
│                                              │
└── pom.xml ──────────────────────────────────┘ (зависимости)
    ├── TestNG 7.10.2
    ├── REST Assured 5.5.0
    ├── Jackson 2.18.2
    ├── jackson-datatype-jsr310 2.18.2
    └── Datafaker 2.4.2
```

---

### 🔄 Последовательность вызовов (Call Stack)

```
1. TestNG запускает тест
   └─→ ProfileApiTest.testCreateProfile_Success()

2. Генерация MSISDN
   └─→ TestDataGenerator.generateMsisdn()
       └─→ Faker.number().digits(7)

3. Построение Request DTO
   └─→ ProfileCreateRequest.builder()
       └─→ .msisdn().userId().pricePlanId().build()

4. Построение HTTP запроса
   └─→ RequestBuilder.authorized(adminToken)
       └─→ given().contentType(JSON).header("Authorization", ...)

5. Добавление body
   └─→ .body(ProfileCreateRequest)
       └─→ Jackson.serialize(ProfileCreateRequest → JSON)

6. Отправка запроса
   └─→ .post(TestConfig.PROFILE_CREATE)
       └─→ REST Assured HTTP POST
           └─→ Сервер возвращает Response (201 Created)

7. Проверка ответа
   └─→ ApiAssertions.assertOkResponse(response)
       └─→ assertStatus(response, 200)
           └─→ TestNG Assert.assertEquals(201, 200)
               └─→ ❌ AssertionError: expected 200, got 201
                   └─→ ТЕСТ ПАДАЕТ = БАГ НАЙДЕН! 🐛

8. (Не выполняется из-за падения)
   └─→ ResponseExtractor.extractId(response)

9. (Не выполняется из-за падения)
   └─→ DELETE cleanup
```

---

### 💡 Ключевые моменты

1. **Один тест = 14 файлов**
   - 3 базовых класса
   - 4 утилиты
   - 4 DTOs
   - 1 конфигурация
   - 1 тест
   - 1 pom.xml

2. **Каждый файл имеет одну ответственность**
   - TestConfig → только константы
   - RequestBuilder → только HTTP запросы
   - ApiAssertions → только проверки
   - DTOs → только данные

3. **Падение теста = найденный баг**
   - Тест ожидает 200 (по спецификации)
   - API возвращает 201 (реальность)
   - Несоответствие = баг в API

4. **Переиспользование кода**
   - adminToken получается один раз в @BeforeClass
   - Утилиты используются всеми тестами
   - DTOs общие для всех запросов/ответов

---

## Архитектура фреймворка

### Принципы построения

1. **Spec-First Approach** - все тесты строго по OpenAPI спецификации
2. **Fail on API Bugs** - падающий тест = найденный баг в API
3. **Flat Structure** - минимум вложенности, плоская структура пакетов
4. **Reusable Utilities** - переиспользуемые компоненты вместо дублирования
5. **Clean Code** - простой и понятный код без over-engineering

### Диаграмма зависимостей

```
api.json (OpenAPI Spec)
    ↓
TestConfig → BaseApiTest → {Balance|Counter|Profile}ApiTest
                ↓                           ↓
            Utils (RequestBuilder,      Request/Response DTOs
            ApiAssertions, etc.)
```

---

## Базовые классы

### TestConfig

**Назначение:** Централизованная конфигурация всех URL и credentials.

**Расположение:** `src/test/java/auc/TestConfig.java`

**Поля:**

```java
// Базовый URL API (можно переопределить через -DbaseUrl)
public static final String BASE_URL = "http://195.38.164.168:7173";

// Credentials администратора (можно переопределить через параметры)
public static final String ADMIN_USERNAME = "superuser";
public static final String ADMIN_PASSWORD = "Admin123!@#";

// Auth endpoints
public static final String AUTH_REGISTER = "/api/auth/sign_up";
public static final String AUTH_SIGN_IN = "/api/auth/sign_in";

// Balance endpoints
public static final String BALANCE_GET_BY_ID = "/api/balance/{id}";
public static final String BALANCE_GET_ALL = "/api/balance/all";
public static final String BALANCE_UPDATE = "/api/balance/update/{id}";

// Profile endpoints
public static final String PROFILE_CREATE = "/api/admin/profile/create";
public static final String PROFILE_UPDATE = "/api/admin/profile/update/{id}";
public static final String PROFILE_GET_BY_ID = "/api/admin/profile/{id}";
public static final String PROFILE_GET_ALL = "/api/admin/profile/all";
public static final String PROFILE_GET_ALL_REMOVED = "/api/admin/profile/all-removed";
public static final String PROFILE_DELETE = "/api/admin/profile/delete/{id}";

// Counter endpoints
public static final String COUNTER_GET_BY_ID = "/api/admin/counter/{id}";
public static final String COUNTER_GET_ALL = "/api/admin/counter/all";
public static final String COUNTER_GET_ALL_ACTIVE = "/api/admin/counter/all-active";
```

**Использование:**

```java
// В тестах
Response response = RequestBuilder.authorized(adminToken)
    .get(TestConfig.BALANCE_GET_ALL);

// С параметрами
String url = url(TestConfig.BALANCE_GET_BY_ID, balanceId);
```

### BaseApiTest

**Назначение:** Базовый класс для всех тестов с общей логикой.

**Расположение:** `src/test/java/auc/BaseApiTest.java`

**Поля:**

```java
protected static String adminToken; // JWT токен администратора
```

**Методы:**

```java
// Выполняется ПЕРЕД всеми тестами класса
@BeforeClass
public void globalSetup() {
    RestAssured.baseURI = TestConfig.BASE_URL;
    adminToken = getAdminToken();
}

// Вспомогательный метод для подстановки параметров в URL
protected String url(String template, Object... params) {
    String result = template;
    for (Object param : params) {
        result = result.replaceFirst("\\{[^}]+}", String.valueOf(param));
    }
    return result;
}

// Получение токена админа (регистрация при необходимости)
private String getAdminToken() {
    // 1. Пытаемся авторизоваться
    AuthSignInRequest signIn = AuthSignInRequest.builder()
        .username(TestConfig.ADMIN_USERNAME)
        .password(TestConfig.ADMIN_PASSWORD)
        .build();

    Response signInResponse = RequestBuilder.unauthorized().body(signIn)
        .post(TestConfig.AUTH_SIGN_IN);

    if (signInResponse.getStatusCode() == 200) {
        return ResponseExtractor.extractToken(signInResponse);
    }

    // 2. Если юзера нет - регистрируем
    AuthSignUpRequest signUp = AuthSignUpRequest.builder()
        .username(TestConfig.ADMIN_USERNAME)
        .password(TestConfig.ADMIN_PASSWORD)
        .firstName(TestDataGenerator.generateFirstName())
        .lastName(TestDataGenerator.generateLastName())
        .telegramChatId(TestDataGenerator.generateTelegramChatId())
        .build();

    RequestBuilder.unauthorized().body(signUp)
        .post(TestConfig.AUTH_REGISTER);

    // 3. Авторизуемся повторно
    Response secondSignIn = RequestBuilder.unauthorized().body(signIn)
        .post(TestConfig.AUTH_SIGN_IN);

    return ResponseExtractor.extractToken(secondSignIn);
}
```

**Использование:**

```java
public class BalanceApiTest extends BaseApiTest {
    
    @Test
    public void testGetBalance() {
        // adminToken уже доступен
        Response response = RequestBuilder.authorized(adminToken)
            .get(url(TestConfig.BALANCE_GET_BY_ID, 1));
    }
}
```

---

## Утилиты

### RequestBuilder

**Назначение:** Построение HTTP запросов с авторизацией.

**Расположение:** `src/test/java/auc/utils/RequestBuilder.java`

**Методы:**

```java
// Запрос с Bearer токеном
public static RequestSpecification authorized(String token) {
    return given()
        .contentType(JSON)
        .header("Authorization", "Bearer " + token);
}

// Запрос без авторизации
public static RequestSpecification unauthorized() {
    return given().contentType(JSON);
}
```

**Примеры:**

```java
// GET с авторизацией
Response response = RequestBuilder.authorized(adminToken)
    .get("/api/balance/all");

// POST с авторизацией и body
Response response = RequestBuilder.authorized(adminToken)
    .body(requestDto)
    .post("/api/admin/profile/create");

// POST без авторизации (ожидаем 403)
Response response = RequestBuilder.unauthorized()
    .body(requestDto)
    .post("/api/admin/profile/create");
```

### ApiAssertions

**Назначение:** Стандартизированные проверки HTTP ответов.

**Расположение:** `src/test/java/auc/utils/ApiAssertions.java`

**Методы:**

```java
// Проверка успешного ответа (200 OK + code=OK + content не null)
public static void assertOkResponse(Response response) {
    assertStatus(response, 200);
    String body = safeBody(response);
    Assert.assertEquals(response.jsonPath().getString("code"), "OK", 
        "code != OK. Body: " + body);
    Assert.assertNotNull(response.jsonPath().get("content"), 
        "content is null. Body: " + body);
}

// Проверка 403 Forbidden
public static void assertForbidden(Response response) {
    assertStatus(response, 403);
}

// Проверка 404 Not Found
public static void assertNotFound(Response response) {
    assertStatus(response, 404);
}

// Проверка 400 Bad Request
public static void assertBadRequest(Response response) {
    assertStatus(response, 400);
}

// Внутренний метод проверки статуса
private static void assertStatus(Response response, int expected) {
    Assert.assertEquals(response.getStatusCode(), expected, 
        "Unexpected status. Body: " + safeBody(response));
}

// Безопасное получение body (с защитой от больших ответов)
private static String safeBody(Response response) {
    try {
        String s = response.asString();
        return s == null ? "<null>" : 
            (s.length() > 1000 ? s.substring(0, 1000) + "..." : s);
    } catch (Exception e) {
        return "<unavailable: " + e.getMessage() + ">";
    }
}
```

**Примеры:**

```java
// Успешный ответ
ApiAssertions.assertOkResponse(response);

// Ошибки
ApiAssertions.assertForbidden(response);  // 403
ApiAssertions.assertNotFound(response);   // 404
ApiAssertions.assertBadRequest(response); // 400
```

### ResponseExtractor

**Назначение:** Извлечение типизированных данных из JSON ответов.

**Расположение:** `src/test/java/auc/utils/ResponseExtractor.java`

**Методы:**

```java
// Извлечение объекта из поля "content"
public static <T> T extractContent(Response response, Class<T> type) {
    return response.jsonPath().getObject("content", type);
}

// Извлечение списка объектов из поля "content"
public static <T> List<T> extractContentList(Response response, Class<T> type) {
    return response.jsonPath().getList("content", type);
}

// Извлечение ID из "content.id"
public static Long extractId(Response response) {
    return response.jsonPath().getLong("content.id");
}

// Извлечение токена из "content.token"
public static String extractToken(Response response) {
    return response.jsonPath().getString("content.token");
}
```

**Примеры:**

```java
// Получение DTO
BalanceDto balance = ResponseExtractor.extractContent(response, BalanceDto.class);

// Получение списка DTOs
List<ProfileDto> profiles = ResponseExtractor.extractContentList(response, ProfileDto.class);

// Получение ID созданной сущности
Long createdId = ResponseExtractor.extractId(response);

// Получение токена после авторизации
String token = ResponseExtractor.extractToken(response);
```

### TestDataGenerator

**Назначение:** Генерация валидных тестовых данных.

**Расположение:** `src/test/java/auc/utils/TestDataGenerator.java`

**Методы:**

```java
// MSISDN: 99680 + 7 цифр (всего 12 символов)
public static String generateMsisdn() {
    return "99680" + faker.number().digits(7);
}

// Имя: случайное имя из библиотеки Datafaker
public static String generateFirstName() {
    return faker.name().firstName();
}

// Фамилия: случайная фамилия
public static String generateLastName() {
    return faker.name().lastName();
}

// Telegram Chat ID: 9 цифр
public static String generateTelegramChatId() {
    return String.valueOf(faker.number().numberBetween(100000000, 999999999));
}

// Сумма баланса: от 100.00 до 5000.00
public static Double generateBalanceAmount() {
    return faker.number().randomDouble(2, 100, 5000);
}
```

**Примеры:**

```java
ProfileCreateRequest request = ProfileCreateRequest.builder()
    .msisdn(TestDataGenerator.generateMsisdn())  // "996801234567"
    .userId(1L)
    .pricePlanId(1L)
    .build();
```

---

## DTOs

### Request DTOs

#### AuthSignInRequest

```java
{
    "username": "string",  // required
    "password": "string"   // required
}
```

**Поля:**
- `username` - имя пользователя
- `password` - пароль

**Использование:**

```java
AuthSignInRequest request = AuthSignInRequest.builder()
    .username("superuser")
    .password("Admin123!@#")
    .build();
```

#### AuthSignUpRequest

```java
{
    "username": "string",        // required
    "password": "string",        // required
    "firstName": "string",       // required
    "lastName": "string",        // required
    "telegramChatId": "string"   // required
}
```

**Использование:**

```java
AuthSignUpRequest request = AuthSignUpRequest.builder()
    .username("newuser")
    .password("Password123!")
    .firstName(TestDataGenerator.generateFirstName())
    .lastName(TestDataGenerator.generateLastName())
    .telegramChatId(TestDataGenerator.generateTelegramChatId())
    .build();
```

#### BalanceUpdateRequest

```java
{
    "amount": 1500.50  // required, double
}
```

**Использование:**

```java
BalanceUpdateRequest request = BalanceUpdateRequest.builder()
    .amount(TestDataGenerator.generateBalanceAmount())
    .build();
```

#### ProfileCreateRequest

```java
{
    "msisdn": "996801234567",  // required, pattern: ^99680\d{7}$
    "userId": 1,               // required, int64
    "pricePlanId": 1           // required, int64
}
```

**Использование:**

```java
ProfileCreateRequest request = ProfileCreateRequest.builder()
    .msisdn(TestDataGenerator.generateMsisdn())
    .userId(1L)
    .pricePlanId(1L)
    .build();
```

### Response DTOs

#### BalanceDto

```java
{
    "id": 1,                            // int64
    "amount": 1500.50,                  // double
    "userId": 1,                        // int64
    "currency": "USD",                  // string
    "createdAt": "2025-11-01T10:00:00", // LocalDateTime
    "updatedAt": "2025-11-01T10:00:00"  // LocalDateTime
}
```

#### CounterDto

```java
{
    "id": 1,                            // int64
    "profileId": 1,                     // int64
    "megabytes": 5000,                  // int64
    "seconds": 3600,                    // int64
    "sms": 100,                         // int32
    "status": "ACTIVE",                 // string
    "createdAt": "2025-11-01T10:00:00", // LocalDateTime
    "updatedAt": "2025-11-01T10:00:00"  // LocalDateTime
}
```

#### ProfileDto

```java
{
    "id": 1,                            // int64
    "msisdn": "996801234567",           // string
    "userId": 1,                        // int64
    "pricePlanId": 1,                   // int64
    "status": "ACTIVE",                 // string
    "createdAt": "2025-11-01T10:00:00", // LocalDateTime
    "updatedAt": "2025-11-01T10:00:00"  // LocalDateTime
}
```

---

## Тесты

### BalanceApiTest

**Покрытие:** 9 тестов для Balance API

**Setup:**

```java
private static long testBalanceId;

@BeforeClass
public void setup() {
    Response response = RequestBuilder.authorized(adminToken)
        .get(TestConfig.BALANCE_GET_ALL);
    ApiAssertions.assertOkResponse(response);
    testBalanceId = response.jsonPath().getLong("content[0].id");
}
```

**Тесты:**

1. `testGetBalanceById_Success` - успешное получение баланса по ID
2. `testGetBalanceById_NotFound` - получение несуществующего баланса
3. `testGetBalanceById_Unauthorized` - получение без токена
4. `testGetAllBalances_Success` - получение списка балансов
5. `testGetAllBalances_Unauthorized` - получение списка без токена
6. `testUpdateBalance_AsPerSpecification` - обновление по спецификации (**БАГ**)
7. `testUpdateBalance_NotFound` - обновление несуществующего баланса
8. `testUpdateBalance_Unauthorized` - обновление без токена
9. `testUpdateBalance_MissingAmount` - валидация обязательного поля

### CounterApiTest

**Покрытие:** 7 тестов для Counter API

**Setup:**

```java
private static Long existingCounterId;

@BeforeClass
public void setup() {
    Response response = RequestBuilder.authorized(adminToken)
        .get(TestConfig.COUNTER_GET_ALL);
    ApiAssertions.assertOkResponse(response);
    existingCounterId = response.jsonPath().getLong("content[0].id");
}
```

**Тесты:**

1. `testGetCounterById_Success` - успешное получение счётчика
2. `testGetCounterById_NotFound` - получение несуществующего счётчика
3. `testGetCounterById_Unauthorized` - получение без токена
4. `testGetAllCounters_Success` - получение всех счётчиков
5. `testGetAllCounters_Unauthorized` - получение всех без токена
6. `testGetAllActiveCounters_Success` - получение активных счётчиков (**БАГ**)
7. `testGetAllActiveCounters_Unauthorized` - получение активных без токена

### ProfileApiTest

**Покрытие:** 13 тестов для Profile API

**Setup:**

```java
private static Long testProfileId;
private static String testMsisdn;
private static Long testUserId = 1L;
private static Long testPricePlanId = 1L;

@BeforeClass
public void setup() {
    testMsisdn = TestDataGenerator.generateMsisdn();
    
    ProfileCreateRequest body = ProfileCreateRequest.builder()
        .msisdn(testMsisdn)
        .userId(testUserId)
        .pricePlanId(testPricePlanId)
        .build();

    Response createResponse = RequestBuilder.authorized(adminToken)
        .body(body)
        .post(TestConfig.PROFILE_CREATE);

    ApiAssertions.assertOkResponse(createResponse); // БАГ: ожидает 200, получает 201
    testProfileId = ResponseExtractor.extractId(createResponse);
}
```

**Тесты:**

1. `testCreateProfile_Success` - успешное создание профиля
2. `testCreateProfile_DuplicateMsisdn` - создание с дубликатом MSISDN
3. `testGetProfileById_Success` - получение профиля по ID
4. `testGetProfileById_NotFound` - получение несуществующего профиля
5. `testGetAllProfiles_Success` - получение списка профилей
6. `testUpdateProfile_Success` - обновление профиля
7. `testDeleteProfile_StatusCode` - удаление профиля
8. `testGetAllRemovedProfiles_Success` - получение удалённых профилей
9. `testCreateProfile_Unauthorized` - создание без токена
10. `testGetProfileById_Unauthorized` - получение без токена
11. `testGetAllProfiles_Unauthorized` - получение списка без токена
12. `testUpdateProfile_Unauthorized` - обновление без токена
13. `testDeleteProfile_Unauthorized` - удаление без токена

---

## Тест-кейсы

### BalanceApiTest - 9 тестов

#### TC-BAL-001: GET Balance by ID - Success
- **Метод:** GET
- **URL:** `/api/balance/{id}`
- **Цель:** Проверка успешного получения баланса
- **Шаги:**
  1. Отправить GET запрос с валидным ID
  2. Проверить статус 200
  3. Проверить наличие данных баланса
- **Ожидаемый результат:** Статус 200, данные баланса получены

#### TC-BAL-002: GET Balance by ID - Not Found
- **Метод:** GET
- **URL:** `/api/balance/{id}` где id=999999999
- **Цель:** Проверка получения несуществующего баланса
- **Ожидаемый результат:** Статус 404

#### TC-BAL-003: GET Balance by ID - Unauthorized
- **Метод:** GET
- **URL:** `/api/balance/{id}`
- **Цель:** Проверка доступа без токена
- **Ожидаемый результат:** Статус 403

#### TC-BAL-004: GET All Balances - Success
- **Метод:** GET
- **URL:** `/api/balance/all`
- **Цель:** Получение списка всех балансов
- **Ожидаемый результат:** Статус 200, массив балансов

#### TC-BAL-005: GET All Balances - Unauthorized
- **Метод:** GET
- **URL:** `/api/balance/all`
- **Цель:** Получение списка без токена
- **Ожидаемый результат:** Статус 403

#### TC-BAL-006: PUT Update Balance - As Per Specification 🐛
- **Метод:** PUT
- **URL:** `/api/balance/update/{id}`
- **Тело:** `{"amount": 1500.50}`
- **Цель:** Обновление баланса по спецификации
- **Ожидаемый результат:** Статус 200
- **Реальный результат:** Статус 400 ❌
- **Статус:** **БАГ API**

#### TC-BAL-007: PUT Update Balance - Not Found
- **Метод:** PUT
- **URL:** `/api/balance/update/{id}` где id=999999999
- **Тело:** `{"amount": 1500.50}`
- **Ожидаемый результат:** Статус 404
- **Реальный результат:** Статус 400 ❌

#### TC-BAL-008: PUT Update Balance - Unauthorized
- **Метод:** PUT
- **URL:** `/api/balance/update/{id}`
- **Тело:** `{"amount": 1500.50}`
- **Цель:** Обновление без токена
- **Ожидаемый результат:** Статус 403

#### TC-BAL-009: PUT Update Balance - Missing Amount
- **Метод:** PUT
- **URL:** `/api/balance/update/{id}`
- **Тело:** `{}`
- **Цель:** Валидация обязательного поля
- **Ожидаемый результат:** Статус 400

### CounterApiTest - 7 тестов

#### TC-CNT-001: GET Counter by ID - Success
- **Метод:** GET
- **URL:** `/api/admin/counter/{id}`
- **Ожидаемый результат:** Статус 200, данные счётчика

#### TC-CNT-002: GET Counter by ID - Not Found
- **Метод:** GET
- **URL:** `/api/admin/counter/{id}` где id=999999999
- **Ожидаемый результат:** Статус 404

#### TC-CNT-003: GET Counter by ID - Unauthorized
- **Метод:** GET
- **URL:** `/api/admin/counter/{id}`
- **Ожидаемый результат:** Статус 403

#### TC-CNT-004: GET All Counters - Success
- **Метод:** GET
- **URL:** `/api/admin/counter/all`
- **Ожидаемый результат:** Статус 200, массив счётчиков

#### TC-CNT-005: GET All Counters - Unauthorized
- **Метод:** GET
- **URL:** `/api/admin/counter/all`
- **Ожидаемый результат:** Статус 403

#### TC-CNT-006: GET All Active Counters - Success 🐛
- **Метод:** GET
- **URL:** `/api/admin/counter/all-active`
- **Ожидаемый результат:** Статус 200
- **Реальный результат:** Статус 204 ❌
- **Статус:** **БАГ API**

#### TC-CNT-007: GET All Active Counters - Unauthorized
- **Метод:** GET
- **URL:** `/api/admin/counter/all-active`
- **Ожидаемый результат:** Статус 403

### ProfileApiTest - 13 тестов

#### TC-PRF-001: POST Create Profile - Success 🐛
- **Метод:** POST
- **URL:** `/api/admin/profile/create`
- **Тело:** `{"msisdn": "996801234567", "userId": 1, "pricePlanId": 1}`
- **Ожидаемый результат:** Статус 200
- **Реальный результат:** Статус 201 ❌
- **Статус:** **БАГ API**

#### TC-PRF-002: POST Create Profile - Duplicate MSISDN
- **Метод:** POST
- **URL:** `/api/admin/profile/create`
- **Тело:** Дубликат MSISDN
- **Ожидаемый результат:** Статус 400

#### TC-PRF-003: GET Profile by ID - Success
- **Метод:** GET
- **URL:** `/api/admin/profile/{id}`
- **Ожидаемый результат:** Статус 200, данные профиля

#### TC-PRF-004: GET Profile by ID - Not Found
- **Метод:** GET
- **URL:** `/api/admin/profile/{id}` где id=999999999
- **Ожидаемый результат:** Статус 404

#### TC-PRF-005: GET All Profiles - Success
- **Метод:** GET
- **URL:** `/api/admin/profile/all`
- **Ожидаемый результат:** Статус 200, массив профилей

#### TC-PRF-006: PUT Update Profile - Success
- **Метод:** PUT
- **URL:** `/api/admin/profile/update/{id}`
- **Тело:** `{"msisdn": "996809999999", "userId": 1, "pricePlanId": 1}`
- **Ожидаемый результат:** Статус 200, обновлённые данные

#### TC-PRF-007: DELETE Profile - Status Code
- **Метод:** DELETE
- **URL:** `/api/admin/profile/delete/{id}`
- **Ожидаемый результат:** Статус 200
- **Реальный результат:** Статус 204
- **Примечание:** Возможно, спецификация устарела

#### TC-PRF-008: GET All Removed Profiles - Success
- **Метод:** GET
- **URL:** `/api/admin/profile/all-removed`
- **Ожидаемый результат:** Статус 200, массив удалённых профилей

#### TC-PRF-009: POST Create Profile - Unauthorized
- **Метод:** POST
- **URL:** `/api/admin/profile/create`
- **Ожидаемый результат:** Статус 403

#### TC-PRF-010: GET Profile by ID - Unauthorized
- **Метод:** GET
- **URL:** `/api/admin/profile/{id}`
- **Ожидаемый результат:** Статус 403

#### TC-PRF-011: GET All Profiles - Unauthorized
- **Метод:** GET
- **URL:** `/api/admin/profile/all`
- **Ожидаемый результат:** Статус 403

#### TC-PRF-012: PUT Update Profile - Unauthorized
- **Метод:** PUT
- **URL:** `/api/admin/profile/update/{id}`
- **Ожидаемый результат:** Статус 403

#### TC-PRF-013: DELETE Profile - Unauthorized
- **Метод:** DELETE
- **URL:** `/api/admin/profile/delete/{id}`
- **Ожидаемый результат:** Статус 403

---

## Баг-репорты

### 🐛 БАГ #1: Balance Update - Некорректная обработка requestBody

**Приоритет:** 🔴 Высокий  
**Статус:** 🔴 Открыт  
**Найден:** ProfileApiTest.setup, BalanceApiTest.testUpdateBalance_AsPerSpecification

**Описание:**

API эндпоинт `PUT /api/balance/update/{id}` не принимает requestBody согласно спецификации.

**Спецификация (api.json):**

```json
{
  "requestBody": {
    "content": {
      "application/json": {
        "schema": {
          "$ref": "#/components/schemas/BalanceUpdateRequestDto"
        }
      }
    }
  },
  "responses": {
    "200": {
      "description": "OK"
    }
  }
}
```

**Ожидаемое поведение:**

```bash
PUT /api/balance/update/1
Content-Type: application/json
Authorization: Bearer <token>

{"amount": 1500.50}

→ 200 OK
```

**Текущее поведение:**

```bash
PUT /api/balance/update/1
Content-Type: application/json
Authorization: Bearer <token>

{"amount": 1500.50}

→ 400 Bad Request
{"message": "Введите amount."}
```

**Код воспроизведения:**

```java
BalanceUpdateRequest body = BalanceUpdateRequest.builder()
    .amount(1500.50)
    .build();

Response response = RequestBuilder.authorized(adminToken)
    .body(body)
    .put("/api/balance/update/1");

// Ожидается: 200
// Реально: 400
```

**Возможные причины:**

1. Контроллер не читает requestBody
2. Валидация применяется до десериализации
3. Несоответствие между спецификацией и реализацией

**Рекомендации:**

1. Проверить маппинг `@RequestBody` в контроллере
2. Проверить валидацию `@Valid` и `@Validated`
3. Обновить спецификацию, если поведение намеренное

### 🐛 БАГ #2: Profile Create - Неверный HTTP статус

**Приоритет:** 🟡 Средний  
**Статус:** 🟡 Известный  

**Описание:**

API возвращает `201 Created` вместо `200 OK` при создании профиля.

**Спецификация:**

```json
"responses": {
  "200": {
    "description": "OK"
  }
}
```

**Реальность:**

```bash
POST /api/admin/profile/create
→ 201 Created
```

**Примечание:**

`201 Created` является более корректным статусом для операции создания согласно REST best practices. Возможно, спецификация устарела.

**Рекомендации:**

1. Обновить спецификацию на `201 Created`
2. Или изменить контроллер на возврат `200 OK`

### 🐛 БАГ #3: Counter All Active - Некорректный статус для пустого списка

**Приоритет:** 🟡 Средний  
**Статус:** 🟡 Известный  

**Описание:**

API возвращает `204 No Content` вместо `200 OK` для `/api/admin/counter/all-active`.

**Спецификация:**

```json
"responses": {
  "200": {
    "description": "OK"
  }
}
```

**Реальность:**

```bash
GET /api/admin/counter/all-active
→ 204 No Content
```

**Примечание:**

`204 No Content` часто используется для пустых результатов, но по спецификации должен быть `200 OK` с пустым массивом.

**Рекомендации:**

Вернуть `200 OK` с `{"code": "OK", "content": []}`

---

## Best Practices

### 1. Используйте утилиты вместо дублирования

✅ **Правильно:**

```java
ApiAssertions.assertOkResponse(response);
```

❌ **Неправильно:**

```java
response.then()
    .statusCode(200)
    .body("code", equalTo("OK"))
    .body("content", notNullValue());
```

### 2. Генерируйте уникальные данные

✅ **Правильно:**

```java
String msisdn = TestDataGenerator.generateMsisdn();
```

❌ **Неправильно:**

```java
String msisdn = "996801234567"; // Может конфликтовать
```

### 3. Cleanup после создания ресурсов

✅ **Правильно:**

```java
Long createdId = ResponseExtractor.extractId(response);
RequestBuilder.authorized(adminToken)
    .delete(url(TestConfig.PROFILE_DELETE, createdId));
```

❌ **Неправильно:**

```java
// Ничего не удаляем → захламляем БД
```

### 4. Информативные описания тестов

✅ **Правильно:**

```java
@Test(priority = 1, description = "GET /api/balance/{id} - успешное получение баланса")
public void testGetBalanceById_Success() { }
```

❌ **Неправильно:**

```java
@Test
public void test1() { }
```

### 5. Проверяйте по спецификации

✅ **Правильно:**

```java
// Спека говорит 200 → проверяем 200
ApiAssertions.assertOkResponse(response);
```

❌ **Неправильно:**

```java
// Подстраиваемся под баг API
Assert.assertEquals(response.getStatusCode(), 201);
```

---

## Troubleshooting

### Тесты падают с 403 Forbidden

**Причина:** Не получен или истёк adminToken

**Решение:**

1. Проверьте credentials в `TestConfig.java`
2. Убедитесь, что API доступен
3. Проверьте консольный вывод на наличие ошибок авторизации

### Тесты падают с Connection Refused

**Причина:** API сервер недоступен

**Решение:**

```bash
# Проверьте доступность
curl http://195.38.164.168:7173/api/auth/sign_in

# Измените BASE_URL если нужно
```

### Profile тесты падают с "MSISDN invalid pattern"

**Причина:** Неправильный формат MSISDN

**Решение:**

MSISDN должен соответствовать: `^99680\d{7}$` (всего 12 символов)

```java
// Правильно
String msisdn = TestDataGenerator.generateMsisdn(); // "996801234567"

// Неправильно
String msisdn = "1234567"; // Не соответствует паттерну
```

---

**Версия:** 2.0.0  
**Обновлено:** 1 ноября 2025