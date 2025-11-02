# Billing API Test Framework - Technical Documentation# Billing API Test Framework - Technical Documentation# Billing API Test Framework - Technical Documentation



Полная техническая документация фреймворка для автоматизированного тестирования Billing API.



## СодержаниеПолная техническая документация фреймворка для автоматизированного тестирования Billing API.Полная техническая документация фреймворка для автоматизированного тестирования Billing API.



1. [Полный воркфлоу теста](#полный-воркфлоу-теста)

2. [Архитектура фреймворка](#архитектура-фреймворка)

3. [Базовые классы](#базовые-классы)## Содержание## 📚 Содержание

4. [Утилиты](#утилиты)

5. [DTOs](#dtos)

6. [Тесты](#тесты)

7. [Тест-кейсы](#тест-кейсы)1. [Полный воркфлоу теста](#полный-воркфлоу-теста)1. [Полный воркфлоу теста](#полный-воркфлоу-теста)

8. [Баг-репорты](#баг-репорты)

9. [Best Practices](#best-practices)2. [Архитектура фреймворка](#архитектура-фреймворка)2. [Архитектура фреймворка](#архитектура-фреймворка)



---3. [Базовые классы](#базовые-классы)3. [Базовые классы](#базовые-классы)



## Полный воркфлоу теста4. [Утилиты](#утилиты)4. [Утилиты](#утилиты)



### Пример: testCreateProfile_Success5. [DTOs](#dtos)5. [DTOs](#dtos)



Разберём пошагово, как выполняется тест создания профиля и какие файлы участвуют в процессе.6. [Тесты](#тесты)6. [Тесты](#тесты)



#### Шаг 0: Подготовка (Before Class)7. [Тест-кейсы](#тест-кейсы)7. [Тест-кейсы](#тест-кейсы)



**Файл:** `ProfileApiTest.java`8. [Баг-репорты](#баг-репорты)8. [Баг-репорты](#баг-репорты)



```java9. [Best Practices](#best-practices)9. [Best Practices](#best-practices)

public class ProfileApiTest extends BaseApiTest {

    

    @BeforeClass

    public void setup() {------

        // Вызывается BaseApiTest.globalSetup()

    }

}

```## Полный воркфлоу теста## Полный воркфлоу теста



**Что происходит:**



1. **BaseApiTest.globalSetup()** ← `BaseApiTest.java`### Пример: testCreateProfile_Success### Пример: testCreateProfile_Success

   ```java

   @BeforeClass

   public void globalSetup() {

       RestAssured.baseURI = TestConfig.BASE_URL;Разберём пошагово, как выполняется тест создания профиля и какие файлы участвуют в процессе.Разберём пошагово, как выполняется тест создания профиля и какие файлы участвуют в процессе.

       adminToken = getAdminToken();

   }

   ```

#### Шаг 0: Подготовка (Before Class)#### 🎬 Шаг 0: Подготовка (Before Class)

2. **getAdminToken()** ← `BaseApiTest.java`

   - Создаёт `AuthSignInRequest`

   - Вызывает `RequestBuilder.unauthorized()`

   - Отправляет POST на `TestConfig.AUTH_SIGN_IN`**Файл:** `ProfileApiTest.java`**Файл:** `ProfileApiTest.java`

   - Если 401, создаёт `AuthSignUpRequest`

   - Генерирует данные через `TestDataGenerator`

   - Извлекает токен через `ResponseExtractor.extractToken()`

```java```java

**Файлы задействованы:**

- BaseApiTest.javapublic class ProfileApiTest extends BaseApiTest {public class ProfileApiTest extends BaseApiTest {

- TestConfig.java

- RequestBuilder.java        

- AuthSignInRequest.java

- AuthSignUpRequest.java    @BeforeClass    @BeforeClass

- TestDataGenerator.java

- ResponseExtractor.java    public void setup() {    public void setup() {



#### Шаг 1: Запуск теста        // Вызывается BaseApiTest.globalSetup()        // Вызывается BaseApiTest.globalSetup()



**Файл:** `ProfileApiTest.java`    }    }



```java}}

@Test(priority = 1, description = "POST /api/admin/profile/create - успешное создание профиля")

public void testCreateProfile_Success() {``````

    // Начало теста

```



**Что происходит:****Что происходит:****Что происходит:**



TestNG видит аннотацию `@Test` и запускает метод.



**Файлы задействованы:**1. **BaseApiTest.globalSetup()** ← `BaseApiTest.java`1. **BaseApiTest.globalSetup()** ← `BaseApiTest.java`

- ProfileApiTest.java

- pom.xml (зависимость TestNG 7.10.2)   ```java   ```java



#### Шаг 2: Генерация тестовых данных   @BeforeClass   @BeforeClass



```java   public void globalSetup() {   public void globalSetup() {

String msisdn = TestDataGenerator.generateMsisdn();

```       RestAssured.baseURI = TestConfig.BASE_URL; // ← TestConfig.java       RestAssured.baseURI = TestConfig.BASE_URL; // ← TestConfig.java



**Файл:** `utils/TestDataGenerator.java`       adminToken = getAdminToken(); // ← BaseApiTest.java       adminToken = getAdminToken(); // ← BaseApiTest.java



```java   }   }

public class TestDataGenerator {

    private static final Faker faker = new Faker();   ```   ```

    

    public static String generateMsisdn() {

        return "99680" + faker.number().digits(7);

    }2. **getAdminToken()** ← `BaseApiTest.java`2. **getAdminToken()** ← `BaseApiTest.java`

}

```   - Создаёт `AuthSignInRequest` ← `dto/request/AuthSignInRequest.java`   - Создаёт `AuthSignInRequest` ← `dto/request/AuthSignInRequest.java`



**Что происходит:**   - Вызывает `RequestBuilder.unauthorized()` ← `utils/RequestBuilder.java`   - Вызывает `RequestBuilder.unauthorized()` ← `utils/RequestBuilder.java`



1. `Faker` из библиотеки Datafaker генерирует случайные 7 цифр   - Отправляет POST на `TestConfig.AUTH_SIGN_IN` ← `TestConfig.java`   - Отправляет POST на `TestConfig.AUTH_SIGN_IN` ← `TestConfig.java`

2. Добавляется префикс "99680"

3. Результат: уникальный MSISDN "996801234567"   - Если 401, создаёт `AuthSignUpRequest` ← `dto/request/AuthSignUpRequest.java`   - Если 401, создаёт `AuthSignUpRequest` ← `dto/request/AuthSignUpRequest.java`



**Файлы задействованы:**   - Генерирует данные через `TestDataGenerator` ← `utils/TestDataGenerator.java`   - Генерирует данные через `TestDataGenerator` ← `utils/TestDataGenerator.java`

- TestDataGenerator.java

- pom.xml (зависимость Datafaker 2.4.2)   - Извлекает токен через `ResponseExtractor.extractToken()` ← `utils/ResponseExtractor.java`   - Извлекает токен через `ResponseExtractor.extractToken()` ← `utils/ResponseExtractor.java`



#### Шаг 3: Построение Request DTO



```java**Файлы задействованы:****Файлы задействованы:**

ProfileCreateRequest body = ProfileCreateRequest.builder()

    .msisdn(msisdn)- BaseApiTest.java- ✅ `BaseApiTest.java`

    .userId(1L)

    .pricePlanId(1L)- TestConfig.java- ✅ `TestConfig.java`

    .build();

```- RequestBuilder.java- ✅ `RequestBuilder.java`



**Файл:** `dto/request/ProfileCreateRequest.java`- AuthSignInRequest.java- ✅ `AuthSignInRequest.java`



```java- AuthSignUpRequest.java- ✅ `AuthSignUpRequest.java`

public class ProfileCreateRequest {

    @JsonProperty("msisdn")- TestDataGenerator.java- ✅ `TestDataGenerator.java`

    private String msisdn;

    - ResponseExtractor.java- ✅ `ResponseExtractor.java`

    @JsonProperty("userId")

    private Long userId;

    

    @JsonProperty("pricePlanId")#### Шаг 1: Запуск теста#### 🎬 Шаг 1: Запуск теста

    private Long pricePlanId;

    

    public static Builder builder() {

        return new Builder();**Файл:** `ProfileApiTest.java`**Файл:** `ProfileApiTest.java`

    }

    

    public static class Builder {

        private final ProfileCreateRequest request = new ProfileCreateRequest();```java```java

        

        public Builder msisdn(String msisdn) {@Test(priority = 1, description = "POST /api/admin/profile/create - успешное создание профиля")@Test(priority = 1, description = "POST /api/admin/profile/create - успешное создание профиля")

            request.msisdn = msisdn;

            return this;public void testCreateProfile_Success() {public void testCreateProfile_Success() {

        }

            // Начало теста    // Начало теста

        public Builder userId(Long userId) {

            request.userId = userId;``````

            return this;

        }

        

        public Builder pricePlanId(Long pricePlanId) {**Что происходит:****Что происходит:**

            request.pricePlanId = pricePlanId;

            return this;

        }

        TestNG видит аннотацию `@Test` и запускает метод.TestNG видит аннотацию `@Test` и запускает метод.

        public ProfileCreateRequest build() {

            return request;

        }

    }**Файлы задействованы:****Файлы задействованы:**

}

```- ProfileApiTest.java- ✅ `ProfileApiTest.java`



**Что происходит:**- pom.xml (зависимость TestNG 7.10.2)- ✅ `pom.xml` (зависимость TestNG 7.10.2)



1. Builder создаёт новый объект ProfileCreateRequest

2. Заполняет поля через цепочку вызовов

3. Возвращает готовый объект#### Шаг 2: Генерация тестовых данных#### 🎬 Шаг 2: Генерация тестовых данных



**Результат:** Объект ready для сериализации в JSON



**Файлы задействованы:**```java```java

- ProfileCreateRequest.java

String msisdn = TestDataGenerator.generateMsisdn();String msisdn = TestDataGenerator.generateMsisdn();

#### Шаг 4: Построение HTTP запроса

``````

```java

Response response = RequestBuilder.authorized(adminToken)

    .body(body)

    .post(TestConfig.PROFILE_CREATE);**Файл:** `utils/TestDataGenerator.java`**Файл:** `utils/TestDataGenerator.java`

```



**Файл:** `utils/RequestBuilder.java`

```java```java

```java

public class RequestBuilder {public class TestDataGenerator {public class TestDataGenerator {

    public static RequestSpecification authorized(String token) {

        return given()    private static final Faker faker = new Faker();    private static final Faker faker = new Faker();

            .contentType(JSON)

            .header("Authorization", "Bearer " + token);        

    }

}    public static String generateMsisdn() {    public static String generateMsisdn() {

```

        return "99680" + faker.number().digits(7);        return "99680" + faker.number().digits(7);

**Что происходит:**

        // Возвращает: "996801234567"        // Возвращает: "996801234567"

1. RequestBuilder.authorized() создаёт REST Assured RequestSpecification

2. Устанавливает Content-Type: application/json    }    }

3. Добавляет header Authorization: Bearer eyJhbGc...

4. .body(body) ← Jackson сериализует ProfileCreateRequest в JSON}}

5. .post() получает URL из TestConfig.PROFILE_CREATE

``````

**Файл:** `TestConfig.java`



```java

public static final String PROFILE_CREATE = "/api/admin/profile/create";**Что происходит:****Что происходит:**

```



**REST Assured формирует запрос:**

1. `Faker` из библиотеки Datafaker генерирует случайные 7 цифр1. `Faker` из библиотеки Datafaker генерирует случайные 7 цифр

```http

POST http://195.38.164.168:7173/api/admin/profile/create2. Добавляется префикс "99680"2. Добавляется префикс "99680"

Content-Type: application/json

Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...3. Результат: уникальный MSISDN `"996801234567"`3. Результат: уникальный MSISDN `"996801234567"`



{

  "msisdn": "996801234567",

  "userId": 1,**Файлы задействованы:****Файлы задействованы:**

  "pricePlanId": 1

}- TestDataGenerator.java- ✅ `TestDataGenerator.java`

```

- pom.xml (зависимость Datafaker 2.4.2)- ✅ `pom.xml` (зависимость Datafaker 2.4.2)

**Файлы задействованы:**

- RequestBuilder.java

- TestConfig.java

- ProfileCreateRequest.java (сериализация)#### Шаг 3: Построение Request DTO#### 🎬 Шаг 3: Построение Request DTO

- pom.xml (REST Assured 5.5.0, Jackson 2.18.2)



#### Шаг 5: Отправка запроса и получение ответа

```java```java

**Что происходит:**

ProfileCreateRequest body = ProfileCreateRequest.builder()ProfileCreateRequest body = ProfileCreateRequest.builder()

1. REST Assured отправляет HTTP POST запрос на сервер

2. Сервер обрабатывает запрос    .msisdn(msisdn)    .msisdn(msisdn)

3. Возвращается HTTP ответ:

    .userId(1L)    .userId(1L)

```http

HTTP/1.1 201 Created    .pricePlanId(1L)    .pricePlanId(1L)

Content-Type: application/json

    .build();    .build();

{

  "code": "OK",``````

  "content": {

    "id": 42,

    "msisdn": "996801234567",

    "userId": 1,**Файл:** `dto/request/ProfileCreateRequest.java`**Файл:** `dto/request/ProfileCreateRequest.java`

    "pricePlanId": 1,

    "status": "ACTIVE",

    "createdAt": "2025-11-02T10:30:00",

    "updatedAt": "2025-11-02T10:30:00"```java```java

  }

}public class ProfileCreateRequest {public class ProfileCreateRequest {

```

    @JsonProperty("msisdn")    @JsonProperty("msisdn")

4. REST Assured парсит ответ в объект Response

    private String msisdn;    private String msisdn;

**Файлы задействованы:**

- pom.xml (REST Assured HTTP клиент)        



#### Шаг 6: Проверка статус-кода    @JsonProperty("userId")    @JsonProperty("userId")



```java    private Long userId;    private Long userId;

ApiAssertions.assertOkResponse(response);

```        



**Файл:** `utils/ApiAssertions.java`    @JsonProperty("pricePlanId")    @JsonProperty("pricePlanId")



```java    private Long pricePlanId;    private Long pricePlanId;

public class ApiAssertions {

    public static void assertOkResponse(Response response) {        

        assertStatus(response, 200);

            public static Builder builder() {    public static Builder builder() {

        String body = safeBody(response);

                return new Builder();        return new Builder();

        Assert.assertEquals(

            response.jsonPath().getString("code"),     }    }

            "OK", 

            "code != OK. Body: " + body        

        );

            public static class Builder {    public static class Builder {

        Assert.assertNotNull(

            response.jsonPath().get("content"),         private final ProfileCreateRequest request = new ProfileCreateRequest();        private final ProfileCreateRequest request = new ProfileCreateRequest();

            "content is null. Body: " + body

        );                

    }

            public Builder msisdn(String msisdn) {        public Builder msisdn(String msisdn) {

    private static void assertStatus(Response response, int expected) {

        Assert.assertEquals(            request.msisdn = msisdn;            request.msisdn = msisdn;

            response.getStatusCode(), 

            expected,             return this;            return this;

            "Unexpected status. Body: " + safeBody(response)

        );        }        }

    }

}                

```

        public Builder userId(Long userId) {        public Builder userId(Long userId) {

**Что происходит:**

            request.userId = userId;            request.userId = userId;

1. assertStatus(200) проверяет код ответа

   - Ожидается: 200            return this;            return this;

   - Реально: 201

   - ТЕСТ ПАДАЕТ → найден БАГ API        }        }



2. TestNG выбрасывает AssertionError:                

   ```

   java.lang.AssertionError: Unexpected status. Expected: 200, Actual: 201        public Builder pricePlanId(Long pricePlanId) {        public Builder pricePlanId(Long pricePlanId) {

   ```

            request.pricePlanId = pricePlanId;            request.pricePlanId = pricePlanId;

**Файлы задействованы:**

- ApiAssertions.java            return this;            return this;

- pom.xml (TestNG assertions)

        }        }

#### Шаг 7: Извлечение данных (если тест прошёл бы)

                

```java

Long createdId = ResponseExtractor.extractId(response);        public ProfileCreateRequest build() {        public ProfileCreateRequest build() {

```

            return request;            return request;

**Файл:** `utils/ResponseExtractor.java`

        }        }

```java

public class ResponseExtractor {    }    }

    public static Long extractId(Response response) {

        return response.jsonPath().getLong("content.id");}}

    }

}``````

```



**Что происходит:**

**Что происходит:****Что происходит:**

1. JsonPath парсит JSON ответа

2. Извлекает поле content.id

3. Конвертирует в Long

1. Builder создаёт новый объект `ProfileCreateRequest`1. Builder создаёт новый объект `ProfileCreateRequest`

**Файлы задействованы:**

- ResponseExtractor.java2. Заполняет поля через цепочку вызовов2. Заполняет поля через цепочку вызовов

- pom.xml (REST Assured JsonPath)

3. Возвращает готовый объект3. Возвращает готовый объект

#### Шаг 8: Cleanup (если тест прошёл бы)



```java

RequestBuilder.authorized(adminToken)**Результат:** Объект ready для сериализации в JSON**Результат:** Объект ready для сериализации в JSON

    .delete(url(TestConfig.PROFILE_DELETE, createdId));

```



**Файл:** `BaseApiTest.java`**Файлы задействованы:****Файлы задействованы:**



```java- ProfileCreateRequest.java- ✅ `ProfileCreateRequest.java`

protected String url(String template, Object... params) {

    String result = template;

    for (Object param : params) {

        result = result.replaceFirst("\\{[^}]+}", String.valueOf(param));#### Шаг 4: Построение HTTP запроса#### 🎬 Шаг 4: Построение HTTP запроса

    }

    return result;

}

``````java```java



**Что происходит:**Response response = RequestBuilder.authorized(adminToken)Response response = RequestBuilder.authorized(adminToken)



1. url() подставляет ID в шаблон URL    .body(body)    .body(body)

2. RequestBuilder.authorized() создаёт DELETE запрос

3. Отправляется DELETE для очистки тестовых данных    .post(TestConfig.PROFILE_CREATE);    .post(TestConfig.PROFILE_CREATE);



**Файлы задействованы:**``````

- BaseApiTest.java

- TestConfig.java

- RequestBuilder.java

**Файл:** `utils/RequestBuilder.java`**Файл:** `utils/RequestBuilder.java`

---



### Визуализация полного воркфлоу

```java```java

```

┌─────────────────────────────────────────────────────────────────┐public class RequestBuilder {public class RequestBuilder {

│ 1. ПОДГОТОВКА (@BeforeClass)                                    │

└─────────────────────────────────────────────────────────────────┘    public static RequestSpecification authorized(String token) {    public static RequestSpecification authorized(String token) {

        │

        ├─→ ProfileApiTest.setup()        return given()        return given()

        │   └─→ BaseApiTest.globalSetup()

        │       ├─→ TestConfig.BASE_URL            .contentType(JSON)            .contentType(JSON)

        │       └─→ getAdminToken()

        │           ├─→ AuthSignInRequest.builder()            .header("Authorization", "Bearer " + token);            .header("Authorization", "Bearer " + token);

        │           ├─→ RequestBuilder.unauthorized()

        │           ├─→ TestConfig.AUTH_SIGN_IN    }    }

        │           ├─→ ResponseExtractor.extractToken()

        │           │   (если 401):}}

        │           ├─→ AuthSignUpRequest.builder()

        │           ├─→ TestDataGenerator.*``````

        │           └─→ POST /api/auth/sign_up

        │

        ▼

┌─────────────────────────────────────────────────────────────────┐**Что происходит:****Что происходит:**

│ 2. ЗАПУСК ТЕСТА (@Test)                                         │

└─────────────────────────────────────────────────────────────────┘

        │

        ├─→ testCreateProfile_Success()1. **RequestBuilder.authorized()** создаёт REST Assured `RequestSpecification`1. **RequestBuilder.authorized()** создаёт REST Assured `RequestSpecification`

        │

        ▼2. Устанавливает `Content-Type: application/json`2. Устанавливает `Content-Type: application/json`

┌─────────────────────────────────────────────────────────────────┐

│ 3. ГЕНЕРАЦИЯ ДАННЫХ                                             │3. Добавляет header `Authorization: Bearer eyJhbGc...`3. Добавляет header `Authorization: Bearer eyJhbGc...`

└─────────────────────────────────────────────────────────────────┘

        │4. `.body(body)` ← Jackson сериализует `ProfileCreateRequest` в JSON4. `.body(body)` ← Jackson сериализует `ProfileCreateRequest` в JSON

        ├─→ TestDataGenerator.generateMsisdn()

        │   └─→ Faker.number().digits(7)5. `.post()` получает URL из `TestConfig.PROFILE_CREATE`5. `.post()` получает URL из `TestConfig.PROFILE_CREATE`

        │       └─→ return "99680" + "1234567"

        │

        ▼

┌─────────────────────────────────────────────────────────────────┐**Файл:** `TestConfig.java`**Файл:** `TestConfig.java`

│ 4. ПОСТРОЕНИЕ REQUEST DTO                                       │

└─────────────────────────────────────────────────────────────────┘

        │

        ├─→ ProfileCreateRequest.builder()```java```java

        │   ├─→ .msisdn("996801234567")

        │   ├─→ .userId(1L)public static final String PROFILE_CREATE = "/api/admin/profile/create";public static final String PROFILE_CREATE = "/api/admin/profile/create";

        │   ├─→ .pricePlanId(1L)

        │   └─→ .build()``````

        │

        ▼

┌─────────────────────────────────────────────────────────────────┐

│ 5. ПОСТРОЕНИЕ HTTP ЗАПРОСА                                      │**REST Assured формирует запрос:****REST Assured формирует запрос:**

└─────────────────────────────────────────────────────────────────┘

        │

        ├─→ RequestBuilder.authorized(adminToken)

        │   ├─→ given()```http```http

        │   ├─→ .contentType(JSON)

        │   ├─→ .header("Authorization", "Bearer ...")POST http://195.38.164.168:7173/api/admin/profile/createPOST http://195.38.164.168:7173/api/admin/profile/create

        │   └─→ .body(body)

        │       └─→ Jackson сериализует DTO в JSONContent-Type: application/jsonContent-Type: application/json

        │

        ├─→ .post(TestConfig.PROFILE_CREATE)Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

        │   └─→ URL: "/api/admin/profile/create"

        │

        ▼

┌─────────────────────────────────────────────────────────────────┐{{

│ 6. ОТПРАВКА НА СЕРВЕР                                           │

└─────────────────────────────────────────────────────────────────┘  "msisdn": "996801234567",  "msisdn": "996801234567",

        │

        │   HTTP POST →  http://195.38.164.168:7173  "userId": 1,  "userId": 1,

        │                /api/admin/profile/create

        │  "pricePlanId": 1  "pricePlanId": 1

        │   Headers:

        │   - Content-Type: application/json}}

        │   - Authorization: Bearer eyJhbG...

        │``````

        │   Body:

        │   {

        │     "msisdn": "996801234567",

        │     "userId": 1,**Файлы задействованы:****Файлы задействованы:**

        │     "pricePlanId": 1

        │   }- RequestBuilder.java- ✅ `RequestBuilder.java`

        │

        │   ← HTTP 201 Created- TestConfig.java- ✅ `TestConfig.java`

        │   {

        │     "code": "OK",- ProfileCreateRequest.java (сериализация)- ✅ `ProfileCreateRequest.java` (сериализация)

        │     "content": {

        │       "id": 42,- pom.xml (REST Assured 5.5.0, Jackson 2.18.2)- ✅ `pom.xml` (REST Assured 5.5.0, Jackson 2.18.2)

        │       "msisdn": "996801234567",

        │       "userId": 1,

        │       "pricePlanId": 1,

        │       "status": "ACTIVE",#### Шаг 5: Отправка запроса и получение ответа#### 🎬 Шаг 5: Отправка запроса и получение ответа

        │       "createdAt": "2025-11-02T10:30:00",

        │       "updatedAt": "2025-11-02T10:30:00"

        │     }

        │   }**Что происходит:****Что происходит:**

        │

        ▼

┌─────────────────────────────────────────────────────────────────┐

│ 7. ВАЛИДАЦИЯ ОТВЕТА                                             │1. REST Assured отправляет HTTP POST запрос на сервер1. REST Assured отправляет HTTP POST запрос на сервер

└─────────────────────────────────────────────────────────────────┘

        │2. Сервер обрабатывает запрос2. Сервер обрабатывает запрос

        ├─→ ApiAssertions.assertOkResponse(response)

        │   ├─→ assertStatus(response, 200)3. Возвращается HTTP ответ:3. Возвращается HTTP ответ:

        │   │   ├─→ actual: 201

        │   │   └─→ expected: 200 (по спеке)

        │   │       └─→ AssertionError!

        │   │           БАГ API НАЙДЕН!```http```http

        │   │

        │   ├─→ assertEquals("code", "OK")HTTP/1.1 201 CreatedHTTP/1.1 201 Created

        │   └─→ assertNotNull("content")

        │Content-Type: application/jsonContent-Type: application/json

        ▼

┌─────────────────────────────────────────────────────────────────┐

│ 8. ИЗВЛЕЧЕНИЕ ДАННЫХ (если бы прошёл)                          │

└─────────────────────────────────────────────────────────────────┘{{

        │

        ├─→ ResponseExtractor.extractId(response)  "code": "OK",  "code": "OK",

        │   └─→ jsonPath().getLong("content.id")

        │       └─→ return 42L  "content": {  "content": {

        │

        ▼    "id": 42,    "id": 42,

┌─────────────────────────────────────────────────────────────────┐

│ 9. CLEANUP (если бы прошёл)                                     │    "msisdn": "996801234567",    "msisdn": "996801234567",

└─────────────────────────────────────────────────────────────────┘

        │    "userId": 1,    "userId": 1,

        ├─→ url(TestConfig.PROFILE_DELETE, createdId)

        │   └─→ "/api/admin/profile/delete/42"    "pricePlanId": 1,    "pricePlanId": 1,

        │

        └─→ RequestBuilder.authorized(adminToken)    "status": "ACTIVE",    "status": "ACTIVE",

            └─→ .delete(url)

                └─→ DELETE /api/admin/profile/delete/42    "createdAt": "2025-11-02T10:30:00",    "createdAt": "2025-11-02T10:30:00",

```

    "updatedAt": "2025-11-02T10:30:00"    "updatedAt": "2025-11-02T10:30:00"

---

  }  }

### Полная карта зависимостей файлов

}}

```

testCreateProfile_Success()``````

│

├── ProfileApiTest.java (тест)

│   └── extends BaseApiTest.java

│4. REST Assured парсит ответ в объект `Response`4. REST Assured парсит ответ в объект `Response`

├── BaseApiTest.java (базовый класс)

│   ├── globalSetup()

│   ├── getAdminToken()

│   └── url()**Файлы задействованы:****Файлы задействованы:**

│

├── TestConfig.java (конфигурация)- pom.xml (REST Assured HTTP клиент)- ✅ `pom.xml` (REST Assured HTTP клиент)

│   ├── BASE_URL

│   ├── ADMIN_USERNAME

│   ├── ADMIN_PASSWORD

│   ├── AUTH_SIGN_IN#### Шаг 6: Проверка статус-кода#### 🎬 Шаг 6: Проверка статус-кода

│   ├── AUTH_REGISTER

│   ├── PROFILE_CREATE

│   └── PROFILE_DELETE

│```java```java

├── utils/RequestBuilder.java (HTTP запросы)

│   ├── authorized(token)ApiAssertions.assertOkResponse(response);ApiAssertions.assertOkResponse(response);

│   └── unauthorized()

│``````

├── utils/ApiAssertions.java (проверки)

│   ├── assertOkResponse()

│   ├── assertStatus()

│   └── safeBody()**Файл:** `utils/ApiAssertions.java`**Файл:** `utils/ApiAssertions.java`

│

├── utils/ResponseExtractor.java (извлечение данных)

│   ├── extractId()

│   ├── extractToken()```java```java

│   ├── extractContent()

│   └── extractContentList()public class ApiAssertions {public class ApiAssertions {

│

├── utils/TestDataGenerator.java (генерация данных)    public static void assertOkResponse(Response response) {    public static void assertOkResponse(Response response) {

│   ├── generateMsisdn()

│   ├── generateFirstName()        assertStatus(response, 200); // ← Ожидаем 200        assertStatus(response, 200); // ← Ожидаем 200

│   ├── generateLastName()

│   └── generateTelegramChatId()                

│

├── dto/request/ProfileCreateRequest.java (request DTO)        String body = safeBody(response);        String body = safeBody(response);

│   ├── @JsonProperty fields

│   ├── getters/setters                

│   └── Builder pattern

│        Assert.assertEquals(        Assert.assertEquals(

├── dto/request/AuthSignInRequest.java (auth request)

│   └── Builder pattern            response.jsonPath().getString("code"),             response.jsonPath().getString("code"), 

│

├── dto/request/AuthSignUpRequest.java (register request)            "OK",             "OK", 

│   └── Builder pattern

│            "code != OK. Body: " + body            "code != OK. Body: " + body

├── dto/response/ProfileDto.java (response DTO)

│   ├── @JsonProperty fields        );        );

│   └── getters/setters

│                

└── pom.xml (зависимости)

    ├── TestNG 7.10.2        Assert.assertNotNull(        Assert.assertNotNull(

    ├── REST Assured 5.5.0

    ├── Jackson 2.18.2            response.jsonPath().get("content"),             response.jsonPath().get("content"), 

    ├── jackson-datatype-jsr310 2.18.2

    └── Datafaker 2.4.2            "content is null. Body: " + body            "content is null. Body: " + body

```

        );        );

---

    }    }

### Последовательность вызовов (Call Stack)

        

```

1. TestNG запускает тест    private static void assertStatus(Response response, int expected) {    private static void assertStatus(Response response, int expected) {

   └─→ ProfileApiTest.testCreateProfile_Success()

        Assert.assertEquals(        Assert.assertEquals(

2. Генерация MSISDN

   └─→ TestDataGenerator.generateMsisdn()            response.getStatusCode(),             response.getStatusCode(), 

       └─→ Faker.number().digits(7)

            expected,             expected, 

3. Построение Request DTO

   └─→ ProfileCreateRequest.builder()            "Unexpected status. Body: " + safeBody(response)            "Unexpected status. Body: " + safeBody(response)

       └─→ .msisdn().userId().pricePlanId().build()

        );        );

4. Построение HTTP запроса

   └─→ RequestBuilder.authorized(adminToken)    }    }

       └─→ given().contentType(JSON).header("Authorization", ...)

}}

5. Добавление body

   └─→ .body(ProfileCreateRequest)``````

       └─→ Jackson.serialize(ProfileCreateRequest → JSON)



6. Отправка запроса

   └─→ .post(TestConfig.PROFILE_CREATE)**Что происходит:****Что происходит:**

       └─→ REST Assured HTTP POST

           └─→ Сервер возвращает Response (201 Created)



7. Проверка ответа1. **assertStatus(200)** проверяет код ответа1. **assertStatus(200)** проверяет код ответа

   └─→ ApiAssertions.assertOkResponse(response)

       └─→ assertStatus(response, 200)   - Ожидается: 200   - Ожидается: 200

           └─→ TestNG Assert.assertEquals(201, 200)

               └─→ AssertionError: expected 200, got 201   - Реально: 201   - Реально: 201 ❌

                   └─→ ТЕСТ ПАДАЕТ = БАГ НАЙДЕН

   - ТЕСТ ПАДАЕТ → найден БАГ API   - **ТЕСТ ПАДАЕТ** → найден БАГ API!

8. (Не выполняется из-за падения)

   └─→ ResponseExtractor.extractId(response)



9. (Не выполняется из-за падения)2. TestNG выбрасывает `AssertionError`:2. TestNG выбрасывает `AssertionError`:

   └─→ DELETE cleanup

```   ```   ```



---   java.lang.AssertionError: Unexpected status. Expected: 200, Actual: 201   java.lang.AssertionError: Unexpected status. Expected: 200, Actual: 201



### Ключевые моменты   ```   ```



1. **Один тест = 14 файлов**

   - 3 базовых класса

   - 4 утилиты**Файлы задействованы:****Файлы задействованы:**

   - 4 DTOs

   - 1 конфигурация- ApiAssertions.java- ✅ `ApiAssertions.java`

   - 1 тест

   - 1 pom.xml- pom.xml (TestNG assertions)- ✅ `pom.xml` (TestNG assertions)



2. **Каждый файл имеет одну ответственность**

   - TestConfig → только константы

   - RequestBuilder → только HTTP запросы#### Шаг 7: Извлечение данных (если тест прошёл бы)#### 🎬 Шаг 7: Извлечение данных (если тест прошёл бы)

   - ApiAssertions → только проверки

   - DTOs → только данные



3. **Падение теста = найденный баг**```java```java

   - Тест ожидает 200 (по спецификации)

   - API возвращает 201 (реальность)Long createdId = ResponseExtractor.extractId(response);Long createdId = ResponseExtractor.extractId(response);

   - Несоответствие = баг в API

``````

4. **Переиспользование кода**

   - adminToken получается один раз в @BeforeClass

   - Утилиты используются всеми тестами

   - DTOs общие для всех запросов/ответов**Файл:** `utils/ResponseExtractor.java`**Файл:** `utils/ResponseExtractor.java`



---



## Архитектура фреймворка```java```java



### Принципы построенияpublic class ResponseExtractor {public class ResponseExtractor {



1. **Spec-First Approach** - все тесты строго по OpenAPI спецификации    public static Long extractId(Response response) {    public static Long extractId(Response response) {

2. **Fail on API Bugs** - падающий тест = найденный баг в API

3. **Flat Structure** - минимум вложенности, плоская структура пакетов        return response.jsonPath().getLong("content.id");        return response.jsonPath().getLong("content.id");

4. **Reusable Utilities** - переиспользуемые компоненты вместо дублирования

5. **Clean Code** - простой и понятный код без over-engineering        // Вернёт: 42L        // Вернёт: 42L



### Диаграмма зависимостей    }    }



```}}

api.json (OpenAPI Spec)

    ↓``````

TestConfig → BaseApiTest → {Balance|Counter|Profile}ApiTest

                ↓                           ↓

            Utils (RequestBuilder,      Request/Response DTOs

            ApiAssertions, etc.)**Что происходит:****Что происходит:**

```



---

1. JsonPath парсит JSON ответа1. JsonPath парсит JSON ответа

## Базовые классы

2. Извлекает поле `content.id`2. Извлекает поле `content.id`

### TestConfig

3. Конвертирует в `Long`3. Конвертирует в `Long`

**Назначение:** Централизованная конфигурация всех URL и credentials.



**Расположение:** `src/test/java/auc/TestConfig.java`

**Файлы задействованы:****Файлы задействованы:**

**Поля:**

- ResponseExtractor.java- ✅ `ResponseExtractor.java`

```java

public static final String BASE_URL = "http://195.38.164.168:7173";- pom.xml (REST Assured JsonPath)- ✅ `pom.xml` (REST Assured JsonPath)

public static final String ADMIN_USERNAME = "superuser";

public static final String ADMIN_PASSWORD = "Admin123!@#";



public static final String AUTH_REGISTER = "/api/auth/sign_up";#### Шаг 8: Cleanup (если тест прошёл бы)#### 🎬 Шаг 8: Cleanup (если тест прошёл бы)

public static final String AUTH_SIGN_IN = "/api/auth/sign_in";



public static final String BALANCE_GET_BY_ID = "/api/balance/{id}";

public static final String BALANCE_GET_ALL = "/api/balance/all";```java```java

public static final String BALANCE_UPDATE = "/api/balance/update/{id}";

RequestBuilder.authorized(adminToken)RequestBuilder.authorized(adminToken)

public static final String PROFILE_CREATE = "/api/admin/profile/create";

public static final String PROFILE_UPDATE = "/api/admin/profile/update/{id}";    .delete(url(TestConfig.PROFILE_DELETE, createdId));    .delete(url(TestConfig.PROFILE_DELETE, createdId));

public static final String PROFILE_GET_BY_ID = "/api/admin/profile/{id}";

public static final String PROFILE_GET_ALL = "/api/admin/profile/all";``````

public static final String PROFILE_GET_ALL_REMOVED = "/api/admin/profile/all-removed";

public static final String PROFILE_DELETE = "/api/admin/profile/delete/{id}";



public static final String COUNTER_GET_BY_ID = "/api/admin/counter/{id}";**Файл:** `BaseApiTest.java`**Файл:** `BaseApiTest.java`

public static final String COUNTER_GET_ALL = "/api/admin/counter/all";

public static final String COUNTER_GET_ALL_ACTIVE = "/api/admin/counter/all-active";

```

```java```java

### BaseApiTest

protected String url(String template, Object... params) {protected String url(String template, Object... params) {

**Назначение:** Базовый класс для всех тестов с общей логикой.

    String result = template;    String result = template;

**Расположение:** `src/test/java/auc/BaseApiTest.java`

    for (Object param : params) {    for (Object param : params) {

**Поля:**

        result = result.replaceFirst("\\{[^}]+}", String.valueOf(param));        result = result.replaceFirst("\\{[^}]+}", String.valueOf(param));

```java

protected static String adminToken;    }    }

```

    return result;    return result;

**Методы:**

    // "/api/admin/profile/delete/{id}" → "/api/admin/profile/delete/42"    // "/api/admin/profile/delete/{id}" → "/api/admin/profile/delete/42"

```java

@BeforeClass}}

public void globalSetup() {

    RestAssured.baseURI = TestConfig.BASE_URL;``````

    adminToken = getAdminToken();

}



protected String url(String template, Object... params) {**Что происходит:****Что происходит:**

    String result = template;

    for (Object param : params) {

        result = result.replaceFirst("\\{[^}]+}", String.valueOf(param));

    }1. `url()` подставляет ID в шаблон URL1. `url()` подставляет ID в шаблон URL

    return result;

}2. `RequestBuilder.authorized()` создаёт DELETE запрос2. `RequestBuilder.authorized()` создаёт DELETE запрос



private String getAdminToken() {3. Отправляется DELETE для очистки тестовых данных3. Отправляется DELETE для очистки тестовых данных

    AuthSignInRequest signIn = AuthSignInRequest.builder()

        .username(TestConfig.ADMIN_USERNAME)

        .password(TestConfig.ADMIN_PASSWORD)

        .build();**Файлы задействованы:****Файлы задействованы:**



    Response signInResponse = RequestBuilder.unauthorized().body(signIn)- BaseApiTest.java- ✅ `BaseApiTest.java`

        .post(TestConfig.AUTH_SIGN_IN);

- TestConfig.java- ✅ `TestConfig.java`

    if (signInResponse.getStatusCode() == 200) {

        return ResponseExtractor.extractToken(signInResponse);- RequestBuilder.java- ✅ `RequestBuilder.java`

    }



    AuthSignUpRequest signUp = AuthSignUpRequest.builder()

        .username(TestConfig.ADMIN_USERNAME)------

        .password(TestConfig.ADMIN_PASSWORD)

        .firstName(TestDataGenerator.generateFirstName())

        .lastName(TestDataGenerator.generateLastName())

        .telegramChatId(TestDataGenerator.generateTelegramChatId())### Визуализация полного воркфлоу### 📊 Визуализация полного воркфлоу

        .build();



    RequestBuilder.unauthorized().body(signUp)

        .post(TestConfig.AUTH_REGISTER);``````



    Response secondSignIn = RequestBuilder.unauthorized().body(signIn)┌─────────────────────────────────────────────────────────────────┐┌─────────────────────────────────────────────────────────────────┐

        .post(TestConfig.AUTH_SIGN_IN);

│ 1. ПОДГОТОВКА (@BeforeClass)                                    ││ 1. ПОДГОТОВКА (@BeforeClass)                                    │

    return ResponseExtractor.extractToken(secondSignIn);

}└─────────────────────────────────────────────────────────────────┘└─────────────────────────────────────────────────────────────────┘

```

        │        │

---

        ├─→ ProfileApiTest.setup()        ├─→ ProfileApiTest.setup()

## Утилиты

        │   └─→ BaseApiTest.globalSetup()        │   └─→ BaseApiTest.globalSetup()

### RequestBuilder

        │       ├─→ TestConfig.BASE_URL        │       ├─→ TestConfig.BASE_URL ────────────────┐

**Назначение:** Построение HTTP запросов с авторизацией.

        │       └─→ getAdminToken()        │       └─→ getAdminToken()                     │

**Расположение:** `src/test/java/auc/utils/RequestBuilder.java`

        │           ├─→ AuthSignInRequest.builder()        │           ├─→ AuthSignInRequest.builder() ────┤

**Методы:**

        │           ├─→ RequestBuilder.unauthorized()        │           ├─→ RequestBuilder.unauthorized() ──┤

```java

public static RequestSpecification authorized(String token) {        │           ├─→ TestConfig.AUTH_SIGN_IN        │           ├─→ TestConfig.AUTH_SIGN_IN ────────┤

    return given()

        .contentType(JSON)        │           ├─→ ResponseExtractor.extractToken()        │           ├─→ ResponseExtractor.extractToken() │

        .header("Authorization", "Bearer " + token);

}        │           │   (если 401):        │           │   (если 401):                      │



public static RequestSpecification unauthorized() {        │           ├─→ AuthSignUpRequest.builder()        │           ├─→ AuthSignUpRequest.builder() ────┤

    return given().contentType(JSON);

}        │           ├─→ TestDataGenerator.*        │           ├─→ TestDataGenerator.* ────────────┤

```

        │           └─→ POST /api/auth/sign_up        │           └─→ POST /api/auth/sign_up ─────────┘

### ApiAssertions

        │        │

**Назначение:** Стандартизированные проверки HTTP ответов.

        ▼        ▼

**Расположение:** `src/test/java/auc/utils/ApiAssertions.java`

┌─────────────────────────────────────────────────────────────────┐┌─────────────────────────────────────────────────────────────────┐

**Методы:**

│ 2. ЗАПУСК ТЕСТА (@Test)                                         ││ 2. ЗАПУСК ТЕСТА (@Test)                                         │

```java

public static void assertOkResponse(Response response) {└─────────────────────────────────────────────────────────────────┘└─────────────────────────────────────────────────────────────────┘

    assertStatus(response, 200);

    String body = safeBody(response);        │        │

    Assert.assertEquals(response.jsonPath().getString("code"), "OK", 

        "code != OK. Body: " + body);        ├─→ testCreateProfile_Success()        ├─→ testCreateProfile_Success()

    Assert.assertNotNull(response.jsonPath().get("content"), 

        "content is null. Body: " + body);        │        │

}

        ▼        ▼

public static void assertForbidden(Response response) {

    assertStatus(response, 403);┌─────────────────────────────────────────────────────────────────┐┌─────────────────────────────────────────────────────────────────┐

}

│ 3. ГЕНЕРАЦИЯ ДАННЫХ                                             ││ 3. ГЕНЕРАЦИЯ ДАННЫХ                                             │

public static void assertNotFound(Response response) {

    assertStatus(response, 404);└─────────────────────────────────────────────────────────────────┘└─────────────────────────────────────────────────────────────────┘

}

        │        │

public static void assertBadRequest(Response response) {

    assertStatus(response, 400);        ├─→ TestDataGenerator.generateMsisdn()        ├─→ TestDataGenerator.generateMsisdn()

}

        │   └─→ Faker.number().digits(7)        │   └─→ Faker.number().digits(7) ───────────────┐

private static void assertStatus(Response response, int expected) {

    Assert.assertEquals(response.getStatusCode(), expected,         │       └─→ return "99680" + "1234567"        │       └─→ return "99680" + "1234567" ─────────┤

        "Unexpected status. Body: " + safeBody(response));

}        │        │                                                │



private static String safeBody(Response response) {        ▼        ▼                                                │

    try {

        String s = response.asString();┌─────────────────────────────────────────────────────────────────┐┌─────────────────────────────────────────────────────────────────┐

        return s == null ? "<null>" : 

            (s.length() > 1000 ? s.substring(0, 1000) + "..." : s);│ 4. ПОСТРОЕНИЕ REQUEST DTO                                       ││ 4. ПОСТРОЕНИЕ REQUEST DTO                                       │

    } catch (Exception e) {

        return "<unavailable: " + e.getMessage() + ">";└─────────────────────────────────────────────────────────────────┘└─────────────────────────────────────────────────────────────────┘

    }

}        │        │

```

        ├─→ ProfileCreateRequest.builder()        ├─→ ProfileCreateRequest.builder()

### ResponseExtractor

        │   ├─→ .msisdn("996801234567")        │   ├─→ .msisdn("996801234567") ────────────────┤

**Назначение:** Извлечение типизированных данных из JSON ответов.

        │   ├─→ .userId(1L)        │   ├─→ .userId(1L) ────────────────────────────┤

**Расположение:** `src/test/java/auc/utils/ResponseExtractor.java`

        │   ├─→ .pricePlanId(1L)        │   ├─→ .pricePlanId(1L) ───────────────────────┤

**Методы:**

        │   └─→ .build()        │   └─→ .build() ───────────────────────────────┤

```java

public static <T> T extractContent(Response response, Class<T> type) {        │        │                                                │

    return response.jsonPath().getObject("content", type);

}        ▼        ▼                                                │



public static <T> List<T> extractContentList(Response response, Class<T> type) {┌─────────────────────────────────────────────────────────────────┐┌─────────────────────────────────────────────────────────────────┐

    return response.jsonPath().getList("content", type);

}│ 5. ПОСТРОЕНИЕ HTTP ЗАПРОСА                                      ││ 5. ПОСТРОЕНИЕ HTTP ЗАПРОСА                                      │



public static Long extractId(Response response) {└─────────────────────────────────────────────────────────────────┘└─────────────────────────────────────────────────────────────────┘

    return response.jsonPath().getLong("content.id");

}        │        │



public static String extractToken(Response response) {        ├─→ RequestBuilder.authorized(adminToken)        ├─→ RequestBuilder.authorized(adminToken)

    return response.jsonPath().getString("content.token");

}        │   ├─→ given()        │   ├─→ given() ────────────────────────────────┤ REST

```

        │   ├─→ .contentType(JSON)        │   ├─→ .contentType(JSON) ─────────────────────┤ Assured

### TestDataGenerator

        │   ├─→ .header("Authorization", "Bearer ...")        │   ├─→ .header("Authorization", "Bearer ...") ─┤ 5.5.0

**Назначение:** Генерация валидных тестовых данных.

        │   └─→ .body(body)        │   └─→ .body(body) ────────────────────────────┤

**Расположение:** `src/test/java/auc/utils/TestDataGenerator.java`

        │       └─→ Jackson сериализует DTO в JSON        │       └─→ Jackson сериализует DTO в JSON ─────┤ Jackson

**Методы:**

        │        │                                                │ 2.18.2

```java

public static String generateMsisdn() {        ├─→ .post(TestConfig.PROFILE_CREATE)        ├─→ .post(TestConfig.PROFILE_CREATE)            │

    return "99680" + faker.number().digits(7);

}        │   └─→ URL: "/api/admin/profile/create"        │   └─→ URL: "/api/admin/profile/create" ───────┤



public static String generateFirstName() {        │        │                                                │

    return faker.name().firstName();

}        ▼        ▼                                                │



public static String generateLastName() {┌─────────────────────────────────────────────────────────────────┐┌─────────────────────────────────────────────────────────────────┐

    return faker.name().lastName();

}│ 6. ОТПРАВКА НА СЕРВЕР                                           ││ 6. ОТПРАВКА НА СЕРВЕР                                           │



public static String generateTelegramChatId() {└─────────────────────────────────────────────────────────────────┘└─────────────────────────────────────────────────────────────────┘

    return String.valueOf(faker.number().numberBetween(100000000, 999999999));

}        │        │



public static Double generateBalanceAmount() {        │   HTTP POST →  http://195.38.164.168:7173        │   HTTP POST →  http://195.38.164.168:7173     │

    return faker.number().randomDouble(2, 100, 5000);

}        │                /api/admin/profile/create        │                /api/admin/profile/create       │

```

        │        │                                                │

---

        │   Headers:        │   Headers:                                     │

## DTOs

        │   - Content-Type: application/json        │   - Content-Type: application/json             │

### Request DTOs

        │   - Authorization: Bearer eyJhbG...        │   - Authorization: Bearer eyJhbG...            │

#### AuthSignInRequest

        │        │                                                │

```json

{        │   Body:        │   Body:                                        │

    "username": "string",

    "password": "string"        │   {        │   {                                            │

}

```        │     "msisdn": "996801234567",        │     "msisdn": "996801234567",                  │



#### AuthSignUpRequest        │     "userId": 1,        │     "userId": 1,                               │



```json        │     "pricePlanId": 1        │     "pricePlanId": 1                           │

{

    "username": "string",        │   }        │   }                                            │

    "password": "string",

    "firstName": "string",        │        │                                                │

    "lastName": "string",

    "telegramChatId": "string"        │   ← HTTP 201 Created        │   ← HTTP 201 Created                           │

}

```        │   {        │   {                                            │



#### BalanceUpdateRequest        │     "code": "OK",        │     "code": "OK",                              │



```json        │     "content": {        │     "content": {                               │

{

    "amount": 1500.50        │       "id": 42,        │       "id": 42,                                │

}

```        │       "msisdn": "996801234567",        │       "msisdn": "996801234567",                │



#### ProfileCreateRequest        │       "userId": 1,        │       "userId": 1,                             │



```json        │       "pricePlanId": 1,        │       "pricePlanId": 1,                        │

{

    "msisdn": "996801234567",        │       "status": "ACTIVE",        │       "status": "ACTIVE",                      │

    "userId": 1,

    "pricePlanId": 1        │       "createdAt": "2025-11-02T10:30:00",        │       "createdAt": "2025-11-02T10:30:00",      │

}

```        │       "updatedAt": "2025-11-02T10:30:00"        │       "updatedAt": "2025-11-02T10:30:00"       │



### Response DTOs        │     }        │     }                                          │



#### BalanceDto        │   }        │   }                                            │



```json        │        │                                                │

{

    "id": 1,        ▼        ▼                                                │

    "amount": 1500.50,

    "userId": 1,┌─────────────────────────────────────────────────────────────────┐┌─────────────────────────────────────────────────────────────────┐

    "currency": "USD",

    "createdAt": "2025-11-02T10:00:00",│ 7. ВАЛИДАЦИЯ ОТВЕТА                                             ││ 7. ВАЛИДАЦИЯ ОТВЕТА                                             │

    "updatedAt": "2025-11-02T10:00:00"

}└─────────────────────────────────────────────────────────────────┘└─────────────────────────────────────────────────────────────────┘

```

        │        │

#### CounterDto

        ├─→ ApiAssertions.assertOkResponse(response)        ├─→ ApiAssertions.assertOkResponse(response)

```json

{        │   ├─→ assertStatus(response, 200)        │   ├─→ assertStatus(response, 200) ────────────┤

    "id": 1,

    "profileId": 1,        │   │   ├─→ actual: 201        │   │   ├─→ actual: 201 ❌                       │ TestNG

    "megabytes": 5000,

    "seconds": 3600,        │   │   └─→ expected: 200 (по спеке)        │   │   └─→ expected: 200 (по спеке)            │ 7.10.2

    "sms": 100,

    "status": "ACTIVE",        │   │       └─→ AssertionError!        │   │       └─→ AssertionError! ─────────────────┤

    "createdAt": "2025-11-02T10:00:00",

    "updatedAt": "2025-11-02T10:00:00"        │   │           БАГ API НАЙДЕН!        │   │           БАГ API НАЙДЕН! 🐛               │

}

```        │   │        │   │                                            │



#### ProfileDto        │   ├─→ assertEquals("code", "OK")        │   ├─→ assertEquals("code", "OK") ──────────────┤



```json        │   └─→ assertNotNull("content")        │   └─→ assertNotNull("content") ────────────────┘

{

    "id": 1,        │        │

    "msisdn": "996801234567",

    "userId": 1,        ▼        ▼

    "pricePlanId": 1,

    "status": "ACTIVE",┌─────────────────────────────────────────────────────────────────┐┌─────────────────────────────────────────────────────────────────┐

    "createdAt": "2025-11-02T10:00:00",

    "updatedAt": "2025-11-02T10:00:00"│ 8. ИЗВЛЕЧЕНИЕ ДАННЫХ (если бы прошёл)                          ││ 8. ИЗВЛЕЧЕНИЕ ДАННЫХ (если бы прошёл)                          │

}

```└─────────────────────────────────────────────────────────────────┘└─────────────────────────────────────────────────────────────────┘



---        │        │



## Тесты        ├─→ ResponseExtractor.extractId(response)        ├─→ ResponseExtractor.extractId(response)



### BalanceApiTest - 9 тестов        │   └─→ jsonPath().getLong("content.id")        │   └─→ jsonPath().getLong("content.id")



1. `testGetBalanceById_Success` - успешное получение баланса по ID        │       └─→ return 42L        │       └─→ return 42L

2. `testGetBalanceById_NotFound` - получение несуществующего баланса

3. `testGetBalanceById_Unauthorized` - получение без токена        │        │

4. `testGetAllBalances_Success` - получение списка балансов

5. `testGetAllBalances_Unauthorized` - получение списка без токена        ▼        ▼

6. `testUpdateBalance_AsPerSpecification` - обновление по спецификации (БАГ)

7. `testUpdateBalance_NotFound` - обновление несуществующего баланса┌─────────────────────────────────────────────────────────────────┐┌─────────────────────────────────────────────────────────────────┐

8. `testUpdateBalance_Unauthorized` - обновление без токена

9. `testUpdateBalance_MissingAmount` - валидация обязательного поля│ 9. CLEANUP (если бы прошёл)                                     ││ 9. CLEANUP (если бы прошёл)                                     │



### CounterApiTest - 7 тестов└─────────────────────────────────────────────────────────────────┘└─────────────────────────────────────────────────────────────────┘



1. `testGetCounterById_Success` - успешное получение счётчика        │        │

2. `testGetCounterById_NotFound` - получение несуществующего счётчика

3. `testGetCounterById_Unauthorized` - получение без токена        ├─→ url(TestConfig.PROFILE_DELETE, createdId)        ├─→ url(TestConfig.PROFILE_DELETE, createdId)

4. `testGetAllCounters_Success` - получение всех счётчиков

5. `testGetAllCounters_Unauthorized` - получение всех без токена        │   └─→ "/api/admin/profile/delete/42"        │   └─→ "/api/admin/profile/delete/42"

6. `testGetAllActiveCounters_Success` - получение активных счётчиков (БАГ)

7. `testGetAllActiveCounters_Unauthorized` - получение активных без токена        │        │



### ProfileApiTest - 14 тестов        └─→ RequestBuilder.authorized(adminToken)        └─→ RequestBuilder.authorized(adminToken)



1. `testCreateProfile_Success` - успешное создание профиля (БАГ)            └─→ .delete(url)            └─→ .delete(url)

2. `testCreateProfile_DuplicateMsisdn` - создание с дубликатом MSISDN

3. `testGetProfileById_Success` - получение профиля по ID                └─→ DELETE /api/admin/profile/delete/42                └─→ DELETE /api/admin/profile/delete/42

4. `testGetProfileById_NotFound` - получение несуществующего профиля

5. `testGetAllProfiles_Success` - получение списка профилей``````

6. `testUpdateProfile_Success` - обновление профиля

7. `testDeleteProfile_StatusCode` - удаление профиля (БАГ)

8. `testGetAllRemovedProfiles_Success` - получение удалённых профилей

9. `testCreateProfile_Unauthorized` - создание без токена------

10. `testGetProfileById_Unauthorized` - получение без токена

11. `testGetAllProfiles_Unauthorized` - получение списка без токена

12. `testUpdateProfile_Unauthorized` - обновление без токена

13. `testDeleteProfile_Unauthorized` - удаление без токена### Полная карта зависимостей файлов### 📦 Полная карта зависимостей файлов

14. `testGetAllRemovedProfiles_Unauthorized` - получение удалённых без токена



---

``````

## Тест-кейсы

testCreateProfile_Success()testCreateProfile_Success()

Подробное описание всех 30 тест-кейсов с примерами запросов/ответов, ожидаемыми результатами и процедурами воспроизведения.

││

---

├── ProfileApiTest.java (тест)├── ProfileApiTest.java ──────────────────────┐ (тест)

## Баг-репорты

│   └── extends BaseApiTest.java│   └── extends BaseApiTest.java              │

### БАГ #1: Balance Update - Некорректная обработка requestBody

││                                              │

**Приоритет:** Высокий

├── BaseApiTest.java (базовый класс)├── BaseApiTest.java ─────────────────────────┤ (базовый класс)

**Эндпоинт:** `PUT /api/balance/update/{id}`

│   ├── globalSetup()│   ├── globalSetup()                         │

**Спецификация:** Ожидает requestBody с `{"amount": <double>}`, возвращает 200

│   ├── getAdminToken()│   ├── getAdminToken()                       │

**Реальность:** API возвращает 400 Bad Request

│   └── url()│   └── url()                                 │

**Код воспроизведения:**

││                                              │

```java

BalanceUpdateRequest body = BalanceUpdateRequest.builder()├── TestConfig.java (конфигурация)├── TestConfig.java ──────────────────────────┤ (конфигурация)

    .amount(1500.50)

    .build();│   ├── BASE_URL│   ├── BASE_URL                              │



Response response = RequestBuilder.authorized(adminToken)│   ├── ADMIN_USERNAME│   ├── ADMIN_USERNAME                        │

    .body(body)

    .put("/api/balance/update/1");│   ├── ADMIN_PASSWORD│   ├── ADMIN_PASSWORD                        │



// Ожидается: 200│   ├── AUTH_SIGN_IN│   ├── AUTH_SIGN_IN                          │

// Реально: 400

```│   ├── AUTH_REGISTER│   ├── AUTH_REGISTER                         │



### БАГ #2: Profile Create - Неверный HTTP статус│   ├── PROFILE_CREATE│   ├── PROFILE_CREATE                        │



**Приоритет:** Средний│   └── PROFILE_DELETE│   └── PROFILE_DELETE                        │



**Эндпоинт:** `POST /api/admin/profile/create`││                                              │



**Спецификация:** Должен возвращать 200 OK├── utils/RequestBuilder.java (HTTP запросы)├── utils/RequestBuilder.java ────────────────┤ (HTTP запросы)



**Реальность:** API возвращает 201 Created│   ├── authorized(token)│   ├── authorized(token)                     │



### БАГ #3: Profile Delete - Неверный HTTP статус│   └── unauthorized()│   └── unauthorized()                        │



**Приоритет:** Средний││                                              │



**Эндпоинт:** `DELETE /api/admin/profile/delete/{id}`├── utils/ApiAssertions.java (проверки)├── utils/ApiAssertions.java ─────────────────┤ (проверки)



**Спецификация:** Должен возвращать 200 OK│   ├── assertOkResponse()│   ├── assertOkResponse()                    │



**Реальность:** API возвращает 204 No Content│   ├── assertStatus()│   ├── assertStatus()                        │



### БАГ #4: Counter All Active - Некорректный статус│   └── safeBody()│   └── safeBody()                            │



**Приоритет:** Средний││                                              │



**Эндпоинт:** `GET /api/admin/counter/all-active`├── utils/ResponseExtractor.java (извлечение данных)├── utils/ResponseExtractor.java ─────────────┤ (извлечение данных)



**Спецификация:** Должен возвращать 200 OK│   ├── extractId()│   ├── extractId()                           │



**Реальность:** API возвращает 204 No Content│   ├── extractToken()│   ├── extractToken()                        │



---│   ├── extractContent()│   ├── extractContent()                      │



## Best Practices│   └── extractContentList()│   └── extractContentList()                  │



### Используйте утилиты вместо дублирования││                                              │



Правильно:├── utils/TestDataGenerator.java (генерация данных)├── utils/TestDataGenerator.java ─────────────┤ (генерация данных)



```java│   ├── generateMsisdn()│   ├── generateMsisdn()                      │

ApiAssertions.assertOkResponse(response);

```│   ├── generateFirstName()│   ├── generateFirstName()                   │



Неправильно:│   ├── generateLastName()│   ├── generateLastName()                    │



```java│   └── generateTelegramChatId()│   └── generateTelegramChatId()              │

response.then()

    .statusCode(200)││                                              │

    .body("code", equalTo("OK"))

    .body("content", notNullValue());├── dto/request/ProfileCreateRequest.java (request DTO)├── dto/request/ProfileCreateRequest.java ────┤ (request DTO)

```

│   ├── @JsonProperty fields│   ├── @JsonProperty fields                  │

### Генерируйте уникальные данные

│   ├── getters/setters│   ├── getters/setters                       │

Правильно:

│   └── Builder pattern│   └── Builder pattern                       │

```java

String msisdn = TestDataGenerator.generateMsisdn();││                                              │

```

├── dto/request/AuthSignInRequest.java (auth request)├── dto/request/AuthSignInRequest.java ───────┤ (auth request)

Неправильно:

│   └── Builder pattern│   └── Builder pattern                       │

```java

String msisdn = "996801234567"; // Может конфликтовать││                                              │

```

├── dto/request/AuthSignUpRequest.java (register request)├── dto/request/AuthSignUpRequest.java ───────┤ (register request)

### Cleanup после создания ресурсов

│   └── Builder pattern│   └── Builder pattern                       │

Правильно:

││                                              │

```java

Long createdId = ResponseExtractor.extractId(response);├── dto/response/ProfileDto.java (response DTO)├── dto/response/ProfileDto.java ─────────────┤ (response DTO)

RequestBuilder.authorized(adminToken)

    .delete(url(TestConfig.PROFILE_DELETE, createdId));│   ├── @JsonProperty fields│   ├── @JsonProperty fields                  │

```

│   └── getters/setters│   └── getters/setters                       │

### Информативные описания тестов

││                                              │

Правильно:

└── pom.xml (зависимости)└── pom.xml ──────────────────────────────────┘ (зависимости)

```java

@Test(priority = 1, description = "GET /api/balance/{id} - успешное получение баланса")    ├── TestNG 7.10.2    ├── TestNG 7.10.2

public void testGetBalanceById_Success() { }

```    ├── REST Assured 5.5.0    ├── REST Assured 5.5.0



### Проверяйте по спецификации    ├── Jackson 2.18.2    ├── Jackson 2.18.2



Правильно:    ├── jackson-datatype-jsr310 2.18.2    ├── jackson-datatype-jsr310 2.18.2



```java    └── Datafaker 2.4.2    └── Datafaker 2.4.2

ApiAssertions.assertOkResponse(response);

`````````



Неправильно:



```java------

Assert.assertEquals(response.getStatusCode(), 201); // Подстраиваемся под баг

```



---### Последовательность вызовов (Call Stack)### 🔄 Последовательность вызовов (Call Stack)



## Troubleshooting



### Тесты падают с 403 Forbidden``````



**Причина:** Не получен или истёк adminToken1. TestNG запускает тест1. TestNG запускает тест



**Решение:**   └─→ ProfileApiTest.testCreateProfile_Success()   └─→ ProfileApiTest.testCreateProfile_Success()



1. Проверьте credentials в TestConfig.java

2. Убедитесь, что API доступен

3. Проверьте консольный вывод на наличие ошибок авторизации2. Генерация MSISDN2. Генерация MSISDN



### Тесты падают с Connection Refused   └─→ TestDataGenerator.generateMsisdn()   └─→ TestDataGenerator.generateMsisdn()



**Причина:** API сервер недоступен       └─→ Faker.number().digits(7)       └─→ Faker.number().digits(7)



**Решение:**



```bash3. Построение Request DTO3. Построение Request DTO

curl http://195.38.164.168:7173/api/auth/sign_in

```   └─→ ProfileCreateRequest.builder()   └─→ ProfileCreateRequest.builder()



### Profile тесты падают с "MSISDN invalid pattern"       └─→ .msisdn().userId().pricePlanId().build()       └─→ .msisdn().userId().pricePlanId().build()



**Причина:** Неправильный формат MSISDN



**Решение:** MSISDN должен соответствовать `^99680\d{7}$` (всего 12 символов)4. Построение HTTP запроса4. Построение HTTP запроса


   └─→ RequestBuilder.authorized(adminToken)   └─→ RequestBuilder.authorized(adminToken)

       └─→ given().contentType(JSON).header("Authorization", ...)       └─→ given().contentType(JSON).header("Authorization", ...)



5. Добавление body5. Добавление body

   └─→ .body(ProfileCreateRequest)   └─→ .body(ProfileCreateRequest)

       └─→ Jackson.serialize(ProfileCreateRequest → JSON)       └─→ Jackson.serialize(ProfileCreateRequest → JSON)



6. Отправка запроса6. Отправка запроса

   └─→ .post(TestConfig.PROFILE_CREATE)   └─→ .post(TestConfig.PROFILE_CREATE)

       └─→ REST Assured HTTP POST       └─→ REST Assured HTTP POST

           └─→ Сервер возвращает Response (201 Created)           └─→ Сервер возвращает Response (201 Created)



7. Проверка ответа7. Проверка ответа

   └─→ ApiAssertions.assertOkResponse(response)   └─→ ApiAssertions.assertOkResponse(response)

       └─→ assertStatus(response, 200)       └─→ assertStatus(response, 200)

           └─→ TestNG Assert.assertEquals(201, 200)           └─→ TestNG Assert.assertEquals(201, 200)

               └─→ AssertionError: expected 200, got 201               └─→ ❌ AssertionError: expected 200, got 201

                   └─→ ТЕСТ ПАДАЕТ = БАГ НАЙДЕН                   └─→ ТЕСТ ПАДАЕТ = БАГ НАЙДЕН! 🐛



8. (Не выполняется из-за падения)8. (Не выполняется из-за падения)

   └─→ ResponseExtractor.extractId(response)   └─→ ResponseExtractor.extractId(response)



9. (Не выполняется из-за падения)9. (Не выполняется из-за падения)

   └─→ DELETE cleanup   └─→ DELETE cleanup

``````



------



### Ключевые моменты### 💡 Ключевые моменты



1. **Один тест = 14 файлов**1. **Один тест = 14 файлов**

   - 3 базовых класса   - 3 базовых класса

   - 4 утилиты   - 4 утилиты

   - 4 DTOs   - 4 DTOs

   - 1 конфигурация   - 1 конфигурация

   - 1 тест   - 1 тест

   - 1 pom.xml   - 1 pom.xml



2. **Каждый файл имеет одну ответственность**2. **Каждый файл имеет одну ответственность**

   - TestConfig → только константы   - TestConfig → только константы

   - RequestBuilder → только HTTP запросы   - RequestBuilder → только HTTP запросы

   - ApiAssertions → только проверки   - ApiAssertions → только проверки

   - DTOs → только данные   - DTOs → только данные



3. **Падение теста = найденный баг**3. **Падение теста = найденный баг**

   - Тест ожидает 200 (по спецификации)   - Тест ожидает 200 (по спецификации)

   - API возвращает 201 (реальность)   - API возвращает 201 (реальность)

   - Несоответствие = баг в API   - Несоответствие = баг в API



4. **Переиспользование кода**4. **Переиспользование кода**

   - adminToken получается один раз в @BeforeClass   - adminToken получается один раз в @BeforeClass

   - Утилиты используются всеми тестами   - Утилиты используются всеми тестами

   - DTOs общие для всех запросов/ответов   - DTOs общие для всех запросов/ответов



------



## Архитектура фреймворка## Архитектура фреймворка



### Принципы построения### Принципы построения



1. **Spec-First Approach** - все тесты строго по OpenAPI спецификации1. **Spec-First Approach** - все тесты строго по OpenAPI спецификации

2. **Fail on API Bugs** - падающий тест = найденный баг в API2. **Fail on API Bugs** - падающий тест = найденный баг в API

3. **Flat Structure** - минимум вложенности, плоская структура пакетов3. **Flat Structure** - минимум вложенности, плоская структура пакетов

4. **Reusable Utilities** - переиспользуемые компоненты вместо дублирования4. **Reusable Utilities** - переиспользуемые компоненты вместо дублирования

5. **Clean Code** - простой и понятный код без over-engineering5. **Clean Code** - простой и понятный код без over-engineering



### Диаграмма зависимостей### Диаграмма зависимостей



``````

api.json (OpenAPI Spec)api.json (OpenAPI Spec)

    ↓    ↓

TestConfig → BaseApiTest → {Balance|Counter|Profile}ApiTestTestConfig → BaseApiTest → {Balance|Counter|Profile}ApiTest

                ↓                           ↓                ↓                           ↓

            Utils (RequestBuilder,      Request/Response DTOs            Utils (RequestBuilder,      Request/Response DTOs

            ApiAssertions, etc.)            ApiAssertions, etc.)

``````



------



## Базовые классы## Базовые классы



### TestConfig### TestConfig



**Назначение:** Централизованная конфигурация всех URL и credentials.**Назначение:** Централизованная конфигурация всех URL и credentials.



**Расположение:** `src/test/java/auc/TestConfig.java`**Расположение:** `src/test/java/auc/TestConfig.java`



**Поля:****Поля:**



```java```java

public static final String BASE_URL = "http://195.38.164.168:7173";// Базовый URL API (можно переопределить через -DbaseUrl)

public static final String ADMIN_USERNAME = "superuser";public static final String BASE_URL = "http://195.38.164.168:7173";

public static final String ADMIN_PASSWORD = "Admin123!@#";

// Credentials администратора (можно переопределить через параметры)

public static final String AUTH_REGISTER = "/api/auth/sign_up";public static final String ADMIN_USERNAME = "superuser";

public static final String AUTH_SIGN_IN = "/api/auth/sign_in";public static final String ADMIN_PASSWORD = "Admin123!@#";



public static final String BALANCE_GET_BY_ID = "/api/balance/{id}";// Auth endpoints

public static final String BALANCE_GET_ALL = "/api/balance/all";public static final String AUTH_REGISTER = "/api/auth/sign_up";

public static final String BALANCE_UPDATE = "/api/balance/update/{id}";public static final String AUTH_SIGN_IN = "/api/auth/sign_in";



public static final String PROFILE_CREATE = "/api/admin/profile/create";// Balance endpoints

public static final String PROFILE_UPDATE = "/api/admin/profile/update/{id}";public static final String BALANCE_GET_BY_ID = "/api/balance/{id}";

public static final String PROFILE_GET_BY_ID = "/api/admin/profile/{id}";public static final String BALANCE_GET_ALL = "/api/balance/all";

public static final String PROFILE_GET_ALL = "/api/admin/profile/all";public static final String BALANCE_UPDATE = "/api/balance/update/{id}";

public static final String PROFILE_GET_ALL_REMOVED = "/api/admin/profile/all-removed";

public static final String PROFILE_DELETE = "/api/admin/profile/delete/{id}";// Profile endpoints

public static final String PROFILE_CREATE = "/api/admin/profile/create";

public static final String COUNTER_GET_BY_ID = "/api/admin/counter/{id}";public static final String PROFILE_UPDATE = "/api/admin/profile/update/{id}";

public static final String COUNTER_GET_ALL = "/api/admin/counter/all";public static final String PROFILE_GET_BY_ID = "/api/admin/profile/{id}";

public static final String COUNTER_GET_ALL_ACTIVE = "/api/admin/counter/all-active";public static final String PROFILE_GET_ALL = "/api/admin/profile/all";

```public static final String PROFILE_GET_ALL_REMOVED = "/api/admin/profile/all-removed";

public static final String PROFILE_DELETE = "/api/admin/profile/delete/{id}";

### BaseApiTest

// Counter endpoints

**Назначение:** Базовый класс для всех тестов с общей логикой.public static final String COUNTER_GET_BY_ID = "/api/admin/counter/{id}";

public static final String COUNTER_GET_ALL = "/api/admin/counter/all";

**Расположение:** `src/test/java/auc/BaseApiTest.java`public static final String COUNTER_GET_ALL_ACTIVE = "/api/admin/counter/all-active";

```

**Поля:**

**Использование:**

```java

protected static String adminToken;```java

```// В тестах

Response response = RequestBuilder.authorized(adminToken)

**Методы:**    .get(TestConfig.BALANCE_GET_ALL);



```java// С параметрами

@BeforeClassString url = url(TestConfig.BALANCE_GET_BY_ID, balanceId);

public void globalSetup() {```

    RestAssured.baseURI = TestConfig.BASE_URL;

    adminToken = getAdminToken();### BaseApiTest

}

**Назначение:** Базовый класс для всех тестов с общей логикой.

protected String url(String template, Object... params) {

    String result = template;**Расположение:** `src/test/java/auc/BaseApiTest.java`

    for (Object param : params) {

        result = result.replaceFirst("\\{[^}]+}", String.valueOf(param));**Поля:**

    }

    return result;```java

}protected static String adminToken; // JWT токен администратора

```

private String getAdminToken() {

    AuthSignInRequest signIn = AuthSignInRequest.builder()**Методы:**

        .username(TestConfig.ADMIN_USERNAME)

        .password(TestConfig.ADMIN_PASSWORD)```java

        .build();// Выполняется ПЕРЕД всеми тестами класса

@BeforeClass

    Response signInResponse = RequestBuilder.unauthorized().body(signIn)public void globalSetup() {

        .post(TestConfig.AUTH_SIGN_IN);    RestAssured.baseURI = TestConfig.BASE_URL;

    adminToken = getAdminToken();

    if (signInResponse.getStatusCode() == 200) {}

        return ResponseExtractor.extractToken(signInResponse);

    }// Вспомогательный метод для подстановки параметров в URL

protected String url(String template, Object... params) {

    AuthSignUpRequest signUp = AuthSignUpRequest.builder()    String result = template;

        .username(TestConfig.ADMIN_USERNAME)    for (Object param : params) {

        .password(TestConfig.ADMIN_PASSWORD)        result = result.replaceFirst("\\{[^}]+}", String.valueOf(param));

        .firstName(TestDataGenerator.generateFirstName())    }

        .lastName(TestDataGenerator.generateLastName())    return result;

        .telegramChatId(TestDataGenerator.generateTelegramChatId())}

        .build();

// Получение токена админа (регистрация при необходимости)

    RequestBuilder.unauthorized().body(signUp)private String getAdminToken() {

        .post(TestConfig.AUTH_REGISTER);    // 1. Пытаемся авторизоваться

    AuthSignInRequest signIn = AuthSignInRequest.builder()

    Response secondSignIn = RequestBuilder.unauthorized().body(signIn)        .username(TestConfig.ADMIN_USERNAME)

        .post(TestConfig.AUTH_SIGN_IN);        .password(TestConfig.ADMIN_PASSWORD)

        .build();

    return ResponseExtractor.extractToken(secondSignIn);

}    Response signInResponse = RequestBuilder.unauthorized().body(signIn)

```        .post(TestConfig.AUTH_SIGN_IN);



---    if (signInResponse.getStatusCode() == 200) {

        return ResponseExtractor.extractToken(signInResponse);

## Утилиты    }



### RequestBuilder    // 2. Если юзера нет - регистрируем

    AuthSignUpRequest signUp = AuthSignUpRequest.builder()

**Назначение:** Построение HTTP запросов с авторизацией.        .username(TestConfig.ADMIN_USERNAME)

        .password(TestConfig.ADMIN_PASSWORD)

**Расположение:** `src/test/java/auc/utils/RequestBuilder.java`        .firstName(TestDataGenerator.generateFirstName())

        .lastName(TestDataGenerator.generateLastName())

**Методы:**        .telegramChatId(TestDataGenerator.generateTelegramChatId())

        .build();

```java

public static RequestSpecification authorized(String token) {    RequestBuilder.unauthorized().body(signUp)

    return given()        .post(TestConfig.AUTH_REGISTER);

        .contentType(JSON)

        .header("Authorization", "Bearer " + token);    // 3. Авторизуемся повторно

}    Response secondSignIn = RequestBuilder.unauthorized().body(signIn)

        .post(TestConfig.AUTH_SIGN_IN);

public static RequestSpecification unauthorized() {

    return given().contentType(JSON);    return ResponseExtractor.extractToken(secondSignIn);

}}

``````



### ApiAssertions**Использование:**



**Назначение:** Стандартизированные проверки HTTP ответов.```java

public class BalanceApiTest extends BaseApiTest {

**Расположение:** `src/test/java/auc/utils/ApiAssertions.java`    

    @Test

**Методы:**    public void testGetBalance() {

        // adminToken уже доступен

```java        Response response = RequestBuilder.authorized(adminToken)

public static void assertOkResponse(Response response) {            .get(url(TestConfig.BALANCE_GET_BY_ID, 1));

    assertStatus(response, 200);    }

    String body = safeBody(response);}

    Assert.assertEquals(response.jsonPath().getString("code"), "OK", ```

        "code != OK. Body: " + body);

    Assert.assertNotNull(response.jsonPath().get("content"), ---

        "content is null. Body: " + body);

}## Утилиты



public static void assertForbidden(Response response) {### RequestBuilder

    assertStatus(response, 403);

}**Назначение:** Построение HTTP запросов с авторизацией.



public static void assertNotFound(Response response) {**Расположение:** `src/test/java/auc/utils/RequestBuilder.java`

    assertStatus(response, 404);

}**Методы:**



public static void assertBadRequest(Response response) {```java

    assertStatus(response, 400);// Запрос с Bearer токеном

}public static RequestSpecification authorized(String token) {

    return given()

private static void assertStatus(Response response, int expected) {        .contentType(JSON)

    Assert.assertEquals(response.getStatusCode(), expected,         .header("Authorization", "Bearer " + token);

        "Unexpected status. Body: " + safeBody(response));}

}

// Запрос без авторизации

private static String safeBody(Response response) {public static RequestSpecification unauthorized() {

    try {    return given().contentType(JSON);

        String s = response.asString();}

        return s == null ? "<null>" : ```

            (s.length() > 1000 ? s.substring(0, 1000) + "..." : s);

    } catch (Exception e) {**Примеры:**

        return "<unavailable: " + e.getMessage() + ">";

    }```java

}// GET с авторизацией

```Response response = RequestBuilder.authorized(adminToken)

    .get("/api/balance/all");

### ResponseExtractor

// POST с авторизацией и body

**Назначение:** Извлечение типизированных данных из JSON ответов.Response response = RequestBuilder.authorized(adminToken)

    .body(requestDto)

**Расположение:** `src/test/java/auc/utils/ResponseExtractor.java`    .post("/api/admin/profile/create");



**Методы:**// POST без авторизации (ожидаем 403)

Response response = RequestBuilder.unauthorized()

```java    .body(requestDto)

public static <T> T extractContent(Response response, Class<T> type) {    .post("/api/admin/profile/create");

    return response.jsonPath().getObject("content", type);```

}

### ApiAssertions

public static <T> List<T> extractContentList(Response response, Class<T> type) {

    return response.jsonPath().getList("content", type);**Назначение:** Стандартизированные проверки HTTP ответов.

}

**Расположение:** `src/test/java/auc/utils/ApiAssertions.java`

public static Long extractId(Response response) {

    return response.jsonPath().getLong("content.id");**Методы:**

}

```java

public static String extractToken(Response response) {// Проверка успешного ответа (200 OK + code=OK + content не null)

    return response.jsonPath().getString("content.token");public static void assertOkResponse(Response response) {

}    assertStatus(response, 200);

```    String body = safeBody(response);

    Assert.assertEquals(response.jsonPath().getString("code"), "OK", 

### TestDataGenerator        "code != OK. Body: " + body);

    Assert.assertNotNull(response.jsonPath().get("content"), 

**Назначение:** Генерация валидных тестовых данных.        "content is null. Body: " + body);

}

**Расположение:** `src/test/java/auc/utils/TestDataGenerator.java`

// Проверка 403 Forbidden

**Методы:**public static void assertForbidden(Response response) {

    assertStatus(response, 403);

```java}

public static String generateMsisdn() {

    return "99680" + faker.number().digits(7);// Проверка 404 Not Found

}public static void assertNotFound(Response response) {

    assertStatus(response, 404);

public static String generateFirstName() {}

    return faker.name().firstName();

}// Проверка 400 Bad Request

public static void assertBadRequest(Response response) {

public static String generateLastName() {    assertStatus(response, 400);

    return faker.name().lastName();}

}

// Внутренний метод проверки статуса

public static String generateTelegramChatId() {private static void assertStatus(Response response, int expected) {

    return String.valueOf(faker.number().numberBetween(100000000, 999999999));    Assert.assertEquals(response.getStatusCode(), expected, 

}        "Unexpected status. Body: " + safeBody(response));

}

public static Double generateBalanceAmount() {

    return faker.number().randomDouble(2, 100, 5000);// Безопасное получение body (с защитой от больших ответов)

}private static String safeBody(Response response) {

```    try {

        String s = response.asString();

---        return s == null ? "<null>" : 

            (s.length() > 1000 ? s.substring(0, 1000) + "..." : s);

## DTOs    } catch (Exception e) {

        return "<unavailable: " + e.getMessage() + ">";

### Request DTOs    }

}

#### AuthSignInRequest```



```java**Примеры:**

{

    "username": "string",```java

    "password": "string"// Успешный ответ

}ApiAssertions.assertOkResponse(response);

```

// Ошибки

#### AuthSignUpRequestApiAssertions.assertForbidden(response);  // 403

ApiAssertions.assertNotFound(response);   // 404

```javaApiAssertions.assertBadRequest(response); // 400

{```

    "username": "string",

    "password": "string",### ResponseExtractor

    "firstName": "string",

    "lastName": "string",**Назначение:** Извлечение типизированных данных из JSON ответов.

    "telegramChatId": "string"

}**Расположение:** `src/test/java/auc/utils/ResponseExtractor.java`

```

**Методы:**

#### BalanceUpdateRequest

```java

```java// Извлечение объекта из поля "content"

{public static <T> T extractContent(Response response, Class<T> type) {

    "amount": 1500.50    return response.jsonPath().getObject("content", type);

}}

```

// Извлечение списка объектов из поля "content"

#### ProfileCreateRequestpublic static <T> List<T> extractContentList(Response response, Class<T> type) {

    return response.jsonPath().getList("content", type);

```java}

{

    "msisdn": "996801234567",// Извлечение ID из "content.id"

    "userId": 1,public static Long extractId(Response response) {

    "pricePlanId": 1    return response.jsonPath().getLong("content.id");

}}

```

// Извлечение токена из "content.token"

### Response DTOspublic static String extractToken(Response response) {

    return response.jsonPath().getString("content.token");

#### BalanceDto}

```

```java

{**Примеры:**

    "id": 1,

    "amount": 1500.50,```java

    "userId": 1,// Получение DTO

    "currency": "USD",BalanceDto balance = ResponseExtractor.extractContent(response, BalanceDto.class);

    "createdAt": "2025-11-02T10:00:00",

    "updatedAt": "2025-11-02T10:00:00"// Получение списка DTOs

}List<ProfileDto> profiles = ResponseExtractor.extractContentList(response, ProfileDto.class);

```

// Получение ID созданной сущности

#### CounterDtoLong createdId = ResponseExtractor.extractId(response);



```java// Получение токена после авторизации

{String token = ResponseExtractor.extractToken(response);

    "id": 1,```

    "profileId": 1,

    "megabytes": 5000,### TestDataGenerator

    "seconds": 3600,

    "sms": 100,**Назначение:** Генерация валидных тестовых данных.

    "status": "ACTIVE",

    "createdAt": "2025-11-02T10:00:00",**Расположение:** `src/test/java/auc/utils/TestDataGenerator.java`

    "updatedAt": "2025-11-02T10:00:00"

}**Методы:**

```

```java

#### ProfileDto// MSISDN: 99680 + 7 цифр (всего 12 символов)

public static String generateMsisdn() {

```java    return "99680" + faker.number().digits(7);

{}

    "id": 1,

    "msisdn": "996801234567",// Имя: случайное имя из библиотеки Datafaker

    "userId": 1,public static String generateFirstName() {

    "pricePlanId": 1,    return faker.name().firstName();

    "status": "ACTIVE",}

    "createdAt": "2025-11-02T10:00:00",

    "updatedAt": "2025-11-02T10:00:00"// Фамилия: случайная фамилия

}public static String generateLastName() {

```    return faker.name().lastName();

}

---

// Telegram Chat ID: 9 цифр

## Тестыpublic static String generateTelegramChatId() {

    return String.valueOf(faker.number().numberBetween(100000000, 999999999));

### BalanceApiTest - 9 тестов}



1. `testGetBalanceById_Success` - успешное получение баланса по ID// Сумма баланса: от 100.00 до 5000.00

2. `testGetBalanceById_NotFound` - получение несуществующего балансаpublic static Double generateBalanceAmount() {

3. `testGetBalanceById_Unauthorized` - получение без токена    return faker.number().randomDouble(2, 100, 5000);

4. `testGetAllBalances_Success` - получение списка балансов}

5. `testGetAllBalances_Unauthorized` - получение списка без токена```

6. `testUpdateBalance_AsPerSpecification` - обновление по спецификации (БАГ)

7. `testUpdateBalance_NotFound` - обновление несуществующего баланса**Примеры:**

8. `testUpdateBalance_Unauthorized` - обновление без токена

9. `testUpdateBalance_MissingAmount` - валидация обязательного поля```java

ProfileCreateRequest request = ProfileCreateRequest.builder()

### CounterApiTest - 7 тестов    .msisdn(TestDataGenerator.generateMsisdn())  // "996801234567"

    .userId(1L)

1. `testGetCounterById_Success` - успешное получение счётчика    .pricePlanId(1L)

2. `testGetCounterById_NotFound` - получение несуществующего счётчика    .build();

3. `testGetCounterById_Unauthorized` - получение без токена```

4. `testGetAllCounters_Success` - получение всех счётчиков

5. `testGetAllCounters_Unauthorized` - получение всех без токена---

6. `testGetAllActiveCounters_Success` - получение активных счётчиков (БАГ)

7. `testGetAllActiveCounters_Unauthorized` - получение активных без токена## DTOs



### ProfileApiTest - 14 тестов### Request DTOs



1. `testCreateProfile_Success` - успешное создание профиля (БАГ)#### AuthSignInRequest

2. `testCreateProfile_DuplicateMsisdn` - создание с дубликатом MSISDN

3. `testGetProfileById_Success` - получение профиля по ID```java

4. `testGetProfileById_NotFound` - получение несуществующего профиля{

5. `testGetAllProfiles_Success` - получение списка профилей    "username": "string",  // required

6. `testUpdateProfile_Success` - обновление профиля    "password": "string"   // required

7. `testDeleteProfile_StatusCode` - удаление профиля (БАГ)}

8. `testGetAllRemovedProfiles_Success` - получение удалённых профилей```

9. `testCreateProfile_Unauthorized` - создание без токена

10. `testGetProfileById_Unauthorized` - получение без токена**Поля:**

11. `testGetAllProfiles_Unauthorized` - получение списка без токена- `username` - имя пользователя

12. `testUpdateProfile_Unauthorized` - обновление без токена- `password` - пароль

13. `testDeleteProfile_Unauthorized` - удаление без токена

14. `testGetAllRemovedProfiles_Unauthorized` - получение удалённых без токена**Использование:**



---```java

AuthSignInRequest request = AuthSignInRequest.builder()

## Тест-кейсы    .username("superuser")

    .password("Admin123!@#")

Подробное описание всех 30 тест-кейсов с примерами запросов/ответов, ожидаемыми результатами и процедурами воспроизведения.    .build();

```

---

#### AuthSignUpRequest

## Баг-репорты

```java

### БАГ #1: Balance Update - Некорректная обработка requestBody{

    "username": "string",        // required

**Приоритет:** Высокий    "password": "string",        // required

    "firstName": "string",       // required

**Эндпоинт:** `PUT /api/balance/update/{id}`    "lastName": "string",        // required

    "telegramChatId": "string"   // required

**Спецификация:** Ожидает requestBody с `{"amount": <double>}`, возвращает 200}

```

**Реальность:** API возвращает 400 Bad Request

**Использование:**

**Код воспроизведения:**

```java

```javaAuthSignUpRequest request = AuthSignUpRequest.builder()

BalanceUpdateRequest body = BalanceUpdateRequest.builder()    .username("newuser")

    .amount(1500.50)    .password("Password123!")

    .build();    .firstName(TestDataGenerator.generateFirstName())

    .lastName(TestDataGenerator.generateLastName())

Response response = RequestBuilder.authorized(adminToken)    .telegramChatId(TestDataGenerator.generateTelegramChatId())

    .body(body)    .build();

    .put("/api/balance/update/1");```



// Ожидается: 200#### BalanceUpdateRequest

// Реально: 400

``````java

{

### БАГ #2: Profile Create - Неверный HTTP статус    "amount": 1500.50  // required, double

}

**Приоритет:** Средний```



**Эндпоинт:** `POST /api/admin/profile/create`**Использование:**



**Спецификация:** Должен возвращать 200 OK```java

BalanceUpdateRequest request = BalanceUpdateRequest.builder()

**Реальность:** API возвращает 201 Created    .amount(TestDataGenerator.generateBalanceAmount())

    .build();

### БАГ #3: Profile Delete - Неверный HTTP статус```



**Приоритет:** Средний#### ProfileCreateRequest



**Эндпоинт:** `DELETE /api/admin/profile/delete/{id}````java

{

**Спецификация:** Должен возвращать 200 OK    "msisdn": "996801234567",  // required, pattern: ^99680\d{7}$

    "userId": 1,               // required, int64

**Реальность:** API возвращает 204 No Content    "pricePlanId": 1           // required, int64

}

### БАГ #4: Counter All Active - Некорректный статус```



**Приоритет:** Средний**Использование:**



**Эндпоинт:** `GET /api/admin/counter/all-active````java

ProfileCreateRequest request = ProfileCreateRequest.builder()

**Спецификация:** Должен возвращать 200 OK    .msisdn(TestDataGenerator.generateMsisdn())

    .userId(1L)

**Реальность:** API возвращает 204 No Content    .pricePlanId(1L)

    .build();

---```



## Best Practices### Response DTOs



### Используйте утилиты вместо дублирования#### BalanceDto



Правильно:```java

```java{

ApiAssertions.assertOkResponse(response);    "id": 1,                            // int64

```    "amount": 1500.50,                  // double

    "userId": 1,                        // int64

Неправильно:    "currency": "USD",                  // string

```java    "createdAt": "2025-11-01T10:00:00", // LocalDateTime

response.then()    "updatedAt": "2025-11-01T10:00:00"  // LocalDateTime

    .statusCode(200)}

    .body("code", equalTo("OK"))```

    .body("content", notNullValue());

```#### CounterDto



### Генерируйте уникальные данные```java

{

Правильно:    "id": 1,                            // int64

```java    "profileId": 1,                     // int64

String msisdn = TestDataGenerator.generateMsisdn();    "megabytes": 5000,                  // int64

```    "seconds": 3600,                    // int64

    "sms": 100,                         // int32

Неправильно:    "status": "ACTIVE",                 // string

```java    "createdAt": "2025-11-01T10:00:00", // LocalDateTime

String msisdn = "996801234567"; // Может конфликтовать    "updatedAt": "2025-11-01T10:00:00"  // LocalDateTime

```}

```

### Cleanup после создания ресурсов

#### ProfileDto

Правильно:

```java```java

Long createdId = ResponseExtractor.extractId(response);{

RequestBuilder.authorized(adminToken)    "id": 1,                            // int64

    .delete(url(TestConfig.PROFILE_DELETE, createdId));    "msisdn": "996801234567",           // string

```    "userId": 1,                        // int64

    "pricePlanId": 1,                   // int64

### Информативные описания тестов    "status": "ACTIVE",                 // string

    "createdAt": "2025-11-01T10:00:00", // LocalDateTime

Правильно:    "updatedAt": "2025-11-01T10:00:00"  // LocalDateTime

```java}

@Test(priority = 1, description = "GET /api/balance/{id} - успешное получение баланса")```

public void testGetBalanceById_Success() { }

```---



### Проверяйте по спецификации## Тесты



Правильно:### BalanceApiTest

```java

ApiAssertions.assertOkResponse(response);**Покрытие:** 9 тестов для Balance API

```

**Setup:**

Неправильно:

```java```java

Assert.assertEquals(response.getStatusCode(), 201); // Подстраиваемся под багprivate static long testBalanceId;

```

@BeforeClass

---public void setup() {

    Response response = RequestBuilder.authorized(adminToken)

## Troubleshooting        .get(TestConfig.BALANCE_GET_ALL);

    ApiAssertions.assertOkResponse(response);

### Тесты падают с 403 Forbidden    testBalanceId = response.jsonPath().getLong("content[0].id");

}

**Причина:** Не получен или истёк adminToken```



**Решение:****Тесты:**

1. Проверьте credentials в `TestConfig.java`

2. Убедитесь, что API доступен1. `testGetBalanceById_Success` - успешное получение баланса по ID

3. Проверьте консольный вывод на наличие ошибок авторизации2. `testGetBalanceById_NotFound` - получение несуществующего баланса

3. `testGetBalanceById_Unauthorized` - получение без токена

### Тесты падают с Connection Refused4. `testGetAllBalances_Success` - получение списка балансов

5. `testGetAllBalances_Unauthorized` - получение списка без токена

**Причина:** API сервер недоступен6. `testUpdateBalance_AsPerSpecification` - обновление по спецификации (**БАГ**)

7. `testUpdateBalance_NotFound` - обновление несуществующего баланса

**Решение:**8. `testUpdateBalance_Unauthorized` - обновление без токена

```bash9. `testUpdateBalance_MissingAmount` - валидация обязательного поля

curl http://195.38.164.168:7173/api/auth/sign_in

```### CounterApiTest



### Profile тесты падают с "MSISDN invalid pattern"**Покрытие:** 7 тестов для Counter API



**Причина:** Неправильный формат MSISDN**Setup:**



**Решение:** MSISDN должен соответствовать `^99680\d{7}$` (всего 12 символов)```java

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