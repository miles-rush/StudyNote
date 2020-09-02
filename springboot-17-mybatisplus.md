# SpringBoot17-mybatis-plus

> Spring Boot集成mybatis-plus
>
> ActiveRecord模式操作

#### pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
	<!-- 版本不要太新 不然项目中的有些函数已经不建议使用 暂时没有找到替代方法 官方文档未更新-->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.2.0</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>

    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>5.4.1</version>
    </dependency>

    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
        <version>29.0-jre</version>
    </dependency>
    
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

#### MybatisPlusConfig

```java
@Configuration
@MapperScan(basePackages = {"com.study.mybatisplus.mapper"})
@EnableTransactionManagement // 开启事务支持
public class MybatisPlusConfig {
    /**
     * 分页插件
     */
    // 在较新的版本中 这个方法已经不建议使用 但官方文档中未更新最新的分页配置方法 在项目中降低了mybaits-plus的版本
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
```

#### CommonFieldHandler

通用字段填充

配置后，在实体类中使用注解`@TableField`

可选的填充策略如下：【`@TableField(fill = INSERT_UPDATE)`】

| 值            | 描述               |
| :------------ | :----------------- |
| DEFAULT       | 默认不处理         |
| INSERT        | 插入填充字段       |
| UPDATE        | 更新填充字段       |
| INSERT_UPDATE | 插入和更新填充字段 |

```java
@Slf4j
@Component
public class CommonFieldHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("lastUpdateTime", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.setFieldValByName("lastUpdateTime", new Date(), metaObject);
    }
}
```

#### User

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("orm_user")
public class User implements Serializable {
    private static final long serialVersionUID = -1840831686851699943L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 加密后的密码
     */
    private String password;

    /**
     * 加密使用的盐
     */
    private String salt;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 状态，-1：逻辑删除，0：禁用，1：启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = INSERT)
    private Date createTime;

    /**
     * 上次登录时间
     */
    private Date lastLoginTime;

    /**
     * 上次更新时间
     */
    @TableField(fill = INSERT_UPDATE)
    private Date lastUpdateTime;
}
```

#### application.yml

```yml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/spring-boot-demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.zaxxer.hikari.HikariDataSource
    initialization-mode: always
    continue-on-error: true
    schema:
      - "classpath:db/schema.sql"
    data:
      - "classpath:db/data.sql"
    hikari:
      minimum-idle: 5
      connection-test-query: SELECT 1 FROM DUAL
      maximum-pool-size: 20
      auto-commit: true
      idle-timeout: 30000
      pool-name: SpringBootDemoHikariCP
      max-lifetime: 60000
      connection-timeout: 30000
logging:
  level:
    com.study: debug
    com.study.mapper: trace
mybatis-plus:
  mapper-locations: classpath:mappers/*.xml
  #实体扫描，多个package用逗号或者分号分隔
  typeAliasesPackage: com.xkcoding.orm.mybatis.plus.entity
  global-config:
    # 数据库相关配置
    db-config:
      #主键类型  AUTO:"数据库ID自增", INPUT:"用户输入ID",ID_WORKER:"全局唯一ID (数字类型唯一ID)", UUID:"全局唯一ID UUID";
      id-type: auto
      #字段策略 IGNORED:"忽略判断",NOT_NULL:"非 NULL 判断"),NOT_EMPTY:"非空判断"
      field-strategy: not_empty
      #驼峰下划线转换
      table-underline: true
      #是否开启大写命名，默认不开启
      #capital-mode: true
      #逻辑删除配置
      #logic-delete-value: 1
      #logic-not-delete-value: 0
      db-type: mysql
    #刷新mapper 调试神器
    refresh: true
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
```

`mybatis-plus.global-config.refresh`

是否自动刷新 Mapper 对应的 XML 文件，默认不自动刷新。如果配置了该属性，Mapper 对应的 XML 文件会自动刷新，更改 XML 文件后，无需再次重启工程，由此节省大量时间。

------

`mybatis-plus.configuration.map-underscore-to-camel-case`

否开启自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN（下划线命名） 到经典 Java 属性名 aColumn（驼峰命名） 的类似映射。

`mybatis-plus.configuration.cache-enabled`

全局地开启或关闭配置文件中的所有映射器已经配置的任何缓存，默认为 true。

> 参考：https://www.bookstack.cn/read/mybatis-plus-3.x/spilt.3.09e8fb6c9a564182.md

#### UserMapper

```java
@Component
public interface UserMapper extends BaseMapper<User> {
}
```

#### UserService

```java
public interface UserService extends IService<User> {
}
```

#### UserServiceImpl

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
```

#### UserServiceTest

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserServiceTest {
    @Autowired
    private UserService userService;

    /**
     * 测试Mybatis-Plus 新增
     */
    @Test
    public void testSave() {
        String salt = IdUtil.fastSimpleUUID();
        User testSave3 = User.builder().name("testSave3").password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave3@xkcoding.com").phoneNumber("17300000003").status(1).lastLoginTime(new DateTime()).build();
        boolean save = userService.save(testSave3);
        Assert.assertTrue(save);
        log.debug("【测试id回显#testSave3.getId()】= {}", testSave3.getId());
    }

    /**
     * 测试Mybatis-Plus 批量新增
     */
    @Test
    public void testSaveList() {
        List<User> userList = Lists.newArrayList();
        for (int i = 4; i < 14; i++) {
            String salt = IdUtil.fastSimpleUUID();
            User user = User.builder().name("testSave" + i).password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave" + i + "@xkcoding.com").phoneNumber("1730000000" + i).status(1).lastLoginTime(new DateTime()).build();
            userList.add(user);
        }
        boolean batch = userService.saveBatch(userList);
        Assert.assertTrue(batch);
        List<Long> ids = userList.stream().map(User::getId).collect(Collectors.toList());
        log.debug("【userList#ids】= {}", ids);
    }

    /**
     * 测试Mybatis-Plus 删除
     */
    @Test
    public void testDelete() {
        boolean remove = userService.removeById(1L);
        Assert.assertTrue(remove);
        User byId = userService.getById(1L);
        Assert.assertNull(byId);
    }

    /**
     * 测试Mybatis-Plus 修改
     */
    @Test
    public void testUpdate() {
        User user = userService.getById(1L);
        Assert.assertNotNull(user);
        user.setName("MybatisPlus修改名字");
        boolean b = userService.updateById(user);
        Assert.assertTrue(b);
        User update = userService.getById(1L);
        Assert.assertEquals("MybatisPlus修改名字", update.getName());
        log.debug("【update】= {}", update);
    }

    /**
     * 测试Mybatis-Plus 查询单个
     */
    @Test
    public void testQueryOne() {
        User user = userService.getById(1L);
        Assert.assertNotNull(user);
        log.debug("【user】= {}", user);
    }

    /**
     * 测试Mybatis-Plus 查询全部
     */
    @Test
    public void testQueryAll() {
        List<User> list = userService.list(new QueryWrapper<>());
        Assert.assertTrue(CollUtil.isNotEmpty(list));
        log.debug("【list】= {}", list);
    }

    /**
     * 测试Mybatis-Plus 分页排序查询
     */
    @Test
    public void testQueryByPageAndSort() {
        initData();
        int count = userService.count(new QueryWrapper<>());
        Page<User> userPage = new Page<>(1, 5);// 分页对象 当前页数 每页数目
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        IPage<User> page = userService.page(userPage, wrapper);
        Assert.assertEquals(5, page.getSize());
        Assert.assertEquals(count, page.getTotal());
        log.debug("【page.getRecords()】= {}", page.getRecords());
    }

    /**
     * 测试Mybatis-Plus 自定义查询
     */
    @Test
    public void testQueryByCondition() {
        initData();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name", "Save1").or().eq("phone_number", "17300000001").orderByDesc("id");
        int count = userService.count(wrapper);
        Page<User> userPage = new Page<>(1,3);
        IPage<User> page = userService.page(userPage, wrapper);
        Assert.assertEquals(3, page.getSize());
        Assert.assertEquals(count, page.getTotal());
        log.debug("【page.getRecords()】= {}", page.getRecords());
    }


    /**
     * 初始化数据
     */
    private void initData() {
        testSaveList();
    }
}
```

### ActiveRecord 模式

> 在Mybatis-Plus中提供了ActiveRecord的模式，支持 ActiveRecord 形式调用，实体类只需继承 Model 类即可实现基本 CRUD 操作，简单来说就是一个实体类继承Model类，并通过注解与数据库的表名进行关联，这样就可以通过实体类直接进行表的简单增删改查操作
>
> **其中需要注意的是：实体类中必须要重写pkVal方法**
>
> 参考：https://blog.csdn.net/qq_31142553/article/details/82959626

#### Role.java

```java
@Data
@TableName("orm_role")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Role extends Model<Role> {
    /**
     * 主键
     */
    private Long id;

    /**
     * 角色名
     */
    private String name;

    /**
     * 主键值，ActiveRecord 模式这个必须有，否则 xxById 的方法都将失效！
     * 即使使用 ActiveRecord 不会用到 RoleMapper，RoleMapper 这个接口也必须创建
     */
    @Override
    protected Serializable pkVal() {

        return this.id;
    }
}
```

`Accessors`翻译是存取器。通过该注解可以控制getter和setter方法的形式

1.`@Accessors(fluent = true)`
使用fluent属性，getter和setter方法的方法名都是属性名，且setter方法返回当前对象

```java
class User {
    private Integer id;
    private String name;
    
    // 生成的getter和setter方法如下，方法体略
    public Integer id(){}
    public User id(Integer id){}
    public String name(){}
    public User name(String name){}
}
```

2.`@Accessors(chain = true)`

使用chain属性，setter方法返回当前对象

```java
@Data
@Accessors(chain = true)
class User {
    private Integer id;
    private String name;
    
    // 生成的setter方法如下，方法体略
    public User setId(Integer id){}
    public User setName(String name){}
}
```

3.`@Accessors(prefix = “f”)`

使用prefix属性，getter和setter方法会忽视属性名的指定前缀（遵守驼峰命名）

```java
@Data
@Accessors(prefix = "f")
class User {
    private Integer fId;
    private String fName;
    
    // 生成的getter和setter方法如下，方法体略
    public Integer id(){}
    public void id(Integer id){}
    public String name(){}
    public void name(String name){}
}
```

> 参考：https://www.cnblogs.com/alter888/p/10735559.html

#### RoleMapper.java

**注意：即使使用 ActiveRecord 不会用到 RoleMapper，RoleMapper 这个接口也必须创建**

```java
public interface RoleMapper extends BaseMapper<Role> {
}
```

#### ActiveRecordTest.java

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ActiveRecordTest {
    /**
     * 测试 ActiveRecord 插入数据
     */
    @Test
    public void testActiveRecordInsert() {
        Role role = new Role();
        role.setName("VIP");
        Assert.assertTrue(role.insert());
        // 成功直接拿会写的 ID
        log.debug("【role】= {}", role);
    }

    /**
     * 测试 ActiveRecord 更新数据
     */
    @Test
    public void testActiveRecordUpdate() {
        Assert.assertTrue(new Role().setId(1L).setName("管理员-1").updateById());
        Assert.assertTrue(new Role().update(new UpdateWrapper<Role>().lambda().set(Role::getName, "普通用户-1").eq(Role::getId, 2)));
    }

    /**
     * 测试 ActiveRecord 查询数据
     */
    @Test
    public void testActiveRecordSelect() {
        Assert.assertEquals("管理员", new Role().setId(1L).selectById().getName());
        Role role = new Role().selectOne(new QueryWrapper<Role>().lambda().eq(Role::getId, 2));
        Assert.assertEquals("普通用户", role.getName());
        List<Role> roles = new Role().selectAll();
        Assert.assertTrue(roles.size() > 0);
        log.debug("【roles】= {}", roles);
    }

    /**
     * 测试 ActiveRecord 删除数据
     */
    @Test
    public void testActiveRecordDelete() {
        Assert.assertTrue(new Role().setId(1L).deleteById());
        Assert.assertTrue(new Role().delete(new QueryWrapper<Role>().lambda().eq(Role::getName, "普通用户")));
    }
}
```

## 参考

- mybatis-plus官方文档：http://mp.baomidou.com/