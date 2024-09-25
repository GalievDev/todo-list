# Todo-List



## Description
List of tasks with user authorization

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
