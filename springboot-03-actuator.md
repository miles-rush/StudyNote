# SpringBoot3-actuator

> 演示了如何在spring Boot中通过actuator检查项目运行情况

### 配置依赖

##### pom.xml

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
        <exclusions>
            <exclusion>
                <groupId>org.junit.vintage</groupId>
                <artifactId>junit-vintage-engine</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```



#### 属性配置

```yml
server:
  port: 8055
  servlet:
    context-path: /study
# 若要访问端点信息，需要配置用户名和密码
spring:
  security:
    user:
      name: admin
      password: admin
management:
  # 端点信息接口使用的端口，为了和主系统接口使用的端口进行分离
  server:
    port: 8050
    servlet:
      context-path: /sys
  # 端点健康情况，默认值"never"，设置为"always"可以显示硬盘使用情况和线程情况
  endpoint:
    health:
      show-details: always
  # 设置端点暴露的哪些内容，默认["health","info"]，设置"*"代表暴露所有可访问的端点
  endpoints:
    web:
      exposure:
        include: '*'
```



> 将项目运行起来之后，会在**控制台**里查看所有可以访问的端口信息
>
> 1. 打开浏览器，访问：http://localhost:8090/sys/actuator/mappings ，输入用户名(xkcoding)密码(123456)即可看到所有的mapping信息
> 2. 访问：http://localhost:8090/sys/actuator/beans ，输入用户名(xkcoding)密码(123456)即可看到所有 Spring 管理的Bean
> 3. 其余可访问的路径，参见文档



> - actuator文档：https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#production-ready
> - 具体可以访问哪些路径，参考: https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#production-ready-endpoints



#### 

