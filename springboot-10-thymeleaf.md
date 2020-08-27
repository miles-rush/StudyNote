# SpringBoot10-thymeleaf

> Spring Boot集成thymeleaf模板引擎

#### pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>5.4.0</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
        <exclusions>
            <exclusion>
                <groupId>org.junit.vintage</groupId>
                <artifactId>junit-vintage-engine</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
</dependencies>
```

#### User.java

```java
@Data
public class User {
    private String name;
    private String password;
}
```

#### IndexController.java

```java
@Controller
@Slf4j
public class IndexController {
    @GetMapping(value = {"", "/"})
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();

        User user = (User) request.getSession().getAttribute("user");
        if (ObjectUtil.isNull(user)) {
            mv.setViewName("redirect:/user/login");
        } else {
            mv.setViewName("page/index");
            mv.addObject(user);
        }

        return mv;
    }
}
```

#### UserController.java

```java
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {
    @PostMapping("/login")
    public ModelAndView login(User user, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();

        mv.addObject(user);
        mv.setViewName("page/index");

        request.getSession().setAttribute("user", user);
        return mv;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("page/login");
    }
}
```

#### head.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head th:fragment="header">
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>spring-boot-demo-template-thymeleaf</title>
</head>
<body>
</body>
</html>
```

#### index.html

```html
<!doctype html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<header th:replace="~{common/head :: header}"></header>
<body>
<div id="app" style="margin: 20px 20%">
    欢迎登录，<span th:text="${user.name}"></span>！
</div>
</body>
</html>
```

#### login.html

```html
<!doctype html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<header th:replace="~{common/head :: header}"></header>
<body>
<div id="app" style="margin: 20px 20%">
    <form action="/study/user/login" method="post">
        用户名<input type="text" name="name" placeholder="用户名"/>
        密码<input type="password" name="password" placeholder="密码"/>
        <input type="submit" value="登录">
    </form>
</div>
</body>
</html>
```

#### application.yml

```yml
server:
  port: 8050
  servlet:
    context-path: /study
spring:
  thymeleaf:
    cache: false
    mode: HTML
    encoding: UTF-8
    servlet:
      content-type: text/html
```

