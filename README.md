# What's this?

An example project for learning Spring MVC 4

# Running this project

```sh
$ docker build --tag learning-spring-mvc:latest .
$ docker run --name learning-postgres -e POSTGRES_PASSWORD=postgres -d postgres:9.5
$ docker run -it --rm --name learning-spring-mvc --link learning-postgres:postgres -p 8080:8080 learning-spring-mvc:latest
```

And then open [http://localhost:8080/](http://localhost:8080/) in your browser.

# Using libraries or spec

- Java 8
- Spring MVC 4.3.1
- Servlet API 3.1
- Thymeleaf 3.0
- Hibernate 5.2.1 (JPA 2.1)
- HikariCP 2.4.7
- Spring Security 4.1.1
- Gradle 2.14
- Cucumber 1.2.4
