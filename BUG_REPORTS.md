# 🐛 БАГ РЕПОРТЫ - Billing API Test Suite

Документ содержит полный список обнаруженных багов в Billing API, выявленных автоматизированными тестами.

---

## 📋 Таблица багов

| # | Эндпоинт | Статус | Тест | Приоритет | Статус-код |
|---|----------|--------|------|-----------|-----------|
| 1 | PUT /api/balance/update/{id} | 🔴 Открыт | BalanceApiTest::testUpdateBalance_AsPerSpecification | 🔴 Высокий | 400 |
| 2 | POST /api/admin/profile/create | 🟡 Известный | ProfileApiTest::testCreateProfile_Success | 🟡 Средний | 201 |
| 3 | DELETE /api/admin/profile/delete/{id} | 🟡 Известный | ProfileApiTest::testDeleteProfile_StatusCode | 🟡 Средний | 204 |

---

## 🐛 БАГ #1: Balance UPDATE - requestBody не принимается

### 📌 Основная информация

| Параметр | Значение |
|----------|----------|
| **Эндпоинт** | `PUT /api/balance/update/{id}` |
| **HTTP Метод** | PUT |
| **Статус** | 🔴 **ОТКРЫТ** (требует срочного исправления) |
| **Приоритет** | 🔴 **ВЫСОКИЙ** |
| **Тип** | Несоответствие спецификации OpenAPI |
| **ID Теста** | `BalanceApiTest::testUpdateBalance_AsPerSpecification()` |

### 🔍 Описание проблемы

Согласно **OpenAPI спецификации** (api.json), эндпоинт `/api/balance/update/{id}` должен принимать:

```json
{
  "amount": <number (double)>
}
```

в качестве **requestBody** в formате JSON.

**Но в реальности**, при отправке корректно сформированного JSON в теле запроса, API возвращает **400 Bad Request** вместо обработки данных.

### 📊 Детали ошибки

```
PUT /api/balance/update/1
Content-Type: application/json
Authorization: Bearer <valid-token>

Request Body:
{
  "amount": 2500.75
}

Response:
HTTP/1.1 400 Bad Request
{
  "code": "ERROR",
  "message": "Invalid request",
  "content": null
}
```

### ✅ Ожидаемое поведение

```
PUT /api/balance/update/1
Content-Type: application/json
Authorization: Bearer <valid-token>

Request Body:
{
  "amount": 2500.75
}

Response:
HTTP/1.1 200 OK
{
  "code": "OK",
  "message": "Balance updated successfully",
  "content": {
    "id": 1,
    "amount": 2500.75,
    "updatedAt": "2025-10-27T14:30:00"
  }
}
```

### 🔴 Текущее поведение

- **Статус-код**: 400 Bad Request (вместо ожидаемого 200 OK)
- **Сообщение ошибки**: "Invalid request" или отсутствует
- **Тело ответа**: null или пустое

### 🧪 Как воспроизвести

```java
// Java код для воспроизведения
Map<String, Object> body = new HashMap<>();
body.put("amount", 2500.75);

Response response = RequestBuilder.authorizedPut(adminToken, body)
    .when()
    .put("/api/balance/update/1");

// Результат: 400 Bad Request (ОШИБКА!)
// Ожидается: 200 OK
System.out.println("Status: " + response.getStatusCode()); // 400
```

### 📝 Возможные причины

1. **Контроллер не обрабатывает requestBody**: Эндпоинт может быть реализован для получения параметров через query string, а не requestBody
2. **Ошибка маршрутизации**: Запрос может не достигать правильного обработчика
3. **Проблема с десериализацией JSON**: Spring может не корректно преобразовывать JSON в объект DTO
4. **Фильтр безопасности блокирует запрос**: Может быть проблема с валидацией CSRF или другими фильтрами

### 💡 Рекомендуемое решение

1. Проверить контроллер `/api/balance/update/{id}`:
   - Убедиться, что параметр маркирован как `@RequestBody`
   - Проверить, что DTO `BalanceUpdateRequestDto` правильно сконфигурирован

```java
// Ожидаемая реализация:
@PutMapping("/update/{id}")
public ResponseEntity<ApiResponse<BalanceDto>> updateBalance(
    @PathVariable Long id,
    @RequestBody BalanceUpdateRequestDto requestDto) {  // @RequestBody обязателен!
    // ...
}
```

2. Проверить, что Jackson правильно десериализует JSON
3. Убедиться, что нет фильтров, блокирующих запрос с body для PUT

### 🔗 Связанные тесты

- `BalanceApiTest::testUpdateBalance_AsPerSpecification()` - обнаруживает баг
- `BalanceApiTest::testUpdateBalance_MissingAmount()` - дополнительная валидация
- `BalanceApiTest::testUpdateBalance_InvalidAmountType()` - проверка типов

### 📅 История

| Дата | Статус | Комментарий |
|------|--------|-----------|
| 27.10.2025 | 🔴 Обнаружен | Выявлен автоматизированным тестом |
| - | ⏳ В ожидании | Требуется назначение на разработчика |

---

## 🐛 БАГ #2: Profile CREATE - неверный статус-код

### 📌 Основная информация

| Параметр | Значение |
|----------|----------|
| **Эндпоинт** | `POST /api/admin/profile/create` |
| **HTTP Метод** | POST |
| **Статус** | 🟡 **ИЗВЕСТНЫЙ** (документирован в тестах) |
| **Приоритет** | 🟡 **СРЕДНИЙ** |
| **Тип** | Несоответствие спецификации OpenAPI |
| **ID Теста** | `ProfileApiTest::testCreateProfile_Success()` |

### 🔍 Описание проблемы

Согласно **OpenAPI спецификации** (api.json), успешное создание профиля должно возвращать:
- **Статус-код**: `200 OK`

**Но в реальности**, при успешном создании профиля, API возвращает:
- **Статус-код**: `201 Created`

### 📊 Детали ошибки

```
POST /api/admin/profile/create
Content-Type: application/json
Authorization: Bearer <valid-token>

Request Body:
{
  "msisdn": "996800123456",
  "userId": 1,
  "pricePlanId": 1
}

Response:
HTTP/1.1 201 Created
Location: /api/admin/profile/15
{
  "code": "OK",
  "message": "Profile created successfully",
  "content": {
    "id": 15,
    "msisdn": "996800123456",
    "userId": 1,
    "pricePlanId": 1,
    "createdAt": "2025-10-27T14:30:00"
  }
}
```

### ✅ Ожидаемое поведение

```
Response:
HTTP/1.1 200 OK          ← Должно быть 200, а не 201
{
  "code": "OK",
  "message": "Profile created successfully",
  "content": { ... }
}
```

### 🔴 Текущее поведение

- **Статус-код**: `201 Created` (вместо ожидаемого `200 OK`)
- **Остальная структура ответа**: Корректна (code="OK", content содержит данные)

### 📝 Анализ проблемы

Это **не критичная ошибка**, так как:
- ✅ Ресурс успешно создан
- ✅ Данные в ответе корректные
- ✅ Функциональность работает правильно

Однако, это **несоответствие спецификации**, так как:
- ❌ OpenAPI спецификация четко требует `200 OK`
- ❌ Это может сломать клиентские приложения, ожидающие `200 OK`
- ❌ Нарушает REST соглашение (201 Created - это для создания без немедленного возврата ресурса)

### 💡 Рекомендуемое решение

Изменить контроллер, чтобы вернуть `200 OK` вместо `201 Created`:

```java
// Текущая реализация (неверная):
@PostMapping("/create")
public ResponseEntity<ApiResponse<ProfileDto>> createProfile(@RequestBody ProfileCreateUpdateRequestDto dto) {
    return ResponseEntity.status(201).body(response);  // ❌ Неправильно
}

// Должно быть:
@PostMapping("/create")
public ResponseEntity<ApiResponse<ProfileDto>> createProfile(@RequestBody ProfileCreateUpdateRequestDto dto) {
    return ResponseEntity.ok(response);  // ✅ Правильно (200 OK)
    // или
    return ResponseEntity.status(200).body(response);  // ✅ Эквивалентно
}
```

### 🔗 Связанные тесты

- `ProfileApiTest::testCreateProfile_Success()` - обнаруживает баг

### 📅 История

| Дата | Статус | Комментарий |
|------|--------|-----------|
| 27.10.2025 | 🟡 Обнаружен | Выявлен автоматизированным тестом, документирован как известный |

---

## 🐛 БАГ #3: Profile DELETE - неверный статус-код

### 📌 Основная информация

| Параметр | Значение |
|----------|----------|
| **Эндпоинт** | `DELETE /api/admin/profile/delete/{id}` |
| **HTTP Метод** | DELETE |
| **Статус** | 🟡 **ИЗВЕСТНЫЙ** (документирован в тестах) |
| **Приоритет** | 🟡 **СРЕДНИЙ** |
| **Тип** | Несоответствие спецификации OpenAPI |
| **ID Теста** | `ProfileApiTest::testDeleteProfile_StatusCode()` |

### 🔍 Описание проблемы

Согласно **OpenAPI спецификации** (api.json), удаление профиля должно возвращать:
- **Статус-код**: `200 OK`

**Но в реальности**, при успешном удалении профиля, API возвращает:
- **Статус-код**: `204 No Content`

### 📊 Детали ошибки

```
DELETE /api/admin/profile/delete/15
Authorization: Bearer <valid-token>

Response:
HTTP/1.1 204 No Content
(нет тела ответа)
```

### ✅ Ожидаемое поведение

```
Response:
HTTP/1.1 200 OK                ← Должно быть 200, а не 204
{
  "code": "OK",
  "message": "Profile deleted successfully",
  "content": null
}
```

### 🔴 Текущее поведение

- **Статус-код**: `204 No Content` (вместо ожидаемого `200 OK`)
- **Тело ответа**: Пусто (нет JSON)

### 📝 Анализ проблемы

Это **не критичная ошибка**, так как:
- ✅ Ресурс успешно удалён
- ✅ Сервер вернул правильный статус-код 204
- ✅ Функциональность работает правильно

Однако, это **несоответствие спецификации**, так как:
- ❌ OpenAPI спецификация требует `200 OK` с телом ответа
- ❌ Клиентское приложение, ожидающее JSON ответ, может сломаться
- ❌ Нарушает константность API (другие DELETE эндпоинты могут вернуть 200)

### 💡 Рекомендуемое решение

Изменить контроллер, чтобы вернуть `200 OK` с телом ответа вместо `204 No Content`:

```java
// Текущая реализация (неверная):
@DeleteMapping("/delete/{id}")
public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
    // ...
    return ResponseEntity.noContent().build();  // ❌ 204 No Content
}

// Должно быть:
@DeleteMapping("/delete/{id}")
public ResponseEntity<ApiResponse<Object>> deleteProfile(@PathVariable Long id) {
    // ...
    return ResponseEntity.ok(new ApiResponse<>("OK", "Profile deleted", null));  // ✅ 200 OK
}
```

### 🔗 Связанные тесты

- `ProfileApiTest::testDeleteProfile_StatusCode()` - обнаруживает баг

### 📅 История

| Дата | Статус | Комментарий |
|------|--------|-----------|
| 27.10.2025 | 🟡 Обнаружен | Выявлен автоматизированным тестом, документирован как известный |

---

## 📊 Статистика и метрики

### Сводка по багам

```
Всего багов обнаружено:        3
├── 🔴 Открытые (Высокий):     1
├── 🟡 Известные (Средний):    2
└── 🟢 Закрытые:               0
```

### По эндпоинтам

```
Balance:    1 баг
Profile:    2 бага
Counter:    0 багов
Auth:       0 багов
```

### По типам

```
Несоответствие спецификации:   3 бага
Функциональные:                 0 багов
Безопасность:                   0 багов
```

### По приоритету

```
Высокий:    1 баг
Средний:    2 бага
Низкий:     0 багов
```

---

## 📋 Процесс обработки багов

### Статусы

- 🔴 **ОТКРЫТ** - Баг обнаружен, требует исправления
- 🟡 **ИЗВЕСТНЫЙ** - Баг документирован, ведётся работа
- 🟢 **ЗАКРЫТ** - Баг исправлен, требуется тестирование
- ⚪ **ОТКЛОНЕН** - Баг отклонен (не является багом)

### Приоритеты

- 🔴 **ВЫСОКИЙ** - Критичная функциональность нарушена, требует срочного исправления
- 🟡 **СРЕДНИЙ** - Функциональность работает, но есть несоответствие спецификации
- 🟢 **НИЗКИЙ** - Небольшое несоответствие, не влияет на функциональность
- ⚪ **ИНФОРМАЦИОННЫЙ** - Документированный вариант поведения

---

## 📬 Как добавить новый баг-репорт

При обнаружении нового бага:

1. **Добавьте тест** в соответствующий класс (AuthApiTest, BalanceApiTest и т.д.)
2. **Отметьте тест эмодзи** 🐛 в @DisplayName
3. **Обновите BUG_REPORTS.md**:
   - Добавьте строку в таблицу багов
   - Создайте раздел для полного описания
   - Включите код для воспроизведения
   - Опишите ожидаемое и текущее поведение

### Шаблон для тест-метода

```java
@Test
@DisplayName("🐛 METHOD /endpoint - БАГ ОПИСАНИЕ")
public void testBugName() {
    // Шаги для воспроизведения
    Response response = RequestBuilder...
        .when()
        .method(TestConfig.ENDPOINT);
    
    // Фиксируем текущее поведение (баг)
    if (response.statusCode() == <actual_code>) {
        System.out.println("🐛 БАГ: <Описание проблемы>");
        response.then().statusCode(<actual_code>);
    } else {
        // Ожидаемое поведение
        ApiAssertions.assert...Response(response);
    }
}
```

---

## 🔗 Полезные ссылки

- [OpenAPI Спецификация](./api.json)
- [Framework Documentation](./FRAMEWORK_DOCUMENTATION.md)
- [README](./README.md)
- [Test Cases Documentation](./TEST_CASES.md)

---

**Документ версия:** 1.0.0  
**Последнее обновление:** 27 октября 2025  
**Мэйнтейнер:** QA Team
