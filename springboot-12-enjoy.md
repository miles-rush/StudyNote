# SpringBoot12-Enjoy

> Spring Boot集成enjoy模板引擎。

#### pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
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
        <groupId>com.jfinal</groupId>
        <artifactId>enjoy</artifactId>
        <version>4.9.01</version>
    </dependency>
</dependencies>
```

#### EnjoyConfig.java

```java
/**
 * 模板配置类
 */
@Configuration
public class EnjoyConfig {
    @Bean(name = "jfinalViewResolver")
    public JFinalViewResolver getJFinalViewResolver() {
        JFinalViewResolver jfr = new JFinalViewResolver();
        //setDevMode 配置放在最前面
        jfr.setDevMode(true);
        // 使用ClassPathSourceFactory从 class path 与 jar 包中加载模板文件
        jfr.setSourceFactory(new ClassPathSourceFactory());
        // 在使用 ClassPathSourceFactory 时要使用 setBaseTemplatePath
        // 代替 jfr.setPrefix("/view/")
        JFinalViewResolver.engine.setBaseTemplatePath("/templates/");

        jfr.setSessionInView(true);
        jfr.setSuffix(".html");
        jfr.setContentType("text/html;charset=UTF-8");
        jfr.setOrder(0);
        return jfr;
    }
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
        mv.setViewName("redirect:/");

        request.getSession().setAttribute("user", user);
        return mv;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("page/login");
    }
}
```

#### index.html

```html
<!doctype html>
<html lang="en">
#include("/common/head.html")
<body>
<div id="app" style="margin: 20px 20%">
    欢迎登录，#(user.name)！
</div>
</body>
</html>
```

#### login.html

```html
<!doctype html>
<html lang="en">
#include("/common/head.html")
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