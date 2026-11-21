# Tasks: 004-mvc-web

## Phase 1: Implementation
- [x] T001 [US4] Доповнити model/Booking.java та TimeIntervalStorage.java CRUD і доменними результатами
- [x] T002 [US4] Додати controller/CoworkingController.java без залежностей від фреймворку
- [x] T003 [US4] Додати view/CoworkingWebView.java і WebResponse.java
- [x] T004 [US4] Додати infrastructure/WebServer.java, JavalinWebServer.java та прив’язку Guice
- [x] T005 [US4] Замінити Main.java і вилучити StaticHtmlExporter.java
- [x] T006 [US4] Створити public/index.html, script.js, style.css
- [x] T007 [US4] Оновити docs/class.mmd, sequence.mmd, component.puml і CRC
- [x] T008 [US4] Перевірити CRUD, відмови, перезапуск та README.md у браузері

## Dependencies
Послідовність згори вниз; документацію можна готувати паралельно з незалежним виглядом. MVP — US1 (US4 на етапі 004).

## Phase 2: Convergence

- [x] T009 CRITICAL Узгодити docs/class.mmd, docs/class-print.puml, docs/component.puml, docs/sequence.mmd, docs/sequence-print.puml і CRC з фактичними прив’язками Guice, шляхом повернення даних зі SQLite та співпрацею Main із MVC per Constitution IV, T007 (contradicts)
- [x] T010 Узгодити запис і перечитування для create/update/delete у Booking.java та SqliteTimeIntervalStorage.java, щоб відмова перечитування не залишала зміни в SQLite після відповіді 500 per plan: Phase 1 Design, US4 (partial)
- [x] T011 Виправити генерацію ID в Resident.book(booking, space, start, end), щоб один резидент міг забронювати різні місця з однаковим початком без штучного дубля per FR-003, US1 (partial)
- [x] T012 Відхиляти POST і PUT із додатковими JSON-токенами в JavalinWebServer.java та повертати 400 через CoworkingWebView per US4, contracts/api.md, T004 (partial)
- [x] T013 Зафіксувати в review-evidence браузерний CRUD, відмови та збереження запису після перезапуску вебпроцесу per T008, US4, plan: Technical Context (partial)
- [x] T014 Зафіксувати в review-evidence успіх і відмову для зміни плану та тарифу, включно зі збереженням поточного тарифу per US2, US3, SC-001, plan: Technical Context (partial)

## Phase 3: Convergence

- [x] T015 CRITICAL Узгодити маршрути повернення даних у docs/sequence.mmd і docs/sequence-print.puml з фактичним шляхом SQLite → SqliteTimeIntervalStorage → Booking та CoworkingWebView → CoworkingController → JavalinWebServer → сторінка, доповнити співпрацю CoworkingModule у docs/crc-data.json і docs/crc.xlsx та оновити відповідні рендери per Constitution IV, T007, T009 (contradicts)
- [x] T016 Зафіксувати в review-evidence фактичний перший запуск із новою порожньою SQLite, порожній список у браузері та завершення запуску з помилкою за зайнятого порту 8080 per US4, T008, plan: Technical Context (partial)
- [x] T017 Узгодити опис допустимих значень ID, резидента й місця в README.md з FR-002 і фактичною валідацією per FR-002, T008 (partial)
- [x] T018 Перевірити, чи використовується sources.txt, і вилучити застарілий список за відсутності споживача per T005, plan: Project Structure (unrequested)
