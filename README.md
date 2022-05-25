## MTest 1.0
MTest是一个基于 Groovy、Spock、JUnit5 和 Spring Test 的轻量级微服务单元测试框架，目标是大幅降低编写单元测试的工作量，提升单元测试代码的可维护性。

#### MTest 设计理念

* 数据驱动测试，测试代码与测试数据分离
* BDD行为驱动开发（基于Spock）
* 约定大于配置

#### MTest 核心能力

1. 测试用例自动生成
2. 请求数据准备和结果校验
3. Mock接口服务
4. 数据库数据准备和数据校验
5. 测试代码与测试数据分离

## 快速开始

### Step1: 添加POM依赖配置
*由于添加的测试依赖较多，为了避免不小心把测试用的依赖打入正式包中，建议单独建一个-test子工程用于单元测试*

pom.xml 文件见附录

### Step2: 编写测试类
Demo工程是一个典型的微服务系统，使用Java语言，MySQL数据库，Spring作为依赖注入框架，MyBatis作为持久层框架，Thrift作为RPC框架，Kafka作为消息框架。

我们要测试的类是Demo工程中的 OrderService 接口的 createOrder 方法和 queryOrder 方法，即 创建订单 和 查询订单，接口的实现类是 OrderServiceImpl。

OrderServiceImpl 依赖商品服务提供的远程接口 ItemService.queryItemById 进行商品查询，依赖 OrderDAO 接口进行数据库操作。

测试类：**OrderServiceQueryOrderSpec.groovy**

```
@ContextConfiguration(classes = [OrderServiceQueryOrderSpec.class])
@ComponentScan("com.meituan.mtest.demo.order")
@Configuration
@MTest(testClass = OrderService.class, method = "queryOrder")
// OrderService.queryOrder 方法的测试类
class OrderServiceQueryOrderSpec extends MTestBaseCase {
​
    @Resource
    OrderService orderService
​
    @Unroll
    def "#testcase"() {
        given: ""
​
        when: ""
        def response = orderService.queryOrder(userId, orderId)
​
        then: ""
        with(response) {
            success == expected.success
        }
​
        Assertions.assertThat(response).usingRecursiveComparison()
                .ignoringFields("value.createTime").isEqualTo(expected)
​
        where: ""
        testcase << testCase()
        [userId, orderId] << request()
        expected << expected()
​
    }
​
}
```
代码说明：

* Spring注解：@ContextConfiguration、@ComponentScan、@Configuration 目的是加载Spring容器，并把该测试类配置为 Spring 的一个Bean。这里Demo工程使用的是Spring的注解模式，如果使用XML配置文件，可以把 @ComponentScan 改成 @ImportResource，如果是 SpringBoot，也可以是用 SpringBoot 的注解
* MTest注解：@MTest，testClass代表要测试的接口类，method代表要测试的接口名称，@MTest还有两个参数：beanName代表测试对象的Spring bean name，如果同一个接口在Spring中有多个实例则必须配置，overload代表方法的重载，如果同名方法名有多个重载方法，则需要手动给这些方法标记序号，序号从0开始递增
* 测试类名和父类：一个测试类对应一个待测试的方法（而不是一个接口），基于Spock标准，测试方法名必须以Spec结尾，依赖测试父类MTestBaseCase
* where代码块：在where代码块中拿到 测试用例信息 testcase，接口请求参数 userId, orderId，接口返回值校验对象 expected
* when代码块：待测试的接口方法调用
* then代码块：基于 response和expected 对接口返回值进行校验，推荐使用Spock的 with 功能或 AssertJ

### Step3: 编写测试数据文件
在maven工程的 src/test/resources 目录下新建 mtest-data  文件夹，用于存放测试数据文件

在 mtest-data 文件夹中新建 OrderService-queryOrder 文件夹，用于存放 OrderService.queryOrder 方法的测试数据。文件夹命名规则为 ```${ClassName}-${MethodName}-${overload}```，overload 代表方法重载存在同名方法时手动标记的序号，等同于@Mtest注解中的overload，其中 ```-${overload}``` 为可选

文件目录结构：

```
src/test/resources
  - mtest-data
    - OrderService-queryOrder
      - testcase.csv
      - request.yaml
      - expected.yaml
```

**testcase.csv** 文件

```
id, name, exception, ignore
case_id_333, "商品数量1，价格1.5元", ,
case_id_444, "商品数量4，价格4元", ,
```

文件内容说明：

* 文件第一行为表头，后面每一行代表一个测试用例，第一列代表测试用例的id；第二列代表测试用例的名称；第三列代表是否为异常测试，1 代表异常测试，可空；第四列代表该测试用例是否需要ignore不执行，1 代表不执行，可空

**request.yaml** 文件

```
case_id_333: [2222, 1]
case_id_444: [2222, 2]
```

文件内容说明：

* 每一个测试用例的请求参数，key 为测试用例id，OrderService.queryOrder 方法有2个参数，第一个是userId， 第二个是orderId

**expected.yaml** 文件

```
case_id_333: !!com.meituan.mtest.demo.order.service.dto.ResultDTO
  success: true
  value: !!com.meituan.mtest.demo.order.service.dto.OrderDTO
    {address: 上海, amount: 1.5, createTime: !!timestamp '2022-05-01T08:01:40.438Z', itemCount: 1, itemId: 33333,
     orderId: 1, orderName: 农夫山泉, telephone: '13000000000', userId: 2222}
​
case_id_444: !!com.meituan.mtest.demo.order.service.dto.ResultDTO
  success: true
  value: !!com.meituan.mtest.demo.order.service.dto.OrderDTO
    {address: 上海, amount: 4.0, createTime: !!timestamp '2022-05-01T08:01:40.438Z', itemCount: 4, itemId: 44444,
     orderId: 2, orderName: 哇哈哈, telephone: '13000000000', userId: 2222}
```

文件内容说明：

* 每一个测试用例期待的返回值对象，key 为测试用例id，OrderService.queryOrder 方法的返回对象是封装类 ResultDTO<T>，这里封装了 OrderDTO 对象，即订单对象。

以上就是针对 OrderService.queryOrder 这个方法的测试类，及 2个测试用例的测试数据。

**yaml 文件内容生成工具**：

手写Yaml文件格式容易出错，可以使用 snakeyaml.Yaml 类来在生成 yaml 文件的初始内容格式模版，后续编辑 yaml 文件中的具体数据内容即可。

可以参考Demo工程中的测试类 com.meituan.mtest.test.YamlSpec

yaml文件内容生成示例：
```
def orderDTO = new OrderDTO(orderId:111, userId:2222, itemId:33333, itemCount:2, amount:3.0, orderName:"农夫山泉", address:"上海", telephone:"13000000000", createTime:new Date())
def orderResultDTO = ResultDTO.of(orderDTO)
def OrderService_queryOrder_expected_yaml = new Yaml().dump(Maps.newHashMap("case_id_333", orderResultDTO))
println("OrderService_queryOrder_expected_yaml\n" + OrderService_queryOrder_expected_yaml)
```

### Step4: Mock依赖接口
OrderService.createOder 方法依赖了 商品服务提供的远程接口 ItemService.queryItemById 进行商品查询，因此，在对OrderService.createOder 方法进行单元测试时，需要对 ItemService.queryItemById 进行mock。

首先，先编写OrderService.createOder 方法对应的 测试类、测试用例数据：

测试类需要重载实现父类的 getMockMethods 方法，返回需要mock的方法。

测试类：**OrderServiceCreateOderSpec.groovy**

```
@ContextConfiguration(classes = [OrderServiceCreateOderSpec.class])
@ComponentScan("com.meituan.mtest.demo.order")
@Configuration
@MTest(testClass = OrderService.class, method = "createOder")
// OrderService.createOder 方法的测试类
class OrderServiceCreateOderSpec extends MTestBaseCase {
​
    @Resource
    OrderService orderService
​
    @Unroll
    def "#testcase"() {
        given: ""
​
        when: ""
        def response = orderService.createOrder(userId, orderReqDTO)
​
        then: ""
        with(response) {
            success == expected.success
            value != null
        }
​
        where: ""
        testcase << testCase()
        [userId, orderReqDTO] << request()
        expected << expected()
​
    }
​
    @Override
    MockMethod[] getMockMethods() {
        return [
                new MockMethod(ItemService.class, ItemService.class.getMethod('queryItemById', Long.class), 'itemService')
        ]
    }
​
}
```

代码说明：

* 重载父类的 getMockMethods  方法，返回需要mock的方法列表，MockMethod对象的参数包括：testClass 需要mock的类、method 需要mock的方法、beanName mock的类在Spring中的beanName（可空）、overload 代表方法的重载，如果同一个方法名有多个重载方法，则需要手动给这些方法标记序号，序号从0开始递增

**testcase.csv** 文件

```
id, name, exception, ignore
case_id_111, "商品数量1，价格1.5元", ,
case_id_222, "商品数量4，价格4元", ,
```

**request.yaml** 文件

```
case_id_111:
  - 2222
  - !!com.meituan.mtest.demo.order.service.dto.OrderReqDTO
    {address: 上海, itemCount: 1, itemId: 33333, telephone: '13000000000'}
​
case_id_222:
  - 2222
  - !!com.meituan.mtest.demo.order.service.dto.OrderReqDTO
    {address: 上海, itemCount: 4, itemId: 44444, telephone: '13000000000'}
```

**expected.yaml** 文件

```
case_id_111: !!com.meituan.mtest.demo.order.service.dto.ResultDTO
  {success: true, value: 1}
​
case_id_222: !!com.meituan.mtest.demo.order.service.dto.ResultDTO
  {success: true, value: 2}
```

创建 Mock 数据文件：

在 mtest-data/OrderService-createOder 文件夹中新增 mock 文件夹，并创建 ItemService-queryItemById-response.yaml 文件，用于准备 ItemService.queryItemById mock的返回数据。文件命名规则为 ```${ClassName}-${MethodName}-${overload}-response```，overload 代表方法重载时手动标记的序号，等同于MockMethod类中的overload，其中 ```-${overload}``` 为可选

文件目录结构：

```
src/test/resources
  - mtest-data
    - OrderService-createOder
      - testcase.csv
      - request.yaml
      - expected.yaml
      - mock
        - ItemService-queryItemById-response.yaml
```

**ItemService-queryItemById-response.yaml** 文件

```
case_id_111: !!com.meituan.mtest.demo.item.service.dto.ItemDTO
  {itemId: 33333, price: 1.5, itemName: 农夫山泉}
​
case_id_222: !!com.meituan.mtest.demo.item.service.dto.ItemDTO
  {itemId: 4444, price: 1, itemName: 哇哈哈}
```

文件内容说明：

* 每一个测试用例mock的返回值对象，key 为测试用例id，ItemService.queryItemById 方法的返回对象ItemDTO，包括 商品id itemId，商品价格 price，商品名称 itemName
* 如果在被测试 方法中，需要多次调用同一个mock方法，则需要在 用例id key 后面加上 括号和调用序号，如：

**ItemService-queryItemById-response.yaml** 文件 2

```
case_id_111(0): !!com.meituan.mtest.demo.item.service.dto.ItemDTO
  {itemId: 33333, price: 1.5, itemName: 农夫山泉}
case_id_111(1): !!com.meituan.mtest.demo.item.service.dto.ItemDTO
{itemId: 33333, price: 1.5, itemName: 农夫山泉}
​
case_id_222: !!com.meituan.mtest.demo.item.service.dto.ItemDTO
  {itemId: 4444, price: 1, itemName: 哇哈哈}
```

文件内容说明：

* OrderService.createOder 方法 在测试用例 case_id_111 中，需要调用 ItemService.queryItemById 方法2次，2次返回分别为 case_id_111(0) 和 case_id_111(1) 对应的返回对象。

有时，多个测试case对同一个方法mock的返回值是相同的，这时，可以配置成公用mock返回对象，如：

**ItemService-queryItemById-response.yaml** 文件 3

```
case_id_111,case_id_222: !!com.meituan.mtest.demo.item.service.dto.ItemDTO
  {itemId: 33333, price: 1.5, itemName: 农夫山泉}
```

文件内容说明：

* case_id_111, case_id_222 公用同一个mock返回对象，yaml的key 以 “,” 分割。

#### Mock静态方法

MTest基于 mockito inline mock maker 实现实现静态方法的mock，在MTest中，静态方法的Mock和普通方法的Mock完全相同。

参考mockito文档：https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#static_mocks

### Step5: 数据库数据准备和数据校验
OrderService.createOder 方法会数据库订单表中插入一条记录，需要校验这条数据的正确性；而 OrderService.queryOrder 方法需要提前在数据库订单表中插入一条记录，用于数据查询。

MTest 基于 DBUnit 和 H2 进行数据库测试：

* MTest 基于 DBUnit 进行数据库数据准备和数据校验
* 为了提升单元测试运行的速度，以及避免单元测试用例污染test环境数据库的数据，MTest默认使用H2 内存数据库进行单元测试。

准备数据库ddl 建表语句文件：

**schema.sql**

```
drop table if exists `TABLE_ORDER`;
​
CREATE TABLE `TABLE_ORDER` (
  `id` INTEGER NOT NULL auto_increment,
  `user_id` INTEGER NOT NULL,
  `item_id` INTEGER NOT NULL,
  `item_count` INTEGER NOT NULL,
  `amount` DOUBLE NOT NULL,
  `order_name` VARCHAR(64) ,
  `address` VARCHAR(1024) ,
  `telephone` VARCHAR(64) ,
  `create_time` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;
```

说明：

* 这里的数据库DDL文件从MySQL中批量导出即可。但是 H2 不完全兼容 MySQL，需要通过插件将 MySQL DDL转换成H2 的DDL。参考：MySql转H2插件：https://plugins.jetbrains.com/plugin/14580-mysql-to-h2

Spring配置文件：

**spring-ddl.xml**

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:jdbc="http://www.springframework.org/schema/jdbc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context-4.2.xsd
       http://www.springframework.org/schema/jdbc http://www.springframework.org/schema/jdbc/spring-jdbc-4.1.xsd">
​
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="org.h2.Driver" />
        <property name="url" value="jdbc:h2:mem:PCTDiscount;MODE=MYSQL;DB_CLOSE_DELAY=-1" />
    </bean>
​
    <jdbc:initialize-database data-source="dataSource">
        <jdbc:script location="classpath:/ddl/schema.sql" />
    </jdbc:initialize-database>
​
</beans>
```

在测试类中加上加载spring配置文件的注解：

```
@ImportResource("classpath:spring-ddl.xml")
```

创建 db 数据文件：

在 mtest-data/OrderService-createOder 文件夹 中新增 db-data 文件夹，

对于 OrderService.queryOrder 方法，创建 case_id_333-setUp.xml、case_id_444-setUp.xml 文件，用于提前在数据库中插入订单数据。文件命名规则为 ```${testcase id}-setUp.xml```

对于 OrderService.createOder 方法，创建 case_id_111-expected.xml、case_id_222-expected.xml 文件，用于方法执行完成后，对数据库数据进行校验。文件命名规则为 ```${testcase id}-expected.xml```

文件目录结构：

```
src/test/resources
  - mtest-data
    - OrderService-queryOrder
      - testcase.csv
      - request.yaml
      - expected.yaml
      - db-data
        - case_id_333-setUp.xml
        - case_id_444-setUp.xml
    - OrderService-createOder
      - testcase.csv
      - request.yaml
      - expected.yaml
      - mock
        - ItemService-queryItemById-response.yaml
      - db-data
        - case_id_111-expected.xml
        - case_id_222-expected.xml
```

**case_id_333-setUp.xml** 文件：

```
<?xml version="1.0" encoding="UTF-8"?>
<dataset>
    <TABLE_ORDER
            user_id = "2222"
            item_id = "33333"
            item_count = "1"
            amount = "1.5"
            order_name = "农夫山泉"
            address = "上海"
            telephone = "13000000000"
            create_time = '2022-05-01 08:01:40.438'
    />
</dataset>
```

每个testcase运行前，会将 dateset 中的数据初始化到数据库中。

**case_id_111-expected.xml** 文件：

```
<?xml version="1.0" encoding="UTF-8"?>
<dataset>
    <TABLE_ORDER
            user_id = "2222"
            item_id = "33333"
            item_count = "1"
            amount = "1.5"
            order_name = "农夫山泉"
            address = "上海"
            telephone = "13000000000"
    />
</dataset>
```

文件内容说明：

* 这里不校验 order_id 字段 和 create_time 字段，因此这两个字段未赋值。

所有测试case公用的数据集：

有些数据集可能是所有测试case公用的，例如准备用户数据，配置数据等，在 mtest-data/OrderService-createOder、mtest-data/OrderService-queryOrder 文件夹 中新增 db-data-setUp.xml 、db-data-expected.xml 文件，文件内容格式跟上面的数据集相同。

文件目录结构：

```
src/test/resources
  - mtest-data
    - OrderService-queryOrder
      - testcase.csv
      - request.yaml
      - expected.yaml
      - db-data-setUp.xml
      - db-data-expected.xml
      - db-data
        - case_id_333-setUp.xml
        - case_id_444-setUp.xml
    - OrderService-createOder
      - testcase.csv
      - request.yaml
      - expected.yaml
      - db-data-setUp.xml
      - db-data-expected.xml
      - mock
        - ItemService-queryItemById-response.yaml
      - db-data
        - case_id_111-expected.xml
        - case_id_222-expected.xml
```

数据集 dataset 文件生成：

数据集 dataset 文件可以从MySQL数据库中直接导出。参考：使用插件生成DbUnit的数据集xml文件：https://github.com/alsbury/SequelProCopyPHPUnitDataset

### Step5: 异常测试
异常场景测试，测试方法抛出异常的case。

这里测试 OrderService.createOder 方法接收到不存在的商品id，抛出 OrderException 异常，errorCode = "ERROR_CODE_123"

首先，在测试类 OrderServiceQueryOrderSpec.groovy 中新增一个测试方法，用于测试异常场景。

测试类：**OrderServiceCreateOderSpec.groovy**

```
@ContextConfiguration(classes = [OrderServiceCreateOderSpec.class])
@ComponentScan("com.meituan.mtest.demo.order")
@Configuration
@MTest(testClass = OrderService.class, method = "createOder")
// OrderService.createOder 方法的测试类
class OrderServiceCreateOderSpec extends MTestBaseCase {
​
    @Resource
    OrderService orderService
​
    @Unroll
    def "#testcase"() {
        ...
    }
​
    @Unroll
    def "#testcase exception"() {
        given: ""
​
        when: ""
        def response = orderService.createOrder(userId, orderReqDTO)
​
        then: ""
        def exception = thrown(expectedException.class)
        with(exception) {
            errorCode == expectedException.errorCode
        }
​
        where: ""
        testcase << testCase(TestCase.EXCEPTION)
        [userId, orderReqDTO] << request(TestCase.EXCEPTION)
        expectedException << expectedException()
​
    }
​
    @Override
    MockMethod[] getMockMethods() {
        return [
                new MockMethod(ItemService.class, ItemService.class.getMethod('queryItemById', Long.class), 'itemService')
        ]
    }
​
}
```

代码说明：

* where代码块：testCase 和 request 新增了一个参数，传入常量 TestCase.EXCEPTION，expectedException 为方法预期抛出的异常
* when代码块：待测试的接口方法调用
* then代码块：使用thrown方法拿到抛出的异常对象，跟expectedException进行对比验证，推荐使用Spock的 with 功能或 AssertJ

创建 预期抛出异常 数据文件：

在 mtest-data/OrderService-createOder 文件夹中新增 exception.yaml 文件，用于准备方法预期抛出的异常。

文件目录结构：

```
src/test/resources
  - mtest-data
    - OrderService-createOder
      - testcase.csv
      - request.yaml
      - expected.yaml
      - exception.yaml
      - mock
        - ItemService-queryItemById-response.yaml
```

**exception.yaml** 文件

```
case_id_999:
  !!com.meituan.mtest.demo.order.OrderException
  ["ERROR_CODE_123", ""]
```

**testcase.csv** 文件中新增对应的异常测试用例:

```
id, name, exception, ignore
case_id_111, "商品数量1，价格1.5元", ,
case_id_222, "商品数量4，价格4元", ,
case_id_999, "商品不存在", 1,
```

mock/ItemService-queryItemById-response.yaml 文件中新增一条 mock 返回，该用例下，商品查询返回 null

**ItemService-queryItemById-response.yaml** 文件

```
case_id_111: !!com.meituan.mtest.demo.item.service.dto.ItemDTO
  {itemId: 33333, price: 1.5, itemName: 农夫山泉}
​
case_id_222: !!com.meituan.mtest.demo.item.service.dto.ItemDTO
  {itemId: 4444, price: 1, itemName: 哇哈哈}
​
case_id_999:
  null
```

这样，OrderService 接口的 createOrder 方法和 queryOrder 方法 的测试用例就写完了，接下来补充具体的测试用例数据即可。

### 参考文档
* Spock单元测试框架介绍及在美团优选的实践：https://tech.meituan.com/2021/08/06/spock-practice-in-meituan.html
* Spock：https://spockframework.org/spock/docs/2.1/all_in_one.html
* DbUnit：https://www.dbunit.org/howto.html
* Mockito static mock：https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#static_mocks
* MySql转H2插件：https://plugins.jetbrains.com/plugin/14580-mysql-to-h2
* 使用插件生成DbUnit的数据集xml文件：https://github.com/alsbury/SequelProCopyPHPUnitDataset
* GMavenPlus：https://github.com/groovy/GMavenPlus/wiki/Examples#spock-2-and-junit

### 附录
pom.xml 文件：
``` 
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>
​
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.spockframework</groupId>
        <artifactId>spock-bom</artifactId>
        <version>2.0-groovy-3.0</version>
        <!-- use below for Groovy 4 -->
        <!-- <version>2.2-M1-groovy-4.0</version> -->
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
​
  <dependencies>
    <dependency>
      <groupId>com.meituan.mtest</groupId>
      <artifactId>mtest-core</artifactId>
      <version>1.0-SNAPSHOT</version>
      <scope>test</scope>
    </dependency>
​
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version>1.18.24</version>
      <scope>test</scope>
    </dependency>
​
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-core</artifactId>
      <version>5.0.11.RELEASE</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-beans</artifactId>
      <version>5.0.11.RELEASE</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>5.0.11.RELEASE</version>
      <scope>test</scope>
    </dependency>
​
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-test</artifactId>
      <version>5.0.11.RELEASE</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-jdbc</artifactId>
      <version>5.0.11.RELEASE</version>
      <scope>test</scope>
    </dependency>
​
    <dependency>
      <groupId>org.yaml</groupId>
      <artifactId>snakeyaml</artifactId>
      <version>1.27</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.assertj</groupId>
      <artifactId>assertj-core</artifactId>
      <!-- use 2.9.1 for Java 7 projects -->
      <version>3.18.0</version>
      <scope>test</scope>
    </dependency>
​
    <dependency>
      <groupId>com.google.guava</groupId>
      <artifactId>guava</artifactId>
      <version>18.0</version>
      <scope>test</scope>
    </dependency>
​
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-core</artifactId>
      <version>4.3.1</version>
      <scope>test</scope>
    </dependency>
​
    <dependency>
      <groupId>org.codehaus.groovy</groupId>
      <artifactId>groovy-all</artifactId>
      <version>3.0.9</version>
      <!-- use below for Groovy 4 -->
      <!-- <groupId>org.apache.groovy</groupId> -->
      <!-- <artifactId>groovy-all</artifactId> -->
      <!-- <version>4.0.0</version> -->
      <type>pom</type>
    </dependency>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-api</artifactId>
      <version>5.8.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-engine</artifactId>
      <version>5.8.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.spockframework</groupId>
      <artifactId>spock-core</artifactId>
      <scope>test</scope>
    </dependency>
​
    <!-- add dependencies below to enable JUnit 4 style tests -->
    <dependency>
      <groupId>org.junit.vintage</groupId>
      <artifactId>junit-vintage-engine</artifactId>
      <version>5.8.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.spockframework</groupId>
      <artifactId>spock-junit4</artifactId>
      <scope>test</scope>
    </dependency>
​
    <dependency>
      <groupId>org.spockframework</groupId>
      <artifactId>spock-spring</artifactId>
      <scope>test</scope>
    </dependency>
​
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <version>1.4.200</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.dbunit</groupId>
      <artifactId>dbunit</artifactId>
      <version>2.5.1</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>com.github.springtestdbunit</groupId>
      <artifactId>spring-test-dbunit</artifactId>
      <version>1.2.0</version>
      <scope>test</scope>
    </dependency>
​
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <version>3.5.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis-spring</artifactId>
      <version>2.0.2</version>
      <scope>test</scope>
    </dependency>
​
  </dependencies>
​
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.gmavenplus</groupId>
        <artifactId>gmavenplus-plugin</artifactId>
        <version>1.13.1</version>
        <executions>
          <execution>
            <goals>
              <goal>addSources</goal>
              <goal>addTestSources</goal>
              <goal>generateStubs</goal>
              <goal>compile</goal>
              <goal>generateTestStubs</goal>
              <goal>compileTests</goal>
              <goal>removeStubs</goal>
              <goal>removeTestStubs</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.0.0-M5</version>
        <configuration>
          <includes>
            <include>**/*Spec.class</include>
            <include>**/*Test.java</include>
          </includes>
        </configuration>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-resources-plugin</artifactId>
        <version>2.6</version>
        <executions>
          <execution>
            <id>copy-resources</id>
            <phase>compile</phase>
            <goals>
              <goal>copy-resources</goal>
            </goals>
            <configuration>
              <outputDirectory>${basedir}/target/resources</outputDirectory>
              <resources>
                <resource>
                  <directory>${basedir}/src/main/resources</directory>
                  <filtering>true</filtering>
                </resource>
              </resources>
            </configuration>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <version>0.8.2</version>
        <executions>
          <execution>
            <id>prepare-agent</id>
            <goals>
              <goal>prepare-agent</goal>
            </goals>
          </execution>
          <execution>
            <id>report</id>
            <phase>prepare-package</phase>
            <goals>
              <goal>report</goal>
            </goals>
          </execution>
          <execution>
            <id>post-unit-test</id>
            <phase>test</phase>
            <goals>
              <goal>report</goal>
            </goals>
            <configuration>
              <dataFile>target/jacoco.exec</dataFile>
              <outputDirectory>target/jacoco-ut</outputDirectory>
            </configuration>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-maven-plugin</artifactId>
        <version>1.16.8.0</version>
        <executions>
          <execution>
            <phase>generate-sources</phase>
            <goals>
              <goal>delombok</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```