# SpringBoot7-AopLog

> 使用AOP切面对请求进行日志记录，同时记录UserAgent信息

#### pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
	<!-- aop -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
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

    <!-- 解析 UserAgent 信息 -->
    <dependency>
        <groupId>eu.bitwalker</groupId>
        <artifactId>UserAgentUtils</artifactId>
        <version>1.21</version>
    </dependency>
</dependencies>
```

#### AopLog.java

```java
@Aspect
@Component
@Slf4j
public class AopLog {
    private static final String START_TIME = "request-start";

    /**
     * 切入点
     */
    //Pointcut表达式 
    @Pointcut("execution(public * com.study.aoplog.controller.*Controller.*(..))")
    //Pointcut签名
    public void log() {

    }
    /**
     * 前置操作
     *
     * @param point 切入点
     */
    //AOP通知中可以用JoinPoint获取数据
    //Around通知比较特殊，是ProceedingJoinPoint
    @Before("log()")
    public void beforeLog(JoinPoint point) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

        log.info("【请求 URL】:{}",request.getRequestURL());
        log.info("【请求 IP】:{}",request.getRemoteAddr());
        log.info("【请求类名】:{},【请求方法名】:{}",point.getSignature().getDeclaringTypeName(),point.getSignature().getName());

        Map<String,String[]> parameterMap = request.getParameterMap();
        log.info("【请求参数】:{}", JSONUtil.toJsonStr(parameterMap));
        Long start = System.currentTimeMillis();
        request.setAttribute(START_TIME, start);
    }

    /**
     * 环绕操作
     *
     * @param point 切入点
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around("log()")
    public Object aroundLog(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        log.info("【返回值】:{}",JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 后置操作
     */
    @AfterReturning("log()")
    public void afterReturning() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

        Long start = (Long) request.getAttribute(START_TIME);
        Long end = System.currentTimeMillis();
        log.info("【请求耗时】:{}毫秒",end - start);

        String header = request.getHeader("User-Agent");

        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        log.info("【浏览器类型】：{}，【操作系统】：{}，【原始User-Agent】：{}", userAgent.getBrowser().toString(), userAgent.getOperatingSystem().toString(), header);

    }
}
```

> AOP：面向切面编程，相对于OOP面向对象编程 Spring的AOP的存在目的是为了解耦。**AOP可以让一组类共享相同的行为**。在OOP中只能继承和实现接口，且类继承只能单继承，阻碍更多行为添加到一组类上，AOP弥补了OOP的不足。

##### AOP术语

**1.通知(有的地方叫增强)(Advice)**

需要完成的工作叫做通知，就是你写的业务逻辑中需要比如事务、日志等先定义好，然后需要的地方再去用。

**2.连接点(Join point)**

对应的是具体被拦截的对象，因为Spring只支持方法，所以被拦截的对象往往就是指特定的方法。

**3.切点(Poincut)**

其实就是筛选出的连接点，一个类中的所有方法都是连接点，但又不全需要，会筛选出某些作为连接点做为切点。如果说通知定义了切面的动作或者执行时机的话，切点则定义了执行的地点。

有时候，我们的切面不单单应用于单个方法，也可以是多个类的不同方法，这时，可以通过正则表达式和指示器的规则去定义。

**4.切面(Aspect)**

其实就是通知和切点的结合，通知和切点共同定义了切面的全部内容，它是干什么的，什么时候在哪执行。

**5.引入(Introduction)**

在不改变一个现有类代码的情况下，为该类添加属性和方法,可以在无需修改现有类的前提下，让它们具有新的行为和状态。其实就是把切面（也就是新方法属性：通知定义的）用到目标类中去。

**6.目标(target)**

被通知的对象。也就是需要加入额外代码的对象，也就是真正的业务逻辑被组织织入切面。

**7.织入(Weaving)**

把切面加入程序代码的过程。切面在指定的连接点被织入到目标对象中，在目标对象的生命周期里有多个点可以进行织入：

- 编译期：切面在目标类编译时被织入，这种方式需要特殊的编译器。
- 类加载期：切面在目标类加载到JVM时被织入，这种方式需要特殊的类加载器，它可以在目标类被引入应用之前增强该目标类的字节码。
- 运行期：切面在应用运行的某个时刻被织入，一般情况下，在织入切面时，AOP容器会为目标对象动态创建一个代理对象，Spring AOP就是以这种方式织入切面的。



```java
public class UserService{
    void save(){}
    List list(){}
    ....
}

```

> 以UserService为例，在UserService中的save()方法前需要开启事务，在方法后关闭事务，在抛异常时回滚事务。
> 那么,UserService中的所有方法都是连接点(JoinPoint),save()方法就是切点(Poincut)。需要在save()方法前后执行的方法就是通知(Advice)，切点和通知合起来就是一个切面(Aspect)。save()方法就是目标(target)。把想要执行的代码动态的加入到save()方法前后就是织入(Weaving)。



##### AOP通知

1.before(前置通知)：  在方法开始执行前执行

2.after(后置通知)：  在方法执行后执行

3.afterReturning(返回后通知)：   在方法返回后执行

4.afterThrowing(异常通知)： 在抛出异常时执行

5.around(环绕通知)：  在方法执行前和执行后都会执行

> 执行顺序 around > before > 【方法执行】>around > after > afterReturning

#### 定义简单切面

```java
@Aspect // 使用@Aspect注解将一个java类定义为切面类
@Component
public class MyAspect {

  // 使用@Pointcut定义一个切入点，可以是一个规则表达式，也可以是一个注解等
  // Pointcut的定义包括两个部分：Pointcut表示式(expression)和Pointcut签名(signature)
  // Pointcut表示式
  // execution(* *(..)):表示匹配所有方法
  // execution(public * com.test.TestController.*(..)):表示匹配com.test.TestController类中所有的公有方法
  // execution(* com.test..*.*(..)):表示匹配com.test包中所有的方法
  @Pointcut("execution(public * com.test.TestController.testFunc(..))")
  // Pointcut签名
  public void pointCut() {}
    
  // 使用@Before在切入点开始处切入内容
  @Before("pointCut()")
  public void before() {
      log.info("MyAspect before ...");
  }
    
  // 使用@After在切入点结尾处切入内容
  @After("pointCut()")
  public void after() {
      log.info("MyAspect after ...");
  }
    
  //使用@AfterReturning在切入点return内容之后切入内容（可以用来对处理返回值做一些加工处理）
  @AfterReturning("pointCut()")
  public void afterReturning() {
      log.info("MyAspect after returning ...");
  }
    
  //使用@AfterThrowing用来处理当切入内容部分抛出异常之后的处理逻辑
  @AfterThrowing("pointCut()")
  public void afterThrowing() {
      log.info("MyAspect after throwing ...");
  }
    
  //使用@Around在切入点前后切入内容，并自己控制何时执行切入点自身的内容
  @Around("pointCut()")
  public void around(ProceedingJoinPoint joinPoint) throws Throwable {
      log.info("MyAspect around before ...");
      joinPoint.proceed();
      log.info("MyAspect around after ...");
  }
}

```

Spring AOP提供使用org.aspectj.lang.JoinPoint类型获取连接点数据，任何通知方法的第一个参数都可以是JoinPoint(环绕通知是ProceedingJoinPoint，JoinPoint子类)。

1. **JoinPoint：提供访问当前被通知方法的目标对象、代理对象、方法参数等数据**
2. **ProceedingJoinPoint：只用于环绕通知，使用proceed()方法来执行目标方法**

如参数类型是JoinPoint、ProceedingJoinPoint类型，可以从“argNames”属性省略掉该参数名（可选，写上也对），这些类型对象会自动传入的，但必须作为第一个参数。

##### 例子-切面中使用JoinPoint和ProceedingJoinPoint

```java
@Aspect
@Component
public class MyAspect {

  @Pointcut("execution(public * com.test.TestController.testFunc(..))")
  public void pointCut() {}

  @Before("pointCut()")
  public void before(JoinPoint joinPoint) {
      String method = joinPoint.getSignature().getName();
      log.info("MyAspect before Method：{}::{}", joinPoint.getSignature().getDeclaringTypeName(), method);
      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      HttpServletRequest request = attributes.getRequest();
      log.info("ClientIP：{}", request.getRemoteAddr());
  }

  @After("pointCut()")
  public void after(JoinPoint joinPoint) {
      String method = joinPoint.getSignature().getName();
      log.info("MyAspect after Method：{}::{}", joinPoint.getSignature().getDeclaringTypeName(), method);
  }

  @AfterReturning("pointCut()")
  public void afterReturning(JoinPoint joinPoint) {
      String method = joinPoint.getSignature().getName();
      log.info("MyAspect after returning Method：{}::{}", joinPoint.getSignature().getDeclaringTypeName(), method);
  }

  @AfterThrowing("pointCut()")
  public void afterThrowing(JoinPoint joinPoint) {
      log.info("MyAspect after throwing ...");
  }

  @Around("pointCut()")
  public void around(ProceedingJoinPoint joinPoint) throws Throwable {
      log.info("MyAspect around before ...");
      joinPoint.proceed();// 用proceed执行原方法 可用OBJ对象接收原方法返回值
      log.info("MyAspect around after ...");
  }
}

```

##### 优化：AOP切面的优先级

由于通过AOP实现，程序得到了很好的解耦，但是也会带来一些问题，比如：我们可能会对Web层做多个切面，校验用户，校验头信息等等，这个时候经常会碰到切面的处理顺序问题。

所以，我们需要定义每个切面的优先级，我们需要`@Order(i)`注解来标识切面的优先级。**i的值越小，优先级越高**。假设我们还有一个切面是`CheckNameAspect`用来校验name必须为didi，我们为其设置`@Order(10)`，假设另外有一个WebLogAspect设置为`@Order(5)`，所以WebLogAspect有更高的优先级，这个时候执行顺序是这样的：

- 在`@Before`中优先执行`@Order(5)`的内容，再执行`@Order(10)`的内容
- 在`@After`和`@AfterReturning`中优先执行`@Order(10)`的内容，再执行`@Order(5)`的内容

所以我们可以这样子总结：

- 在切入点前的操作，按order的值由小到大执行
- 在切入点后的操作，按order的值由大到小执行



##### JoinPoint  getSignature()  返回属性详解

```java
public interface Signature {
    String toString();
 
    String toShortString();
 
    String toLongString();
 
    String getName();// 请求方法名
 
    int getModifiers();
 
    Class getDeclaringType();
 
    String getDeclaringTypeName();// 请求类名
}
```

##### tips

```java
@Around("log()")
public Object aroundLog(ProceedingJoinPoint point) throws Throwable {
    Object result = point.proceed();
    log.info("【返回值】:{}",JSONUtil.toJsonStr(result));
    return result;
}
```
```java
  @Around("pointCut()")
  public void around(ProceedingJoinPoint joinPoint) throws Throwable {
      log.info("MyAspect around before ...");
      joinPoint.proceed();
      log.info("MyAspect around after ...");
  }
```

> **在实际使用中，如果@Around下的方法是void，无返回类型，接口返回值将会被阻塞，需要使用第一种方法，才能使接口正确返回数据**



> 参考网址：
>
> https://juejin.im/post/6844903766035005453
>
> https://juejin.im/post/6844903942254510093
>
> http://blog.didispace.com/springbootaoplog/



#### RequestContextHolder 分析

> 持有上下文的Request容器 在Controller层外取用Request Response时使用 获取方法如下

```java
ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
HttpServletResponse response = Objects.requireNonNull(attributes).getResponse();
```



> 参考：
>
> https://my.oschina.net/ojeta/blog/801640



#### UserAgentUtils

> UserAgentUtils是一个处理user-agent 字符的一个工具。可以用来实时地处理http请求和分析http请求日志文件。这里可以使用UserAgentUtils对访问用户的浏览器类型、操作系统、设备种类等进行统计分析。

基本用法

```java
UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
Browser browser = userAgent.getBrowser();
String browserName = browser.getName();// 浏览器名称
String group = browser.getGroup().getName();// 浏览器大类
Version browserVersion = userAgent.getBrowserVersion();// 详细版本
String version = browserVersion.getMajorVersion();// 浏览器主版本
System.out.println(browserName);
System.out.println(group);
System.out.println(browserVersion);
System.out.println(version);
System.out.println(userAgent.getOperatingSystem());// 访问设备系统
System.out.println(userAgent.getOperatingSystem().getDeviceType());// 访问设备类型
System.out.println(userAgent.getOperatingSystem().getManufacturer());// 访问设备制造厂商
```



#### TestController.java

```java
@RestController
public class TestController {
    @GetMapping("/test")
    public Dict test(String who) {
        return Dict.create().set("who", StrUtil.isBlank(who) ? "me" : who);
    }
}
```