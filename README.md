# seckill-project
java高并发秒杀项目
@[toc]
## 1. 选用秒杀的原因
* 秒杀业务场景具有典型“事务”特性
* 秒杀/红包类需求越来越常见
* 面试常问问题

学习秒杀的过程以及优化思路、框架的使用整合技巧

###  1.2 相关技术
**Mysql**
* 表设计
* 事务
* 行级锁

**Mybatis**
* Dao层的设计与开发
* MyBatis的合理使用
* MyBatis与Spring的整合

**Spring**
* Spring IOC整合Service
* 声明式事务

**Spring MVC**
* Restful接口设计与使用
* 框架运作流程
* Controller开发技巧

**高并发**
* 高并发点和高并发分析
* 优化思路并实现

 
### 1.2 秒杀业务分析
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191129103109608.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NhbzEzMTUwMjA2MjY=,size_16,color_FFFFFF,t_70)
 秒杀业务的核心：库存
 **用户针对库存业务分析**
 * 减库存+记录购买明细 =>完整事务 =>数据落地  （完整的逻辑）
 
  

数据落地：
MySQL和NoSQL（对事务机制支持不太好）
事务机制依然是目前最可靠的落地解决方案。

### 1.3 秒杀的难点分析
难点问题-“竞争”：多个人同时秒杀一件商品的时候如何处理这种情况下的竞争问题。
[图1.3.1](https://img-blog.csdnimg.cn/20191129103410145.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NhbzEzMTUwMjA2MjY=,size_16,color_FFFFFF,t_70)

**事务**
* Start Transaction
* Update 库存数量
* Insert 购买明细
* Commit 提交事务


![在这里插入图片描述](https://img-blog.csdnimg.cn/20191129103511214.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NhbzEzMTUwMjA2MjY=,size_16,color_FFFFFF,t_70)
行级锁的概念！
行级锁最大的特点就是锁定对象的颗粒度很小，也是目前各大数据库管理软件所实现的锁定颗粒度最小的。由于锁定颗粒度很小，所以发生锁定资源争用的概率也最小，能够给予应用程序尽可能大的并发处理能力而提高一些需要高并发应用系统的整体性能。
行级锁在INNODB中使用，如果有很多的用户同时对一行数据进行修改的话，此时当第一个用户正在修改未提交之前，它是占有锁的，其他的用户都必须等待第一个用户commit之后释放锁以后才能够获取锁来得修改数据的机会。这就是行级锁的大致含义。

虽然能够在并发处理能力上面有较大的优势，但是行级锁也因此带来了不少弊端。由于锁定资源的颗粒度很小，所以每次获取锁和释放锁需要做的事情也更多，带来的消耗自然也就更大了。此外，行级锁也最最容易发生死锁。

使用行级锁的主要是InnoDB存储引擎！

<font color="red">秒杀的难点是如何高线的处理竞争问题！</font>

### 1.4 秒杀功能
* 秒杀接口暴露
* 执行秒杀
* 相关查询

**代码开发阶段**
* DAO层编码
* Service设计编码
* Web设计编码

### 1.5 数据库设计与编码
具体的SQL脚本：[SQL脚本](https://github.com/Aaron-cdx/seckill-project/tree/feature1.0/src/main/sql)
### 1.6 MyBatis作用
mybatis的主要作用就是实现数据库中的数据与程序中对象之间的映射，与Hibernate一样，都是映射的工具，但是MyBatis需要提供的有参数和SQL，而Hibernate只需要提供参数即可。Mybatis是自己手动写SQL语句，比较灵活，Hibernate是依照封装来定的，只需要提供参数即可。

**Mybatis的特点：**
* 参数+SQL = Entity/List

**SQL书写的位置**
* XML提供SQL，通过编写Xml文件来提供sql
* 注解提供SQL，通过mybatis提供的注解标签来编写SQL

**如何实现Dao接口**
* Mapper自动实现DAO接口，Mapper类实现DAO接口，DAO中没有实现，只需要关系结果即可。
* API编程方式实现DAO接口，通过编写Connection来手动实现API编程方式实现DAO接口，这种方式比较原始化。


###  1.7 MyBatis整合Spring
mybatis只写接口，不用实现。

更少的配置
* Spring中有package scan可以扫描所有包下的类
* mapper的扫描

更少的配置Dao实现：
* 自动实现DAO接口
* 自动注入spring容器

mybatis足够的灵活性
* 自己定制sql
* 自由传参
* 结果集自动赋值

整合之后
* xml提供sql
* DAO接口提供mapper

**mybatis整合Spring**
主要就是编写XML文件做配置。

主要的配置编写步骤：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">
    <!--这里只是单纯的整合Spring而已，没有配置Spring的参数！配置整合Mybatis过程-->
    <!--1.配置数据库相关参数-->
    <context:property-placeholder location="classpath:jdbc.properties"></context:property-placeholder>

    <!--2.配置数据库连接池-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <!--连接数据库的基本属性-->
        <property name="driverClass" value="${driver}"></property>
        <property name="jdbcUrl" value="${url}"></property>
        <property name="user" value="${user}"></property>
        <property name="password" value="${password}"></property>

        <!--配置私有属性-->
        <!--初始连接数-->
<!--        <property name="initialPoolSize" value="30"></property>-->
        <!--最大最小连接数-->
        <property name="maxPoolSize" value="30"></property>
        <property name="minPoolSize" value="10"></property>
        <!--最大等待时间-->
        <property name="checkoutTimeout" value="10000"></property>
        <!--自动提交关闭-->
        <property name="autoCommitOnClose" value="false"></property>
        <!--重试次数-->
        <property name="acquireRetryAttempts" value="2"></property>
    </bean>


    <!--3. 整合Mybatis-->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!--数据库连接池-->
        <property name="dataSource" ref="dataSource"></property>
        <!--mybatis全局配置文件-->
        <property name="configLocation" value="classpath:mybatis-config.xml"></property>
        <!--mapper地址-->
        <property name="mapperLocations" value="classpath:Mapper/*.xml"></property>
        <!--使用别名-->
        <property name="typeAliasesPackage" value="classpath:org.seckill.entity"></property>
    </bean>

    <!--4.配置扫描DAO接口，动态实现Dao接口，自动实现注入spring容器-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!--注入sqlSessionFactory-->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
        <!--配置扫描Dao接口-->
        <property name="basePackage" value="org.seckill.dao"></property>
    </bean>
</beans>
```


## 2. Service层
Dao层工作演变为：接口设计+SQL编写
代码和SQL的分离，方便Review
Dao拼接等逻辑在Service层完成

### 2.1 秒杀接口设计
秒杀接口的设计，需要站在使用者的角度来设计接口，这样才会让接口的作用最大。
响应的数据传输类使用Dto定义
如果有针对项目中的异常抛出问题，需要自己自定义异常然后编写异常。

秒杀接口设计中的异常注意：
Spring中的事务管理只有运行时异常才会执行回滚操作！


**Spring管理Service**

**Spring IOC功能理解**
对象工厂+依赖管理=一致的访问接口

**业务对象依赖**
SeckillService依赖SeckillDao以及SuccessKilledDao
SeckillDao以及SuccessKilledDao依赖SqlSessionFactory会话工厂
SQLSessionFactory会话工厂依赖DataSource

**为什么使用IOC**
* 对象创建统一托管
* 规范的生命周期管理
* 灵活的依赖注入
* 一致的获取对象（主要是因为Spring获取对象使用的是单例模式）

**Spring-IOC注入方式和场景**
* XML：Bean实现类来自第三方类库，如DataSource等；需要命名空间配置：context、aop、mvc等
* 项目中自身开发使用的类，可以直接在代码中使用注解：@Service、@Controller
* java配置类：需要通过代码控制对象创建逻辑的场景。如：自定义修改依赖类库。

**项目中IOC使用**
* XML配置
* package-scan
* Annotation注解


**声明式事务**
流程：开启事务->修改SQL-1->修改SQL-2->修改SQL-n->提交/回滚事务
主要目的就是解脱事务代码！

**声明式事务使用方式**
* ProxyFactoryBean+XML配置
* tx:advice+aop命名空间（一次配置永久生效）
* 注解@Transactional（注解控制）

第三种方式可以告诉同事或者团队内成员，这里有事务方法。

**使用注解控制事务方法的优点**
* 开发团队达成一致约定，明确标注事务方法的编程风格
* 保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部
* 不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制

**执行事务**
抛出运行期异常的时候执行事务！

**事务方法嵌套** 
声明式事务独有的概念：***传播行为***

## 3. Web层
 列表页-详情页-login-yes-展示逻辑
 								  no-登录操作-写入cookie-展示逻辑

详情页流程逻辑

1. 获取标准系统时间
2. 根据获取的时间比较开始与结束时间，时间不满足直接结束
3. 未开始的话，启动倒计时，然后获取秒杀地址；已开始的话直接过去秒杀地址
4. 点击执行秒杀
5. 结果显示

**Restful**
* 兴起于Rails
* 是一种优雅的URI表述形式
* 资源的状态以及状态转移

**Restful规范**
* GET 查询操作
* POST 添加、修改操作
* PUT 修改操作

**其中POST与PUT只是幂等性有所区别**：

HTTP/1.1中对幂等性的定义是：**一次和多次请求某一个资源对于资源本身应该具有同样的结果（网络超时等问题除外）。也就是说，其任意多次执行对资源本身所产生的影响均与一次执行的影响相同。**

**URL设计格式**
`/模块/资源/{标示}/集合1/......`

### 3.1 SpringMVC知识
**SpringMVC运行流程**：
1. 如果在web.xml中设置的DispathcerServlet的url-pattern为/时，当用户发起请求，首先请求一个控制器
2. DispathcerServlet调用HandlerMapping中的DefaulttAnnotationHandlerMapping来解析URL
3. HandlerMapping解析完URL以后，调用HandlerAdapter中的DefaultAnnotationHandlerAdapter来调用Controller中的HandlerMethod，交由业务逻辑处理
4. HandlerMethod执行完之后会返回一个View视图，会被ViewResolver进行视图解析，然后调用.jsp对应的.class文件运行
5. 最终把运行的.class文件的结果响应给客户端。

**比较完整的！**
客户端请求被DispatcherServlet(前端控制器)接收
DispatcherServlet请求HandlerMapping查询Handler
HandlerMapping根据请求URL查找Handler，将Handler和HandlerInterceptor以HandlerExecutionChain的形式一并返回给DispatcherServlet
DispatcherServlet请求HandlerAdapter执行Handler
HandlerAdapter调用Handler的方法做业务逻辑处理
HandlerAdapter处理完Handler会生成一个ModelAndView对象
将ModelAndView对象返回给DispatcherServlet
DispatcherServlet将获取的ModelAndView对象传给ViewResolver视图解析器，请求进行视图解析
ViewResolver将逻辑视图解析成物理视图View，返回给DispatcherServlet
DispatcherServlet根据View进行视图渲染(将模型数据填充到视图中)
DispatcherServlet将渲染后的视图响应给客户端

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191130202518784.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NhbzEzMTUwMjA2MjY=,size_16,color_FFFFFF,t_70)
 
 **HTTP请求地址映射原理**
 http请求 ==> Servlet容器（Tomcat、Jetty等）==>SpringMVCHandlerMapping注解、xml编程等。 ==> Handler处理方法

**请求方法细节处理**
1. 请求参数绑定
2. 请求方式限定
3. 请求转发和重定向
4. 数据模型赋值
5. 返回json数据
6. cookie访问


### 3.2 整合配置SpringMVC框架
配置整合Spring框架，需要根据他的原理图来，首选需要有一个重要控制器，负责拦截所有的请求，配置`DispatcherServlet`，然后需要HandlerMapping来解析处理URL，并查找到可以获取到处理业务方法的HandlerAdapter，DispatcherServlet调用HandlerAdapter执行业务逻辑，返回ModelAndView给DispatcherServlet，此时Dispatcher将ModelAndView交由ViewResolver处理，返回物理视图View，此时DispatherServlet将渲染后的视图返回给客户端。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
	http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd" id="WebApp_ID" version="3.1">
    <servlet>
        <servlet-name>seckill-servlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring/spring-*.xml</param-value>
        </init-param>
        <!--表示一开始就加载-->
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>seckill-servlet</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
    <!--表示编码的过滤-->
    <filter>
        <filter-name>encoding</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>encoding</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
</web-app>
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
    <!--配置MVC注解扫描激活-->
    <mvc:annotation-driven></mvc:annotation-driven>
    <!--静态资源访问设置-->
    <mvc:default-servlet-handler></mvc:default-servlet-handler>

    <!--设置试图解析器-->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"></property>
        <property name="prefix" value="/WEB-INF/jsp/"></property>
        <property name="suffix" value=".jsp"></property>
    </bean>

    <!--配置包扫描-->
    <context:component-scan base-package="org.seckill.web"></context:component-scan>
</beans>
```
SpringMVC的运行过程：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191201113539750.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NhbzEzMTUwMjA2MjY=,size_16,color_FFFFFF,t_70)
流程说明：

（1）客户端（浏览器）发送请求，直接请求到DispatcherServlet。

（2）DispatcherServlet根据请求信息调用HandlerMapping，解析请求对应的Handler。

（3）解析到对应的Handler后，开始由HandlerAdapter适配器处理。

（4）HandlerAdapter会根据Handler来调用真正的处理器开处理请求，并处理相应的业务逻辑。

（5）处理器处理完业务后，会返回一个ModelAndView对象，Model是返回的数据对象，View是个逻辑上的View。

（6）ViewResolver会根据逻辑View查找实际的View。

（7）DispaterServlet把返回的Model传给View。

（8）通过View返回给请求者（浏览器）

## 4. 秒杀系统的高并发优化
秒杀系统的整体流程如下：
进入详情页==>获取系统时间 ==>如果时间满足的话，则暴露秒杀接口的地址，如果时间不满足的话，先倒计时，然后暴露秒杀地址接口 ==> 执行秒杀操作 ==> 返回结果。.
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191201200301728.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NhbzEzMTUwMjA2MjY=,size_16,color_FFFFFF,t_70)
详情页中将会有大量的用户刷新详情页，此时可以通过CDN（内容分发网络）将detail页静态化，以及存储静态资源css,js等，然后再到秒杀系统。

**CDN的理解**
* CDN(内容分发网络)加速用户获取数据的系统
* 部署在距离用户最近的网络节点上
* 命中CDN不需要访问后端服务器
* 互联网公司自己搭建或租用

**秒杀地址接口分析**
* 无法使用CDN缓存
* 适合服务端缓存：redis等
* 一致性维护成本低


**秒杀地址接口优化**
可以将请求地址通过redis与mysql之间的一致性维护，通过超时穿透/主动更新策略来优化。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191201114313246.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NhbzEzMTUwMjA2MjY=,size_16,color_FFFFFF,t_70)

**秒杀操作优化分析**
* 无法使用CDN缓存
* 后端缓存困难：库存问题
* 一行数据竞争：热点商品

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191201114520656.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NhbzEzMTUwMjA2MjY=,size_16,color_FFFFFF,t_70)
**成本分析**
* 运维成本和稳定性：NoSQL，MQ等
* 开发成本：数据一致性，回滚方案等
* 幂等性难保证，针对重复秒杀问题
* 不适合新手

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191201114858792.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NhbzEzMTUwMjA2MjY=,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191201115012983.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NhbzEzMTUwMjA2MjY=,size_16,color_FFFFFF,t_70)
**优化分析**
* 行级锁在Commit之后释放
* 优化方向减少行级锁持有时间（具体操作，先执行插入，再执行减库存操作，这样的话可以将行级锁提前释放，可以减少一次GC的时间）


**延迟分析**
同城机房与异地机房有关系

**如何放到MySQL服务端**
1. 定制SQL方案：update /* +[auto_commit] */，这种方案需要修改源码。
2. 使用存储过程，整个事务在MySQL端完成

**优化总结**
* 前端控制：暴露接口，按钮防重复
* 动静态数据分离：CDN缓存，后端缓存
* 事务竞争优化：减少事务锁时间

**并发深度优化**
事务SQL在MySQL端执行（使用存储过程）



**系统部署架构**
系统可能用到的服务
* CDN
* WebServer：Nginx+Jetty
* Redis
* Mysql

**常规的架构**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191201171348649.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NhbzEzMTUwMjA2MjY=,size_16,color_FFFFFF,t_70)
**高并发优化总结**
1:使用Redis做后端缓存处理，将商品的信息存入redis中，如果在redis中查找到了，直接返回，否则查找mysql，返回对象，将对象放入到redis中。
2:为了减少网络延迟以及GC的时间，减少GC次数，由于如果先执行减库存，再执行插入，最后commit/rollback的话，会经历两次网络延迟以及GC，此时考虑减少GC的次数，将插入提前，减库存的操作放在后面，如果插入不成功，直接rollback，插入成功的话，执行减库存，最后commit，可以在竞争热点处解决这个问题。
3:存储过程优化，直接通过mysql的存储过程来实现事务，从而减少服务器端的事务处理，交由mysql存储过程函数处理。简单的数据逻辑可以使用存储过程优化，在阿里巴巴开发手册中，是禁止使用存储过程函数来处理mysql的。
更改：添加了jedis也就是redis的依赖，在linux上安装redis来处理。添加了存储过程处理秒杀的逻辑。

## 5. 总结
**Dao层**
主要学习了数据库相关的知识
* 数据库的设计与实现：要求掌握基本的手写DDL的能力（公司一般是可视化建表）
* Mybatis的理解与使用技巧（现在基本都是用的Mybatis plus，但是如果遇到底层问题还是要了解mybatis）
* mybatis整合spring技巧

**业务层service**
* 业务层接口的设计与封装（需要站在使用者的角度来设计接口）
* SpringIOC的配置技巧
* Spring声明式事务的使用和理解

**Web层**
* Restful的风格的URL设计与使用
* SpringMVC的使用技巧以及配置
* 前端交互bootstrap+js

**并发优化**
* 系统的瓶颈分析
* 事务、锁、网络延迟的理解
* 前端控制、CDN缓存等的理解使用
* 集群化部署

