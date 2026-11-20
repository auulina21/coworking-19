# Coworking — практика 3, гілка di
Система бронювання коворкінгу, варіант19. Попередні актори й сценарії збережені; композиція Booking–TimeInterval тепер зберігається в SQLite.
## Збирання і запуск
JDK21+ і Maven3.9.11. Якщо Maven не у PATH, можна скористатися `..\tools\apache-maven-3.9.11\bin\mvn.cmd` у робочому каталозі на цьому ПК.
```powershell
mvn clean test compile exec:java
```
Файл `target/coworking.db`, таблиця `time_intervals`. Після сценаріїв `target/intervals.html` формується з даних повторного читання SQLite. Відкрийте HTML браузером через локальний статичний сервер.
Повторний запуск зберігає наявні дані; дублі отримують відмову. Для чистої демонстрації `mvn clean` видаляє попередній target. Не запускайте clean, якщо потрібно зберегти введені раніше дані.
## DIP і DI
Booking залежить від TimeIntervalStorage. @Inject-конструктор отримує інтерфейс у final-поле. CoworkingModule прив'язує його до SqliteTimeIntervalStorage, а @Provides @Singleton надає Connection. Main отримує Booking через Injector; модель не Singleton. SQL є тільки в інфраструктурному сховищі. Одне INSERT — атомарна транзакція SQLite в autoCommit.
Інтервали: незмінний id, обов'язкові resident/space/start/end, локальний ISO-час до хвилини, start<end, заборона перетинів [start,end) для одного місця. Сценарії підтвердження, рахунку, тарифу й плану збережено.
Використано Codex, Spec Kit0.15.0. Результати залишені локально за дорученням власника.

Швидкий запуск на цьому ПК: `powershell -NoProfile -File .\run.ps1`. Для окремого розпакування потрібні JDK21+ і Maven (для 3–4).
