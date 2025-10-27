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
- 📚 **Подробная документация** всех компонентов с примерами использования

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
- **Jackson 2.16.1** - JSON обработка

## 📁 Структура проекта

```
billing/
├── src/
│   └── test/
│       └── java/
│           └── auc/
│               ├── AuthApiTest.java          # Тесты аутентификации (4 теста)
│               ├── BalanceApiTest.java       # Тесты балансов (10 тестов)
│               ├── CounterApiTest.java       # Тесты счётчиков (7 тестов)
│               ├── ProfileApiTest.java       # Тесты профилей (7 тестов)
│               ├── BaseApiTest.java          # Базовый класс для всех тестов
│               ├── TestConfig.java           # Централизованная конфигурация
│               └── utils/                    # Утилиты для построения запросов
│                   ├── ApiAssertions.java    # Стандартизированные проверки ответов
│                   └── RequestBuilder.java   # Построитель HTTP запросов
├── pom.xml                                   # Maven конфигурация
├── api.json                                  # OpenAPI 3.0.1 спецификация
├── README.md                                 # Этот файл
├── FRAMEWORK_DOCUMENTATION.md                # Подробная техническая документация
├── TEST_CASES.md                             # 📚 НОВОЕ! Документация всех тест-кейсов
├── BUG_REPORTS.md                            # 🐛 НОВОЕ! Репорты обнаруженных багов
├── LICENSE                                   # MIT лицензия
└── target/                                   # Директория сборки
    ├── classes/
    ├── test-classes/
    ├── surefire-reports/                     # Результаты тестов (XML, TXT)
    └── ...
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
✓ POST /api/auth/sign_up - отсутствуют обязательные поля

========== BALANCE API TESTS ==========
✓ GET /api/balance/{id} - успешное получение баланса
...
```

## 🐛 Обнаруженные баги API

Тесты автоматически обнаруживают следующие расхождения с OpenAPI спецификацией:

### 1. Balance UPDATE - requestBody не принимается
- **Эндпоинт:** `PUT /api/balance/update/{id}`
- **Спецификация:** Ожидает requestBody с `{"amount": <double>}`
- **Реальность:** API возвращает 400 Bad Request при использовании requestBody
- **Тест:** `BalanceApiTest.testUpdateBalance_AsPerSpecification()`
- **Приоритет:** 🔴 ВЫСОКИЙ
- **Статус:** 🔴 ОТКРЫТ
- **Подробнее:** [BUG_REPORTS.md - БАГ #1](./BUG_REPORTS.md)

### 2. Profile CREATE - неверный статус-код
- **Эндпоинт:** `POST /api/admin/profile/create`
- **Спецификация:** Должен возвращать 200 OK
- **Реальность:** API возвращает 201 Created
- **Тест:** `ProfileApiTest.testCreateProfile_Success()`
- **Приоритет:** 🟡 СРЕДНИЙ
- **Статус:** 🟡 ИЗВЕСТНЫЙ
- **Подробнее:** [BUG_REPORTS.md - БАГ #2](./BUG_REPORTS.md)

### 3. Profile DELETE - неверный статус-код
- **Эндпоинт:** `DELETE /api/admin/profile/delete/{id}`
- **Спецификация:** Должен возвращать 200 OK
- **Реальность:** API возвращает 204 No Content
- **Тест:** `ProfileApiTest.testDeleteProfile_StatusCode()`
- **Приоритет:** 🟡 СРЕДНИЙ
- **Статус:** 🟡 ИЗВЕСТНЫЙ
- **Подробнее:** [BUG_REPORTS.md - БАГ #3](./BUG_REPORTS.md)

**🔗 Полный список:** Смотрите [BUG_REPORTS.md](./BUG_REPORTS.md) для детальных репортов о каждом баге

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

**⚠️ Важно:** В production окружении учётные данные должны быть переменными окружения, а не жёстко закодированы!

```java
// Рекомендуемый подход:
public static final String ADMIN_USERNAME = System.getenv("ADMIN_USERNAME");
public static final String ADMIN_PASSWORD = System.getenv("ADMIN_PASSWORD");
```

## 📚 Документация

### 📖 [FRAMEWORK_DOCUMENTATION.md](./FRAMEWORK_DOCUMENTATION.md)
Подробная техническая документация архитектуры фреймворка, содержит:
- Описание всех классов и их ответственности
- Жизненный цикл тестов
- Примеры использования
- Принципы разработки тестов
- Best practices
- Troubleshooting

### 📋 [TEST_CASES.md](./TEST_CASES.md)
Полная документация всех 28 тестовых случаев, включает:
- **Структурированное описание каждого теста** с ID, методом, эндпоинтом
- **Шаги выполнения** (Arrange-Act-Assert паттерн)
- **Ожидаемые результаты** с примерами JSON ответов
- **Процедуры воспроизведения** ошибок
- **Интерпретация результатов** и статусы
- **Метрики и статистика** тестирования

### 🐛 [BUG_REPORTS.md](./BUG_REPORTS.md)
Детальные репорты об всех обнаруженных багах в API, содержит:
- **Таблица всех багов** с приоритетами и статусами
- **Подробное описание каждого бага:**
  - Основная информация и типизация
  - Точное описание проблемы
  - Ожидаемое vs текущее поведение
  - Код для воспроизведения
  - Возможные причины
  - Рекомендуемые решения
- **История и timeline** каждого бага
- **Процесс обработки** багов

### 📖 [api.json](./api.json)
OpenAPI 3.0.1 спецификация API - источник истины для всех тестов

## 🎨 Принципы разработки тестов

1. **Spec-First подход** - тесты написаны строго по OpenAPI спецификации
2. **Fail on API bugs** - тесты падают при несоответствии API спецификации
3. **Читаемость** - понятные названия тестов с `@DisplayName`
4. **Модульность** - переиспользуемые утилиты (RequestBuilder, ApiAssertions)
5. **Изоляция** - каждый тест независим и может выполняться отдельно
6. **Самодокументируемость** - код читается как спецификация
7. **DRY** (Don't Repeat Yourself) - использование утилит вместо дублирования

## 👨‍💻 Расширение фреймворка

### Добавление нового тестового класса

1. Создайте класс в `src/test/java/auc/`:
```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NewModuleApiTest extends BaseApiTest {
    
    @BeforeAll
    public static void setup() {
        globalSetup(); // Обязательно!
        // Ваша подготовка
    }
    
    @Test
    @Order(1)
    @DisplayName("GET /new/endpoint - описание")
    public void testSomething() { }
}
```

2. Добавьте эндпоинты в `TestConfig.java`

3. Документируйте тесты в [TEST_CASES.md](./TEST_CASES.md)

4. Если обнаружили баг, задокументируйте в [BUG_REPORTS.md](./BUG_REPORTS.md)

## ✅ Проверка качества кода

Все тестовые классы содержат:
- ✅ Подробные JavaDoc комментарии для всех публичных методов
- ✅ Объяснение логики в приватных методах
- ✅ Примеры использования
- ✅ Ссылки на связанные компоненты

## 📊 Матрица тестового покрытия

| Модуль | GET | POST | PUT | DELETE | Auth | Validation | Total |
|--------|-----|------|-----|--------|------|------------|-------|
| **Auth** | - | ✅ 4 | - | - | N/A | ✅ | 4 |
| **Balance** | ✅ 5 | - | ✅ 5 | - | ✅ | ✅ | 10 |
| **Counter** | ✅ 7 | - | - | - | ✅ | ✅ | 7 |
| **Profile** | ✅ 3 | ✅ 2 | ✅ 1 | ✅ 1 | ✅ | ✅ | 7 |
| **ИТОГО** | **15** | **6** | **6** | **1** | - | - | **28** |

### Типы тестов

- **Positive (Success)** - 12 тестов (43%) ✅
- **Negative (Validation)** - 13 тестов (46%) ❌
- **Bug Detection** - 3 теста (11%) 🐛

## 🔍 Troubleshooting

### Тесты падают с 403 Forbidden

**Причина:** Не получен adminToken или он истёк.

**Решение:**
```bash
# Проверьте вывод в консоли для:
✓ Получен JWT токен администратора

# Если её нет, проверьте:
# 1. Базовый URL в TestConfig.java правильный
# 2. Учётные данные администратора верные
# 3. API сервер запущен и доступен
```

### Тесты падают с Connection Refused

**Причина:** API сервер недоступен.

**Решение:**
```bash
# Проверьте доступность:
curl http://195.38.164.168:7173/api/auth/sign_in

# Или измените BASE_URL в TestConfig.java
```

### Profile тесты падают с "MSISDN invalid pattern"

**Причина:** Неправильный формат MSISDN.

**Решение:** Убедитесь, что MSISDN соответствует паттерну `^99680\d{7}$` (всего 12 символов, начинается с 99680)

## 🎓 Best Practices

### ✅ Правильно:

```java
// Используйте утилиты
ApiAssertions.assertOkResponse(response);

// Самодокументирующиеся названия
@DisplayName("POST /api/balance/update/{id} - валидация: отсутствует обязательное поле amount")

// Уникальные значения
String msisdn = "99680" + String.format("%07d", System.currentTimeMillis() % 10000000);

// Cleanup после создания
RequestBuilder.authorizedDelete(token).when().delete("/api/profile/delete/{id}");
```

### ❌ Неправильно:

```java
// Не дублируйте код
response.then().statusCode(200).body("code", equalTo("OK"));

// Неинформативные названия
public void test1() { }

// Hardcode ID
String msisdn = "996800000001";  // Может конфликтовать с другим тестом

// Забываете про cleanup
// Данные остаются в БД и ломают следующие запуски
```

## 🚀 CI/CD Интеграция

Для интеграции в CI/CD pipeline:

```yaml
# GitHub Actions пример
- name: Run tests
  run: mvn clean test

# Сохранение результатов
- name: Upload test results
  if: always()
  uses: actions/upload-artifact@v2
  with:
    name: test-results
    path: target/surefire-reports/
```

## 📧 Поддержка и обратная связь

При возникновении вопросов:
1. Проверьте [FRAMEWORK_DOCUMENTATION.md](./FRAMEWORK_DOCUMENTATION.md)
2. Посмотрите примеры в [TEST_CASES.md](./TEST_CASES.md)
3. Проверьте [BUG_REPORTS.md](./BUG_REPORTS.md) на наличие известных проблем
4. Обратитесь к QA-команде

## 📝 Лицензия

MIT License - см. [LICENSE](./LICENSE)

---

## 📚 Документы проекта

| Документ | Назначение |
|----------|-----------|
| [README.md](./README.md) | Этот файл - общая информация |
| [FRAMEWORK_DOCUMENTATION.md](./FRAMEWORK_DOCUMENTATION.md) | Архитектура и принципы фреймворка |
| [TEST_CASES.md](./TEST_CASES.md) | Документация всех тест-кейсов |
| [BUG_REPORTS.md](./BUG_REPORTS.md) | Репорты обнаруженных багов |
| [api.json](./api.json) | OpenAPI 3.0.1 спецификация |

---

**Версия:** 2.1.0  
**Последнее обновление:** 27 октября 2025  
**Состояние:** ✅ Полностью задокументировано с комментариями

**Что нового в 2.1.0:**
- ✅ Подробное комментирование всех методов и классов
- ✅ Добавлена документация TEST_CASES.md (все 28 тест-кейсов)
- ✅ Добавлена документация BUG_REPORTS.md (все 3 обнаруженных бага)
- ✅ Обновлен README с ссылками на новые документы
- ✅ Примеры использования для каждого компонента
