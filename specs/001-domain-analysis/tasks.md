# Tasks: 001-domain-analysis

## Phase 1: Implementation
- [x] T001 [US1] Створити docs/usecase.puml з трьома акторами й чотирма сценаріями
- [x] T002 [US1] Створити docs/class.mmd із типами зв’язків та членами класів
- [x] T003 [US1] Створити docs/sequence.mmd для успіху та відмови
- [x] T004 [US1] Створити docs/state.mmd з початковим і кінцевим станами
- [x] T005 [US2] Створити docs/crc.xlsx для всіх типів моделі
- [x] T006 [US3] Звірити docs з specs/001-domain-analysis/spec.md і візуально відобразити діаграми

## Dependencies
Послідовність згори вниз; документацію можна готувати паралельно з незалежним виглядом. MVP — US1 (US4 на етапі 004).

## Phase 2: Convergence

- [x] T007 CRITICAL. У docs/sequence.mmd і docs/sequence-print.puml розрізнити українською причини відмови для порожніх атрибутів, відсутнього чи повторного ID, невалідної дати, end <= start і перетину для одного місця. Зафіксувати незмінність ID, локальний ISO-час із точністю до хвилини, приймання суміжних інтервалів і одночасних інтервалів для різних місць та відсутність змін після відмови per Конституція I, FR-002, FR-003, US1, SC-002, T003 (contradicts)
- [x] T008 CRITICAL. Показати передавання AccessControl через конструктор або setter у docs/class.mmd і docs/class-print.puml та узгодити виклик у docs/sequence.mmd і docs/sequence-print.puml. Зберегти використання абстракції під час підтвердження per Конституція II, plan.md § Constitution Check (contradicts)
- [x] T009 Узгодити docs/sequence.mmd, docs/sequence-print.puml, docs/state.mmd і docs/state-print.puml. Показати надання доступу лише під час переходу з DRAFT за наявності інтервалу та відмови при повторному підтвердженні й зміні інтервалів у CONFIRMED або CANCELLED per FR-004, US1, T003–T004 (partial)
- [x] T010 Змоделювати в docs/sequence.mmd і docs/sequence-print.puml відмову для порожньої назви плану простору та заміну попереднього опису після приймання коректної назви per US2, SC-001 (partial)
- [x] T011 Показати в docs/sequence.mmd і docs/sequence-print.puml відмову у виставленні рахунку до підтвердження та імітаційний розрахунок за ставкою 150 грн/год для підтвердженого бронювання per FR-005, US3, SC-001 (partial)
- [x] T012 Зафіксувати в docs/class.mmd, docs/class-print.puml і docs/crc-data.json допустимі тарифи BASIC і FLEX та правило, за яким changeTariff змінює лише тариф наступного місяця per FR-005, US3, SC-001 (partial)
- [x] T013 Узгодити відповідальність Booking у docs/crc-data.json із діаграмою етапу 1. Перевірити наявність і вміст docs/crc.xlsx та за потреби відновити картки для всіх типів актуальної моделі per T005, plan.md § Project Structure, Конституція IV (partial)
- [x] T014 Звірити наявні матеріали візуальної перевірки з актуальними docs/usecase.puml, docs/class.mmd, docs/sequence.mmd і docs/state.mmd. Якщо їх бракує, виконати візуальну перевірку під час реалізації та зафіксувати фактичний результат per T006 (partial)

## Phase 3: Convergence

- [x] T015 CRITICAL. У docs/sequence.mmd і docs/sequence-print.puml покажіть окремі українські причини відмови для відсутнього ID, повторного ID та інших порожніх обов’язкових полів. Зафіксуйте, що після кожної відмови колекція не змінюється per Конституція I, FR-002, SC-002, T007 (contradicts)
- [x] T016 CRITICAL. У docs/sequence.mmd і docs/sequence-print.puml покажіть окремі українські причини відмови confirm() для порожнього DRAFT, повторного підтвердження та CANCELLED. Звірте гілки з docs/state.mmd і docs/state-print.puml per Конституція I, FR-004, US1, T009 (contradicts)
- [x] T017 У docs/sequence.mmd і docs/sequence-print.puml покажіть український результат зміни nextTariff для BASIC/FLEX і відмову для недопустимого тарифу. Зафіксуйте, що currentTariff не змінюється в обох випадках, а nextTariff після відмови зберігає попереднє значення per FR-005, US3, SC-001, T012 (partial)

## Phase 4: Convergence

- [x] T018 CRITICAL. У docs/sequence.mmd і docs/sequence-print.puml покажіть окрему українську відповідь для відсутнього ID, повторного ID та інших порожніх обов’язкових полів. Зафіксуйте, що після кожної з цих відмов колекція залишається незмінною per Конституція I, FR-002, SC-002, T015 (contradicts)
- [x] T019 Узгодьте docs/state.mmd і docs/state-print.puml з гілкою confirm() у docs/sequence.mmd і docs/sequence-print.puml. Покажіть перехід DRAFT → CONFIRMED лише за наявності інтервалу та впровадженого AccessControl, а за відсутності сервісу покажіть відмову зі збереженням DRAFT per FR-004, US1, T009, T016 (partial)
- [x] T020 Зафіксуйте в docs/class.mmd і docs/class-print.puml контракт Booking.getIntervals(), за яким зовнішній клієнт не може змінити внутрішню колекцію інтервалів через повернене значення per plan.md § Phase 1, data-model.md, T002 (partial)

## Phase 5: Convergence

- [ ] T021 CRITICAL. У docs/sequence.mmd і docs/sequence-print.puml покажіть окремі українські відповіді add(interval) для невалідної дати, порушення точності до хвилини, start >= end, перетину інтервалів для одного місця та спроби зміни у CONFIRMED або CANCELLED. Покажіть збереження колекції після кожної відмови per Конституція I, FR-002–FR-004, US1, SC-002, T007, T009 (contradicts)
- [ ] T022 CRITICAL. У docs/state.mmd і docs/state-print.puml сформулюйте українською умову confirm для переходу DRAFT → CONFIRMED зі збереженням програмних ідентифікаторів і перевірок на наявність інтервалу та впровадженого AccessControl per Конституція III, FR-004, T004 (contradicts)
- [ ] T023 Зафіксуйте в review-evidence/artifact_readback.json фактичний результат прямої візуальної перевірки актуальних docs/class.mmd, docs/sequence.mmd і docs/state.mmd, прив’язавши результат до версій перевірених джерел per T006, T014 (partial)
