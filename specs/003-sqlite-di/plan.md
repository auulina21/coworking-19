# Implementation Plan: 003-sqlite-di
**Date**: 2026-11-20 | **Spec**: [spec.md](spec.md)
## Summary
Послідовна реалізація системи бронювання коворкінгу, етап 3. Модель описує акторів, бронювання та інтервали; транспорт і збереження не змішуються з бізнес-правилами.
## Technical Context
Стек: Java 21, Maven 3.9.11, Guice 7.0.0, sqlite-jdbc 3.49.1.0. Платформа Windows; JDK 26 з --release 21. Навчальний обсяг: один Booking, десятки інтервалів. Імітація зовнішнього доступу та рахунків. Тести: фактичний запуск сценаріїв, граничних інтервалів, відмов; для БД — повторне читання після відкриття з'єднання; для MVC — HTTP і браузер.
## Constitution Check
До і після дизайну: SRP, constructor DI, українські повідомлення, текстові діаграми, відсутність SQL у домені — виконано дизайном. Конституція етапу дозволяє тільки його стек. Невирішених питань немає.
## Project Structure
pom.xml, src/main/java/ua/coworking/model/Booking.java, infrastructure/SqliteTimeIntervalStorage.java, infrastructure/CoworkingModule.java, StaticHtmlExporter.java
## Phase 0: Research
Рішення та первинні джерела — research.md. Колекція інтервалів належить Booking. Межі [start,end), ISO-текст дозволяє стабільне збереження. Під час вебзапитів synchronized методи моделі серіалізують перевірку й запис.
## Phase 1: Design
Дані — data-model.md. Клас-ціле Booking не Singleton. Результат операції не містить HTTP. PreparedStatement із параметрами й екранування HTML/DOM. Після відмови запис не змінюється.
## Complexity Tracking
Порушень конституції немає. Повний ORM і Spring не потрібні для однієї таблиці.
