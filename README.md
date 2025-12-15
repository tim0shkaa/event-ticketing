# Event Ticketing System - Система бронирования билетов

## Описание проекта

Система бронирования билетов на события (Event Ticketing Lite) - REST API приложение для управления событиями, местами и заказами билетов. Реализована на Spring Boot с использованием Spring Data JPA и H2 Database.

### Реализованные функции

**Обязательный функционал:**
- ✅ Список событий (GET /api/events)
- ✅ Покупка билета с резервированием места (POST /api/orders)
- ✅ Просмотр деталей события и доступных мест (GET /api/events/{id})
- ✅ Отмена заказа (DELETE /api/orders/{id})

**Опциональный функционал:**
- ✅ Скидки в зависимости от возрастной группы (CHILD: 50%, STUDENT: 20%, SENIOR: 30%)
- ✅ Скидки в зависимости от времени покупки:
  - Более 30 дней до события: 20% скидка
  - 14-30 дней: 10% скидка
  - 7-14 дней: 5% скидка
  - Менее 7 дней: без скидки
- ✅ Категории мест (VIP: +50%, STANDARD: базовая цена, ECONOMY: -30%)
- ✅ Максимальная суммарная скидка ограничена 70%

## Модель данных

### Сущности и связи

1. **Event** (Событие)
   - id, name, description, eventDate, venue, basePrice
   - Связи:OneToMany → Seat, OneToMany → Order

2. **Seat** (Место)
   - id, seatNumber, category (VIP/STANDARD/ECONOMY), available
   - Связи: ManyToOne → Event, OneToOne → Ticket

3. **Order** (Заказ)
   - id, customerName, email, orderDate, totalPrice, status
   - Связи: ManyToOne → Event, OneToMany → Ticket

4. **Ticket** (Билет)
   - id, price, ageGroup, discountPercentage
   - Связи: OneToOne → Seat, ManyToOne → Order

## Установка и запуск

### Требования

- Java 17 или выше
- IntelliJ IDEA Community Edition (или любая другая IDE)

### Способ 1: Запуск через IntelliJ IDEA

1. Откройте IntelliJ IDEA
2. File → Open → выберите папку `event-ticketing`
3. Подождите, пока Gradle загрузит зависимости
4. Найдите класс `EventTicketingApplication.java`
5. Нажмите правой кнопкой → Run 'EventTicketingApplication'

### Способ 2: Запуск через командную строку

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

Приложение запустится на `http://localhost:8080`

## API Endpoints

### События (Events)

#### Получить все события
```http
GET http://localhost:8080/api/events
```

#### Получить предстоящие события
```http
GET http://localhost:8080/api/events/upcoming
```

#### Получить событие по ID
```http
GET http://localhost:8080/api/events/1
```

#### Поиск событий по названию
```http
GET http://localhost:8080/api/events/search?name=Rock
```

### Заказы (Orders)

#### Создать заказ (купить билеты)
```http
POST http://localhost:8080/api/orders
Content-Type: application/json

{
  "eventId": 1,
  "customerName": "Ivan Petrov",
  "email": "ivan@example.com",
  "tickets": [
    {
      "seatId": 1,
      "ageGroup": "ADULT"
    },
    {
      "seatId": 2,
      "ageGroup": "STUDENT"
    }
  ]
}
```

#### Получить заказ по ID
```http
GET http://localhost:8080/api/orders/1
```

#### Получить заказы по email
```http
GET http://localhost:8080/api/orders/email/ivan@example.com
```

#### Отменить заказ
```http
DELETE http://localhost:8080/api/orders/1
```

## Примеры использования

### Пример 1: Просмотр доступных событий

**Запрос:**
```bash
curl http://localhost:8080/api/events
```

**Ответ:**
```json
[
  {
    "id": 1,
    "name": "Rock Concert 2025",
    "description": "An amazing rock concert featuring top bands",
    "eventDate": "2025-01-14T12:00:00",
    "venue": "Central Stadium",
    "basePrice": 50.0,
    "totalSeats": 35,
    "availableSeats": 35
  }
]
```

### Пример 2: Покупка билета со скидками

**Запрос:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": 1,
    "customerName": "Anna Ivanova",
    "email": "anna@example.com",
    "tickets": [
      {
        "seatId": 6,
        "ageGroup": "STUDENT"
      }
    ]
  }'
```

**Ответ:**
```json
{
  "id": 1,
  "customerName": "Anna Ivanova",
  "email": "anna@example.com",
  "orderDate": "2024-12-15T10:30:00",
  "totalPrice": 36.0,
  "status": "CONFIRMED",
  "eventId": 1,
  "eventName": "Rock Concert 2025",
  "tickets": [
    {
      "id": 1,
      "seatNumber": "STD-1",
      "price": 36.0,
      "ageGroup": "STUDENT",
      "discountPercentage": 28.0
    }
  ]
}
```

**Расчет скидки:**
- Базовая цена: 50.0
- Категория STANDARD: ×1.0 = 50.0
- Скидка за возраст (STUDENT): ×0.8 = 40.0
- Скидка за раннюю покупку (30 дней): ×0.9 = 36.0
- Итоговая цена: 36.0 (28% общая скидка)

## H2 Console

Для просмотра базы данных в браузере:

1. Откройте http://localhost:8080/h2-console
2. Используйте настройки:
   - JDBC URL: `jdbc:h2:mem:ticketingdb`
   - User Name: `sa`
   - Password: (пусто)

## Тестирование

### Запуск всех тестов

```bash
# Windows
gradlew.bat test

# Linux/Mac
./gradlew test
```

### Покрытие тестами

Реализованы unit-тесты для:
- ✅ EventService (получение событий, обработка ошибок)
- ✅ OrderService (создание заказа, валидация, скидки)

## Валидация и обработка ошибок

### Валидация входных данных

- Email проверяется на корректность формата
- Все обязательные поля проверяются на null
- Цена должна быть положительной
- Список билетов не может быть пустым

### Обработка ошибок

**404 Not Found** - Ресурс не найден
```json
{
  "status": 404,
  "message": "Event not found with id: 999",
  "timestamp": "2024-12-15T10:30:00"
}
```

**409 Conflict** - Место недоступно
```json
{
  "status": 409,
  "message": "Seat A1 is not available",
  "timestamp": "2024-12-15T10:30:00"
}
```

**400 Bad Request** - Ошибка валидации
```json
{
  "email": "Invalid email format",
  "customerName": "Customer name is required"
}
```

## Демонстрация работы

### Тестовые данные

При запуске приложения автоматически создаются 3 события:
1. **Rock Concert 2025** (через 30 дней) - 50€
2. **Classical Music Evening** (через 15 дней) - 80€
3. **Comedy Show** (через 5 дней) - 30€

Каждое событие имеет 35 мест:
- 5 VIP мест
- 20 STANDARD мест
- 10 ECONOMY мест

### Сценарии тестирования

#### Сценарий 1: Просмотр событий и покупка билета

1. Получить список событий: `GET /api/events`
2. Просмотреть детали события: `GET /api/events/1`
3. Купить билет: `POST /api/orders` (см. пример выше)
4. Проверить заказ: `GET /api/orders/1`

#### Сценарий 2: Проверка скидок

1. Купить билет для ребенка (CHILD) - скидка 50%
2. Купить билет для студента (STUDENT) - скидка 20%
3. Купить билет для пенсионера (SENIOR) - скидка 30%
4. Купить билет за месяц до события - дополнительная скидка 20%

#### Сценарий 3: Обработка ошибок

1. Попытка купить занятое место - ошибка 409
2. Попытка купить билет на несуществующее событие - ошибка 404
3. Некорректный email - ошибка 400

## Архитектура

### Слои приложения

1. **Controller Layer** - обработка HTTP запросов
2. **Service Layer** - бизнес-логика и валидация
3. **Repository Layer** - доступ к данным
4. **Entity Layer** - модель данных

### Паттерны проектирования

- DTO Pattern - для передачи данных между слоями
- Repository Pattern - для работы с БД
- Service Layer Pattern - для бизнес-логики
- Exception Handling - глобальная обработка ошибок
