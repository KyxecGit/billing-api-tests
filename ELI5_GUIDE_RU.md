# Объяснение на пальцах (ELI5): как устроены тесты Billing API

Цель: объяснить простым человеческим языком, без жаргона. Представьте, что мы проверяем работу магазина.

- API — это магазин, у него есть двери (эндпоинты) и охрана (авторизация).
- Наши тесты — это проверяющие, которые приходят, показывают паспорт (токен), делают покупку (запрос) и проверяют чек (ответ).

## Что тут вообще есть

- `TestConfig.java` — адрес магазина и важные таблички: где вход, как зовут админа, пароли, пути.
- `BaseApiTest.java` — общая подготовка: включаем навигатор на адрес магазина и получаем пропуск (токен).
- `utils/RequestBuilder.java` — как правильно формировать «заход в магазин»: заголовки, токен, формат.
- `utils/ApiAssertions.java` — как выглядит «правильный чек» и какие ошибки ловим сразу.
- `utils/ResponseExtractor.java` — как достать нужное поле из чека, например номер заказа.
- `utils/TestDataGenerator.java` — как придумать правдоподобные данные (телефон, имена), чтобы не повторяться.
- Тесты `*ApiTest.java` — конкретные проверки: пришли → показали паспорт → сделали действие → сверили чек.

## Мини-картина: один прогон тестов

1) Перед всеми тестами: настраиваем базовый адрес и получаем токен админа.
2) В каждом тесте: готовим данные → отправляем запрос → проверяем ответ → (иногда) чистим за собой.

Дальше — маленькие фрагменты кода и разбор каждой строки «на пальцах». Это упрощённые куски, смысл сохранён.

---

## 1) Общая подготовка: BaseApiTest

Файл: `src/test/java/auc/BaseApiTest.java`

```java
@BeforeClass
public void globalSetup() {
    RestAssured.baseURI = TestConfig.BASE_URL;
    adminToken = getAdminToken();
}
```
- `@BeforeClass` — «сделать один раз перед всеми проверками», как заправить машину перед рейсом.
- `RestAssured.baseURI = TestConfig.BASE_URL;` — говорим навигатору базовый адрес магазина.
- `adminToken = getAdminToken();` — получаем «пропуск» (токен), чтобы охрана нас пускала внутрь.

Идея `getAdminToken()` простая: сначала пытаемся войти (sign in). Если не вышло — регистрируемся (sign up) и снова входим. В итоге получаем токен.

---

## 2) Как собрать правильный запрос: RequestBuilder

Файл: `src/test/java/auc/utils/RequestBuilder.java`

```java
public static RequestSpecification authorized(String token) {
    return given()
        .contentType(JSON)
        .header("Authorization", "Bearer " + token);
}
```
- `authorized(token)` — «зайти в магазин с паспортом».
- `given()` — начинаем собирать запрос.
- `.contentType(JSON)` — говорим, что говорим на языке JSON.
- `.header("Authorization", "Bearer " + token)` — показываем охране паспорт: «я — админ, меня пустят».

Для незащищённых входов есть `unauthorized()` — без паспорта.

---

## 3) Как понять, что чек правильный: ApiAssertions

Файл: `src/test/java/auc/utils/ApiAssertions.java`

```java
public static void assertOkResponse(Response response) {
    assertStatus(response, 200);
    String body = safeBody(response);
    Assert.assertEquals(response.jsonPath().getString("code"), "OK",
        "code != OK. Body: " + body);
    Assert.assertNotNull(response.jsonPath().get("content"),
        "content is null. Body: " + body);
}
```
- `assertOkResponse` — «нормальный чек выглядит так».
- `assertStatus(..., 200)` — касса должна сказать «всё прошло успешно» (код 200).
- Дальше проверяем, что в чеке поле `code` равно `OK`, а раздел `content` вообще есть.
- Если что-то не так — кричим и показываем кусок чека, чтобы быстро понять, где сломалось.

Есть ещё `assertForbidden(403)`, `assertNotFound(404)`, `assertBadRequest(400)` — это типовые «ошибочные чеки».

---

## 4) Как вытащить нужное из чека: ResponseExtractor

Файл: `src/test/java/auc/utils/ResponseExtractor.java`

```java
public static Long extractId(Response response) {
    return response.jsonPath().getLong("content.id");
}
```
- `extractId` — достаём номер созданной записи из раздела `content.id`.
- Аналогично можно вытащить токен (`content.token`) или целый объект (`content`).

---

## 5) Как делать правдоподобные данные: TestDataGenerator

Файл: `src/test/java/auc/utils/TestDataGenerator.java`

```java
public static String generateMsisdn() {
    return "99680" + faker.number().digits(7);
}
```
- `generateMsisdn` — придумаем номер, похожий на реальный: фиксированный префикс + 7 случайных цифр.
- Так минимизируем конфликты (не создаём одинаковые записи два раза).

---

## 6) Пример одного теста: создать профиль

Файл: `src/test/java/auc/ProfileApiTest.java` (упрощённый фрагмент по смыслу)

```java
@Test(description = "POST /api/admin/profile/create — создать профиль")
public void testCreateProfile_Success() {
    String msisdn = TestDataGenerator.generateMsisdn();

    var body = ProfileCreateRequest.builder()
        .msisdn(msisdn)
        .userId(1L)
        .pricePlanId(1L)
        .build();

    Response response = RequestBuilder.authorized(adminToken)
        .body(body)
        .post(TestConfig.PROFILE_CREATE);

    ApiAssertions.assertOkResponse(response);
}
```
Пояснение «по строкам»:
- `@Test(...)` — пометили это как проверку, которая будет запущена автоматически.
- `generateMsisdn()` — придумали уникальный номер, чтобы не пересекался с уже существующими.
- `body = ...builder()` — собрали «заявку» в магазин: какие данные хотим создать.
- `authorized(adminToken)` — показали охране паспорт.
- `.body(body)` — отдали кассиру форму с данными.
- `.post(PROFILE_CREATE)` — сказали: «хотим создать профиль».
- `assertOkResponse(response)` — чек должен быть «ОК», иначе считаем это проблемой на стороне API.

Если в этом месте API вернёт не 200, а, скажем, 201 — тест упадёт. Это нормально: мы ловим несоответствие спецификации, чтобы команда сервиса могла исправить поведение.

---

## Частые вопросы коротко

- Почему тесты падают с 403? — Токен не получили/просрочен. Проверьте логин/пароль и доступность API.
- Где смотреть результаты? — Папка `target/surefire-reports` после запуска Maven.
- Как запустить только один файл тестов? — `mvn -q -Dtest=ProfileApiTest test`.
- Можно без Java-разговоров? — Да: меняйте `TestConfig.java`, запускайте Maven, смотрите отчёт. Остальное — «под капотом».

---

## Итог

Запомнить главное:
- Перед тестами — настраиваем адрес и берём токен.
- В тесте — готовим данные → отправляем запрос → проверяем ответ.
- Проверки простые: правильный код, `code == "OK"`, есть `content`.
- Если что-то не сходится — это сигнал команде API, а не проблема автотестов.
