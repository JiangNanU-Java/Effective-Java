开发环境：IDEA
插件1：Junit4
插件2：JunitGenerator 自动生成Junit模板

#### 关于：
单元测试代码不会出现在最终产品当中

#### 目录结构(maven)

project 

* src
	* main
		* java
		* resources
  * **test**
	  * java
	  * resources

-----------------------
### junit运行流程
static **@BeforeClass** ，加载初始配置信息

**@Before**，实例化对象

**@Test**，测试

**@After**，释放资源

static **@AfterClass**，资源的清理

----------
### 常用注解
#### 捕获异常
@Test(exception = 异常.class) 此异常会被捕获，不会报错
#### 运行时长
@Test(timeout = 毫秒) 超时报错
#### 忽略
@ignore
@Test
不执行
#### 更改运行器
@Runwith
当使用框架测试时，可更改为框架运行期，如springtest

--------
### 断言方法
assertXXXXX(args)

import **static** org.junit.Assert.*;

举例
assertNull(obj) 检查对象是否为空
assertEquals(1,2) 检查是否相等
...


---------
### 测试套件
对多个测试类进行统一测试

@RunWith(Suite.class)
@Suite.suiteClasses(测试类列表)

-------------
### Mock测试
**创建mock对象，可用于网络接口的测试，springMVC的测试等等**

#### 举例：servlet的测试⇒ **EasyMock框架**

#### 1. 定义
private HttpServletRequest request;
private HttpServletResponse response;
private HttpSession session;

#### 2. 创建
request = EasyMock.createMock(类名.class)

#### 3. 测试
EasyMock.expect(request.get.....).andReturn(....).one()


