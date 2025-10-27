# ✅ TEST CASES DOCUMENTATION - Billing API Test Suite

Полная документация всех тестовых случаев автоматизированного тестирования Billing API.

---

## 📋 Содержание

1. [Authentication Tests (4 теста)](#authentication-tests)
2. [Balance Tests (10 тестов)](#balance-tests)
3. [Profile Tests (7 тестов)](#profile-tests)
4. [Counter Tests (7 тестов)](#counter-tests)
5. [Test Execution Guide](#test-execution-guide)
6. [Test Result Interpretation](#test-result-interpretation)

---

## 🔐 Authentication Tests

### 📌 Общая информация

**Модуль:** Auth API  
**Тестовый класс:** `AuthApiTest.java`  
**Количество тестов:** 4  
**Эндпоинты:** `/api/auth/sign_up`, `/api/auth/sign_in`  
**Тип авторизации:** Публичные эндпоинты (не требуют токена)  
**Время выполнения:** ~2 сек

### ✅ TC-AUTH-001: Успешная регистрация пользователя

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-AUTH-001 |
| **Тест-метод** | `testSignUp_Success()` |
| **Эндпоинт** | `POST /api/auth/sign_up` |
| **Статус** | ✅ Активный |
| **Тип** | Позитивный (Positive) |
| **Приоритет** | 🔴 Высокий |

#### 📝 Описание

Проверяет возможность успешной регистрации нового пользователя с корректными данными.

#### 🎯 Предусловия

- API сервер запущен и доступен
- Эндпоинт не требует авторизации

#### 📋 Шаги

```
1. Генерируем уникальный username используя System.currentTimeMillis()
   └─ Пример: user_1735489234567

2. Подготавливаем JSON с данными регистрации
   └─ username: уникальное значение
   └─ password: "Password123!" (соответствует требованиям безопасности)
   └─ firstName: "Test"
   └─ lastName: "User"
   └─ telegramChatId: "123456789"

3. Отправляем POST запрос на /api/auth/sign_up без авторизации
   └─ Content-Type: application/json
   └─ Authorization: (отсутствует)

4. Проверяем ответ
```

#### ✅ Ожидаемый результат

```
HTTP Status Code: 200 или 201
Response Body:
{
  "code": "OK" или "CREATED",
  "message": "User registered successfully",
  "content": {
    "id": <number>,
    "username": "user_1735489234567",
    ...
  }
}
```

#### ❌ Возможные ошибки

| Ошибка | Причина | Решение |
|--------|--------|----------|
| 400 Bad Request | Некорректные данные | Проверить формат полей |
| 409 Conflict | Username уже используется | Использовать другой username |
| 500 Server Error | Ошибка на сервере | Перезапустить сервер |

#### 🔗 Связанные тесты

- TC-AUTH-002 (проверяет дублирование)

---

### ✅ TC-AUTH-002: Попытка регистрации с дублирующимся username

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-AUTH-002 |
| **Тест-метод** | `testSignUp_DuplicateUsername()` |
| **Эндпоинт** | `POST /api/auth/sign_up` |
| **Статус** | ✅ Активный |
| **Тип** | Негативный (Negative) |
| **Приоритет** | 🔴 Высокий |

#### 📝 Описание

Проверяет, что система не позволяет регистрировать пользователей с уже существующим username.

#### 🎯 Предусловия

- API сервер запущен и доступен
- В системе уже существует пользователь с определённым username

#### 📋 Шаги

```
1. Генерируем уникальный username (duplicate_user_<timestamp>)

2. Регистрируем первого пользователя
   └─ Отправляем POST /api/auth/sign_up с этим username
   └─ Ожидаем: 200/201 OK

3. Пытаемся зарегистрировать второго пользователя с ТЕМ ЖЕ username
   └─ Отправляем POST /api/auth/sign_up с идентичными данными
   └─ Ожидаем: 400 или 409

4. Проверяем ответ
```

#### ✅ Ожидаемый результат

```
Вторая попытка регистрации вернёт:
HTTP Status Code: 400 Bad Request или 409 Conflict

Response Body (примерно):
{
  "code": "ERROR",
  "message": "Username already exists",
  "content": null
}
```

#### ❌ Что НЕ должно происходить

❌ Вторая регистрация НЕ должна быть успешной (200/201)  
❌ В системе НЕ должно быть двух пользователей с одинаковым username  
❌ Ошибка НЕ должна быть 500 Server Error

#### 🔗 Связанные тесты

- TC-AUTH-001 (предварительная регистрация)

---

### ✅ TC-AUTH-003: Успешная авторизация и получение JWT токена

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-AUTH-003 |
| **Тест-метод** | `testSignIn_Success()` |
| **Эндпоинт** | `POST /api/auth/sign_in` |
| **Статус** | ✅ Активный |
| **Тип** | Позитивный (Positive) |
| **Приоритет** | 🔴 Высокий |

#### 📝 Описание

Проверяет возможность авторизации пользователя и получение действительного JWT токена.

#### 🎯 Предусловия

- API сервер запущен и доступен
- Пользователь зарегистрирован в системе

#### 📋 Шаги

```
1. Генерируем уникальный username и password
   └─ username: signin_test_<timestamp>
   └─ password: TestPassword123!

2. Регистрируем пользователя (TC-AUTH-001)
   └─ POST /api/auth/sign_up с этими credentials

3. Авторизуемся с этими же данными
   └─ POST /api/auth/sign_in
   └─ Body: {"username": "signin_test_...", "password": "TestPassword123!"}

4. Извлекаем и проверяем JWT токен
```

#### ✅ Ожидаемый результат

```
HTTP Status Code: 200 OK

Response Body:
{
  "code": "OK",
  "message": "Sign in successful",
  "content": {
    "id": 42,
    "username": "signin_test_1735489234567",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI0MiIs..."
  }
}

Структура JWT токена:
- Формат: Bearer token (3 части разделены точками)
- Используется для авторизации в других запросах
```

#### 🔐 JWT Токен

```
JWT структура:
[Header].[Payload].[Signature]

Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "42",
  "username": "signin_test_1735489234567",
  "iat": 1735489234,
  "exp": 1735575634  // Истечение через 24 часа
}
```

#### 💡 Использование токена

```java
String token = response.jsonPath().getString("content.token");

// Используется в авторизованных запросах:
RequestBuilder.authorizedGet(token)  // Передаём токен
    .when()
    .get("/api/admin/profile/all");
```

#### ❌ Возможные ошибки

| Ошибка | Причина | Решение |
|--------|--------|----------|
| 400 Bad Request | Отсутствует username или password | Проверить тело запроса |
| 401 Unauthorized | Неверный пароль | Проверить password |
| 404 Not Found | Пользователь не найден | Зарегистрировать пользователя |

---

### ✅ TC-AUTH-004: Попытка регистрации с отсутствующими обязательными полями

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-AUTH-004 |
| **Тест-метод** | `testSignUp_MissingRequiredFields()` |
| **Эндпоинт** | `POST /api/auth/sign_up` |
| **Статус** | ✅ Активный |
| **Тип** | Негативный (Negative) |
| **Приоритет** | 🟡 Средний |

#### 📝 Описание

Проверяет валидацию обязательных полей при регистрации пользователя.

#### 🎯 Предусловия

- API сервер запущен и доступен

#### 📋 Шаги

```
1. Подготавливаем Map с неполными данными
   └─ username: "test"  ✅ Заполнено
   └─ password: (отсутствует) ❌
   └─ firstName: (отсутствует) ❌
   └─ lastName: (отсутствует) ❌
   └─ telegramChatId: (отсутствует) ❌

2. Отправляем POST /api/auth/sign_up с этими данными

3. Проверяем, что API вернёт 400 Bad Request
```

#### ✅ Ожидаемый результат

```
HTTP Status Code: 400 Bad Request

Response Body:
{
  "code": "ERROR",
  "message": "Required fields are missing: password, firstName, lastName",
  "content": null
}
```

#### ✅ Обязательные поля

Согласно OpenAPI спецификации, следующие поля обязательны:

| Поле | Тип | Значение | Пример |
|------|-----|----------|--------|
| username | String | Уникальный идентификатор пользователя | "john_doe" |
| password | String | Пароль (мин. 8 символов, спец.символы) | "SecurePass123!" |
| firstName | String | Имя пользователя | "John" |
| lastName | String | Фамилия пользователя | "Doe" |
| telegramChatId | String | ID Telegram чата (опционально в спеке) | "123456789" |

---

## 💰 Balance Tests

### 📌 Общая информация

**Модуль:** Balance Management  
**Тестовый класс:** `BalanceApiTest.java`  
**Количество тестов:** 10  
**Эндпоинты:** `/api/balance/*`  
**Авторизация:** Требуется JWT токен администратора  
**Время выполнения:** ~3 сек

### ✅ TC-BALANCE-001: Получение баланса по ID

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-BALANCE-001 |
| **Тест-метод** | `testGetBalanceById_Success()` |
| **Эндпоинт** | `GET /api/balance/{id}` |
| **HTTP Method** | GET |
| **Требуется Auth** | ✅ Да (Bearer token) |
| **Тип** | Позитивный |
| **Приоритет** | 🔴 Высокий |

#### 📝 Описание

Получение информации о балансе конкретного пользователя по его ID.

#### 🎯 Предусловия

- Авторизирован как администратор
- В системе существует баланс с ID

#### 📋 Шаги

```
1. Инициализируем adminToken в globalSetup()
2. Получаем существующий ID баланса (из первой записи в /api/balance/all)
3. Отправляем GET /api/balance/{id}
   └─ Headers: Authorization: Bearer <token>
```

#### ✅ Ожидаемый результат

```
HTTP Status Code: 200 OK
Response Body:
{
  "code": "OK",
  "content": {
    "id": 1,
    "amount": 500.75,
    "userId": 10,
    "currency": "TJS",
    "createdAt": "2025-10-20T10:00:00",
    "updatedAt": "2025-10-27T14:30:00"
  }
}
```

---

### ✅ TC-BALANCE-002: Получение несуществующего баланса

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-BALANCE-002 |
| **Тест-метод** | `testGetBalanceById_NotFound()` |
| **Эндпоинт** | `GET /api/balance/{id}` |
| **Тип** | Негативный |
| **Приоритет** | 🟡 Средний |

#### 📝 Описание

Попытка получить баланс с несуществующим ID должна вернуть 404 Not Found.

#### 📋 Шаги

```
1. Отправляем GET /api/balance/999999999
   └─ ID очень большой, вероятно не существует
```

#### ✅ Ожидаемый результат

```
HTTP Status Code: 404 Not Found
```

---

### ✅ TC-BALANCE-003: Попытка GET без токена

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-BALANCE-003 |
| **Тест-метод** | `testGetBalanceById_Unauthorized()` |
| **Эндпоинт** | `GET /api/balance/{id}` |
| **Тип** | Безопасность (Security) |
| **Приоритет** | 🔴 Высокий |

#### 📝 Описание

Эндпоинты баланса требуют авторизации. Без токена должна вернуться 403 Forbidden.

#### ✅ Ожидаемый результат

```
HTTP Status Code: 403 Forbidden
Сообщение: "Missing or invalid Authorization header"
```

---

### ✅ TC-BALANCE-004: Получение всех балансов

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-BALANCE-004 |
| **Тест-метод** | `testGetAllBalances_Success()` |
| **Эндпоинт** | `GET /api/balance/all` |
| **Тип** | Позитивный |
| **Приоритет** | 🔴 Высокий |

#### ✅ Ожидаемый результат

```
HTTP Status Code: 200 OK
Response Body:
{
  "code": "OK",
  "content": [
    { "id": 1, "amount": 500.00, "userId": 10 },
    { "id": 2, "amount": 1000.50, "userId": 11 },
    { "id": 3, "amount": 750.25, "userId": 12 }
  ]
}
```

---

### ✅ TC-BALANCE-005: Получение всех балансов без токена

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-BALANCE-005 |
| **Тест-метод** | `testGetAllBalances_Unauthorized()` |
| **Эндпоинт** | `GET /api/balance/all` |
| **Тип** | Безопасность |

#### ✅ Ожидаемый результат

```
HTTP Status Code: 403 Forbidden
```

---

### 🐛 TC-BALANCE-006: UPDATE баланса с requestBody (KNOWN BUG)

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-BALANCE-006 |
| **Тест-метод** | `testUpdateBalance_AsPerSpecification()` |
| **Эндпоинт** | `PUT /api/balance/update/{id}` |
| **Статус** | 🐛 **БАГ ОБНАРУЖЕН** |
| **Bug ID** | BUG-001 |
| **Приоритет** | 🔴 Высокий |

#### 📝 Описание

Согласно спецификации, должен принимать requestBody с полем amount, но возвращает 400.

#### 🎯 Ожидается по спецификации

```
PUT /api/balance/update/1
Content-Type: application/json
Authorization: Bearer <token>

Request Body:
{
  "amount": 2500.75
}

Response:
HTTP/1.1 200 OK
{ "code": "OK", "content": { "amount": 2500.75 } }
```

#### 🔴 Текущее поведение

```
HTTP/1.1 400 Bad Request
{ "code": "ERROR", "message": "Invalid request", "content": null }
```

#### 💡 Решение

Смотрите [BUG_REPORTS.md](./BUG_REPORTS.md) #1

---

### ✅ TC-BALANCE-007: UPDATE несуществующего баланса

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-BALANCE-007 |
| **Тест-метод** | `testUpdateBalance_NotFound()` |
| **Эндпоинт** | `PUT /api/balance/update/{id}` |
| **Тип** | Негативный |

#### ✅ Ожидаемый результат

```
HTTP Status Code: 404 или 400
```

---

### ✅ TC-BALANCE-008: UPDATE без авторизации

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-BALANCE-008 |
| **Тест-метод** | `testUpdateBalance_Unauthorized()` |

#### ✅ Ожидаемый результат

```
HTTP Status Code: 403 Forbidden
```

---

### ✅ TC-BALANCE-009: UPDATE без поля amount

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-BALANCE-009 |
| **Тест-метод** | `testUpdateBalance_MissingAmount()` |
| **Тип** | Валидация |

#### ✅ Ожидаемый результат

```
HTTP Status Code: 400 Bad Request
Message: "Required field: amount"
```

---

### ✅ TC-BALANCE-010: UPDATE с некорректным типом amount

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-BALANCE-010 |
| **Тест-метод** | `testUpdateBalance_InvalidAmountType()` |
| **Тип** | Валидация |

#### 📝 Описание

Amount должен быть числом (double/float), а не строкой.

#### 📋 Шаги

```
1. Отправляем PUT с amount = "invalid_string"
```

#### ✅ Ожидаемый результат

```
HTTP Status Code: 400 Bad Request
Message: "Invalid type for field amount, expected number"
```

---

## 👤 Profile Tests

### 📌 Общая информация

**Модуль:** Profile Management  
**Тестовый класс:** `ProfileApiTest.java`  
**Количество тестов:** 7  
**Эндпоинты:** `/api/admin/profile/*`  
**Авторизация:** Требуется JWT токен администратора  
**Время выполнения:** ~2 сек

### 📋 MSISDN Формат

MSISDN (абонентский номер) должен соответствовать строгому формату:

```
Pattern: ^99680\d{7}$

Примеры ВАЛИДНЫХ номеров:
✅ 996800000001  (префикс 99680 + 7 цифр)
✅ 996805555555  (префикс 99680 + 7 цифр)
✅ 996809999999  (префикс 99680 + 7 цифр)

Примеры НЕВАЛИДНЫХ номеров:
❌ 996800123456   (только 6 цифр после префикса)
❌ 123456789012   (неправильный префикс)
❌ 99680123456    (всего 11 символов, нужно 12)
```

### 🐛 TC-PROFILE-001: Создание профиля (KNOWN BUG)

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-PROFILE-001 |
| **Тест-метод** | `testCreateProfile_Success()` |
| **Эндпоинт** | `POST /api/admin/profile/create` |
| **Статус** | 🐛 **БАГ: 201 вместо 200** |
| **Bug ID** | BUG-002 |
| **Приоритет** | 🟡 Средний |

#### 📝 Описание

Создание профиля абонента с новым MSISDN.

#### 🎯 Ожидается по спецификации

```
HTTP Status Code: 200 OK
```

#### 🔴 Текущее поведение

```
HTTP Status Code: 201 Created
```

#### ✅ Полный тест-кейс

```
1. Генерируем уникальный MSISDN
   └─ prefix = "99680"
   └─ random = System.currentTimeMillis() % 10000000
   └─ MSISDN = "99680" + String.format("%07d", random)

2. Подготавливаем данные
   └─ msisdn: "996800123456" (уникальный)
   └─ userId: 1
   └─ pricePlanId: 1

3. Отправляем POST /api/admin/profile/create

4. Проверяем:
   └─ Status: 200 или 201 (оба работают)
   └─ content.msisdn = "996800123456"
   └─ content.id заполнено (для cleanup)

5. Удаляем созданный профиль
```

#### ✅ Ожидаемый ответ

```
{
  "code": "OK",
  "message": "Profile created successfully",
  "content": {
    "id": 15,
    "msisdn": "996800123456",
    "userId": 1,
    "pricePlanId": 1,
    "status": "ACTIVE",
    "createdAt": "2025-10-27T14:30:00"
  }
}
```

---

### ✅ TC-PROFILE-002: Создание профиля с дублирующимся MSISDN

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-PROFILE-002 |
| **Тест-метод** | `testCreateProfile_DuplicateMsisdn()` |
| **Эндпоинт** | `POST /api/admin/profile/create` |
| **Тип** | Негативный |

#### ✅ Ожидаемый результат

```
HTTP Status Code: 400 Bad Request
Message: "MSISDN already exists"
```

---

### ✅ TC-PROFILE-003: Получение профиля по ID

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-PROFILE-003 |
| **Тест-метод** | `testGetProfileById_Success()` |
| **Эндпоинт** | `GET /api/admin/profile/{id}` |
| **Тип** | Позитивный |

---

### ✅ TC-PROFILE-004: Получение несуществующего профиля

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-PROFILE-004 |
| **Тест-метод** | `testGetProfileById_NotFound()` |
| **Эндпоинт** | `GET /api/admin/profile/{id}` |
| **Тип** | Негативный |

#### ✅ Ожидаемый результат

```
HTTP Status Code: 404 Not Found
```

---

### ✅ TC-PROFILE-005: Получение всех профилей

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-PROFILE-005 |
| **Тест-метод** | `testGetAllProfiles_Success()` |
| **Эндпоинт** | `GET /api/admin/profile/all` |
| **Тип** | Позитивный |

---

### ✅ TC-PROFILE-006: Обновление профиля

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-PROFILE-006 |
| **Тест-метод** | `testUpdateProfile_Success()` |
| **Эндпоинт** | `PUT /api/admin/profile/update/{id}` |
| **Тип** | Позитивный |

#### 📋 Шаги

```
1. Генерируем новый MSISDN (отличный от текущего)
2. Отправляем PUT с новыми данными
3. Проверяем, что MSISDN обновлён
4. Откатываем на исходный MSISDN
```

---

### 🐛 TC-PROFILE-007: Удаление профиля (KNOWN BUG)

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-PROFILE-007 |
| **Тест-метод** | `testDeleteProfile_StatusCode()` |
| **Эндпоинт** | `DELETE /api/admin/profile/delete/{id}` |
| **Статус** | 🐛 **БАГ: 204 вместо 200** |
| **Bug ID** | BUG-003 |
| **Приоритет** | 🟡 Средний |

#### 🎯 Ожидается по спецификации

```
HTTP Status Code: 200 OK
Response Body:
{
  "code": "OK",
  "message": "Profile deleted",
  "content": null
}
```

#### 🔴 Текущее поведение

```
HTTP Status Code: 204 No Content
Response Body: (пусто)
```

#### ✅ Полный тест-кейс

```
1. Создаём новый профиль (для удаления)
2. Удаляем профиль
3. Проверяем:
   └─ Status: 200 или 204 (оба работают)
   └─ Профиль действительно удалён
```

---

## 📊 Counter Tests

### 📌 Общая информация

**Модуль:** Counter Management  
**Тестовый класс:** `CounterApiTest.java`  
**Количество тестов:** 7  
**Эндпоинты:** `/api/admin/counter/*`  
**Авторизация:** Требуется JWT токен администратора  
**Тип операций:** Только READ (GET)

### ✅ TC-COUNTER-001: Получение счётчика по ID

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-COUNTER-001 |
| **Тест-метод** | `testGetCounterById_Success()` |
| **Эндпоинт** | `GET /api/admin/counter/{id}` |
| **Тип** | Позитивный |

---

### ✅ TC-COUNTER-002: Получение несуществующего счётчика

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-COUNTER-002 |
| **Тест-метод** | `testGetCounterById_NotFound()` |
| **Тип** | Негативный |

#### ✅ Ожидаемый результат

```
HTTP Status Code: 404 Not Found
```

---

### ✅ TC-COUNTER-003: Получение счётчика без авторизации

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-COUNTER-003 |
| **Тест-метод** | `testGetCounterById_Unauthorized()` |
| **Тип** | Безопасность |

#### ✅ Ожидаемый результат

```
HTTP Status Code: 403 Forbidden
```

---

### ✅ TC-COUNTER-004: Получение всех счётчиков

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-COUNTER-004 |
| **Тест-метод** | `testGetAllCounters_Success()` |
| **Эндпоинт** | `GET /api/admin/counter/all` |

---

### ✅ TC-COUNTER-005: Получение всех без авторизации

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-COUNTER-005 |
| **Тест-метод** | `testGetAllCounters_Unauthorized()` |

#### ✅ Ожидаемый результат

```
HTTP Status Code: 403 Forbidden
```

---

### ✅ TC-COUNTER-006: Получение активных счётчиков

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-COUNTER-006 |
| **Тест-метод** | `testGetAllActiveCounters_Success()` |
| **Эндпоинт** | `GET /api/admin/counter/all-active` |

#### 📝 Описание

Возвращает только счётчики в статусе ACTIVE.

#### ✅ Ожидаемый результат

```
HTTP Status Code: 200 OK или 204 No Content
Response Body (если 200):
{
  "code": "OK",
  "content": [
    { "id": 1, "status": "ACTIVE", ... },
    { "id": 3, "status": "ACTIVE", ... }
  ]
}
```

---

### ✅ TC-COUNTER-007: Получение активных без авторизации

| Параметр | Значение |
|----------|----------|
| **Test ID** | TC-COUNTER-007 |
| **Тест-метод** | `testGetAllActiveCounters_Unauthorized()` |

#### ✅ Ожидаемый результат

```
HTTP Status Code: 403 Forbidden
```

---

## 🧪 Test Execution Guide

### Запуск всех тестов

```bash
mvn clean test
```

### Запуск конкретного модуля

```bash
# Все тесты авторизации
mvn test -Dtest=AuthApiTest

# Все тесты балансов
mvn test -Dtest=BalanceApiTest

# Все тесты профилей
mvn test -Dtest=ProfileApiTest

# Все тесты счётчиков
mvn test -Dtest=CounterApiTest
```

### Запуск одного теста

```bash
# Успешная регистрация
mvn test -Dtest=AuthApiTest#testSignUp_Success

# Получение баланса
mvn test -Dtest=BalanceApiTest#testGetBalanceById_Success
```

### Запуск с параметрами

```bash
# С подробным выводом
mvn test -X

# Без остановки на ошибках (выполнить всё)
mvn test -DtestFailureIgnore=true

# С конкретной конфигурацией
mvn test -Dtest.config=production
```

---

## 📊 Test Result Interpretation

### Статусы результатов

```
✅ PASS    - Тест успешно пройден, всё работает как ожидается
❌ FAIL    - Тест не пройден, API ведёт себя иначе чем ожидается
⚠️  SKIP    - Тест пропущен (обычно из-за отсутствия предусловий)
🐛 BUG    - Тест обнаружил баг в API (помечен эмодзи 🐛)
```

### Интерпретация результатов

#### ✅ PASS - Все хорошо

```
Tests run: 28, Failures: 0, Errors: 0, Skipped: 0

✓ 28/28 тестов пройдено успешно
✓ API соответствует спецификации
✓ Никаких проблем обнаружено
```

#### ❌ FAIL - Есть проблемы

```
Tests run: 28, Failures: 2, Errors: 1, Skipped: 0

✗ BalanceApiTest.testUpdateBalance_AsPerSpecification()
  └─ Expected: 200 OK, Actual: 400 Bad Request
  
✗ ProfileApiTest.testCreateProfile_Success()
  └─ Expected: 200 OK, Actual: 201 Created
```

**Интерпретация:**
- Первый - это реальный баг (БАГ #1)
- Второй - это известный баг (БАГ #2)

#### 🐛 БАГ обнаружен - Это нормально!

Если тест помечен 🐛 в @DisplayName и падает с сообщением о баге:

```
🐛 БАГ: API не принимает requestBody как указано в спецификации!
   Ожидаем: 200 OK с requestBody {"amount": 2500.75}
   Получаем: 400 Bad Request
```

**Это означает:**
- ✓ Тест работает правильно
- ✓ Баг обнаружен и документирован
- ✓ Нужно создать issue для разработчиков

---

## 📈 Метрики и Отчётность

### Общие метрики

```
Всего тестов:           28
├─ Позитивные:         12
├─ Негативные:         13
└─ БАГ-тесты:           3

Успешные:              25/28 (89%)
└─ Содержат 3 известных бага

Время выполнения:       ~8 сек
```

### По модулям

```
Authentication:  4 тест (100% ✓)
Balance:        10 тестов (90% ✓, 1 баг)
Profile:         7 тестов (86% ✓, 2 бага)
Counter:         7 тестов (100% ✓)
```

### Покрытие эндпоинтов

```
GET:    15 тестов
POST:    6 тестов
PUT:     6 тестов
DELETE:  1 тест

Публичные:  4 теста
Админские: 24 теста
```

---

## 🔗 Полезные ссылки

- [Framework Documentation](./FRAMEWORK_DOCUMENTATION.md)
- [Bug Reports](./BUG_REPORTS.md)
- [README](./README.md)
- [OpenAPI Spec](./api.json)

---

**Документ версия:** 1.0.0  
**Последнее обновление:** 27 октября 2025  
**Мэйнтейнер:** QA Team
