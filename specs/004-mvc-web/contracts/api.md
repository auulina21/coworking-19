# REST contract
Body: {"id":"T19","resident":"Олена","space":"Переговорна A","start":"2026-12-01T09:00","end":"2026-12-01T11:00"}.
GET /api/coworking → 200 {"items":[...]}.
POST /api/coworking → 201 {"message":"Інтервал додано."}.
PUT /api/coworking/{id} → 200 {"message":"Інтервал змінено."}; body.id має збігатися з URL id.
DELETE /api/coworking/{id} → 200 {"message":"Інтервал видалено."}.
400 invalid, 404 not found, 409 conflict/forbidden, JSON {"message":"пояснення українською"}. Невалідний JSON також 400 через вигляд. Сервісна помилка → 500 без деталей SQL.
