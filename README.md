# 📱 Billing API Test Framework

Простой и понятный фреймворк для тестирования REST API на базе TestNG и REST Assured.

## 🚀 Быстрый старт

### Запуск всех тестов
```bash
mvn test
```

### Запуск конкретного класса тестов
```bash
mvn test -Dtest=BalanceTests
mvn test -Dtest=CounterTests
mvn test -Dtest=ProfileTests
```

### Запуск одного теста
```bash
mvn test -Dtest=BalanceTests#getAll
```

## 📁 Структура проекта

```
src/test/java/qa/
├── config/
│   └── Config.java           # Настройки (URL, credentials)
├── helpers/
│   └── AuthHelper.java       # Авторизация и получение токена
└── tests/
    ├── BaseTest.java         # Базовый класс с общими методами
    ├── BalanceTests.java     # Тесты для балансов
    ├── CounterTests.java     # Тесты для счетчиков
    └── ProfileTests.java     # Тесты для профилей
```

## 🎯 Как писать тесты

### Пример простого теста
```java
@Test
public void getAll() {
    checkStatus(get("/api/balance/all"), 200);
}
```

### Пример теста с телом запроса
```java
@Test
public void create() {
    String body = "{\"amount\":100.0}";
    checkStatus(post("/api/balance/create", body), 201);
}
```

## 🔧 Доступные методы

### HTTP методы
- `get(path)` - GET запрос
- `post(path, body)` - POST запрос
- `put(path, body)` - PUT запрос
- `delete(path)` - DELETE запрос

### Проверки
- `checkStatus(response, 200)` - проверить статус код
- `checkStatusOneOf(response, 200, 204)` - проверить несколько вариантов

### Получение данных
- `text(response, "content.name")` - получить строку из JSON
- `number(response, "content.id")` - получить число из JSON
- `firstId("/api/balance/all")` - получить первый ID из списка

### Создание данных
- `newPhone()` - создать уникальный номер телефона
- `json(phone, planId, userId)` - создать JSON для профиля
- `json(phone)` - создать JSON с дефолтными значениями

## ⚙️ Настройка

Откройте `src/test/java/qa/config/Config.java` и измените:

```java
public static final String BASE_URL = "http://your-api.com";
public static final String ADMIN_USERNAME = "admin";
public static final String ADMIN_PASSWORD = "password";
```

## 📊 Результаты

Отчеты находятся в: `target/surefire-reports/`

## 🛠️ Технологии

- Java 21
- TestNG 7.10.2
- REST Assured 5.5.0
- Maven 3.9+
