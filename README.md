# idempotent-spring-boot-starter

接口或方法防重复提交工具

## 1.项目配置

### 1.1.引入依赖

```xml
<dependency>
    <groupId>com.guazi</groupId>
    <artifactId>idempotent-spring-boot-starter</artifactId>
    <version>LATEST</version>
</dependency>
```

### 1.2.开启自动装配

- 在任意配置类上增加注解@EnableIdempotent，即可开启自动配置

- @EnableIdempotent注解中**Adivce目前仅支持Proxy(动态代理)模式**, Aspectj模式下配置为空

- 可以通过order指定切面优先级

```java
@Configuration
@EnableIdempotent
public class SpringConfig {}
```

### 1.3.初始化IdempotentManager

- 工具需要配合IdempotentManager使用，项目里内置了两种类型的IdempotentManager，SimpleIdempotentManager和RedisIdempotentManager，只建议使用RedisIdempotentManager

- IdempotentManager是一个接口，可以根据自己的需求定制，只需实现它的execute方法

- IdempotentManager的使用很简单，仅需在配置类中将它初始化为一个Bean，并设置一个作用时间expire(非必须，但建议设置)

- 在一个项目中可以设置不同的IdempotentManager，通过name属性引用不同的IdempotentManager

```java
@Configuration
@EnableIdempotent
public class SpringConfig {
    
    /**
     * 这里以RedisIdempotentManager为例介绍配置方法，RedisIdempotentManager需要RedisConnectionFactory
     */
    @Bean
    public IdempotentManager idempotentManager(RedisConnectionFactory redisConnectionFactory) {
        AbstractTimeBasedManager manager = new RedisIdempotentManager(redisConnectionFactory);
        // 设置作用时间，不设置为永久生效
        manager.setExpires(Duration.ofMinutes(1));
        return manager;
    }

}
```

## 2.项目使用

### 2.1 @Idempotent注解

方法中使用@Idempotent注解即可完成对方法提交的限制，该注解的属性如下

- key : 唯一约束，必填属性，使用SpEL表达式动态取值

- condition ：触发条件，非必填属性，使用SpEL表达式动态计算一个boolean值

- manager ：使用哪个管理器，非必填属性，不同的方法可以使用不同的管理器

- fallback : 熔断接口，在该接口中指定一个IdempotentFallBack接口的实现类，在出错或者失败时会回调相应的方法，接口泛型需与方法返回值保持一致

```java
@Data
public class Model{
    private String id;
    private int version;
}

public class Test{
    
    @Idempotent(key = "#model.id", condition = "#model.version == 2")   
    public Model test(Model model) {
        return null;
    }
}
```

**在Proxy模式下，被注解修饰的方法不能为final和static方法，并且保证方法被外部调用**

### 2.2 SpEL表达式

> 给个链接自己看吧 http://itmyhome.com/spring/expressions.html

