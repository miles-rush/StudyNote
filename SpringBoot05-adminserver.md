# SpringBoot5-adminserver

> 本项目演示搭建Spring Boot Admin的服务端，可视化客户端项目的运行状态，和adminclient配套使用

#### pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>com.pig4cloud</groupId>
        <artifactId>spring-boot-admin-starter-server</artifactId>
        <version>2.2.0</version>
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

#### AdminserverApplication

> 启动类添加@EnableAdminServer注解

```java
@EnableAdminServer
@SpringBootApplication
public class AdminserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminserverApplication.class, args);
    }

}
```

#### application.properties

```xml
server.port=8000
```

> 这里的端口便是adminclient中配置的服务端地址



###### Tip:

> 两个项目建立完毕后，先启动adminserver，后启动adminclient，adminclient便能在adminserver注册成功

```ini
2020-08-24 18:09:57.954  INFO 7728 --- [           main] c.s.adminclient.AdminclientApplication   : Started AdminclientApplication in 4.772 seconds (JVM running for 6.194)
2020-08-24 18:09:59.172  INFO 7728 --- [-10.200.105.199] o.a.c.c.C.[Tomcat].[localhost].[/study]  : Initializing Spring DispatcherServlet 'dispatcherServlet'
2020-08-24 18:09:59.173  INFO 7728 --- [-10.200.105.199] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2020-08-24 18:09:59.219  INFO 7728 --- [-10.200.105.199] o.s.web.servlet.DispatcherServlet        : Completed initialization in 46 ms
2020-08-24 18:09:59.635  INFO 7728 --- [gistrationTask1] d.c.b.a.c.r.ApplicationRegistrator       : Application registered itself as 767e73d76ada
```

adminclient中出现上信息时，表明adminclient在adminserver中注册成功，此时可以访问adminserver的地址进入ui界面，如下图：

![](files\file05\show.png)

在该界面中，可以查看adminclient应用的可视化信息