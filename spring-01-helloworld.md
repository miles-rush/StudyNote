# SpringBoot1-Hello World!

> 使用Spring Boot写一个返回hello world的接口   

##### 1.创建项目，删除不必要的文件   

###### 2.配置pom.xml

​	

```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
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
        <artifactId>spring-boot</artifactId>
        <version>2.3.3.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
    </dependency>
</dependencies>
```

###### 3.配置application.yml

​	

```
server:
  port: 8055
  servlet:
    context-path: /study
```

3.1server.servlet.context-path= # Context path of the application. 应用的上下文路径，也可以称为项目路径，是构成url地址的一部分

3.2server.servlet.context-path不配置时，默认为 / ，如：localhost:8080/xxxxxx

3.3server.servlet.context-path有配置时，比如 /demo，此时的访问方式为localhost:8080/demo/xxxxxx



###### 4.HelloworldApplication.java

​	

```
/**
 * Springboot启动类
 */
@SpringBootApplication
@RestController
public class HelloworldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloworldApplication.class, args);
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam(required = false, name = "who") String who) {
        if (StrUtil.isBlank(who)) {
            who = "World";
        }
        return StrUtil.format("Hello,{}!", who);
    }

}
```



