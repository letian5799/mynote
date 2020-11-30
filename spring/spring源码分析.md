# Spring源码分析

Spring中的一些接口和类的概念

BeanDefinition 接口是spring定义的用来描述bean的

基于Annotation的源码分析

```java
public AnnotationConfigApplicationContext() {
    //创建一个读取基于注解的BeanDefinition读取器
    this.reader = new AnnotatedBeanDefinitionReader(this);
    //创建一个扫描器
    this.scanner = new ClassPathBeanDefinitionScanner(this);
}
```

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    //先调用父类的构造方法，再调用自己的空参造器方法
    this();
    //根据传入的配置类进行注册
    this.register(componentClasses);
    this.refresh();
}
```

```java
/**
*
*/
public void register(Class<?>... componentClasses) {
    Assert.notEmpty(componentClasses, "At least one component class must be specified");
    this.reader.register(componentClasses);
}
```

```java
//通过读取器去注册bean
private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name, @Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier, @Nullable BeanDefinitionCustomizer[] customizers) {
    //AnnotatedGenericBeanDefinition是BeanDefinition接口的父类，用来定义被注解的bean
    AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
    //判断类有没有被注解
    if (!this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
        abd.setInstanceSupplier(supplier);
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());
        String beanName = name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
        int var10;
        int var11;
        if (qualifiers != null) {
            Class[] var9 = qualifiers;
            var10 = qualifiers.length;

            for(var11 = 0; var11 < var10; ++var11) {
                Class<? extends Annotation> qualifier = var9[var11];
                if (Primary.class == qualifier) {
                    abd.setPrimary(true);
                } else if (Lazy.class == qualifier) {
                    abd.setLazyInit(true);
                } else {
                    abd.addQualifier(new AutowireCandidateQualifier(qualifier));
                }
            }
        }
		//判断自定义注解
        if (customizers != null) {
            BeanDefinitionCustomizer[] var13 = customizers;
            var10 = customizers.length;

            for(var11 = 0; var11 < var10; ++var11) {
                BeanDefinitionCustomizer customizer = var13[var11];
                customizer.customize(abd);
            }
        }
		//BeanDefinitionHolder是一种数据结构（Map）,它存储了类及类名。
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
        //ScopedProxyMode代理模型
        definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
        
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
    }
}
```

## Spring的拓展器 

- BeanPostProcessor	Spring框架的一个扩展器接口，通过实现该接口，可以插手bean的初始化过程，减轻beanFactory的负担。 
  - ApplicationContextAwareProcessor	当应用程序定义的Bean实现ApplicationContextAware接口时，注入ApplicationContext对象。
  -  InitDestoryAnnotationBeanPostProcessor	用来处理自定义的初始化方法和销毁方法。
  - InstantiationAwareBeanPostProcessor     可以在Bean生命周期的另外两个时期提供扩展的回调接口， 即实例化Bean之前（调用postProcessBeforeInstantiation方法）和实例化Bean之后（调用postProcessAfterInstantiation方法）。
  - CommonAnnotationBeanPostProcessor
  - AutowiredAnnotationBeanPostProcessor
  - RequiredAnnotationBeanPostProcessor
  - BeanValidationPostProcessor
  - AbstractAutoProxyCreator
- BeanFactory  PostProcessor	