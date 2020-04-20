#  开放论坛项目

[【Spring Boot 实战】论坛项目](<https://www.bilibili.com/video/av65117012>)

仿照项目：[elastic中文社区](<https://elasticsearch.cn/>)

## 【12】 Github登陆获取用户信息

2020.3.11

[^当前目录]: 增加dto，provider文件夹

<img src="C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583907218297.png" style= "width=200px height=500px">

### 1.dto文件夹

用来存放参数类，封装需要的参数。除了set()和get()方法之外没有其他方法。

![1583910928264](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583910928264.png)

![1583910989081](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583910989081.png)

创建好类成员之后，光标在类大括号之内，alt+insert（windows）可以自动创建set&get方法。

![1583911243274](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583911243274.png)

### 2.GithubProvider.class

【注解】

@Controller 控制器（注入服务）：用于标注控制层，相当于struts中的action层

@Service 服务 （注入dao）：用于标注服务层，主要用来进行业务的逻辑处理

@repository（实现dao访问）：用于标注数据访问层，也可以说用于标注数据访问组件，即DAO组件.

@component （把普通pojo实例化到spring容器中，相当于配置文件中的  `<bean id="" class=""/>`）

泛指各种组件，就是说当我们的类不属于各种归类的时候（不属于@Controller、@Services等的时候），我们就可以使用@Component来标注这个类。

### 3.使用OkHttp进行POST

[OkHttp](<https://square.github.io/okhttp/>)

官方给出例子：Post to a Server

```java
public static final MediaType JSON
    = MediaType.get("application/json; charset=utf-8");

OkHttpClient client = new OkHttpClient();

String post(String url, String json) throws IOException {
  RequestBody body = RequestBody.create(json, JSON);
  Request request = new Request.Builder()
      .url(url)
      .post(body)
      .build();
  try (Response response = client.newCall(request).execute()) {
    return response.body().string();
  }
}
```

使用上述例子，进行post

```java
/*Githubprovider.class*/
@Component
public class GithubProvider {
    public String getAccessToken(AccessTokenDTO){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        }
    }
}
```

注意到方法参数为DTO,直接使用MediaType 创建类型，并通过类内的方法post

### 4.fastjson:class—>json

安装依赖：在[mvnrepository](<https://mvnrepository.com/>)官网搜索，粘贴到pom中即可

可以将json转换成String、Object

## 【13】配置application properties

在使用字符串的时候，直接写在代码中会导致更改非常麻烦。

```java
accessTokenDTO.setClient_secret("6d515e35283375dbb9d8de4690dff86cbedd6782")
```

配置properties之后，代码的更改就会非常简单。

```java
//application.properties
github.client.id=fd2f148d147009760ddf
github.client.secret = 6d515e35283375dbb9d8de4690dff86cbedd6782
github.redirect.uri  = http://localhost:8887/callback
```
用@Value 注释，将配置中的值注入到spring中，同时申明一个String常量
```java
//com.admond.community.controller.AuthorzeController
@Value("${github.client.id}")
private String clientId;

```
下面的方法中就可以直接使用
```java
accessTokenDTO.setClient_id(clientId);
```

## 【14】session和cookies

session相当于银行，cookies是银行卡。

cookie机制采用的是在客户端保持状态的方案。它是在用户端的会话状态的存贮机制。

cookie的内容主要包括：名字，值，过期时间，路径和域。

而session机制采用的是一种在服务器端保持状态的解决方案。session是针对每一个用户的，变量的值保存在服务器上，用一个sessionID来区分是哪个用户session变量,这个值是通过用户的浏览器在访问的时候返回给服务器，当客户禁用cookie时，这个值也可能设置为由get来返回给服务器。

就安全性来说：当你访问一个使用session 的站点，同时在自己机子上建立一个cookie，建议在服务器端的session机制更安全些，因为它不会任意读取客户存储的信息。

目标：导航栏右上角未登录时显示【登陆】，登录后显示用户名

![1583946122430](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583946122430.png)

![1583946165422](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583946165422.png)

### 1.接受request并提取session

```java
/*
community.controller.AuthoizeController.class
*/
public String callback(@RequestParam(name="code") String code,
                       @RequestParam(name="state") String state,
                       HttpServletRequest request)
```

HttpServletRequest request：spring中约定取值为上下文中的request中的报文

### 2.重定向

```java
GithubUser user = githubProvider.getUser(accessToken);
if(user!=null){
    // 登陆成功，写cookies和session
    request.getSession().setAttribute("user",user);
    return "redirect:/"; 
}else{
    return "redirect:/";
    // 登陆失败，重新登陆
}
```

redirect:/ 重定向为根目录，即初始目录

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

### 3.thymeleaf

[thymeleaf](<https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html>)是java模板引擎,学习可以参考这个[博客](<https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html>)


```xml
/*pom.xml*/
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

上网查询语法
```html
<li th:if="${session.user == null}"> //判断语句

<li th:text="${session.user.getName()}"> //text语句
```

### 4.结果

登录后：![1583947006690](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583947006690.png)

由于没读出名字，是null

![1583947050731](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583947050731.png)

jsessionid就是自动分配的cookie，domain是作用域。

## 【15】MySQL与实例化

[菜鸟教程](<https://www.runoob.com/mysql/mysql-create-database.html>)

```sql
/*创建数据库*/
CREATE DATABASE 数据库名;

/*创建数据表*/
CREATE TABLE table_name (column_name column_type);

/*插入数据*/
INSERT INTO table_name ( field1, field2,...fieldN )
                       VALUES
                       ( value1, value2,...valueN );
/*查询语句
1.查询语句中你可以使用一个或者多个表，表之间使用逗号(,)分割，并使用WHERE语句来设定查询条件。
2.SELECT 命令可以读取一条或者多条记录。
3.你可以使用星号（*）来代替其他字段，SELECT语句会返回表的所有字段数据
4.你可以使用 WHERE 语句来包含任何条件。
5.你可以使用 LIMIT 属性来设定返回的记录数。
6.你可以通过OFFSET指定SELECT语句开始查询的数据偏移量。默认情况下偏移量为0。*/
SELECT column_name,column_name
FROM table_name
[WHERE Clause]
[LIMIT N][ OFFSET M]

/*更新*/
UPDATE table_name SET field1=new-value1, field2=new-value2
[WHERE Clause]

/*删除*/
DELETE FROM table_name [WHERE Clause]
```

## 【16】初识H2数据库

[H2数据库](<http://www.h2database.com/html/main.html>)特点：快，小，可内置。

### 1.Quickstart，查看配置要求：

![1583984713302](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583984713302.png)

首先从[mvn](https://mvnrepository.com/)中安装依赖,包括[H2.jar](<https://mvnrepository.com/artifact/com.h2database/h2/1.4.200>),发现找不到JDBC driver。查看jar包中是否自带driver：

![1583984794415](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583984794415.png)

![1583984823401](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583984823401.png)

确实自带driver

### 2.创建数据库

使用idea内置的数据库创建工具即可

![1583985089304](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583985089304.png)

![1583985288268](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583985288268.png)

创建table，设置id为主键且自增。（下面是sql语句，通常使用右边的+进行创建）

![1583985996408](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583985996408.png)

设置gmt_create和gmt_modified为时间戳，方便检查。

![1583986312220](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583986312220.png)

创建完成后，使用+创建新的条目，箭头submit

![1583986583125](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1583986583125.png)

## 【17】集成Mybatis,插入

MyBatis 是支持定制化 SQL、存储过程以及高级映射的优秀的持久层框架。MyBatis 避免了几乎所有的 JDBC 代码和手动设置参数以及获取结果集。MyBatis 可以对配置和原生Map使用简单的 XML 或注解，将接口和 Java 的 POJOs(Plain Old Java Objects,普通的 Java对象)映射成数据库中的记录。

### 1.数据库连接池的配置

spring有一个内置数据库连接池，高性能且并发。按照[Spingboot官网](<https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/reference/html/spring-boot-features.html#boot-features-connect-to-production-database>)提示,需要

#### （1）安装jdbc依赖

![1584002613179](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584002613179.png)

```xml
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

#### （2）配置文件

在配置文件properties中声明name、password，spring会自动识别。h2的初始用户为sa，初始密码为123.

![1584002254299](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584002254299.png)

### 2.数据持久化操作

[MyBatis-Spring-Boot](<http://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/>)官网一手资料。

#### （1）建立持久化类

DTO用于网络传输的时候传递参数，与数据库的交互用model中的持久化Object。

新建一个community.model pakage,新建User持久化类。这个类将是数据库和spring之间的桥梁。

```java
/community.model.User*/
public class User {
    private Integer id;
    private String name;
    private String accountId;
    private String token;
    private Long gmtCreate;
    private Long gmtModified;
    /*get() & set() function*/
} 
```

####  （2）持久化类到数据库的映射

新建community.model package ,新建UserMapper接口。这个接口中使用【注解】的方法定义对持久化类的操作，并映射到数据库中。这样spring只需要对持久化类进行操作就可以完成数据库的操作。

可以从[官网](<https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/reference/html/spring-boot-features.html#boot-features-connect-to-production-database>)的Quick Setup中进行学习。

```java spring
@Mapper
public interface UserMapper {
    @Insert("insert into user (name,account_id,token,gmt_create,gmt_modified) values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified})")
    void insert(User user);
}
```

#### （3）Controller中编写逻辑代码

```java
/******************************
controller.AuthorizeController
******************************/
/*注入spring*/
@Autowired
private UserMapper userMapper;

/*public String callback*/
        if(githubUser!=null){
            // 登陆成功，写cookies和session
            User user = new User();
            user.setToken(UUID.randomUUID().toString());
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(System.currentTimeMillis());
            userMapper.insert(user);
            request.getSession().setAttribute("user",user);
            return "redirect:/";
        }
```

#### （4）bug及修复

遇到问题，wrong name or password。花了大量时间鼓捣之后，选择新建数据库，更改路径。注意，properties中的文件符号是"/"，否则会出现报错。

![1584080412467](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584080412467.png)

重新建数据库，路径改为本地的某个路径，这里设置为项目地址。

![1584072407266](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584072407266.png)

成功，console中有提示,导航栏右上角变成自己的名字。

![1584077134974](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584077134974.png)

![1584079677475](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584079677475.png)

停止服务并重启数据库之后，发现数据库已经有了user数据。为什么要停止服务：h2数据库只能维持一个连接，观点服务才能用idea连接数据库。

![1584080506479](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584080506479.png)

注意，可能是数据库连接的原因，仍然会遇到java.net.SocketException: Connection reset问题。

## 【18】持久化登陆状态获取

即使用保存在本地的token，使得浏览器断开连接后保有登录状态。

### 1.原理

![1584158938558](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584158938558.png)

浏览器和服务器第一次连接之后，服务器为之分配一个随机的token，并通过response报文返回给浏览器。浏览器将其储存起来（大概是以映射的方式，<网址，token>）

下次连接时，浏览器的request报文中带有token，并被服务器接收。服务器查询数据库是否存在对应的用户，如果有则直接跳转到登陆后的界面。

### 2.操作

```java
/*AuthorizeController.java*/
/*public String callback参数注入*/
HttpServletResponse response

/*设定一个token变量*/
String token = UUID.randomUUID().toString();
user.setToken(token);

/*将token作为cookie传入response报文中，删除request.getSession().setAttribute("user",user);*/
response.addCookie(new Cookie("token",token));

/*UserMapper.java*/
/*查询语句*/
    @Select("select * from User where token = #{token}")
    User findByToken(@Param("token")  String token);

/*IndexController.java*/
/*在index界面验证request报文中的token*/
    public  String index(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {    //保证cookie为空时也能正常运行
            for(Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    User user = userMapper.findByToken(token);
                    if (user != null) {
                        System.out.println("user not null");
                        request.getSession().setAttribute("user", user);
                    }
                    break;
                }
            }
            System.out.println("cookies not null");
        }
        return "index";
    }
```

### 3.结果

只要服务开着，关闭浏览器后再访问页直接跳到登录界面

![1584159669767](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584159669767.png)

可以看到token

![1584159690639](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584159690639.png)

数据库中有这一条目

![1584159753932](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584159753932.png)

## 【19】 Flyway Migration

Flyway 数据库版本控制插件，支持h2数据库，windows需要gitbash。

## 【20】发布问题页面

使用[bootstrap](<https://v3.bootcss.com/css/>)栅格系统。

快捷键：div.col-lg-9 + tab  =   <div class="col-lg-9"></div>

## 【21】发布问题功能

### 1.创建提问表格

![1584168856516](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584168856516.png)

### 2.新建model和mapper

```java
/*model.java*/
public class Question {
    private Integer id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creator;
    private Integer viewCount;
    /*get()&set()*/
}
```

```java
/*QuesionMapper.java*/
@Mapper
public interface QuestionMapper {
    @Insert("insert into (title,description,gmt_create,gmt_modified,creator,tag) values (#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag}) ")
    void create(Question question);
}
```

### 3.前后端交互

### （1）原理

![1584195026424](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584195026424.png)

浏览器已经通过get方式访问到浏览器，浏览器通过GetMappingx响应，路由到相应的页面publish.html。

publish.html文件中有<form>表单，内含三个条目（title，description，tag）。使用post方式提交到服务器中,服务器用PostMapping响应，并在doPublish方法的参数中接受表单的条目。下面就可以在方法中使用这些条目信息。

另外，还可以接受request报文，内含cookies；model应该是springboot包装好的、在浏览器服务器之间可传送的层级。目前使用信息都是在model中存储以<key,value>形式存储的<map>。

### （2）操作

```html
<!-publish.html->
<!-表单->
<form action="/publish" method="post">
    <div>输入框</div>
    <div>文本框</div>
    <div>输入框</div>
    <button type="submit"></button>
</form>
```

get方法渲染页面，post方法执行请求。

新建PublishController.java，接受post的信息并存储到数据库中。

```java
/*PublishController.java*/
@Controller
public class PublishController {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }
    @PostMapping("/publish")
    public String doPublish(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("tag") String tag,
            HttpServletRequest request,
            Model model){

        User user = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {  //和IndexController.java中相同代码段
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    user = userMapper.findByToken(token);
                    if (user != null) {
                        System.out.println("user not null");
                        request.getSession().setAttribute("user", user);
                    }
                    break;
                }
            }
            if (user == null) {                         //另一重判断
                model.addAttribute("error", "用户未登录");
                return "publish";
            }
        }
        Question question = new Question();            //为文章创建一个表
        question.setDescription(description);
        question.setTitle(title);
        question.setTag(tag);
        question.setGmtCreate(System.currentTimeMillis());
        question.setCreator(user.getId());
        question.setGmtModified(System.currentTimeMillis());
        questionMapper.create(question);
        return "redirect:/";
    }
}
```

### 4.差错校验，错误信息处理

### （1）publish.html中添加警告信息。

```html
<!-当有error时显示->
<span class="alert alert-danger" th:text="${error}"
                      th:if="${error != null}"></span>
```

### （2）使用model传递差错信息

```java
/*PublishController.java*/
/*使用model将信息传递到页面上*/
model.addAttribute("title",title);      
model.addAttribute("description",description);
model.addAttribute("tag",tag);
```
注意：，thymeleaf语法规定，input标签要使用value=“”、textarea使用text=“”，才能将title信息显示在内部
```html
<!-publish.html->
<!-将title，description，tag传递过来->
<div class="form-group">
    <label for="title">标题：</label>
    <!-注意，input标签要使用value，才能将title信息显示在内部->
    <input type="text" class="form-control" id="title" th:value="${title}" name="title" placeholder="问题标题.....">
</div>
<div class="form-group">
    <label for="description">补充：</label>
    <textarea name="description" id="description" th:text="${description}" class="form-control" cols="30" rows="10"></textarea>
</div>
<div class="form-group">
    <lable for="tag">添加标签：</lable>
    <input type="text" class="form-control" th:value="${tag}" id="tag" name="tag" placeholder="输入标签，以，分割">
</div>
```

### （3）判断输入是否为空

通常在前端进行判断，但时后端也会有校验。

```java
/*PublishController.java*/
 if (title == null || title == ""){
     model.addAttribute("error","标题不能为空");
     return "publish";
 }if (description == null || description == ""){
     model.addAttribute("error","问题补充不能为空");
     return "publish";
 }if (tag == null || tag == ""){
     model.addAttribute("error","标签不能为空");
     return "publish";
 }
```

### （4）结果

![1584191452205](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584191452205.png)

## 【22】头像（Lombok支持）

### 1.修改数据库,添加avatar_url表项；修改model.User.java，声明avatarUrl，添加set()&get()

### 2.Lombok安装

[Lombok](https://projectlombok.org/)是一款为了减少无用代码而诞生的插件，可以用简单的注释完成常用的java功能。

[Lombok的基本使用、优缺点及原理](<https://blog.csdn.net/ThinkWon/article/details/101392808>)，主要缺点是只有无参数和全参数构造函数，不能重载多参数构造函数。

本项目主要是用@Data

> All together now: A shortcut for `@ToString`, `@EqualsAndHashCode`, `@Getter` on all fields, and `@Setter` on all non-final fields, and `@RequiredArgsConstructor`!

使用时，只需要在类前加上@Data注释即可使用。

但是，由于不可抗力，插件无效，这里就不使用了。

### 3.获取头像

![1584203502756](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584203502756.png)

这一条就是头像。

```java
/*AuthorizeController.java*/
/*向user中添加avatarUrl*/
user.setAvatarUrl(githubUser.getAvatar_url());
```

清除掉浏览器cookie后重新登陆，成功登录。数据库变化如下：

![1584204959918](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584204959918.png)

而<https://avatars1.githubusercontent.com/u/38072215?v=4>正好是头像。

![1584205141595](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584205141595.png)

最后，发现h2数据库显示有其他连接时，可以通过任务管理器杀死所有java进程来解决。

## 【23】首页问题列表功能

### 1.前端设计

目标网页首页：

![1584240149284](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584240149284.png)

bootstrap前端提供[媒体样式](<https://v3.bootcss.com/components/#media>)以及[图片形式](<https://v3.bootcss.com/css/#images>)。再稍微设定一下css可以得到：

![1584241155692](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584241155692.png)

html如下：

```html
<div class="row">
    <!--左边，占9-->
    <div class="col-lg-9 col-mid-12 col-sm-12 col-xs-12">
        <h2><span class="glyphicon glyphicon-th-list" aria-hidden="true"></span>发现</h2>
        <hr>
        <div class="media">
            <div class="media-left">
                <a href="#">
                    <img class=" img-rounded media-object" src="https://avatars1.githubusercontent.com/u/38072215?v=4" >
                </a>
            </div>
            <div class="media-body">
                <h4 class="media-heading">怎么快速学会springboot开发</h4>
                点击进入<br>
                <span class="text-desc">• 18 个评论 • 7846 次浏览 • 2019-12-06 21:10</span>
            </div>
        </div>
    </div>
    <!--右边，占3-->
    <div class="col-lg-3 col-mid-12 col-sm-12 col-xs-12">
        <h3>热门话题</h3>
    </div>
</div>
```

### 2.前后端交互----service层

注意，目前为止还是直接在Controller写业务逻辑。先按照下面的逻辑进行

![1584255579003](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584255579003.png)

```java
/*IndexController.java*/
@Resource
private QuestionMapper questionMapper;

/*index方法*/
model.addAttribute("question",questionList);
```

你会发现Question类中并没有定义avatartUrl，User中定义了。怎样写才能将User中的信息和Question中的信息整合起来返回到Model中呢？

注意：添加外键会影响性能，一般的公司业务都不会这样写查询外键。

新建一个service.package，也就是spring中大名鼎鼎的中间层解决这个问题。QuestionService可以使用所有mapper层的类及其方法，更加适合写web需要的逻辑组件。

![1584256315197](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584256315197.png)

### 3.前后端交互---操作

创建QuestionService.java

```java
/*QuestionService.java*/
//方法：以列表形式返回所有的QuestionDTO(Question + User)
public List<QuestionDTO> list() {
    List<Question> questions = questionMapper.list();
    List<QuestionDTO> questionDTOList = new ArrayList<>();
    for (Question question : questions) {
       User user =  userMapper.findByID(question.getCreator());
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);  //将前一个对象的属性拷贝到第二个对象中
        questionDTO.setUser(user);
        questionDTOList.add(questionDTO);
    }
    return questionDTOList;
}
```

IndexController.java也要更改：

```java
List<QuestionDTO> questionList = questionService.list();
model.addAttribute("question",questionList);
```

不妨使用debug检测一下IndexController.java

![1584248795592](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584248795592.png)

左边是栈，右边是各个变量数值，可以看到questionlist中有8个元素，打开其中一个：

![1584248984110](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584248984110.png)

说明是有效的。

这样就实现了返回所有需要的信息。而首页中需要所有的问题都显示，需要在前端写一个循环模板，见[这里](<https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#iteration>)。

```html
<!--index.html-->
<div class="media" th:each="question:${questions}">
    <div class="media-left">
        <a href="#">
            <img class=" img-rounded media-object"
                 th:src="${question.user.avatarUrl}">
        </a>
    </div>
    <div class="media-body">
        <h4 class="media-heading" th:text="${question.title}"></h4>
        <span th:text="${question.description}"></span><br>
        <span class="text-desc">
            <span th:text="${question.commentCount}"></span>
            <span>个回复</span>
            <span th:text="${question.viewCount}"></span>
            <span>次浏览</span>
            • 一小时前
        </span>
    </div>
</div>
```

### 3.debug

写好之后，发现用户图片没有显示

![1584253067763](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584253067763.png)

在QuestionService.java中debug一下，发现有驼峰命名的都为null；

![1584252814816](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584252814816.png)

可能是数据库中的avatar_url没有映射到avatarUrl，这时在[mabatis官网](<https://mybatis.org/mybatis-3/configuration.html>)中查询驼峰（camel）

![1584253582990](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584253582990.png)

在propertise中配置一下

```xml
mybatis.configuration.map-underscore-to-camel-case=true 
```

发现仍然没有user头像，继续debug排查问题

![1584253713436](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584253713436.png)

发现除了avatar都不为null，考虑是不是以前的测试登陆没有avatar_url，手动将其填满

![1584253923888](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584253923888.png)

有了

![1584254029738](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584254029738.png)

使用thymeleaf的format，将时间转换为日、月、年格式。这里的时间格式可以用SQL的time format更改。

```html
<!--添加时间-->
<span th:text="${#dates.format(question.gmtCreate,'yyyy-MM-dd HH:mm')}"></span>
```

成功：

![1584255266289](C:\Users\Admond Guo\AppData\Roaming\Typora\typora-user-images\1584255266289.png)

## 【25】自动部署

应用[Developer Tools](<https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/reference/html/using-spring-boot.html#using-boot-devtools>)实现快速加载、自动重启。原理是同时有两个class存储信息，每当有更改就部署新的抛弃旧的。

还需要(1)通过maven下载后，勾选setting->compiler->build project automaticlly （2）shift +alt+ctrl +?打开idea配置，勾选compiler.automake.allow.when.app.running

spring还可以集成[LiveReload](<https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/reference/html/using-spring-boot.html#using-boot-devtools-livereload>)，这是一款浏览器插件，可以在[官网](<http://livereload.com/extensions/>)下载。它可以帮助自动刷新浏览器页面。

## 【26】分页

### 1.原理

前后端共同实现：数据库每次查表取出固定个数的条目，前端加以显示，并且显示出还有多少页。

[数据库](<https://www.runoob.com/mysql/mysql-select-query.html>)语句：

```mysql
SELECT column_name,column_name
FROM table_name
[WHERE Clause]
[LIMIT N][ OFFSET M]
```

- 你可以使用 LIMIT 属性来设定返回的记录数。

- 你可以通过OFFSET指定SELECT语句开始查询的数据偏移量。默认情况下偏移量为0。

假设每一页有5条，则检索出第一页的代码为：

```mysql
select  * from QUESTION limit 0,5;
```

同理第二页、第三页....第n页的代码为：

```mysql
select  * from QUESTION limit 5,5;
select  * from QUESTION limit 10,5;
```

设页数为i，则有关系：

N = M * ( i - 1 ) ------->i=N/M向上取整

### 2.操作

```java
/*indexController.java*/
/*index方法中添加参数*/
@RequestParam(name = "page",defaultValue = "1") Integer page,
@RequestParam(name = "size",defaultValue = "5") Integer size

/*下面的方法也更改为接受page和size作为参数*/
List<QuestionDTO> questionList = questionService.list(page,size)
```

