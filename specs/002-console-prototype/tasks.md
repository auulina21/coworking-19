# Tasks: 002-console-prototype

## Phase 1: Implementation
- [x] T001 [US1] Реалізувати модель інтервалів і Booking у src/ua/coworking/model
- [x] T002 [US1] Перевірити час, дублікати, перетин та переходи в model/Booking.java
- [x] T003 [US2] Реалізувати CoworkingMember, SpaceManager і TariffPlan у src/ua/coworking/model
- [x] T004 [US3] Реалізувати Resident, Accountant та AccessControl у src/ua/coworking/model
- [x] T005 [US3] Створити демонстраційний src/ua/coworking/Main.java і README.md
- [x] T006 [US1] Скомпілювати й запустити сценарії; звірити docs/class.mmd

## Dependencies
Послідовність згори вниз; документацію можна готувати паралельно з незалежним виглядом. MVP — US1 (US4 на етапі 004).

## Phase 2: Convergence

- [x] T007 CRITICAL Узгодити docs/sequence.mmd і docs/sequence-print.puml із Main.java, Booking.confirm() та Accountant.invoice(): показати впровадження AccessControl у точці входу, відмову без сервісу та виклик hours() лише після перевірки CONFIRMED per Constitution IV, T006 (contradicts)
- [x] T008 Доповнити демонстраційну перевірку в src/ua/coworking/Main.java випадками порожнього ID, невалідної дати, ненульових секунд і відмов після скасування. Зафіксувати фактичні результати компіляції та запуску в review-evidence/ per T006, plan: фактичний запуск сценаріїв і відмов, FR-002, FR-004 (partial)
- [x] T009 Усунути колізію автоматичних ID у src/ua/coworking/model/Resident.java для одного резидента й однакового початку на різних місцях. Перевірити прийняття обох інтервалів через перевантаження book із автоматичним ID per FR-003, US1 (partial)
- [x] T010 Переглянути ліміти 80/120/120 у src/ua/coworking/model/Booking.java та вилучити обмеження, для яких немає вимоги per FR-002, T002 (unrequested)
- [x] T011 Переглянути потребу у відстежуваному sources.txt і усунути абсолютні шляхи до іншої копії проєкту per T005, plan: структура проєкту (unrequested)

## Phase 3: Convergence

- [x] T012 CRITICAL Узгодити docs/state.mmd і docs/state-print.puml із Booking.confirm(): додати впроваджений AccessControl до умов переходу DRAFT → CONFIRMED і показати збереження DRAFT при відмові per Constitution IV, FR-004, T006 (contradicts)
- [x] T013 CRITICAL Позначити конструктор CoworkingMember як protected у docs/class.mmd і docs/class-print.puml відповідно до CoworkingMember.java per Constitution IV, T006 (contradicts)
- [x] T014 CRITICAL Доповнити src/ua/coworking/Main.java демонстрацією відмови в підтвердженні непорожнього DRAFT без AccessControl і відмови при порожньому обов’язковому полі, відмінному від ID. Підтвердити збереження стану та колекції відповідно й зафіксувати фактичний запуск у review-evidence/ per Constitution V, T006, T008 (partial)
- [x] T015 Забезпечити у src/ua/coworking/model/Booking.java формат ISO local datetime до хвилини без явно заданих секунд чи дробових частин. Перевірити відмову для значень із :00 та .000 і прийняття формату HH:mm per FR-002, SC-002, T002 (partial)
- [x] T016 Прибрати другий Booking із src/ua/coworking/Main.java. Продемонструвати прийняття автоматичних ID для різних місць з однаковим початком в одному Booking і зберегти перевірку рахунку 600 грн за 4 години per spec Assumptions, plan Technical Context, T009 (unrequested)

## Phase 4: Convergence

- [ ] T017 CRITICAL Узгодити лінії життя, виклики й відповіді в docs/sequence.mmd та docs/sequence-print.puml із src/ua/coworking/Main.java і моделлю. Показати делегування Resident.book() до Booking.add(), виклик SpaceManager.manageLayout() з точки входу та повернення результату Accountant.invoice() у точку входу для виведення per Constitution IV, T006, US1–US3 (contradicts)
- [ ] T018 CRITICAL Доповнити демонстрацію в src/ua/coworking/Main.java перевіркою переходу DRAFT → CANCELLED і відмови Accountant.invoice() після скасування. Зберегти обмеження одного Booking на процес, за потреби використавши окремий запуск демонстрації, та зафіксувати фактичні результати в review-evidence/ per Constitution V, FR-004, US3, T006 (partial)
