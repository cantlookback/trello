# Trello Clone (Java + Maven)

Учебный backend-проект, реализующий упрощённую модель Trello.
Проект предназначен для изучения Java, Maven, JPA/Hibernate, Servlet API, тестирования и CI/CD.

## Цели проекта

- Практика архитектуры backend-приложений
- Работа с Maven (lifecycle, зависимости, multi-module в будущем)
- Настройка JPA и Hibernate без Spring
- Работа с PostgreSQL
- Понимание транзакций и persistence context
- Интеграционное тестирование
- Настройка CI через GitHub Actions

## Стек технологий

- Java 17
- Maven
- Jakarta Servlet API
- JPA (Jakarta Persistence)
- Hibernate
- PostgreSQL
- JUnit 5

## Архитектура

Проект построен по слоистой архитектуре:

```code
web → service → repository → JPA → PostgreSQL
```

## Слои

- `domain` — JPA-сущности (Board, BoardColumn, Card)
- `repository` — доступ к данным
- `service` — бизнес-логика и управление транзакциями
- `web` — HTTP-слой (Servlet)
- `config/util` — инфраструктурные компоненты

## Модель данных

```code
Board
└── Column
    └── Card
```

- Board — aggregate root
- Column и Card управляются через Board
- Используются каскады и orphanRemoval

## Запуск проекта

### 1. Поднять PostgreSQL

Пример через Docker:

```bash
docker run --name trello-pg \
    -e POSTGRES_DB=trello \
    -e POSTGRES_USER=trello \
    -e POSTGRES_PASSWORD=trello \
    -p 5432:5432 \
    -d postgres:16
```

### 2. Настроить `persistence.xml`

Проверить:

```code
jdbc:postgresql://localhost:5432/trello
user: trello
password: trello
```

### 3. Сборка WAR

```bash
mvn clean package
```

Файл появится в:

    target/trello.war

### 4. Деплой в Tomcat

Скопировать WAR в:

```code
<tomcat>/webapps
```

Запустить Tomcat:

```bash
<tomcat>/bin/startup.bat
```

После запуска приложение будет доступно по адресу:

```code
http://localhost:8080/trello
```

### Запуск тестов

```bash
mvn test
```

Тесты:

- Проверяют создание `Board`
- Проверяют добавление `Column`
- Проверяют добавление `Card`
- Проверяют каскады и связи

Для тестов используется отдельный `persistence.xml` в `src/test/resources`.

## Что изучается в проекте

### Maven

- Lifecycle
- Управление зависимостями
- Плагины
- Разделение main/test конфигураций

### JPA / Hibernate

- `@Entity`
- `@OneToMany` / `@ManyToOne`
- `CascadeType`
- `orphanRemoval`
- Транзакции
- Persistence Context
- LAZY vs EAGER
- `hbm2ddl`

### Тестирование

- JUnit 5
- Интеграционные тесты
- Проверка реальных операций с БД

### Планы по развитию

- Переход на Spring
- Multi-module структура
- JSON REST API
- Flyway миграции
- Docker Compose
- CI/CD пайплайн
- Покрытие тестами сервисного слоя
- Авторизация и роли пользователей

### Назначение проекта

Проект не является production-сервисом.
Он предназначен для системного изучения backend-разработки на Java.
