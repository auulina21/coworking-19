# Модель даних
Booking(id, state, intervals): композиція 1 → 0..* TimeInterval. Інтервали мають id, resident, space, start, end. Ідентифікатор є первинним ключем, start/end — ISO local datetime. Колекція не повертається змінюваною назовні.
Стани DRAFT→CONFIRMED→CANCELLED; DRAFT→CANCELLED. У CONFIRMED/CANCELLED зміна інтервалів заборонена.
CoworkingMember(id,name) — абстрактний; Resident(currentTariff,nextTariff), SpaceManager(layout), Accountant — реалізують TariffPlan. Booking залежить від AccessControl лише при confirm().
Етап 003+: таблиця time_intervals(id TEXT PRIMARY KEY, resident TEXT NOT NULL, space TEXT NOT NULL, start TEXT NOT NULL, end TEXT NOT NULL). Сховище — TimeIntervalStorage; SQL лише у SqliteTimeIntervalStorage. Інтервал не містить посилання на інші бронювання: один Booking на процес є явною навчальною межею.

AccessControl передається через setAccessControl(access); confirm() використовує вже впроваджений сервіс.
