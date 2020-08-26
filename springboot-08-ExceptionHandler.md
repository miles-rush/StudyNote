# SpringBoot8-Exception Handler

> 学习如何在Spring Boot中进行统一的异常处理，其中包括了两种方式的处理：第一种对API形式的接口进行异常处理，统一封装了返回格式；第二种是对模板页面请求的异常处理，统一处理错误页面

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
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

#### Status枚举

```java
public enum Status {
    /**
     * 操作成功
     */
    OK(200, "操作成功"),

    /**
     * 未知异常
     */
    UNKNOWN_ERROR(500,"服务器出错啦");
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 内容
     */
    private String message;
    Status(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
```

#### Exception

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException{
    private Integer code;
    private String message;

    public BaseException(Status status) {
        super(status.getMessage());
        this.code = status.getCode();
        this.message = status.getMessage();
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
```

###### `@EqualsAndHashCode`

1. 此注解会生成`equals(Object other)` 和 `hashCode()`方法。
2. 它默认使用非静态，非瞬态的属性
3. 可通过参数exclude排除一些属性
4. 可通过参数of指定仅使用哪些属性
5. 它默认仅使用该类中定义的属性且不调用父类的方法

> 当启动`@EqualsAndHashCode`时，**默认不调用父类的`equals`方法**，当做类型相等判断时，会遇到麻烦，例如:

```java
@Data
public class People {
    private Integer id;
}

@Data
public class User extends People {
    private String name;
    private Integer age;
}

public static void main(String[] args) {
    User user1 = new User();
    user1.setName("jiangxp");
    user1.setAge(18);
    user1.setId(1);

    User user2 = new User();
    user2.setName("jiangxp");
    user2.setAge(18);
    user2.setId(2);

    System.out.println(user1.equals(user2));
}

输出结果：true
```

**注意：两条user数据，ID完全不一样，结果明显是错的，没有做id的equals判断**。

需要将`@EqualsAndHashCode`修改为`@EqualsAndHashCode(callSuper = true)`才能得到正确结果。

另外因为`@Data`相当于`@Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode`这5个注解的合集，所以代码中`@Data`和`@EqualsAndHashCode(callSuper = true)`联合使用。

```java
@Getter
public class JsonException extends BaseException{
    public JsonException(Status status) {
        super(status);
    }

    public JsonException(Integer code, String message) {
        super(code, message);
    }
}
```

```java
@Getter
public class PageException extends BaseException{
    public PageException(Status status) {
        super(status);
    }

    public PageException(Integer code, String message) {
        super(code, message);
    }
}
```

#### ApiResponse.java

>统一的API格式返回封装

```java
@Data
public class ApiResponse {
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回内容
     */
    private String message;

    /**
     * 返回数据
     */
    private Object data;

    /**
     * 无参构造函数
     */
    private ApiResponse() {

    }

    /**
     * 全参构造函数
     *
     * @param code 状态码
     * @param message 返回内容
     * @param data 返回数据
     */
    private ApiResponse(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构造一个自定义的API返回
     *
     * @param code    状态码
     * @param message 返回内容
     * @param data    返回数据
     * @return ApiResponse
     */
    public static ApiResponse of(Integer code, String message, Object data) {
        return new ApiResponse(code, message, data);
    }

    /**
     * 构造一个有状态的API返回
     *
     * @param status 状态 {@link Status}
     * @return ApiResponse
     */
    public static ApiResponse ofStatus(Status status) {
        return ofStatus(status, null);
    }

    /**
     * 构造一个有状态且带数据的API返回
     *
     * @param status 状态 {@link Status}
     * @param data   返回数据
     * @return ApiResponse
     */
    public static ApiResponse ofStatus(Status status, Object data) {
        return of(status.getCode(), status.getMessage(), data);
    }


    /**
     * 构造一个成功且带数据的API返回
     *
     * @param data 返回数据
     * @return ApiResponse
     */
    public static ApiResponse ofSuccess(Object data) {
        return ofStatus(Status.OK, data);
    }

    /**
     * 构造一个成功且自定义消息的API返回
     *
     * @param message 返回内容
     * @return ApiResponse
     */
    public static ApiResponse ofMessage(String message) {
        return of(Status.OK.getCode(), message, null);
    }

    /**
     * 构造一个异常且带数据的API返回
     *
     * @param t    异常
     * @param data 返回数据
     * @param <T>  {@link BaseException} 的子类
     * @return ApiResponse
     */
    public static <T extends BaseException> ApiResponse ofException(T t, Object data) {
        return of(t.getCode(), t.getMessage(), data);
    }

    /**
     * 构造一个异常的API返回
     *
     * @param t   异常
     * @param <T> {@link BaseException} 的子类
     * @return ApiResponse
     */
    public static <T extends BaseException> ApiResponse ofException(T t) {
        return ofException(t, null);
    }
    
}
```

#### DemoExceptionHandler.java

```java
@ControllerAdvice
@Slf4j
public class DemoExceptionHandler {
    private static final String DEFAULT_ERROR_VIEW = "error";
    /**
     * 统一 json 异常处理
     *
     * @param exception JsonException
     * @return 统一返回 json 格式
     */
    @ExceptionHandler(value = JsonException.class)
    @ResponseBody
    public ApiResponse jsonErrorHandler(JsonException exception) {
        log.error("【JsonException】:{}", exception.getMessage());
        return ApiResponse.ofException(exception);
    }

    /**
     * 统一 页面 异常处理
     *
     * @param exception PageException
     * @return 统一跳转到异常页面
     */
    @ExceptionHandler(value = PageException.class)
    public ModelAndView pageErrorHandler(PageException exception) {
        log.error("【DemoPageException】:{}", exception.getMessage());
        ModelAndView view = new ModelAndView();
        view.addObject("message", exception.getMessage());
        view.setViewName(DEFAULT_ERROR_VIEW);
        return view;
    }

    @ModelAttribute
    public void addMyAttribute(Model model) {
        model.addAttribute("user", "miles");
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
//        GenericConversionService genericConversionService = (GenericConversionService) binder.getConversionService();
//        if (genericConversionService != null) {
//            genericConversionService.addConverter(new DateConverter());
//        }

        binder.registerCustomEditor(String.class,
                new StringTrimmerEditor(true));

        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), false));
    }
}
```

`@ControllerAdvice`是一个特殊的`@Component`，用于标识一个类，这个类中可以使用三种注解标识的方法：`@ExceptionHandler`，`@InitBinder`，`@ModelAttribute`，将作用于所有的`@Controller`类的接口上。

`@InitBinder`

作用：注册属性编辑器，对HTTP请求参数进行处理，再绑定到对应的接口，比如格式化的时间转换等。应用于单个@Controller类的方法上时，仅对该类里的接口有效。与`@ControllerAdvice`组合使用可全局生效。

```java
@ControllerAdvice
public class ActionAdvice {
    //@InitBinder标注的方法必须有一个参数WebDataBinder
    @InitBinder
    public void handleException(WebDataBinder binder) {
        binder.addCustomFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"));
    }
}

```

`@ExceptionHandler`

作用：统一异常处理，也可以指定要处理的异常类型

```java
@ControllerAdvice
public class ActionAdvice {
    
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Map handleException(Exception ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 400);
        map.put("msg", ex.toString());
        return map;
    }
}

```

`@ModelAttribute`

作用：绑定数据

```java
@ControllerAdvice
public class ActionAdvice {
    
    @ModelAttribute
    public void handleException(Model model) {
        model.addAttribute("user", "zfh");
    }
}

```

在接口中获取前面绑定的参数：

```java
@RestController
public class BasicController {
    
    @GetMapping(value = "index")
    public Map index(@ModelAttribute("user") String user) {
        //...
    }
}

```

完整示例代码:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @InitBinder
    public void initMyBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class,
                new StringTrimmerEditor(true));

        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), false));
    }

    @ModelAttribute
    public void addMyAttribute(Model model) {
        model.addAttribute("user", "miles"); // 在@RequestMapping的接口中使用@ModelAttribute("name") Object name获取
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody // 如果使用了@RestControllerAdvice，这里就不需要@ResponseBody了
    public Map handler(Exception ex) {
        logger.error("统一异常处理", ex);
        Map<String, Object> map = new HashMap<>();
        map.put("code", 400);
        map.put("msg", ex);
        return map;
    }
}

```

测试接口：

```java
@RestController
public class TestAction {

    @GetMapping(value = "testAdvice")
    public JsonResult testAdvice(@ModelAttribute("user") String user, Date date) throws Exception {
        System.out.println("user: " + user);
        System.out.println("date: " + date);
        throw new Exception("直接抛出异常");
    }
}

```

#### 使用`@InitBinder`来对页面数据进行解析绑定

接口数据的一些处理，比如时间字符串转化为Date格式等

###### way1：

```java
@InitBinder
public void initMyBinder(WebDataBinder binder) {
    //字符串处理
    binder.registerCustomEditor(String.class,
            new StringTrimmerEditor(true));// spring自带的PropertyEditor可以在此注册 也可以使用自定的类型转化器
	
    //时间转化
    binder.registerCustomEditor(Date.class,
            new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), false));
}
```
> `Controller`方法的参数类型可以是基本类型，也可以是封装后的普通Java类型。若这个普通Java类型没有声明任何注解，则意味着它的每一个属性都需要到Request中去查找对应的请求参数。众所周知，无论客户端传入的是什么类型的请求参数，最终都要以字节的形式传给服务端。而服务端通过`Request`的`getParameter`方法取到的参数也都是字符串形式的结果。所以，需要有一个把字符串形式的参数转换成服务端真正需要的类型的转换工具，在spring中这个转换工具为`WebDataBinder`。
>
> `WebDataBinder`不需要我们自己去创建，我们只需要向它注册参数类型对应的属性编辑器`PropertyEditor`。`PropertyEditor`可以将字符串转换成其真正的数据类型，它的`void setAsText(String text)`方法实现数据转换的过程。
>
> 具体的做法是，在Controller中声明一个`InitBinder`方法，方法中利用`WebDataBinder`将自己实现的或者spring自带的`PropertyEditor`进行注册，如上代码。



> 参考：https://blog.csdn.net/hongxingxiaonan/article/details/50282001

###### way2：

自定义时间类型转换器

```java
public class DateConverter implements Converter<String, Date> {
    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private static final String shortDateFormat = "yyyy-MM-dd";
    private static final String timeStampFormat = "^\\d+$";

    @Override
    public Date convert(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        value = value.trim();
        try {
            if (value.contains("-")) {
                SimpleDateFormat formatter;
                if (value.contains(":")) {
                    formatter = new SimpleDateFormat(dateFormat);
                } else {
                    formatter = new SimpleDateFormat(shortDateFormat);
                }
                return formatter.parse(value);
            } else if (value.matches(timeStampFormat)) {
                Long lDate = new Long(value);
                return new Date(lDate);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("parser %s to Date fail", value));
        }
        throw new RuntimeException(String.format("parser %s to Date fail", value));
    }
}
```

```java
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        //类型转化器的注册
        GenericConversionService genericConversionService = (GenericConversionService) binder.getConversionService();
       if (genericConversionService != null) {
            genericConversionService.addConverter(new DateConverter());
        }
    }
```

扩展例子：

```java
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        // 方法1，注册converter
        GenericConversionService genericConversionService = (GenericConversionService) binder.getConversionService();
        if (genericConversionService != null) {
            genericConversionService.addConverter(new DateConverter());
        }

        // 方法2，定义单格式的日期转换，可以通过替换格式，定义多个dateEditor，代码不够简洁
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CustomDateEditor dateEditor = new CustomDateEditor(df, true);
        binder.registerCustomEditor(Date.class, dateEditor);


        // 方法3，同样注册converter
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(new DateConverter().convert(text));
            }
        });

    }
```

> 参考：https://my.oschina.net/sugarZone/blog/706417
>
> 参考：https://www.javazhiyin.com/56535.html



测试接口

```java
@GetMapping("/json")
@ResponseBody
public ApiResponse jsonException(@ModelAttribute("user") String user, Date date) {
    log.info("【user】:{}", user);
    log.info("【date】:{}", date);
    throw new JsonException(Status.UNKNOWN_ERROR);
}
```

**注意的是接口中的date是Date类型的**

> `@RestControllerAdvice` = `@ControllerAdvice` + `@ResponseBody`

> 参考:https://juejin.im/post/6844903826412011533

#### error.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head lang="en">
    <meta charset="UTF-8"/>
    <title>统一页面异常处理</title>
</head>
<body>
<h1>统一页面异常处理</h1>
<div th:text="${message}"></div>
</body>
</html>
```

#### TestController.java

```java
@Controller
@Slf4j
public class TestController {
    @GetMapping("/json")
    @ResponseBody
    public ApiResponse jsonException(@ModelAttribute("user") String user, Date date) {
        log.info("【user】:{}", user);
        log.info("【date】:{}", date);
        throw new JsonException(Status.UNKNOWN_ERROR);
    }

    @GetMapping("/page")
    public ModelAndView pageException() {
        throw new PageException(Status.UNKNOWN_ERROR);
    }
}
```