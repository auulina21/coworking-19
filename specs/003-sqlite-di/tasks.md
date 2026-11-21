# Tasks: 003-sqlite-di

## Phase 1: Implementation
- [x] T001 [US1] Перетворити структуру src на Maven src/main/java і додати pom.xml
- [x] T002 [US1] Додати model/TimeIntervalStorage.java та infrastructure/SqliteTimeIntervalStorage.java
- [x] T003 [US1] Додати infrastructure/CoworkingModule.java з @Provides і @Inject Booking
- [x] T004 [US1] Прочитати БД й експортувати StaticHtmlExporter.java
- [x] T005 [US2] Оновити docs/class.mmd, docs/sequence.mmd та CRC
- [x] T006 [US3] Запустити Maven, перевірити БД, HTML і README.md

## Dependencies
Послідовність згори вниз; документацію можна готувати паралельно з незалежним виглядом. MVP — US1 (US4 на етапі 004).

## Phase 2: Convergence

- [x] T007 CRITICAL Узгодити docs/class.mmd, docs/class-print.puml, docs/sequence.mmd, docs/sequence-print.puml, docs/crc-data.json і docs/crc.xlsx із фактичним DI та поведінкою Booking.java. Показати final storage, початкове читання, перевірку з гілкою відмови до запису, перечитування після запису та виклик grant під час підтвердження згідно з Конституцією IV і T005 (contradicts).
- [x] T008 Усунути колізію автоматичних ID для різних місць з однаковим часом початку в src/main/java/ua/coworking/model/Resident.java та перевірити цей шлях через перевантажений book у BookingTest.java згідно з FR-003 і US1 (partial).
- [x] T009 Узгодити стан БД, колекцію та результат Booking.add() у src/main/java/ua/coworking/model/Booking.java при помилці читання після запису. Перевірити цей сценарій у BookingTest.java згідно з plan.md, Phase 1 (partial).
- [x] T010 Зафіксувати в review-evidence/ звірку рядків SQLite з нового з’єднання та значень у target/intervals.html, включно з екрануванням HTML, згідно з T006 і plan.md, Technical Context (partial).
- [x] T011 Переглянути ліміти довжини id, resident і space у src/main/java/ua/coworking/model/Booking.java та прибрати необґрунтовані обмеження згідно з FR-002 (unrequested).

## Phase 3: Convergence

- [x] T012 CRITICAL Узгодити docs/sequence.mmd, docs/sequence-print.puml, docs/state.mmd і docs/state-print.puml з Main.java, CoworkingModule.java та Booking.java. Показати експорт через сховище поточного інжектора, окрему тестову перевірку після повторного відкриття БД, передачу винятку при збої сховища та умову наявності AccessControl під час підтвердження згідно з Конституцією IV, T005 і T007 (contradicts).
- [x] T013 Узгодити результат транзакції та колекцію в src/main/java/ua/coworking/infrastructure/SqliteTimeIntervalStorage.java і src/main/java/ua/coworking/model/Booking.java при збої відновлення autocommit після commit і при невдалому rollback. Покрити обидва шляхи регресійними перевірками в src/test/java/ua/coworking/BookingTest.java згідно з plan.md, Phase 1 і T009 (partial).
- [x] T014 Узгодити демонстраційні сценарії в src/main/java/ua/coworking/Main.java та README.md для порожньої й наявної БД. Зберегти дані повторного запуску та виконувати перевірку дублів у DRAFT згідно з SC-001 і T006 (partial).
- [x] T015 Оновити опис транзакції в README.md, відобразивши вставку й перечитування в одному TimeIntervalStorage.inTransaction(work) згідно з plan.md, Phase 1 і T006 (partial).

## Phase 4: Convergence

- [ ] T016 CRITICAL Узгодити docs/sequence.mmd і docs/sequence-print.puml з Booking.add та SqliteTimeIntervalStorage.inTransaction. Показати оновлення колекції всередині work до commit, її відновлення при винятку та поширення винятку до виклику. Перекласти пояснення переходів у docs/state.mmd і docs/state-print.puml українською та обмежити картку TimeIntervalStorage у docs/crc-data.json і docs/crc.xlsx контрактом 003 згідно з Конституцією III–IV, T005, T007 і T012 (contradicts).
- [ ] T017 Додати в src/test/java/ua/coworking/BookingTest.java окремі перевірки відмов для календарно неможливої дати, порожнього обов’язкового поля за валідного ID та рівних меж інтервалу. Підтвердити збереження попереднього стану БД і колекції та зафіксувати фактичний результат у review-evidence/ згідно з FR-002, SC-002 і plan.md § Technical Context (partial).
- [ ] T018 У README.md описати вставку й перечитування в одному TimeIntervalStorage.inTransaction(work), фіксацію успіху та відновлення колекції при винятку згідно з plan.md § Phase 1, T006 і T015 (partial).
