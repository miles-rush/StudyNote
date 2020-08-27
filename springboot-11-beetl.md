# SpringBoot11-Beetl

> Spring Boot集成Beetl模板引擎

#### pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>com.ibeetl</groupId>
        <artifactId>beetl-framework-starter</artifactId>
        <version>1.2.28.RELEASE</version>
    </dependency>
    
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

</dependencies>
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
            mv.setViewName("page/index.btl");
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
        return new ModelAndView("page/login.btl");
    }
}
```

#### index.btl

```html
<!doctype html>
<html lang="en">
<% include("/common/head.html"){} %>
<body>
<div id="app" style="margin: 20px 20%">
   欢迎登录，${user.name}！
</div>
</body>
</html>
```

#### login.btl

```html
<!doctype html>
<html lang="en">
<% include("/common/head.html"){} %>
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

