/**
 * 
 */
package com.david.web.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.david.web.rest.annotation.CookieValue;
import com.david.web.rest.annotation.DefaultValue;
import com.david.web.rest.annotation.PathParam;
import com.david.web.rest.annotation.RequestJson;
import com.david.web.rest.annotation.RequestParam;

/**
 * Class上有的注解为Path
 * <pre>
 * 类中方法上的注解为：
 * Path
 * RequestMethod
 * ResponseType[*]
 * </pre>
 * <pre>
 * 参数中的注解可有
 * DefaultValue
 * PathParam
 * RequestParam
 * RequestJson
 * CookieValue
 * </pre>
 * @author david
 */
public class UriHandlerMetaData {
	/**
	 * 用户定义的path /user/{username}/{id}等
	 */
	private String path;
	private Class clazz;
	private Method method;
	private Annotation[] methodAnnotations;
	private Annotation[][] parameterAnnotations;
	private Class<?>[] parameterTypes;
	
	/**
	 * 判断是否使用正则表达式
	 * @return
	 */
	public boolean isUseRegexp(){
		return path.matches(".*\\{\\w+\\}.*");
	}
	/**
	 * 判断当前的path是否要作用于uri
	 * @param uri
	 * @return
	 */
	public boolean matchUri(String uri){
		return uri.equals(path) || (isUseRegexp() && uri.matches(path.replaceAll("\\{\\w+\\}", "(\\\\w+)")));
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		if(path.length() > 1 && path.lastIndexOf("/") == path.length()-1){
			path = path.substring(0, path.length()-1);
		}
		this.path = path;
	}
	public Class getClazz() {
		return clazz;
	}
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
		methodAnnotations = method.getAnnotations();
		parameterAnnotations = method.getParameterAnnotations();
		parameterTypes = method.getParameterTypes();
	}
	public Annotation[] getMethodAnnotations() {
		return methodAnnotations;
	}
	public Annotation[][] getParameterAnnotations() {
		return parameterAnnotations;
	}
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	public void setMethodAnnotations(Annotation[] methodAnnotations) {
		this.methodAnnotations = methodAnnotations;
	}
	public void setParameterAnnotations(Annotation[][] parameterAnnotations) {
		this.parameterAnnotations = parameterAnnotations;
	}
	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	
}
