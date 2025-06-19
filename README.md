# Spring Boot приложение онлайн банка
![logo.png](front-ui/src/main/resources/static/logo.png)

## Installation & Run
_run cmds_

./gradlew clean build

docker compose up --build


## Database
[account](account-service/src/main/resources/db/changelog/init-data-account.xml)

[audit](audit-service/src/main/resources/db/changelog/init-storedata-rule.xml)

[cash](cash-service/src/main/resources/db/changelog/init-storedata-cash.xml)

[exchange](exchange-service/src/main/resources/db/changelog/init-storedata-exchange.xml)

[notification](notification-service/src/main/resources/db/changelog/init-storedata-notification.xml)

[transfer](transfer-service/src/main/resources/db/changelog/init-storedata-transfer.xml)



## Run tests
_**Module**_

./gradlew test

_**Integration**_

./gradlew verify

## Build
./gradlew build

Использовано для реализации:
* Build: Gradle.
* Java: 21.
* UI: thymeleaf + Spring MVC + bootstrap
* DB: Spring Data JPA + Hibernate ORM
* PostgreSQL: Database per Service.
* Service Discovery: Consul
* Externalized/Distributed Config: Consul
* Gateway API: Spring Cloud Gateway.
* Security: Keycloak Access Token (JWT) по client credentials для выполнения запросов в другие микросервисы.
* API: Resilence4j + Open Feign
* Test: JUnit 5, TestContext Framework, Spring Boot Test, Spring Cloud Contract.
* Container: Docker Compose

Проект состоит из
* front-ui микросервис с графическим интерфейсом реализован на thymeleaf в дальнейшем желательно переписать полностью на  
JS, используется интеграция c keycloak для создания и аутентификации пользователей.
* gateway-service микросервис гейтвея для общения сервисов между собой перенаправляет токен безопасности
* account-service микросервис счетов реализован паттерн Transactional Outbox
* cash-service микросервис проведения операции с наличными средствами
* transfer-service микросервис переводов
* exchange-service микросервис обмена валют
* dictionaries-service микросервис справочной информации
* audit-service микросервис проверки операций на соответствие правилам
* notification-service сервис уведомлений
* chassis-api сервис с общими классами
* chassis-service-client сервис с общими настройками Openfeign 