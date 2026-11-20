# Дослідження
Стек: Java 21, Maven, Guice 7.0.0, SQLite, Javalin 6.7.0, Jackson 2.18.3, slf4j-simple 2.0.17.
Рішення: JDK 26 компілює --release 21; Maven compiler 3.14.0. Альтернатива — окремий JDK21; не потрібний для перевіреної локальної збірки.
Конструкторне DI робить storage final. Setter відхилено, щоб Booking не існував без сховища на етапі 003.
Javalin 6: маршрути app.get/post/put/delete перед start, не API Javalin7. Модель/контролер/вигляд не імпортують Javalin або Jackson.
Одна Connection на процес; synchronized методи сховища і Booking, PreparedStatement. Порожня база після першого запуску MVC.
Мінімалістичний CSS: #ffffff, #161a22, акцент #2356d8; Arial/system sans; без CDN і бібліотек.
Джерела:
- https://github.com/github/spec-kit/tree/v0.15.0
- https://github.com/google/guice/wiki/Injections
- https://github.com/google/guice/wiki/ProvidesMethods
- https://github.com/xerial/sqlite-jdbc/releases/tag/3.49.1.0
- https://raw.githubusercontent.com/javalin/javalin/javalin-parent-6.7.0/pom.xml
- https://maven.apache.org/guides/mini/guide-java-target.html
