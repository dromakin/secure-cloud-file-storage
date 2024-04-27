# Визуализация работы

## Архитектура
![FileCloudService.drawio.png](docs%2FFileCloudService.drawio.png)

Основные компоненты:
- frontend (Vue.js)
- backend
  - Spring boot 2.x
- DB
  - PostgreSQL
  - MinIO (default profile)
- Admin panels
  - Swagger (rest api docs)
  - MinIO admin panel
  - Pgadmin

Вариант для редактирования: [FileCloudService.drawio](docs%2FFileCloudService.drawio)

## Запуск
```shell
docker-compose up
```

По умолчанию поднимается все с профилем default (с min.io хранилищем файлов), если нужно поднять в рамках локальной системы, то следует поменять это в properties, либо использовать глобальную переменную.

1 вариант через [application.properties](src%2Fmain%2Fresources%2Fapplication.properties)
```properties
spring.profiles.active=local
```

2 вариант через APP_PROFILE в [docker-compose.yml](docker-compose.yml)
```yaml
    environment:
      APP_PROFILE: local
```

## Схемы в БД Postgres
Все необходимые схемы в БД Postgres создаются с помощью Flyway миграции.

### Пользователи
С помощью Flyway создается 3 пользователя:

Login/Password:
```text
admin@localhost/admin
test@localhost/admin
writer@localhost/admin
```

### Визуализация работы

Работа backend с "local", "default" профилями выглядит одинаково.

Единственное отличие - это сервис min.io

#### min.io admin site
##### До загрузки файла

![0.png](docs%2Fimg%2F0.png)

![01.png](docs%2Fimg%2F01.png)

##### После загрузки файла
![02.png](docs%2Fimg%2F02.png)

![03.png](docs%2Fimg%2F03.png)

#### docker-compose

Этот профиль можно явно не указывать, он по умолчанию активирован.

```yaml
    environment:
      APP_PROFILE: default
```
![1_1.png](docs%2Fimg%2F1_1.png)

```yaml
    environment:
      APP_PROFILE: local
```
![1.png](docs%2Fimg%2F1.png)

![2.png](docs%2Fimg%2F2.png)

#### Start Spring Boot App

![3.png](docs%2Fimg%2F3.png)

![4.png](docs%2Fimg%2F4.png)

#### Swagger

![5.png](docs%2Fimg%2F5.png)

![6.png](docs%2Fimg%2F6.png)

![7.png](docs%2Fimg%2F7.png)

![8.png](docs%2Fimg%2F8.png)

#### Frontend

![9.png](docs%2Fimg%2F9.png)

![10.png](docs%2Fimg%2F10.png)

![11.png](docs%2Fimg%2F11.png)

![12.png](docs%2Fimg%2F12.png)

![13.png](docs%2Fimg%2F13.png)

## Тестирование

Для тестов я использовал отдельный docker-compose файл: [docker-compose-db-only.yml](src%2Ftest%2Fresources%2Fdocker-compose-db-only.yml)

Использовал testcontainers и MockMvc.

Также были создан отдельный файл postman для проведения unit тестирования.

### Testing Controllers

![14.png](docs%2Fimg%2F14.png)

![15.png](docs%2Fimg%2F15.png)

### Postman

#### get new token

![001.png](docs%2Fimg%2F001.png)

### Upload new file

![002.png](docs%2Fimg%2F002.png)

### Check list of files

![003.png](docs%2Fimg%2F003.png)

### Set new name of file

![004.png](docs%2Fimg%2F004.png)

### Delete file (mark status DELETED)

![006.png](docs%2Fimg%2F006.png)

### Delete file from user-folder

![007.png](docs%2Fimg%2F007.png)

### Check min.io

![008.png](docs%2Fimg%2F008.png)

### Download file
![009.png](docs%2Fimg%2F009.png)