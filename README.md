Собрать билд mvn clean package -DskipTests
Поднять среду docker-compose up --build
Доступно будет по localhost:8080


1.	Сервис должен поддерживать аутентификацию и авторизацию пользователей по email и паролю.
2.	Доступ к API должен быть аутентифицирован с помощью JWT токена.
3.	Создать ролевую систему администратора и пользователей.
4.	Администратор может управлять всеми задачами: создавать новые, редактировать существующие, просматривать и удалять, менять статус и приоритет, назначать исполнителей задачи, оставлять комментарии.
5.	Пользователи могут управлять своими задачами, если указаны как исполнитель: менять статус, оставлять комментарии.
6.	API должно позволять получать задачи конкретного автора или исполнителя, а также все комментарии к ним.
7.	Сервис должен корректно обрабатывать ошибки и возвращать понятные сообщения, а также валидировать входящие данные.
8.	Сервис должен быть хорошо задокументирован. API должен быть описан с помощью Open API и Swagger. В сервисе должен быть настроен Swagger UI. Необходимо написать README с инструкциями для локального запуска проекта. Дев среду нужно поднимать с помощью docker compose.
9.	Напишите несколько базовых тестов для проверки основных функций вашей системы.
