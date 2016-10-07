[![Build Status](https://travis-ci.org/crizin/learning-spring-mvc.svg?branch=master)](https://travis-ci.org/crizin/learning-spring-mvc)

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
- Spring MVC 4.3
- Servlet API 3.1
- Thymeleaf 3.0
- Hibernate 5.2 (JPA 2.1)
- HikariCP 2.5
- Spring Security 4.1
- Gradle 3.1

# Another branches

- [Test with Cucumber](https://github.com/crizin/learning-spring-mvc/tree/cucumber)
