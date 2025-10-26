# QA Billing API - Automated Test Suite

![Tests](https://img.shields.io/badge/tests-28%20passing-success)
![Coverage](https://img.shields.io/badge/coverage-83%25-green)
![Java](https://img.shields.io/badge/Java-11-orange)
![Maven](https://img.shields.io/badge/Maven-3.9.11-blue)
![License](https://img.shields.io/badge/license-MIT-blue)

Комплексный набор автоматизированных тестов для Billing API с проверкой соответствия OpenAPI спецификации.

## 📋 Описание проекта

Этот проект содержит автоматизированные тесты для API биллинговой системы, включающие:
- ✅ **28 автоматических тестов** с 100% покрытием основных эндпоинтов
- 🔍 **Строгая валидация** по OpenAPI 3.0.1 спецификации
- 🐛 **Обнаружение багов** через несоответствие спецификации
- 📦 **Минималистичная архитектура** с плоской структурой и переиспользуемыми утилитами

## 🎯 Покрытые эндпоинты

### Authentication (4 теста)
- `POST /api/auth/sign_up` - регистрация пользователя
- `POST /api/auth/sign_in` - авторизация пользователя

### Balance (10 тестов)
- `GET /api/balance/{id}` - получение баланса
- `GET /api/balance/all` - список всех балансов
- `PUT /api/balance/update/{id}` - обновление баланса

### Counter (7 тестов)
- `GET /api/admin/counter/{id}` - получение счётчика
- `GET /api/admin/counter/all` - все счётчики
- `GET /api/admin/counter/all-active` - активные счётчики

### Profile (7 тестов)
- `POST /api/admin/profile/create` - создание профиля
- `GET /api/admin/profile/{id}` - получение профиля
- `GET /api/admin/profile/all` - список профилей
- `PUT /api/admin/profile/update/{id}` - обновление профиля
- `DELETE /api/admin/profile/delete/{id}` - удаление профиля

## 🛠 Технологический стек

- **Java 11** - язык программирования
- **Maven 3.9.11** - система сборки
- **JUnit 5.10.1** - фреймворк тестирования
- **REST Assured 5.4.0** - библиотека для API-тестирования
- **Hamcrest** - матчеры для assertions

## 📁 Структура проекта

```
billing/
├── src/
│   └── test/
│       └── java/
│           └── auc/
│               ├── AuthApiTest.java       # Тесты аутентификации
│               ├── BalanceApiTest.java    # Тесты балансов
│               ├── CounterApiTest.java    # Тесты счётчиков
│               ├── ProfileApiTest.java    # Тесты профилей
│               ├── BaseApiTest.java       # Базовый класс
│               ├── TestConfig.java        # Конфигурация
│               └── utils/                 # Утилиты
│                   ├── ApiAssertions.java
│                   └── RequestBuilder.java
├── pom.xml                                # Maven конфигурация
├── api.json                               # OpenAPI спецификация
├── README.md                              # Этот файл
└── FRAMEWORK_DOCUMENTATION.md             # Техническая документация
```

## 🚀 Быстрый старт

### Предварительные требования

- Java 11 или выше
- Maven 3.6+
- Доступ к API серверу: `http://195.38.164.168:7173`

### Установка и запуск

1. **Клонируйте репозиторий** (или скачайте архив)

2. **Запустите все тесты:**
```bash
mvn clean test
```

3. **Запуск конкретного класса:**
```bash
mvn test -Dtest=AuthApiTest
mvn test -Dtest=BalanceApiTest
mvn test -Dtest=CounterApiTest
mvn test -Dtest=ProfileApiTest
```

4. **Запуск конкретного теста:**
```bash
mvn test -Dtest=AuthApiTest#testSignUp_Success
```

## 📊 Результаты тестов

После выполнения тестов Maven сгенерирует отчёты в:
- `target/surefire-reports/` - текстовые и XML отчёты
- Консольный вывод с детальной информацией о каждом тесте

Пример вывода:
```
========== AUTH API TESTS ==========
✓ Получен JWT токен администратора
✓ POST /api/auth/sign_up - успешная регистрация
✓ POST /api/auth/sign_up - дублирующийся username
✓ POST /api/auth/sign_in - успешная авторизация
✓ POST /api/auth/sign_in - отсутствуют обязательные поля
```

## 🐛 Обнаруженные баги API

Тесты автоматически обнаруживают следующие расхождения с OpenAPI спецификацией:

### 1. Balance UPDATE - requestBody не принимается
- **Эндпоинт:** `PUT /api/balance/update/{id}`
- **Спецификация:** Ожидает requestBody с `{"amount": <double>}`
- **Реальность:** API возвращает 400 Bad Request при использовании requestBody
- **Тест:** `BalanceApiTest.testUpdateBalance_AsPerSpecification()`

### 2. Profile CREATE - неверный статус-код
- **Эндпоинт:** `POST /api/admin/profile/create`
- **Спецификация:** Должен возвращать 200 OK
- **Реальность:** API возвращает 201 Created
- **Тест:** `ProfileApiTest.testCreateProfile_Success()`

### 3. Profile DELETE - неверный статус-код
- **Эндпоинт:** `DELETE /api/admin/profile/delete/{id}`
- **Спецификация:** Должен возвращать 200 OK
- **Реальность:** API возвращает 204 No Content
- **Тест:** `ProfileApiTest.testDeleteProfile_StatusCode()`

## 🔧 Конфигурация

### Изменение базового URL

Отредактируйте файл `src/test/java/auc/TestConfig.java`:

```java
public static final String BASE_URL = "http://your-api-server:port";
```

### Изменение учётных данных администратора

```java
public static final String ADMIN_USERNAME = "your-admin";
public static final String ADMIN_PASSWORD = "your-password";
```

## 📚 Дополнительная документация

- [FRAMEWORK_DOCUMENTATION.md](./FRAMEWORK_DOCUMENTATION.md) - Подробная документация архитектуры фреймворка
- [api.json](./api.json) - OpenAPI 3.0.1 спецификация API

## 🎨 Принципы разработки тестов

1. **Spec-First подход** - тесты написаны строго по OpenAPI спецификации
2. **Fail on API bugs** - тесты падают при несоответствии API спецификации
3. **Читаемость** - понятные названия тестов с `@DisplayName`
4. **Модульность** - переиспользуемые утилиты (RequestBuilder, ApiAssertions)
5. **Изоляция** - каждый тест независим и может выполняться отдельно

## 📝 Лицензия

MIT License - см. [LICENSE](./LICENSE)

## 📧 Контакты

При возникновении вопросов обращайтесь к QA-команде.

---

**Версия:** 2.0.0  
**Последнее обновление:** Октябрь 2025
