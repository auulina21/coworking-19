# Coworking — практика 2
Система бронювання коворкінгу. Варіант 19, КІ-26. Три актори й чотири сценарії: бронювання, план розміщення, рахунок, тариф наступного місяця.
## Збирання і запуск (PowerShell, JDK 21+)
```powershell
New-Item -ItemType Directory -Force bin | Out-Null
Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName | Set-Content sources.txt
javac -encoding UTF-8 --release 21 -d bin @sources.txt
java -Dfile.encoding=UTF-8 -cp bin ua.coworking.Main
```
У PowerShell аргумент @sources.txt потрібно передати як рядок: `'@sources.txt'`, щоб уникнути splatting.
Програма виконує відтворюваний демонстраційний сценарій без введення з консолі. Усі зовнішні операції імітуються. Логи фактичного запуску зберігаються окремо у пакеті звіту.
## Архітектура
CoworkingMember — абстракція спільної ідентичності, final getId; ролі перевизначають role і реалізують TariffPlan. Resident.book перевантажено. Booking володіє TimeInterval (композиція); залежність від AccessControl передається в confirm.
SOLID: ролі мають окремі обов'язки; інтерфейс доступу дозволяє нову реалізацію; підтипи зберігають getId/getName; малі інтерфейси не нав'язують зайвих методів; Booking не створює зовнішньої системи.
Навчальна межа: один Booking на процес. Час локальний, [start,end), без перетинів для одного місця. Зміни лише DRAFT. Рахунок 150 грн/год лише CONFIRMED; tariff STAFF для службових ролей не змінює тариф резидента.
Використано Codex і Spec Kit0.15.0. Авторство ШІ та перевірки зазначені у звітах. Публікація і подання у Moodle не виконані за вказівкою власника.
