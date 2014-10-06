restful-java
============

restful annotation for javaEE

CREATE RESTFUL STYLE WEB FOR JAVAEE

使用方法:
step 1. 在web.xml中添加rest过滤器，如下所示
    <filter>
        <filter-name>rest-core</filter-name>
        <filter-class>com.david.web.rest.filter.RestFilter</filter-class>
        <init-param>
			<param-name>controllers</param-name>
			<param-value>com.david.sae.hello.controller</param-value>
		</init-param>
		<init-param>
			<param-name>interceptors</param-name>
			<param-value>com.david.sae.hello.interceptor</param-value>
		</init-param>
    </filter>
    <filter-mapping>
        <filter-name>rest-core</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
step 2. 定义自己的controller
	controller为POJO类
	
	 * Class上有的注解为Path【*】
	 * 类中方法上的注解为：
	 * Path
	 * RequestMethod【*默认为GET】
	 * ResponseType[*默认为JSON]
	 
	 * 参数中的注解可有
	 * DefaultValue
	 * PathParam
	 * RequestParam
	 * RequestJson
	 * CookieValue
	 以及可传HttpServletRequest,HttpServletReponse,HttpSession参数
step 3. 自定义自己的过滤器【可选步骤】
通过实现RequestInterceptor接口，return false表示不进行下一步，return true表示进行下一步
类上通过添加RestInterceptor注解以排序此过滤器
