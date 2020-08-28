# SpringBoot13-jdbctemplate

> 在Spring Boot中使用jdbctemplate操作数据库，简易封装了一个通用的Dao层，完成增删改查操作

#### pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
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
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>

    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>5.4.0</version>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

> 首先给出的还是pom.xml文件，其中集成了数据库操作相关的依赖

#### 数据库

> 在本机MySql上新建名为spring-boot-demo的数据库

> 在resources/db文件夹下放两个sql文件

##### data.sql

```sql
INSERT INTO `orm_user`(`id`,`name`,`password`,`salt`,`email`,`phone_number`) VALUES (1, 'user_1', 'ff342e862e7c3285cdc07e56d6b8973b', '412365a109674b2dbb1981ed561a4c70', 'user1@xkcoding.com', '17300000001');
INSERT INTO `orm_user`(`id`,`name`,`password`,`salt`,`email`,`phone_number`) VALUES (2, 'user_2', '6c6bf02c8d5d3d128f34b1700cb1e32c', 'fcbdd0e8a9404a5585ea4e01d0e4d7a0', 'user2@xkcoding.com', '17300000002
```

##### schema.sql

```sql
DROP TABLE IF EXISTS `orm_user`;
CREATE TABLE `orm_user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `name` VARCHAR(32) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(32) NOT NULL COMMENT '加密后的密码',
  `salt` VARCHAR(32) NOT NULL COMMENT '加密使用的盐',
  `email` VARCHAR(32) NOT NULL UNIQUE COMMENT '邮箱',
  `phone_number` VARCHAR(15) NOT NULL UNIQUE COMMENT '手机号码',
  `status` INT(2) NOT NULL DEFAULT 1 COMMENT '状态，-1：逻辑删除，0：禁用，1：启用',
  `create_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '创建时间',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '上次登录时间',
  `last_update_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '上次更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Spring Boot Demo Orm 系列示例表';
```

#### application.yml

> 数据库配置

```yml
server:
  port: 8050
  servlet:
    context-path: /study
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/spring-boot-demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8
    username: root
    password: root
    # 指定driver的类名
    driver-class-name: com.mysql.cj.jdbc.Driver
    # 配置数据源的类型
    type: com.zaxxer.hikari.HikariDataSource
    # 初始化模式 always:每次都初始化
    initialization-mode: always
    # 初始化数据库时，遇到错误是否继续
    continue-on-error: true
    # 启动时需要初始化的建表语句
    schema:
    - "classpath:db/schema.sql"
    # 初始化的数据
    data:
    - "classpath:db/data.sql"
    hikari:
      minimum-idle: 5 # 池中维护的最小空闲连接数
      connection-test-query: SELECT 1 FROM DUAL # 如果您的驱动程序支持JDBC4，我们强烈建议您不要设置此属性
      maximum-pool-size: 20 # 池中最大连接数，包括闲置和使用中的连接
      auto-commit: true # 自动提交从池中返回的连接
      idle-timeout: 30000 # 连接允许在池中闲置的最长时间
      max-lifetime: 60000 # 池中连接最长生命周期
      connection-timeout: 30000 # 等待来自池的连接的最大毫秒数
      pool-name: SpringBootDemoHikariCP # 连接池的用户定义名称，主要出现在日志记录和JMX管理控制台中以识别池和池配置
logging:
  level:
    com.study: debug
```

#### Annotation

> 自定义注解

注解（Annotation），也叫元数据（Metadata），是Java5的新特性，JDK5引入了Metadata很容易的就能够调用Annotations。注解与类、接口、枚举在同一个层次，并可以应用于包、类型、构造方法、方法、成员变量、参数、本地变量的声明中，用来对这些元素进行说明注释。

**注解的语法与定义形式**

（1）以`@interface`关键字定义
（2）注解包含成员，成员以无参数的方法的形式被声明。其方法名和返回值定义了该成员的名字和类型。
（3）成员赋值是通过`@Annotation(name=value)`的形式。
（4）注解需要标明注解的生命周期，注解的修饰目标等信息，这些信息是通过元注解实现。

------

1.`@interface`表示该类是一个注解，并实现了`Annotation接口`。

2.`@Target`表示该注解可以修饰的类型；如果不填，则此注解可以使用任何元素之上；

3.@Retention用来定义注解在哪一个级别可用，是源码中可见、编译器可见还是VM可见；如果不填，默认级别是CLASS。

4.注解元素可用的类型
所有基本类型（int,float,boolean,byte,double,char,long,short)、 String、Class、enum、Annotation和以上类型的数组；其它的值都会抛出`Error： 注释值不是允许的类型`。

5.注解的快捷方式
当注解内元素方法名为value()时，**并且该元素是唯一一个需要赋值的值**(另外的值有默认值)，可以不用使用键值对来传值，直接使用`@MyAnnotation("2")`传值；如果有两个，则要使用`@MyAnnotation(id=1,value = "2")`来传值。

##### 注解的生命周期

1、RetentionPolicy.SOURCE：注解只保留在源文件，当Java文件编译成class文件的时候，注解被遗弃；
2、RetentionPolicy.CLASS：注解被保留到class文件，但jvm加载class文件时候被遗弃，这是默认的生命周期；
3、RetentionPolicy.RUNTIME：注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在；

**1. @Target**
表示该注解可以用在什么地方，由ElementType枚举定义

```java
@Documented
@Retention(RetentionPolicy.RUNTIME) //被VM识别
@Target({ElementType.ANNOTATION_TYPE}) //修饰注解类型
public @interface Target {
    ElementType[] value(); //当使用此注解时，无默认注解，必须要传入ElementType
}
```

```java
package java.lang.annotation; //java.lang.annotation包下
public enum ElementType {
    TYPE,  // 类、接口（包括注释类型）或枚举声明
    FIELD,  // 字段声明（包括枚举常量）
    METHOD,  //方法声明
    PARAMETER,  //参数声明
    CONSTRUCTOR,  //构造方法声明
    LOCAL_VARIABLE,  //局部变量声明
    ANNOTATION_TYPE,  //注解类型声明
    PACKAGE,  //包声明
    TYPE_PARAMETER,  //类型参数声明(1.8新加入)，表示这个注解可以用来标注类型参数
    TYPE_USE; //类型使用声明(1.8新加入)，用于标注各种类型，只要是类型名称，都可以进行注解

    private ElementType() {
    }
}

```

**2. @Retention**
表示需要在什么级别保存该注解信息，由RetentionPolicy枚举定义

```java
package java.lang.annotation;//java.lang.annotation包下
public enum RetentionPolicy {
    SOURCE,  //注解将被编译器丢弃；Annotation信息仅存在于编译器处理期间，编译器处理完之后就没有该Annotation信息了
    CLASS,  //注解在class文件中可用，但会被VM丢弃
    RUNTIME;  //VM将在运行期也保留注解信息，因此可以通过反射机制读取注解的信息

    private RetentionPolicy() {
    }
}
```

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Retention {
    RetentionPolicy value(); // 必须要传入
}
```

#### 项目中定义的注解

##### Column

```java
/**
 * 列注解
 */
// 元注解:负责注解其他注解的注解
// 元注解@Retention，成员value的值为RetentionPolicy.RUNTIME
// @Retention标明注解的生命周期
@Retention(RetentionPolicy.RUNTIME)
// 元注解@Target，成员value是个数组，用{}形式赋值，值为ElementType.FIELD
@Target({ElementType.FIELD})
public @interface Column {
    /**
     * 列名
     *
     * @return 列名
     */
    String name();
}
```

##### Ignore

```java
/**
 * 需要忽略的字段
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Ignore {
}
```

##### Pk

```java
/**
 * 主键注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Pk {
    /**
     * 自增
     *
     * @return 自增主键
     */
    boolean auto() default true;
}
```

##### Table

```java
/**
 * 表注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
    /**
     * 表名
     *
     * @return 表名
     */
    String name();
}
```

> 相关参考资料：
>
> https://blog.csdn.net/github_35180164/article/details/52107204
>
> https://blog.csdn.net/github_35180164/article/details/52118286
>
> https://juejin.im/post/6844903470198194189

#### 常量池 Const.java

```java
/**
 * 常量池
 */
public interface Const {
    /**
     * 加密盐前缀
     */
    String SALT_PREFIX = "::SpringBootDemo::";

    /**
     * 逗号分隔符
     */
    String SEPARATOR_COMMA = ",";
}
```

#### 实体类 User.java

```java
/**
 * 用户实体类
 */
@Data
@Table(name = "orm_user")
public class User implements Serializable {
    /**
     * 主键
     */
    @Pk
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
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * 状态，-1：逻辑删除，0：禁用，1：启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 上次登录时间
     */
    @Column(name = "last_login_time")
    private Date lastLoginTime;

    /**
     * 上次更新时间
     */
    @Column(name = "last_update_time")
    private Date lastUpdateTime;
}
```

#### BaseDao.java

```java
/**
 *Dao基类
 */
@Slf4j
public class BaseDao<T, P> {
    private JdbcTemplate jdbcTemplate;
    private Class<T> clazz;

    // 作用：用于抑制编译器产生警告信息
    @SuppressWarnings(value = "unchecked") 
    public BaseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        // Java 得到泛型中的T.class
        clazz = (Class<T>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    /**
     * 通用插入，自增列需要添加 {@link Pk} 注解
     *
     * @param t          对象
     * @param ignoreNull 是否忽略 null 值
     * @return 操作的行数
     */
    protected Integer insert(T t, Boolean ignoreNull) {
        String table = getTableName(t);

        List<Field> filterField = getField(t, ignoreNull);

        List<String> columnList = getColumns(filterField);

        String columns = StrUtil.join(Const.SEPARATOR_COMMA, columnList);

        //构造占位符
        //StrUtil.repeatAndJoin() 重复某个字符串并通过分界符连接
        //StrUtil.repeatAndJoin("?", 5, ",")   = "?,?,?,?,?"
 		//StrUtil.repeatAndJoin("?", 0, ",")   = ""
 		//StrUtil.repeatAndJoin("?", 5, null) = "?????"
        String params = StrUtil.repeatAndJoin("?", columnList.size(), Const.SEPARATOR_COMMA);

        //构造值
        Object[] values = filterField.stream().map(field -> ReflectUtil.getFieldValue(t, field)).toArray();

        String sql = StrUtil.format("INSERT INTO {table} ({columns}) VALUES ({params})", Dict.create().set("table", table).set("columns", columns).set("params", params));
        log.debug("【执行SQL】SQL：{}", sql);
        log.debug("【执行SQL】参数：{}", JSONUtil.toJsonStr(values));
        return jdbcTemplate.update(sql, values);
    }
    /**
     * 通用根据主键删除
     *
     * @param pk 主键
     * @return 影响行数
     */
    protected Integer deleteById(P pk) {
        String tableName = getTableName();
        String sql = StrUtil.format("DELETE FROM {table} where id = ?", Dict.create().set("table",tableName));
        log.debug("【执行SQL】SQL：{}", sql);
        log.debug("【执行SQL】参数：{}", JSONUtil.toJsonStr(pk));
        return jdbcTemplate.update(sql, pk);
    }

    /**
     * 通用根据主键更新，自增列需要添加 {@link Pk} 注解
     *
     * @param t          对象
     * @param pk         主键
     * @param ignoreNull 是否忽略 null 值
     * @return 操作的行数
     */
    protected Integer updateById(T t, P pk, Boolean ignoreNull) {
        //获取实体类对应的表名 从注解获取
        String tableName = getTableName(t);
		//获取实体类中定义的所有字段
        List<Field> filterField = getField(t, ignoreNull);
		//获取实体类中定义的字段对应的数据库字段
        List<String> columnList = getColumns(filterField);

        // StrUtil.appendIfMissing() 如果给定字符串不是以给定的一个或多个字符串为结尾，则在尾部添加结尾字符串
        List<String> columns = columnList.stream().map(s -> StrUtil.appendIfMissing(s, " = ?")).collect(Collectors.toList());
        String params = StrUtil.join(Const.SEPARATOR_COMMA, columns);

        // 构造值 ReflectUtil.getFieldValue()获取类中字段对应的值
        List<Object> valueList = filterField.stream().map(field -> ReflectUtil.getFieldValue(t, field)).collect(Collectors.toList());
        //添加主键 位置在最后
        valueList.add(pk);

        Object[] values = ArrayUtil.toArray(valueList, Object.class);

        String sql = StrUtil.format("UPDATE {table} SET {params} where id = ?", Dict.create().set("table", tableName).set("params", params));
        log.debug("【执行SQL】SQL：{}", sql);
        log.debug("【执行SQL】参数：{}", JSONUtil.toJsonStr(values));
        return jdbcTemplate.update(sql, values);
    }
    /**
     * 通用根据主键查询单条记录
     *
     * @param pk 主键
     * @return 单条记录
     */
    public T findOneById(P pk) {
        String tableName = getTableName();
        String sql = StrUtil.format("SELECT * FROM {table} where id = ?", Dict.create().set("table", tableName));
        // sping中的RowMapper可以将数据中的每一行数据封装成用户定义的类
        RowMapper<T> rowMapper = new BeanPropertyRowMapper<>(clazz);
        log.debug("【执行SQL】SQL：{}", sql);
        log.debug("【执行SQL】参数：{}", JSONUtil.toJsonStr(pk));
        return jdbcTemplate.queryForObject(sql, new Object[]{pk}, rowMapper);
    }
    /**
     * 根据对象查询
     *
     * @param t 查询条件
     * @return 对象列表
     */
    public List<T> findByExample(T t) {
        String tableName = getTableName(t);
        
        List<Field> filterField = getField(t, true);
        
        List<String> columnList = getColumns(filterField);

        List<String> columns = columnList.stream().map(s -> " and " + s + " = ? ").collect(Collectors.toList());

        String where = StrUtil.join(" ", columns);
        // 构造值
        Object[] values = filterField.stream().map(field -> ReflectUtil.getFieldValue(t, field)).toArray();

        String sql = StrUtil.format("SELECT * FROM {table} where 1=1 {where}", Dict.create().set("table", tableName).set("where", StrUtil.isBlank(where) ? "" : where));
        log.debug("【执行SQL】SQL：{}", sql);
        log.debug("【执行SQL】参数：{}", JSONUtil.toJsonStr(values));
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, values);
        List<T> ret = CollUtil.newArrayList();
        //BeanUtil.fillBeanWithMap()使用Map填充Bean对象，可配置将下划线转换为驼峰 isToCamelCase(参数3) - 是否将下划线模式转换为驼峰模式
        maps.forEach(map -> ret.add(BeanUtil.fillBeanWithMap(map, ReflectUtil.newInstance(clazz), true, false)));
        return ret;
    }

    /**
     * 获取表名
     *
     * @param t 对象
     * @return 表名
     */
    private String getTableName(T t) {
        // 如果存在该元素的指定类型的注释，则返回这些注释，否则返回 null
        Table tableAnnotation = t.getClass().getAnnotation(Table.class);
        if (ObjectUtil.isNotNull(tableAnnotation)) {
            return StrUtil.format("`{}`", tableAnnotation.name());
        }else {
            return StrUtil.format("`{}`", t.getClass().getName().toLowerCase());
        }
    }

    /**
     * 获取表名
     *
     * @return 表名
     */
    private String getTableName() {
        // 如果存在该元素的指定类型的注释，则返回这些注释，否则返回 null
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        if (ObjectUtil.isNotNull(tableAnnotation)) {
            return StrUtil.format("`{}`", tableAnnotation.name());
        } else {
            return StrUtil.format("`{}`", clazz.getName().toLowerCase());
        }
    }

    /**
     * 获取列
     *
     * @param fieldList 字段列表
     * @return 列信息列表
     */
    //根据实体类的字段来获取对应的数据库字段
    private List<String> getColumns(List<Field> fieldList) { // Java反射中Field用于获取某个类的属性或该属性的属性值
        // 构造列
        List<String> columnList = CollUtil.newArrayList();
        for (Field field : fieldList) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            String columnName;
            if (ObjectUtil.isNotNull(columnAnnotation)) {
                columnName = columnAnnotation.name();
            } else {
                columnName =field.getName();
            }
            columnList.add(StrUtil.format("`{}`", columnName));
        }
        return columnList;
    }
    /**
     * 获取字段列表 {@code 过滤数据库中不存在的字段，以及自增列}
     *
     * @param t          对象
     * @param ignoreNull 是否忽略空值
     * @return 字段列表
     */
    //从实体类中获取字段
    private List<Field> getField(T t, Boolean ignoreNull) {
        // 获取所有字段，包含父类中的字段
        // ReflectUtil.getFields()获得一个类中所有字段列表，包括其父类中的字段
        Field[] fields = ReflectUtil.getFields(t.getClass());

        //过滤数据库中不存在的字段，以及自增列
        List<Field> filterField;
        Stream<Field> fieldStream =  CollUtil.toList(fields).stream().filter(field -> ObjectUtil.isNull(field.getAnnotation(Ignore.class)) || ObjectUtil.isNull(field.getAnnotation(Pk.class)));

        // 是否过滤字段值为null的字段
        if (ignoreNull) {
            filterField = fieldStream.filter(field -> ObjectUtil.isNotNull(ReflectUtil.getFieldValue(t, field))).collect(Collectors.toList());
        } else {
            filterField = fieldStream.collect(Collectors.toList());
        }
        return filterField;
    }
}
```

#### UserDao.java

> `@Repository`注解在持久层中，具有将数据库操作抛出的原生异常翻译转化为spring的持久层异常的功能。

```java
@Repository
public class UserDao extends BaseDao<User, Long> {
    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    /**
     * 保存用户
     *
     * @param user 用户对象
     * @return 操作影响行数
     */
    public Integer insert(User user) {
        return super.insert(user, true);
    }

    /**
     * 根据主键删除用户
     *
     * @param id 主键id
     * @return 操作影响行数
     */
    public Integer delete(Long id) {
        return super.deleteById(id);
    }

    /**
     * 更新用户
     *
     * @param user 用户对象
     * @param id   主键id
     * @return 操作影响行数
     */
    public Integer update(User user, Long id) {
        return super.updateById(user, id, true);
    }

    /**
     * 根据主键获取用户
     *
     * @param id 主键id
     * @return id对应的用户
     */
    public User selectById(Long id) {
        return super.findOneById(id);
    }

    /**
     * 根据查询条件获取用户列表
     *
     * @param user 用户查询条件
     * @return 用户列表
     */
    public List<User> selectUserList(User user) {
        return super.findByExample(user);
    }
}
```

#### IUserService.java

```javascript
public interface IUserService {
    /**
     * 保存用户
     *
     * @param user 用户实体
     * @return 保存成功 {@code true} 保存失败 {@code false}
     */
    Boolean save(User user);

    /**
     * 删除用户
     *
     * @param id 主键id
     * @return 删除成功 {@code true} 删除失败 {@code false}
     */
    Boolean delete(Long id);

    /**
     * 更新用户
     *
     * @param user 用户实体
     * @param id   主键id
     * @return 更新成功 {@code true} 更新失败 {@code false}
     */
    Boolean update(User user, Long id);

    /**
     * 获取单个用户
     *
     * @param id 主键id
     * @return 单个用户对象
     */
    User getUser(Long id);

    /**
     * 获取用户列表
     *
     * @param user 用户实体
     * @return 用户列表
     */
    List<User> getUser(User user);
}
```

#### UserServiceImpl.java

```java
@Service
public class UserServiceImpl implements IUserService {
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 保存用户
     *
     * @param user 用户实体
     * @return 保存成功 {@code true} 保存失败 {@code false}
     */
    @Override
    public Boolean save(User user) {
        String rawPass = user.getPassword();
        String salt = IdUtil.simpleUUID();
        String pass = SecureUtil.md5(rawPass + Const.SALT_PREFIX + salt);
        user.setPassword(pass);
        user.setSalt(salt);
        return userDao.insert(user) > 0;
    }

    /**
     * 删除用户
     *
     * @param id 主键id
     * @return 删除成功 {@code true} 删除失败 {@code false}
     */
    @Override
    public Boolean delete(Long id) {
        return userDao.delete(id) > 0;
    }

    /**
     * 更新用户
     *
     * @param user 用户实体
     * @param id   主键id
     * @return 更新成功 {@code true} 更新失败 {@code false}
     */
    @Override
    public Boolean update(User user, Long id) {
        User exist = getUser(id);
        if (StrUtil.isNotBlank(user.getPassword())) {
            String rawPass = user.getPassword();
            String salt = IdUtil.simpleUUID();
            String pass = SecureUtil.md5(rawPass + Const.SALT_PREFIX + salt);
            user.setPassword(pass);
            user.setSalt(salt);
        }
        BeanUtil.copyProperties(user, exist, CopyOptions.create().setIgnoreNullValue(true));
        exist.setLastUpdateTime(new DateTime());
        return userDao.update(exist, id) > 0;
    }

    /**
     * 获取单个用户
     *
     * @param id 主键id
     * @return 单个用户对象
     */
    @Override
    public User getUser(Long id) {
        return userDao.findOneById(id);
    }

    /**
     * 获取用户列表
     *
     * @param user 用户实体
     * @return 用户列表
     */
    @Override
    public List<User> getUser(User user) {
        return userDao.findByExample(user);
    }
}
```

#### UserController.java

```java
@RestController
@Slf4j
public class UserController {
    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public Dict save(@RequestBody User user) {
        Boolean save = userService.save(user);
        return Dict.create().set("code", save ? 200 : 500).set("msg", save ? "成功" : "失败").set("data", save ? user : null);
    }

    @DeleteMapping("/user/{id}")
    public Dict delete(@PathVariable Long id) {
        Boolean delete = userService.delete(id);
        return Dict.create().set("code", delete ? 200 : 500).set("msg", delete ? "成功" : "失败");
    }

    @PutMapping("/user/{id}")
    public Dict update(@RequestBody User user, @PathVariable Long id) {
        Boolean update = userService.update(user, id);
        return Dict.create().set("code", update ? 200 : 500).set("msg", update ? "成功" : "失败").set("data", update ? user : null);
    }

    @GetMapping("/user/{id}")
    public Dict getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return Dict.create().set("code", 200).set("msg", "成功").set("data", user);
    }

    @GetMapping("/user")
    public Dict getUser(User user) {
        List<User> userList = userService.getUser(user);
        return Dict.create().set("code", 200).set("msg", "成功").set("data", userList);
    }
}
```