# Spring Boot приложение онлайн банка

## Installation & Run
_run cmds_

### Minikube way

Удалите данные установки для Jenkins (Docker desktop)

Установите Minikube
https://kubernetes.io/ru/docs/tasks/tools/install-minikube/

Установите Helm
https://helm.sh/docs/intro/install/

./gradlew clean build

minikube start

minikube addons enable ingress

minikube start

Настройка dns и hosts

kubectl -n kube-system get configmap coredns -o yaml > corednstest.yaml

добавить rewrite name keycloak.local financeapp-keycloak.test.svc.cluster.local
после health секции

**Linux:**

minikube ip

полученный [IP] скопировать в


sudo nano /etc/hosts
добавить

**[IP] finance.local**
**[IP] keycloak.local**

**Windows:**

kubectl patch svc ingress-nginx-controller -n ingress-nginx -p '{"spec": {"type": "LoadBalancer"}}'
c:\windows\system32\drivers\etc\hosts
добавить

**127.0.0.1 finance.local**

**127.0.0.1 keycloak.local**

minikube tunel


Воспользоваться redeploy.sh или в ручную

helm uninstall financeapp -n test

_Строим образы_

docker build -t account-service:0.1.0 ./account-service

docker build -t audit-service:0.1.0 ./audit-service

docker build -t cash-service:0.1.0 ./cash-service

docker build -t dictionaries-service:0.1.0 ./dictionaries-service

docker build -t exchange-service:0.1.0 ./exchange-service

docker build -t front-ui:0.1.0 ./front-ui

docker build -t notification-service:0.1.0 ./notification-service

docker build -t transfer-service:0.1.0 ./transfer-service

_Импортируем образы_

minikube image load account-service:0.1.0

minikube image load audit-service:0.1.0

minikube image load cash-service:0.1.0

minikube image load dictionaries-service:0.1.0

minikube image load exchange-service:0.1.0

minikube image load front-ui:0.1.0

minikube image load notification-service:0.1.0

minikube image load transfer-service:0.1.0


_Обновляем репозитории_

helm repo add stable https://charts.helm.sh/stable

helm repo add bitnami https://raw.githubusercontent.com/bitnami/charts/refs/heads/archive-full-index/bitnami

helm repo update

_Установка релиза_
helm upgrade --install financeapp ./financeapp -f ./financeapp/values.yaml --namespace test --create-namespace




### Docker Desktop Jenkins way

Удалите данные установки для Minikube

Установите Docker Desktop
https://docs.docker.com/desktop/setup/install/windows-install/

В настройках включить встроенный кластер

Установите Helm
https://helm.sh/docs/intro/install/

Установите Ingress

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx

helm repo update

helm install my-nginx-ingress ingress-nginx/ingress-nginx

./gradlew clean build

docker compose up --build

**Linux:**
sudo nano /etc/hosts
добавить

**127.0.0.1 finance.local**
**127.0.0.1 keycloak.local**

**Windows:**
c:\windows\system32\drivers\etc\hosts
добавить
**127.0.0.1 finance.local**
**127.0.0.1 keycloak.local**

docker compose up --build

дальше работать с Jenkins

http://localhost:8080/



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