# Billing API Test Framework# Billing API Test Framework# Billing API Test Framework



REST Assured автотесты для проверки соответствия Billing API спецификации OpenAPI 3.0.1.



![Java](https://img.shields.io/badge/Java-21-orange)REST Assured автотесты для проверки соответствия Billing API спецификации OpenAPI 3.0.1.REST Assured автотесты для проверки соответствия Billing API спецификации OpenAPI 3.0.1.

![Maven](https://img.shields.io/badge/Maven-3.9.11-blue)

![TestNG](https://img.shields.io/badge/TestNG-7.10.2-green)

![Tests](https://img.shields.io/badge/tests-30-blue)

![Passing](https://img.shields.io/badge/passing-26-success)![Java](https://img.shields.io/badge/Java-21-orange)![Java](https://img.shields.io/badge/Java-21-orange)

![Bugs Found](https://img.shields.io/badge/bugs%20found-4-red)

![Maven](https://img.shields.io/badge/Maven-3.9.11-blue)![Maven](https://img.shields.io/badge/Maven-3.9.11-blue)

# Billing API Tests

Коротко: это набор автотестов для Billing API на Java (REST Assured + TestNG).

## Что нужно

- JDK 21
- Maven 3.9+
- Доступ к API (URL, логин/пароль админа)

## Быстрый старт

1) Откройте `src/test/java/auc/TestConfig.java` и задайте BASE_URL и креды.
2) Запустите все тесты:

```powershell
mvn -q test
```

3) Запустить один класс (пример):

```powershell
mvn -q -Dtest=ProfileApiTest test
```

Отчёты: `target/surefire-reports`.

## Технологии

Java 21, Maven, TestNG, REST Assured, Jackson, Datafaker.

## Полезно

Простое объяснение для новичков: см. `ELI5_GUIDE_RU.md`.
Результат: 30 tests run, 26 passed, 4 failed
