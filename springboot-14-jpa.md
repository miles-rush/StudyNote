# SpringBoot14-JPA

> Spring Boot使用JPA操作数据库

------

#### pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>5.4.0</version>
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

#### 数据库

###### schema.sql

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


DROP TABLE IF EXISTS `orm_department`;
CREATE TABLE `orm_department` (
  `id` INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `name` VARCHAR(32) NOT NULL COMMENT '部门名称',
  `superior` INT(11)  COMMENT '上级id',
  `levels` INT(11) NOT NULL COMMENT '层级',
  `order_no` INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '创建时间',
  `last_update_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '上次更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Spring Boot Demo Orm 系列示例表';

DROP TABLE IF EXISTS `orm_user_dept`;
CREATE TABLE `orm_user_dept` (
  `id` INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `user_id` INT(11) NOT NULL COMMENT '用户id',
  `dept_id` INT(11) NOT NULL COMMENT '部门id',
  `create_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '创建时间',
  `last_update_time` DATETIME NOT NULL DEFAULT NOW() COMMENT '上次更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Spring Boot Demo Orm 系列示例表';
```

###### data.sql

```java
INSERT INTO `orm_user`(`id`,`name`,`password`,`salt`,`email`,`phone_number`) VALUES (1, 'user_1', 'ff342e862e7c3285cdc07e56d6b8973b', '412365a109674b2dbb1981ed561a4c70', 'user1@xkcoding.com', '17300000001');
INSERT INTO `orm_user`(`id`,`name`,`password`,`salt`,`email`,`phone_number`) VALUES (2, 'user_2', '6c6bf02c8d5d3d128f34b1700cb1e32c', 'fcbdd0e8a9404a5585ea4e01d0e4d7a0', 'user2@xkcoding.com', '17300000002');
```

#### application.yml

> 这里使用的是jdbc-url，而不是url
>
> 如果您碰巧在类路径上有Hikari，那么这个基本设置就不起作用了，因为Hikari没有url属性(但是确实有一个jdbcUrl属性)。在这种情况下，您必须重写您的配置如下
>
> 参考:https://blog.csdn.net/qq_22156459/article/details/80283054

```yml
server:
  port: 8050
  servlet:
    context-path: /study
spring:
  datasource:
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.zaxxer.hikari.HikariDataSource
    initialization-mode: always
    continue-on-error: true
    jdbc-url: jdbc:mysql://127.0.0.1:3306/spring-boot-demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8
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
  jpa:
    show-sql: true # 打印sql语句
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL57InnoDBDialect # 数据库引擎配置
    open-in-view: true
logging:
  level:
    com.study: debug
    org.hibernate.SQL: debug
    org.hibernate.type: trace
```

注：因为Hikari没有url属性（但是有一个jdbcUrl属性）。在这种情况下，您必须按如下方式重写配置

```yml
app.datasource.jdbc-url=jdbc:mysql://localhost/test
app.datasource.username=dbuser
app.datasource.password=dbpass
```

##### JPA配置相关

`spring.jpa.hibernate.ddl-auto`

- validate 加载 Hibernate 时，验证创建数据库表结构
- create 每次加载 Hibernate ，重新创建数据库表结构
- create-drop 加载 Hibernate 时创建，退出是删除表结构
- update 加载 Hibernate 自动更新数据库结构

所以，如果你想保留表结构的数据，使用 update 即可

`jpa.open-in-view`

此属性将注册OpenEntityManagerInViewInterceptor，它将EntityManager注册到当前线程，因此在Web请求完成之前，您将拥有相同的EntityManager。它与Hibernate SessionFactory等无关。

#### JpaConfig.java 

> 配置文件包括三块内容 
>
> 数据源创建  实体管理器工厂生成 事务管理器配置
>
> 另外，注解中需要开启事务注解，开启审计功能，添加配置项，repository的包位置，指定transactionManagerRef事务管理器工厂

```java
@Configuration
@EnableTransactionManagement //开启事务注解
@EnableJpaAuditing //开启审计功能
//其他配置项 
@EnableJpaRepositories(basePackages = "com.study.jpa.repository", transactionManagerRef = "jpaTransactionManager")
public class JpaConfig {
    // 数据源创建
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    // 声明实体管理器工厂
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter jpaVendor = new HibernateJpaVendorAdapter();
        jpaVendor.setGenerateDdl(false);
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        // 设置数据源
        entityManagerFactory.setDataSource(dataSource());
        // 适配器设置
        entityManagerFactory.setJpaVendorAdapter(jpaVendor);
        // 替代persistences.xml 对该路径下的@Entity扫描初始化
        entityManagerFactory.setPackagesToScan("com.study.jpa.entity");
        return entityManagerFactory;
    }
	// 该方法和@EnableJpaRepositories注解中的transactionManagerRef对应--事务管理工厂
    @Bean
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
```

`@Configuration` 和 `@Bean`
Spring的Java配置方式是通过 `@Configuration` 和 `@Bean` 注解实现的：
a、@Configuration 作用于类上，相当于一个xml配置文件
b、@Bean 作用于方法上，相当于xml配置中的<bean>

`@Bean`注解将会实例化、配置和初始化一个新对象，这个对象将由Spring的IoC容器来管理。`@Bean`声明所起到的作用与<bean/> 元素类似。
`@Configuration`注解的类表示这个类的主要目的是作为bean定义的资源。被`@Configuration`声明的类可以通过在同一个类的内部调用`@bean`方法来设置嵌入bean的依赖关系。

```java
@Configuration    
public class AppConfig{    
    @Bean    
    public MyService myService() {    
        return new MyServiceImpl();    
    }    
}
```

上注解类等同于下配置文件

```xml
<beans>    
    <bean id="myService" class="com.somnus.services.MyServiceImpl"/>    
</beans>
```

------

`@EnableTransactionManagement`  开启事务注解

Spring Boot 使用事务非常简单，首先使用注解 `@EnableTransactionManagement` 开启事务支持后，然后在访问数据库的Service方法上添加注解 `@Transactional` 便可。

------

`@EnableJpaAuditing` 审计功能

JPA Audit 说明
在 Spring JPA 中，支持在字段或者方法上进行注解 `@CreateDate`、`@CreatedBy`、`@LastModifiedDate`、`@LastModifiedBy`

`@CreateDate`
表示该字段为创建时间时间字段，在这个实体被 insert 的时候，会设置默认值

`@CreatedBy`
表示该字段为创建人，在这个实体被insert的时候，会设置值。

@`LastModifiedDate`、`@LastModifiedBy`同理。

使用步骤

1. 实体类上添加 `@EntityListeners(AuditingEntityListener.class)`
2. 在需要的字段上加上 `@CreatedDate`、`@CreatedBy`、`@LastModifiedDate`、`@LastModifiedBy` 等注解。
3. 在Xxx Application 启动类上或是配置类中添加 `@EnableJpaAuditing`
4. 实现 AuditorAware 接口来返回你需要插入的值。重点！【使用 `@CreateDate`、`@CreatedBy`时需要重写】

> 参考：https://www.cnblogs.com/niceyoo/p/10908647.html

------

`@EnableJpaRepositories`

`basePackage`用于配置扫描Repositories所在的package及子package。简单配置中的配置则等同于此项配置值，
basePackages可以配置为单个字符串，也可以配置为字符串数组形式。

`transactionManagerRef`

事务管理工厂引用名称，对应到`@Bean`注解对应的方法

> 参考：[https://codertw.com/%E7%A8%8B%E5%BC%8F%E8%AA%9E%E8%A8%80/495407/](https://codertw.com/程式語言/495407/)

------

Spring Boot还提供了一个实用程序生成器类 `DataSourceBuilder` ，可用于创建标准数据源之一（如果它位于类路径上）。构建器可以根据类路径上可用的内容来检测要使用的一个。它还会根据JDBC URL自动检测驱动程序。

模板如下，通过`@ConfigurationProperties`注解读取配置文件

```java
@Bean
@ConfigurationProperties(prefix = "spring.datasource")
public DataSource dataSource() {
    return DataSourceBuilder.create().build();
}
```
------

`LocalContainerEntityManagerFactoryBean`

- is the most powerful JPA setup option, allowing for flexible local configuration within the application. It supports links to an existing JDBC DataSource, supports both local and global transactions

- [是最强大的JPA设置选项，允许在应用程序内进行灵活的本地配置。它支持到现有JDBC数据源的链接，支持本地和全局事务]

实体管理器工厂EntityManagerFactory是获得实体管理器EntityManager对象的入口

在 JPA 规范中, EntityManager 是完成持久化操作的核心对象。

实体作为普通 Java 对象，只有在调用 EntityManager 将其持久化后才会变成持久化对象。

EntityManager 对象在一组实体类与底层数据源之间进行 O/R 映射的管理。它可以用来管理和更新 Entity Bean, 根椐主键查找 Entity Bean, 还可以通过JPQL语句查询实体。

> 参考：https://blog.csdn.net/why_2012_gogo/article/details/78591495

------

`PlatformTransactionManager`

事务管理器

> 参考资料：https://juejin.im/post/6844904086341419021

#### AbstractAuditModel.java

```java
// 用在父类上面。当这个类肯定是父类时，加此标注。如果改成@Entity，则继承后，多个类继承，只会生成一个表，而不是多个继承，生成多个表
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) //JPA的审计功能
@Data
public abstract class AbstractAuditModel implements Serializable {
    /**
     * 主键
     */
    @Id
    //JPA通用策略生成器 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)// 日期注解
    @Column(name = "create_time", nullable = false, updatable = false)
    @CreatedDate
    private Date createTime;

    /**
     * 上次更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update_time", nullable = false)
    @LastModifiedDate
    private Date lastUpdateTime;
}
```

`@MappedSuperclass`

用在父类上面。当这个类肯定是父类时，加此标注。如果改成@Entity，则继承后，多个类继承，只会生成一个表，而不是多个继承，生成多个表

------

`@GeneratedValue(strategy = GenerationType.IDENTITY)`

通过annotation来映射hibernate实体的,基于annotation的hibernate主键标识为@Id,其生成规则由`@GeneratedValue`设定的

- TABLE：使用一个特定的数据库表格来保存主键。

- SEQUENCE：根据底层数据库的序列来生成主键，条件是数据库支持序列。

- IDENTITY：主键由数据库自动生成（主要是自动增长型）

- AUTO：主键由程序控制

  

------

`@Temporal(TemporalType.TIMESTAMP)`日期注解

`@Temporal` 是属性或方法级别的注解，用于声明属性持久化到数据库时所使用的时间精度。该注解可以应用于任何以下类型的实体类属性：

java.util.Date
java.util.Calendar

可选项：

TemporalType.DATE（日期）
TemporalType.TIME（时间）
TemporalType.TIMESTAMP（日期和时间）

------

`@Column(name = "create_time", nullable = false, updatable = false)`

> **name 可选,字段名(默认值是属性名)**
> unique 可选,是否在该字段上设置唯一约束(默认值false)
> **nullable 可选,是否设置该字段的值可以为空(默认值false)**
> insertable 可选,该字段是否作为生成的insert语句中的一个字段(默认值true)
> **updatable 可选,该字段是否作为生成的update语句中的一个字段(默认值true)**
> **columnDefinition 可选: 为这个特定字段覆盖sql DDL片段 （这可能导致无法在不同数据库间移植）**
> table 可选,定义对应的表(默认为主表)
> length 可选,字段长度(默认值255)
> precision可选,字段数字精精度(默认值0)
> scale 可选,如果字段数字刻度可用,在此设置(默认值0)

------

#### User.java

```java
@EqualsAndHashCode(callSuper = true)// 前面分析过 使用@Data时同时加上@EqualsAndHashCode(callSuper=true)注解
@NoArgsConstructor// 提供无参构造函数
@AllArgsConstructor// 提供全参构造函数
@Data
@Builder//@Builder声明实体，表示可以进行Builder方式初始化
@Entity// 实体类
@Table(name = "orm_user")// 表名
@ToString(callSuper = true)// 生成toString方法，callSuper包括父类中的参数
public class User extends AbstractAuditModel {
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
     * 上次登录时间
     */
    @Column(name = "last_login_time")
    private Date lastLoginTime;

    /**
     * 关联部门表
     * 1、关系维护端，负责多对多关系的绑定和解除
     * 2、@JoinTable注解的name属性指定关联表的名字，joinColumns指定外键的名字，关联到关系维护端(User)
     * 3、inverseJoinColumns指定外键的名字，要关联的关系被维护端(Department)
     * 4、其实可以不使用@JoinTable注解，默认生成的关联表名称为主表表名+下划线+从表表名，
     * 即表名为user_department
     * 关联到主表的外键名：主表名+下划线+主表中的主键列名,即user_id,这里使用referencedColumnName指定
     * 关联到从表的外键名：主表中用于关联的属性名+下划线+从表的主键列名,department_id
     * 主表就是关系维护端对应的表，从表就是关系被维护端对应的表
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "orm_user_dept", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "dept_id", referencedColumnName = "id"))
    private Collection<Department> departmentList;
}
```

`@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)`

`CascadeType.REMOVE`
Cascade remove operation，级联删除操作。
删除当前实体时，与它有映射关系的实体也会跟着被删除。

`CascadeType.MERGE`
Cascade merge operation，级联更新（合并）操作。
当Student中的数据改变，会相应地更新Course中的数据。

`CascadeType.DETACH`
Cascade detach operation，级联脱管/游离操作。
如果你要删除一个实体，但是它有外键无法删除，你就需要这个级联权限了。它会撤销所有相关的外键关联。

`CascadeType.REFRESH`
Cascade refresh operation，级联刷新操作。
假设场景 有一个订单,订单里面关联了许多商品,这个订单可以被很多人操作,那么这个时候A对此订单和关联的商品进行了修改,与此同时,B也进行了相同的操作,但是B先一步比A保存了数据,那么当A保存数据的时候,就需要先刷新订单信息及关联的商品信息后,再将订单及商品保存。

`CascadeType.ALL`
Cascade all operations，清晰明确，拥有以上所有级联操作权限。

`fetch`

引：在 一對多 中略為介紹過Fetch模式，FetchType.LAZY時， 除非真正要使用到該屬性的值，否則不會真正將資料從表格中載入物件，所以EntityManager後，才要載入該屬性值，就會發生例外錯誤，解決的方式 之一是在EntityManager關閉前取得資料，另一個方式則是標示為FetchType.EARGE， 表示立即從表格取得資料。

> 参考:https://openhome.cc/Gossip/EJB3Gossip/CascadeTypeFetchType.html

------

#### Department.java

```java
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orm_department")
@ToString(callSuper = true)
public class Department extends AbstractAuditModel {
    /**
     * 部门名
     */
    // columnDefinition
    // 使用特定字段覆盖sql ddl
    // DDL（Data Definition Languages）语句：数据定义语言，定义数据段、数据库、表、列、索引等数据库对象。
    @Column(name = "name", columnDefinition = "varchar(255) not null")
    private String name;

    /**
     * 上级部门id
     */
    @ManyToOne(cascade = {CascadeType.REFRESH})// 级联刷新操作
    @JoinColumn(name = "superior", referencedColumnName = "id")
    private Department superior;

    /**
     * 所属层级
     */
    @Column(name = "levels", columnDefinition = "int not null default 0")
    private Integer levels;

    /**
     * 排序
     */
    @Column(name = "order_no", columnDefinition = "int not null default 0")
    private Integer orderNo;

    /**
     * 子部门集合
     */
    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, fetch = FetchType.EAGER, mappedBy = "superior")
    private Collection<Department> children;

    /**
     * 部门下用户集合
     */
    @ManyToMany(mappedBy = "departmentList")
    private Collection<User> userList;
}
```

 `@JoinColumn(name = "superior", referencedColumnName = "id")`

`@JoinColumn` 的作用就是声明关联关系的

Hibernate的`@OneToMany`和`@ManyToOne`都可以用`@JoinColumn`。`@JoinColumn`有name属性需要设置成为多的一方的外键，当`@OneToMany`用`@JoinColumn`时，表示一的一方控制关联关系，并且`@OneToMany`设置了cascade=CascadeType.ALL，这时删除一端时不会抛出异常，多端外键被设置为null。

`name`,`referencedColumnName`这两个字段的值指的都是数据库字段，而不是 `Entity` 的属性字段。

- name：当前表的字段
- referencedColumnName：引用表对应的字段，如果不注明，默认就是引用表的主键

> 参考：https://www.codeleading.com/article/41773389395/
>
> 参考：https://www.codeleading.com/article/33744419615/

------

`@OneToMany(cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, fetch = FetchType.EAGER, mappedBy = "superior")`

在一的一方配置`@OneToMany(mappedBy="department")`，将维护权交由多的一方来维护

如果配置`mappedBy`属性的同时加上`@JoinColumn`会抛出异常，所以不能同时使用`@JoinColumn`和`mappedBy`

> 总结：`mappedBy`属性跟xml配置文件里的inverse一样。在一对多或一对一的关系映射中，如果不表明`mappedBy`属性，默认是由本方维护外键。但如果两方都由本方来维护的话，会多出一些update语句，性能有一定的损耗。
>
> 解决的办法就是在一的一方配置上`mappedBy`属性，将维护权交给多的一方来维护，就不会有update语句了。
>
> 至于为何要将维护权交给多的一方，可以这样考虑：要想一个国家的领导人记住所有人民的名字是不可能的，但可以让所有人民记住领导人的名字！
>
> 注意，配了mappedBy属性后，不要再有@JoinColumn，会冲突！

**注：`@JoinColumn`和`mappedBy`都不用时会生成一个中间表。**

#### UserDao.java

```java
@Repository
public interface UserDao extends JpaRepository<User, Long> {

}
```

#### DepartmentDao.java

```java
@Repository
public interface DepartmentDao extends JpaRepository<Department, Long> {
    /**
     * 根据层级查询部门
     *
     * @param level 层级
     * @return 部门列表
     */
    List<Department> findDepartmentByLevels(Integer level);
}
```

**注意：`@Repository`不能少**

#### UserDaoTest.java

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserDaoTest {
    @Autowired
    private UserDao userDao;

    /**
     * 测试保存
     */
    @Test
    public void testSave() {
        String salt = IdUtil.fastSimpleUUID();
        User testSave3 = User.builder().name("testSave3").password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave3@xkcoding.com").phoneNumber("17300000003").status(1).lastLoginTime(new DateTime()).build();
        userDao.save(testSave3);

        Assert.assertNotNull(testSave3.getId());
        
        Optional<User> byId = userDao.findById(testSave3.getId());
        Assert.assertTrue(byId.isPresent());
        log.debug("【byId】={}", byId.get());
    }

    /**
     * 测试删除
     */
    @Test
    public void testDelete() {
        long count = userDao.count();
        userDao.deleteById(1L);
        long left = userDao.count();
        Assert.assertEquals(count - 1, left);
    }

    /**
     * 测试修改
     */
    @Test
    public void testUpdate() {
        userDao.findById(1L).ifPresent(user -> {
            user.setName("JPA修改名字");
            userDao.save(user);
        });
        Assert.assertEquals("JPA修改名字", userDao.findById(1L).get().getName());
    }

    /**
     * 测试查询单个
     */
    @Test
    public void testQueryOne() {
        Optional<User> byId = userDao.findById(1L);
        Assert.assertTrue(byId.isPresent());
        log.debug("【byId】= {}", byId.get());
    }

    /**
     * 测试查询所有
     */
    @Test
    public void testQueryAll() {
        List<User> users = userDao.findAll();
        Assert.assertNotEquals(0, users.size());
        log.debug("【users】= {}", users);
    }

    /**
     * 测试分页排序查询
     */
    @Test
    public void testQueryPage() {
        // 初始化数据
        initData();
        // JPA分页的时候起始页是页码减1
        Integer currentPage = 0;
        Integer pageSize = 5;
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize, sort);
        Page<User> userPage = userDao.findAll(pageRequest);

        Assert.assertEquals(5, userPage.getSize());
        Assert.assertEquals(userDao.count(), userPage.getTotalElements());
        log.debug("【id】= {}", userPage.getContent().stream().map(User::getId).collect(Collectors.toList()));
    }


    /**
     * 初始化10条数据
     */
    private void initData() {
        List<User> userList = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            String salt = IdUtil.fastSimpleUUID();
            int index = 3 + i;
            User user = User.builder().name("testSave" + index).password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave" + index + "@xkcoding.com").phoneNumber("1730000000" + index).status(1).lastLoginTime(new DateTime()).build();
            userList.add(user);
        }
        userDao.saveAll(userList);
    }

}
```

`Optional<User> byId = userDao.findById(testSave3.getId());`

Optional 类是一个可以为null的容器对象。如果值存在则isPresent()方法会返回true，调用get()方法会返回该对象。

Optional 是个容器：它可以保存类型T的值，或者仅仅保存null。Optional提供很多有用的方法，这样我们就不用显式进行空值检测。

Optional 类的引入很好的解决空指针异常。

------



#### DepartmentDaoTest.java

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DepartmentDaoTest {
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private UserDao userDao;

    /**
     * 测试保存，根节点
     */
    @Test
    @Transactional
    public void testSave() {
        Collection<Department> departmentList = departmentDao.findDepartmentByLevels(0);
        if (departmentList.size() == 0) {
            Department testSave1 = Department.builder().name("testSave1").orderNo(0).levels(0).superior(null).build();
            Department testSave1_1 = Department.builder().name("testSave1_1").orderNo(0).levels(1).superior(testSave1).build();
            Department testSave1_2 = Department.builder().name("testSave1_2").orderNo(0).levels(1).superior(testSave1).build();
            Department testSave1_1_1 = Department.builder().name("testSave1_1_1").orderNo(0).levels(2).superior(testSave1_1).build();

            departmentList.add(testSave1);
            departmentList.add(testSave1_1);
            departmentList.add(testSave1_2);
            departmentList.add(testSave1_1_1);
            departmentDao.saveAll(departmentList);

            Collection<Department> deptall = departmentDao.findAll();
            log.debug("【部门】={}", JSONArray.toJSONString((List) deptall));
        }

        userDao.findById(1L).ifPresent(user -> {
            user.setName("添加部门");
            Department dept = departmentDao.findById(2L).get();
            user.setDepartmentList(departmentList);
            userDao.save(user);
        });

        log.debug("用户部门={}", JSONUtil.toJsonStr(userDao.findById(1L).get().getDepartmentList()));

        departmentDao.findById(2L).ifPresent(dept -> {
            Collection<User> userlist = dept.getUserList();
            //关联关系由user维护中间表，department userlist不会发生变化，可以增加查询方法来处理  重写getUserList方法
            log.debug("部门下用户={}", JSONUtil.toJsonStr(userlist));
        });

        userDao.findById(1L).ifPresent(user -> {
            user.setName("清空部门");
            user.setDepartmentList(null);
            userDao.save(user);
        });
        log.debug("用户部门={}", userDao.findById(1L).get().getDepartmentList());
    }
}
```