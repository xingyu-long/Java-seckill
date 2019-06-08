

# 第一部分：项目介绍以及 Dao 层（数据访问层）搭建

> 该项目主要参考慕课网的[Java秒杀系列教程](https://www.imooc.com/learn/587)

## 涉及到的知识

- MySQL
  - 表设计
  - SQL 技巧
  - 事务和行级锁
- MyBatis
  - Dao 层设计与开发
  - MyBatis 合理使用
  - MyBatis 与 Spring 整合
- Spring
  - Spring IOC 整合 service
  - 声明式事务运用
- SpringMVC
  - RESTful 接口设计和使用
  - 框架运行流程
  - Controller 开发技巧
- 高并发（使用 Redis 处理）
  - 高并发点和高并发处理
  - 优化思路并实现

## 项目结构

```shell
seckill
├─ src
│    ├─ main
│    │    ├─ java
│    │    │    └─ org
│    │    │           └─ seckill
│    │    │                  ├─ dao
│    │    │                  │    ├─ SeckillDao.java
│    │    │                  │    ├─ SuccessKilledDao.java
│    │    │                  │    └─ cache
│    │    │                  ├─ dto
│    │    │                  │    ├─ Exposer.java
│    │    │                  │    ├─ SeckillExecution.java
│    │    │                  │    └─ SeckillResult.java
│    │    │                  ├─ entity
│    │    │                  │    ├─ Seckill.java
│    │    │                  │    └─ SuccessKilled.java
│    │    │                  ├─ enums
│    │    │                  │    └─ SeckillStateEnum.java
│    │    │                  ├─ exception
│    │    │                  │    ├─ RepeatKillException.java
│    │    │                  │    ├─ SeckillCloseException.java
│    │    │                  │    └─ SeckillException.java
│    │    │                  ├─ service
│    │    │                  │    ├─ SeckillService.java
│    │    │                  │    └─ impl
│    │    │                  └─ web
│    │    │                         └─ SeckillController.java
│    │    ├─ resources
│    │    │    ├─ jdbc.properties
│    │    │    ├─ logback.xml
│    │    │    ├─ mapper
│    │    │    │    ├─ SeckillDao.xml
│    │    │    │    └─ SuccessKilledDao.xml
│    │    │    ├─ mybatis-config.xml
│    │    │    └─ spring
│    │    │           ├─ spring-dao.xml
│    │    │           ├─ spring-service.xml
│    │    │           └─ spring-web.xml
│    │    ├─ sql
│    │    │    ├─ schema.sql
│    │    │    └─ seckill.sql
│    │    └─ webapp
│    │           ├─ WEB-INF
│    │           │    ├─ jsp
│    │           │    │    ├─ common
│    │           │    │    │    ├─ head.jsp
│    │           │    │    │    └─ tag.jsp
│    │           │    │    ├─ detail.jsp
│    │           │    │    └─ list.jsp
│    │           │    └─ web.xml
│    │           ├─ index.jsp
│    │           └─ resources
│    │                  └─ script
│    │                         └─ seckill.js
│    └─ test
│           └─ java
│                  └─ org
│                         └─ seckill
│                                ├─ dao
│                                │    ├─ SeckillDaoTest.java
│                                │    ├─ SuccessKilledDaoTest.java
│                                │    └─ cache
│                                └─ service
│                                       └─ SeckillServiceTest.java
```

## maven 最新版本生成项目

```shell
mvn archetype:generate -DgroupId=org.seckill -DartifactId=seckill -DarchetypeArtifactId=maven-archetype-webapp
```

## 值得注意的知识点

### MyBatis 的特点以及与 Hibernate的对比

#### 两者共同点

两者均为 ORM 框架，屏蔽了 JDBC API 的底层访问细节(手动加载驱动，创建连接...)方便直接对数据库进行持久化操作。

#### MyBatis

将 SQL 语句与 Java代码进行分离；提供了将结果集自动封装为实体对象和对象集合的功能。

#### Hibernate

全自动化的 ORM 映射工具，它可以自动生成 SQL 语句，执行然后返回结果。

#### 不同点

- Hibernate 比 MyBatis 功能强大，因为可以自动生成 SQL 语句。
- MyBatis 灵活度更高可以写出更为复杂的 SQL 语句。

### 不建议注解提供 SQL

1. 注解本身还是 Java源码，修改需要重新编译
2. 注解处理复杂复杂 SQL 会比较繁琐

### 如何返回数据中也带有一个对象

```sql
<select id="queryByIdWithSeckill" resultType="SuccessKilled" parameterType="long">
        <!--根据id查询SuccessKilled并携带秒杀产品对象实体（Seckill）-->
        <!--如何告诉MyBatis把结果映射到SuccessKilled实体 以及Seckill实体-->
        select
          sk.seckill_id,
          sk.user_phone,
          sk.create_time,
          sk.state,
          s.seckill_id "seckill.seckill_id",
          s.name "seckill.name",
          s.number "seckill.number",
          s.start_time "seckill.start_time",
          s.end_time "seckill.end_time",
          s.create_time "seckill.create_time"
        from success_killed sk
        inner join seckill s on sk.seckill_id = s.seckill_id
        where sk.seckill_id = #{seckillId}
        and sk.user_phone = #{userPhone};
</select>
<!--利用Mybatis对应，让其值直接进入后面定义的对象属性中例如seckill.xxxx-->
```

### 主要的配置文件

`mybatis-config.xml` 全局的配置文件

`spring-dao.xml`整合到spring的配置文件

## Dao层操作的主要流程

- 编写实体对象类
- 编写实体对象类对象的方法接口类 xxxDao (interface)
- 书写 mybatis-config.xml 以及 jdbc.properties ( jdbc 配置文件)
- 利用 MyBatis 的 mapper 并且在这里面实现 xxxxDao.xml (数据库实现)
- MyBatis 整合到Spring中( spring-dao.xml 文件)
- 对应的接口，进行单元测试

# 第二部分：Service 层

## 主要包说明

- service 包 接口和实现 (impl) 类

- exception 抛出的异常类

- dto 数据传输层：关注 web 和 service 的数据传递

- service 并不是需要和 dao 一一对应

## 主要业务

- 获取秒杀商品列表
- 通过 id 获取商品信息(商品详情页)
- 暴露秒杀地址(已过开启时间)
- 执行秒杀(可以使用存储过程)

## MD5 生成规则

md5 的生成规则 base = 盐值 + 规则 + 输入字符

md5 = 工具类生成 (base)

具体代码：

```java
private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
}
```

## 使用枚举类来划分情况

```java
public enum SeckillStateEnum {
    SUCCESS(1, "秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT_KILL(-1, "重复秒杀"),
    INNER_ERROR(-2, "系统异常"),
    DATA_REWRITE(-3, "数据篡改");

    private int state;

    private String stateInfo;

    SeckillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public static SeckillStateEnum stateOf(int index) {
        for (SeckillStateEnum state : values()) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }
}
```

## 为什么使用 IOC (控制反转)

- 对象创建统一托管
- 规范的生命周期管理
- 灵活的依赖注入
- 一致的获取对象(单例)

## Service 层操作的主要流程

- 在service包下面进行接口的设计以及impl下面实现对应的接口。
  - 创建合适数据传输层对象、枚举类对象、异常类对象。
- 使用Spring管理service (配置spring-service.xml) 里面包括声明式事务配置（管理器以及基于注解的声明式事务）、service包扫描配置。
- 进行service下面的集成式测试。

# 第三部分：Web层

## 秒杀API的URL设计

- GET /seckill/list 秒杀列表
- GET /seckill/{id}/detail 详情页
- GET /seckill/time/now 系统时间
- POST /seckill/{id}/exposer 暴露秒杀
- POST /seckill/{id}/{md5}/execution 执行秒杀

## 使用 SpringMVC

SpringMVC流程

- 书写 web.xml (让其交给 spring framework 处理)
- 书写spring-web.xml
  - 开启 springMVC 注解模式 
  - servlet-mapping 映射路径："/" 
  - 配置 jsp ，显示 ViewResolver
  - 扫描web相关的bean

- 书写 controller (列表、商品详情、接口暴露、秒杀执行、获取时间)

## 前端控制流程

1. 直接调用init方法
2. 验证cookie里面是否有电话
   1. 如果没有，则需要手动输入，然后会刷新页面重新走这个流程
   2. 如果有，则正常
3. 然后使用controller里面的获得时间，再使用seckill.js 中的countdown函数
   1. 如果已经过了结束时间 则显示结束
   2. 如果还没开始，则用jQuery的countdown函数进行**倒计时**，并且在完成倒计时的时候 回调处理秒杀的逻辑
   3. 如果已经开始，则依然是开始秒杀的逻辑
4. 利用exposer得到秒杀地址
   1. 如果这时候还未开启，则依然使用jQuery的countdown函数进行**倒计时** 
5. 点击按钮，开始执行秒杀

# 第四部分：并发优化Redis ，存储过程

## 简单优化：

先insert购买明细 -> update减库存 -> commit

降低mysql rowlock的持有时间

## 深度优化

- 事务SQL在MySQL端执行，即写成存储过程的形式
- 使用Redis缓存商品的信息进而缓解其访问数据库压力