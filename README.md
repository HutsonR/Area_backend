# Area Backend

Проект представляет собой backend-сервис, написанный на Kotlin с использованием Ktor. Он предоставляет REST API для работы с турами, местами и историями. Для доступа к базе данных используется Exposed, таблицы определены через UUIDTable, а миграции – Flyway (или ручное управление через SQL).  

---

- **API Endpoints (not actual):**  
  - **GET /tours** — получить список туров (опционально с параметром `limit`).  
  - **GET /tours/{id}** — получить детальную информацию о туре, включая связанные истории.  
  - **PUT /tours/{id}** — обновить тур, включая синхронизацию историй (обновление, добавление, удаление).
  - **POST /tts** — получить аудио озвучку текста истории.

- **База данных (not actual):**  
  Используется PostgreSQL. Таблицы создаются через Exposed:  
  - `tours` (таблица туров)  
  - `histories` (истории, с внешним ключом на `tours`)  
  - `places` (достопримечательности)  
  Подключение настраивается через YAML-конфиг

- **Миграции:**  
  Flyway используется для миграций. Скрипты-модели миграций (например, добавление нового поля) храните в `src/main/resources/db/migration`. При старте вызывается `Flyway.migrate()` и выполняется миграция только для новых скриптов.

- **Логирование:**  
  Логирование организовано с помощью Kotlin Logging.

---

## Структура проекта (not actual)

```
AreaBackend/
├── routes/               // Определение HTTP-маршрутов (Ktor routes)
├── data/
│   ├── db/           // Таблицы (Exposed) и DatabaseFactory (инициализация базы, миграций)
│   └── repository/   // Реализации и интерфейсы репозиториев
├── di/               // DI-модули (например, NetworkModule для Retrofit, если требуется)
├── models/           // Data классы
└── Application.kt     // Точка входа (Ktor EngineMain)
```

---

## Зависимости

- [Ktor](https://ktor.io)  
- [Exposed](https://github.com/JetBrains/Exposed)  
- PostgreSQL JDBC Driver (`org.postgresql:postgresql`)  
- Flyway
- Kotlin Logging (`io.github.microutils:kotlin-logging`)
- Client (io.ktor:ktor-client-core)
