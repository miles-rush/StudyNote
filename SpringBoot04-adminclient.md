# SpringBoot4-adminclient

> 本项目演示如何集成使用Spring Boot Admin，并将自己的运行状态交给Spring Boot Admin进行展示，和adminclient配套使用的是adminserver
>
> 这里先给出adminclient的代码，在adminserver详细描写使用

#### pom.xml

```xml
<dependencies>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>com.pig4cloud</groupId>
        <artifactId>spring-boot-admin-starter-client</artifactId>
        <version>0.1.2</version>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

</dependencies>
```

> 这里导入的依赖包是阿里云的镜像，按照demo的依赖运行时，出现了错误，在这里更正为阿里云的镜像版本后正常

```xml
<mirror>
  <id>aliyunmaven</id>
  <mirrorOf>*</mirrorOf>
  <name>阿里云公共仓库</name>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

#### application.yml

```yml
server:
  port: 8044
  servlet:
    context-path: /study
spring:
  application:
    # Spring Boot Admin展示的客户端项目名，不设置，会使用自动生成的随机id
    name: spring-boot-demo-admin-client
  boot:
    admin:
      client:
        # Spring Boot Admin 服务端地址
        url: "http://localhost:8000/"
        instance:
          metadata:
            # 客户端端点信息的安全认证信息
            user.name: ${spring.security.user.name}
            user.password: ${spring.security.user.password}
  security:
    user:
      name: admin
      password: admin
management:
  endpoint:
    health:
      # 端点健康情况，默认值"never"，设置为"always"可以显示硬盘使用情况和线程情况
      show-details: always
  endpoints:
    web:
      exposure:
        # 设置端点暴露的哪些内容，默认["health","info"]，设置"*"代表暴露所有可访问的端点
        include: "*"
```

> application.yml中是对Spring Boot Admin的一些配置，其中Spring Boot Admin的服务端地址必须和服务端项目对应，不然无法正确注册服务

#### IndexController.java

```java
@RestController
public class IndexController {
    @GetMapping(value = {"", "/"})
    public String index() {
        return "this is a Spring Boot Admin Client.";
    }
}
```