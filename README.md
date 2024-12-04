# Todo-List



## Description
List of tasks with user authorization

Examples:
```http request
### Register user
POST localhost:8080/api/v1/register/
Content-Type: application/json
{
  "username": "Bob",
  "email": "bob@gmail.com",
  "password: "123456"
}

### Get all tasks
GET localhost:8080/api/v1/tasks
Authorization: Bearer {{auth-token}}

### Get task by id
GET localhost:8080/api/v1/tasks/{{id}}
Authorization: Bearer {{auth-token}}

```


## Deployement

Don't forget to create and connect database named *task* in `application.propertires`
```properties
# Database url
spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/task
# Login username of the database.
spring.datasource.username=postgres
# Login password of the database.
spring.datasource.password=root
```

Firstly you need have to installed `jdk21`

|OS|Download Away|
|-|-|
|Windows|https://adoptium.net/temurin/releases/|
|Ubuntu|`sudo apt install openjdk-21-jre`|
|Arch Linux|`sudo pacman -S jre-openjdk`|

Clone the repository: 
```bash
git clone https://github.com/GalievDev/todo-list.git
```

Build the application:

```bash
./gradlew build
```

And run the `.jar` file:

```bash
java -jar todo-list-1.0.0.jar
```
## Swagger Docs
Swagger UI documentation will be aviable after running app in the link: "your_host:your_port/swagger-ui.html"
