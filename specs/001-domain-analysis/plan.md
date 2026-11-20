# Implementation Plan: 001-domain-analysis
**Date**: 2026-11-20 | **Spec**: [spec.md](spec.md)
## Summary
Послідовна реалізація системи бронювання коворкінгу, етап 1. Модель описує акторів, бронювання та інтервали; транспорт і збереження не змішуються з бізнес-правилами.
## Technical Context
Стек: PlantUML, Mermaid, XLSX. Платформа Windows; JDK 26 з --release 21. Навчальний обсяг: один Booking, десятки інтервалів. Імітація зовнішнього доступу та рахунків. Тести: фактичний запуск сценаріїв, граничних інтервалів, відмов; для БД — повторне читання після відкриття з'єднання; для MVC — HTTP і браузер.
## Constitution Check
До і після дизайну: SRP, constructor DI, українські повідомлення, текстові діаграми, відсутність SQL у домені — виконано дизайном. Конституція етапу дозволяє тільки його стек. Невирішених питань немає.
## Project Structure
docs/usecase.puml, docs/class.mmd, docs/sequence.mmd, docs/state.mmd, docs/crc.xlsx
## Phase 0: Research
Рішення та первинні джерела — research.md. Колекція інтервалів належить Booking. Межі [start,end), ISO-текст дозволяє стабільне збереження. Під час вебзапитів synchronized методи моделі серіалізують перевірку й запис.
## Phase 1: Design
Дані — data-model.md. Клас-ціле Booking не Singleton. Результат операції не містить HTTP. PreparedStatement із параметрами й екранування HTML/DOM. Після відмови запис не змінюється.
## Complexity Tracking
Порушень конституції немає. Повний ORM і Spring не потрібні для однієї таблиці.
