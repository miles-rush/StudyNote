# SpringBoot16-mybatis-mapper-page

> SpringBoot集成通用Mapper插件和分页助手插件，简化Mybatis开发

#### pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <!-- 通用Mapper -->
    <dependency>
        <groupId>tk.mybatis</groupId>
        <artifactId>mapper-spring-boot-starter</artifactId>
        <version>2.1.5</version>
    </dependency>

    <!-- 分页助手 -->
    <!-- 注意 这里使用的springboot对应的pagehelper-->
    <dependency>
        <groupId>com.github.pagehelper</groupId>
        <artifactId>pagehelper-spring-boot-starter</artifactId>
        <version>1.3.0</version>
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
        <version>5.4.1</version>
    </dependency>

    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
        <version>29.0-jre</version>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```

#### MybatismapperpageApplication

```java
@SpringBootApplication
@MapperScan(basePackages = {"com.study.mybatismapperpage.mapper"}) // 注意：这里的 MapperScan 是 tk.mybatis.spring.annotation.MapperScan 这个包下的
public class MybatismapperpageApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatismapperpageApplication.class, args);
    }

}
```

#### application.yml

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
    com.study.mybatismapperpage.mapper: trace
mybatis:
  configuration:
    # 下划线转驼峰
    map-underscore-to-camel-case: true
  mapper-locations: classpath:mappers/*.xml
  type-aliases-package: com.study.mybatismapperpage.entity
mapper:
  mappers:
  - tk.mybatis.mapper.common.Mapper
  not-empty: true
  style: camelhump
  wrap-keyword: "`{0}`"
  safe-delete: true
  safe-update: true
  identity: MYSQL
pagehelper:
  auto-dialect: true
  helper-dialect: mysql
  reasonable: true
  params: count=countSql
```

##### 通用mappers的配置

`mappers`

在 4.0 以前这是一个非常重要的参数，当时只有通过 mappers 配置过的接口才能真正调用，由于很多人不注意看文档，通用 Mapper 90% 的问题都出在这个参数上。

4.0 之后，增加了一个 @RegisterMapper 注解，通用 Mapper 中提供的所有接口都有这个注解，有了该注解后，通用 Mapper 会自动解析所有的接口，如果父接口（递归向上找到的最顶层）存在标记该注解的接口，就会自动注册上。因此 4.0 后使用通用 Mapper 提供的方法时，不需要在配置这个参数。

当你自己扩展通用接口时，建议加上该注解，否则就要配置 mappers 参数

------

`identity`

取回主键的方式，可以配置的值如下所列的数据库类型：

- **DB2**: `VALUES IDENTITY_VAL_LOCAL()`
- **MYSQL**: `SELECT LAST_INSERT_ID()`
- **SQLSERVER**: `SELECT SCOPE_IDENTITY()`
- **CLOUDSCAPE**: `VALUES IDENTITY_VAL_LOCAL()`
- **DERBY**: `VALUES IDENTITY_VAL_LOCAL()`
- **HSQLDB**: `CALL IDENTITY()`
- **SYBASE**: `SELECT @@IDENTITY`
- **DB2_MF**: `SELECT IDENTITY_VAL_LOCAL() FROM SYSIBM.SYSDUMMY1`
- **INFORMIX**: `select dbinfo('sqlca.sqlerrd1') from systables where tabid=1`

------

`not-empty`

`insertSelective` 和 `updateByPrimaryKeySelective` 中，是否判断字符串类型 `!=''`

------

`safe-delete` `safe-update`

配置为 true 后，delete 和 deleteByExample 都必须设置查询条件才能删除，否则会抛出异常。

配置为 true 后，updateByExample 和 updateByExampleSelective 都必须设置查询条件才能删除，否则会抛出异常（`org.apache.ibatis.exceptions.PersistenceException`）。

------

`style`

实体和表转换时的默认规则，可选值如下：

- normal：原值
- camelhump：驼峰转下划线
- uppercase：转换为大写
- lowercase：转换为小写
- camelhumpAndUppercase：驼峰转下划线大写形式
- camelhumpAndLowercase：驼峰转下划线小写形式

------

`wrap-keyword`

配置后会自动处理关键字，可以配的值和数据库有关。

例如 sqlserver 可以配置为 `[{0}]`，使用 `{0}` 替代原来的列名。

MySql 对应的配置如下：

```
wrapKeyword=`{0}`
```

使用该配置后，类似 `private String order` 就不需要通过 `@Column` 来指定别名。

> 参考:https://github.com/abel533/Mapper/wiki/3.config

------

##### 分页插件参数配置

`helperDialect`：分页插件会自动检测当前的数据库链接，自动选择合适的分页方式。 你可以配置`helperDialect`属性来指定分页插件使用哪种方言。配置时，可以使用下面的缩写值：
`oracle`,`mysql`,`mariadb`,`sqlite`,`hsqldb`,`postgresql`,`db2`,`sqlserver`,`informix`,`h2`,`sqlserver2012`,`derby`
**特别注意：**使用 SqlServer2012 数据库时，需要手动指定为 `sqlserver2012`，否则会使用 SqlServer2005 的方式进行分页。
你也可以实现 `AbstractHelperDialect`，然后配置该属性为实现类的全限定名称即可使用自定义的实现方法。

------

`reasonable`：分页合理化参数，默认值为`false`。当该参数设置为 `true` 时，`pageNum<=0` 时会查询第一页， `pageNum>pages`（超过总数时），会查询最后一页。默认`false` 时，直接根据参数进行查询。



> 参考：https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md

#### UserMapper

```java
@Component
public interface UserMapper extends Mapper<User>, MySqlMapper<User> {
}
```

#### UserMapperTest

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testInsert() {
        String salt = IdUtil.fastSimpleUUID();
        User testSave3 = User.builder().name("testSave3").password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave3@xkcoding.com").phoneNumber("17300000003").status(1).lastLoginTime(new DateTime()).createTime(new DateTime()).lastUpdateTime(new DateTime()).build();
        userMapper.insertUseGeneratedKeys(testSave3);
        Assert.assertNotNull(testSave3.getId());
        log.debug("【测试主键回写#testSave3.getId()】= {}", testSave3.getId());
    }

    /**
     * 测试通用Mapper - 批量保存
     */
    @Test
    public void testInsertList() {
        List<User> userList = Lists.newArrayList();
        for (int i = 4; i < 14; i++) {
            String salt = IdUtil.fastSimpleUUID();
            User user = User.builder().name("testSave" + i).password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave" + i + "@xkcoding.com").phoneNumber("1730000000" + i).status(1).lastLoginTime(new DateTime()).createTime(new DateTime()).lastUpdateTime(new DateTime()).build();
            userList.add(user);
        }
        int i = userMapper.insertList(userList);
        Assert.assertEquals(userList.size(), i);
        List<Long> ids = userList.stream().map(User::getId).collect(Collectors.toList());
        log.debug("【测试主键回写#userList.ids】= {}", ids);
    }

    /**
     * 测试通用Mapper - 删除
     */
    @Test
    public void testDelete() {
        Long primaryKey = 1L;
        int i = userMapper.deleteByPrimaryKey(primaryKey);
        Assert.assertEquals(1, i);
        User user = userMapper.selectByPrimaryKey(primaryKey);
        Assert.assertNull(user);
    }

    /**
     * 测试通用Mapper - 更新
     */
    @Test
    public void testUpdate() {
        Long primaryKey = 1L;
        User user = userMapper.selectByPrimaryKey(primaryKey);
        user.setName("通用Mapper名字更新");
        // updateByPrimaryKeySelective的原理，是根据entity对象的属性值，是否为null，如果为null，则最终生成的update语句里，将忽略该列，否则会更新该列
        int i = userMapper.updateByPrimaryKeySelective(user);
        Assert.assertEquals(1, i);
        User update = userMapper.selectByPrimaryKey(primaryKey);
        Assert.assertNotNull(update);
        Assert.assertEquals("通用Mapper名字更新", update.getName());
        log.debug("【update】= {}", update);
    }

    /**
     * 测试通用Mapper - 查询单个
     */
    @Test
    public void testQueryOne(){
        User user = userMapper.selectByPrimaryKey(1L);
        Assert.assertNotNull(user);
        log.debug("【user】= {}", user);
    }

    /**
     * 测试通用Mapper - 查询全部
     */
    @Test
    public void testQueryAll() {
        List<User> users = userMapper.selectAll();
        Assert.assertTrue(CollUtil.isNotEmpty(users));
        log.debug("【users】= {}", users);
    }

    /**
     * 测试分页助手 - 分页排序查询
     */
    @Test
    public void testQueryByPageAndSort() {
        initData();
        int currentPage = 1;
        int pageSize = 5;
        String orderBy = "id desc";
        int count = userMapper.selectCount(null);
        // 在你需要进行分页的 MyBatis 查询方法前调用 PageHelper.startPage 静态方法即可，紧跟在这个方法后的第一个MyBatis 查询方法会被进行分页。
        PageHelper.startPage(currentPage, pageSize, orderBy);
        List<User> users = userMapper.selectAll();
        // 使用pageInfo包装查询后的结果，封装了详细的查询数据
        PageInfo<User> userPageInfo = new PageInfo<>(users);
        Assert.assertEquals(5, userPageInfo.getSize());
        Assert.assertEquals(count, userPageInfo.getTotal());
        log.debug("【userPageInfo】= {}", userPageInfo);
    }

    /**
     * 测试通用Mapper - 条件查询
     */
    @Test
    public void testQueryByCondition() {
        initData();
        // example用法
        Example example = new Example(User.class);
        // 过滤
        example.createCriteria().andLike("name", "%Save1%").orEqualTo("phoneNumber", "17300000001");
        // 排序
        example.setOrderByClause("id desc");
        int count = userMapper.selectCountByExample(example);
        // 分页
        PageHelper.startPage(1, 3);
        // 查询
        List<User> userList = userMapper.selectByExample(example);
        PageInfo<User> userPageInfo = new PageInfo<>(userList);
        Assert.assertEquals(3, userPageInfo.getSize());
        Assert.assertEquals(count, userPageInfo.getTotal());
        log.debug("【userPageInfo】= {}", userPageInfo);
    }


    /**
     * 初始化数据
     */
    private void initData() {
        testInsertList();
    }
}
```

`updateByPrimaryKeySelective`会对字段进行判断再更新(如果为Null就忽略更新)，如果你只想更新某一字段，可以用这个方法。

`updateByPrimaryKey`对你注入的字段全部更新

------

> example用法参考:https://github.com/abel533/Mapper/wiki/6.example

## 参考

- 通用Mapper官方文档：https://github.com/abel533/Mapper/wiki/1.integration
- pagehelper 官方文档：https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md