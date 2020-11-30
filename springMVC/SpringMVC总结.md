# SpringMVC 总结

## Spring MVC 参数传递

### 普通表单参数传递

前端页面的表单提交的参数名如果和后台定义的参数名一致，那么可以直接获取。如果前端参数格式与后台不一致可以使用`@RequestParam(“role_name” ) `做映射处理。使用该注解后，不允许该参数为空，如果想设置为允许为空，则需要添加配置项，如`@RequestParam(value=“role_name”，required=false ) `

如果参数过多，则可以封装一个 Pojo 类来管理参数，如果名称一致，则可以直接用对象接受这些参数，如`public ModelAndView getRole(RoleParams roleParams)`，RoleParams 是一个Pojo类，其属性需与前端参数名称一致。

### Restful 参数传递

Spring MVC 提供注解**@PathVariable **接受url参数。

```java
@RequestMapping(”/getRole/{id}”)
public  ModelAndViewpathVariable( @PathVariable(”id”)  Long id)  {
    
}
```

**注意：@PathVariable允许对应的参数为空**

### Json 参数传递

Spring MVC 使用 @RequestBody 注解接收json数据，并会做类型转换，我们可以用对象接受，举个栗子

```javascript
//前端请求
$.post({
	url:"./findRoles.do",
    //必须指定参数类型为json
	contentType:"application/json",
    //数据需要转换为json格式的字符串进行传递
	data:JSON.stringify({
		roleName:'role',
		note:'note',
		pageParams:{
			start:1,
			limit:10
		}
	}),
	success:function(data){
		console.log(data);
	}
})
```

```java
//后台接收
@RequestMapping (”/findRoles”)
public ModelAndView findRoles(@RequestBody roleParams roleParams){
    List<Role>roleList  =  roleService .f indRoles(roleParams) ;
	ModelAndView mv =new  ModelAndView();
    //绑定模型
    mv.addObject(roleList) ;
    m v.setView();
}
```

