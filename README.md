# Coworking — практика 4 (MVC)
Система бронювання коворкінгу, варіант19. Три актори й чотири початкові бізнес-сценарії збережені у доменних класах; вебінтерфейс керує інтервалами композиції Booking–TimeInterval.
## Запуск
JDK21+; Maven3.9.11. На цьому ПК Maven доступний за шляхом `..\tools\apache-maven-3.9.11\bin\mvn.cmd`.
```powershell
mvn test compile exec:java
```
Відкрити http://localhost:8080/ . Програма одразу запускає сервер, без консольного введення; зупинка Ctrl+C. Файл `target/coworking.db`. На першому запуску нової БД таблиця порожня. `mvn clean` видаляє target разом із даними; для збереження БД повторно запускайте без clean. Якщо порт зайнятий — звільніть його, програма не обирає інший.
## MVC
Модель: Booking, TimeInterval, TimeIntervalStorage, OperationResult. Перевіряє дані й повертає доменні результати, HTTP не знає. Сховище — єдине джерело даних, колекція перечитується після змін.
Контролер: CoworkingController(model,view), без SQL/Javalin/Jackson; отримує ту саму модель, яку Main взяв з Injector.
Вигляд: CoworkingWebView і WebResponse перетворюють доменні результати на HTTP; index.html, script.js, style.css показують дані й надсилають запити. Бізнес-перевірки клієнт не дублює.
Інфраструктура: SqliteTimeIntervalStorage, CoworkingModule, WebServer, JavalinWebServer. Лише адаптер знає Javalin і Jackson. Конструкторне DI для storage збережено; модель не Singleton, Connection і сервер Singleton. Стиль мінімалістичний, власний CSS без CDN.
## API
GET /api/coworking →200 items; POST /api/coworking →201; PUT /api/coworking/{id} →200; DELETE /api/coworking/{id} →200.
Тіло POST/PUT: `{"id":"T19","resident":"Олена","space":"Переговорна A","start":"2026-12-01T09:00","end":"2026-12-01T11:00"}`.
400 — невалідні дані/JSON; 404 — відсутній id; 409 — дубль, перетин або стан забороняє зміни. Відповідь має message українською. 500 — сервісна помилка без деталей SQL.
## Правила
Усі поля обов'язкові. ID≤80 символів, resident/space≤120. ID незмінний; унікальний. Час ISO local datetime до хвилини; start<end. Для одного місця без урахування регістру перетини [start,end) заборонені, суміжні дозволені. Різні місця можуть мати однаковий час. Зміни тільки DRAFT; Main створює DRAFT без автоматичного наповнення. Навчальна межа — один Booking на процес; авторизація та реальні платежі не реалізуються.
## Перевірка й авторство
`mvn test` перевіряє домен, SQLite, Guice і MVC. HTTP-логи та реальні знімки браузерних операцій у пакеті звіту. Використано Codex і Spec Kit0.15.0. Репозиторій локальний, результати не завантажено у Moodle.
