# Implementation Plan: 004-mvc-web
**Date**: 2026-11-20 | **Spec**: [spec.md](spec.md)
## Summary
Послідовна реалізація системи бронювання коворкінгу, етап 4. Модель описує акторів, бронювання та інтервали; транспорт і збереження не змішуються з бізнес-правилами.
## Technical Context
Стек: Java 21, Maven, Guice 7.0.0, SQLite, Javalin 6.7.0, Jackson 2.18.3, slf4j-simple 2.0.17. Платформа Windows; JDK 26 з --release 21. Навчальний обсяг: один Booking, десятки інтервалів. Імітація зовнішнього доступу та рахунків. Тести: фактичний запуск сценаріїв, граничних інтервалів, відмов; для БД — повторне читання після відкриття з'єднання; для MVC — HTTP і браузер.
## Constitution Check
До і після дизайну: SRP, DI через setter для AccessControl, українські повідомлення, текстові діаграми, відсутність SQL у домені — виконано дизайном. Конституція етапу дозволяє тільки його стек. Невирішених питань немає.
## Project Structure
src/main/java/ua/coworking/controller/CoworkingController.java, view/CoworkingWebView.java, infrastructure/WebServer.java, infrastructure/JavalinWebServer.java, src/main/resources/public
## Phase 0: Research
Рішення та первинні джерела — research.md. Колекція інтервалів належить Booking. Межі [start,end), ISO-текст дозволяє стабільне збереження. Під час вебзапитів synchronized методи моделі серіалізують перевірку й запис.
## Phase 1: Design
Дані — data-model.md. Клас-ціле Booking не Singleton. Результат операції не містить HTTP. PreparedStatement із параметрами й екранування HTML/DOM. Після відмови запис не змінюється.
## Complexity Tracking
Порушень конституції немає. Повний ORM і Spring не потрібні для однієї таблиці.

Запис і наступне читання виконуються в одній транзакції TimeIntervalStorage.inTransaction(work). При помилці SQLite виконує ROLLBACK, Booking відновлює попередню колекцію.
