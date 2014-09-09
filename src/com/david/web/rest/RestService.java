/**
 * 
 */
package com.david.web.rest;


/**
 * @author david
 */
public class RestService {
	/**singleton**/
	protected static RestFactory factory = new RestFactory();
	
	public static RestFactory factory(){return factory;}
	/**
	 * register handler class
	 * @param clazz
	 */
	public static void register(Class<?> clazz){factory.register(clazz);}
	
	/**
	 * 注册过滤器
	 * @param clazz
	 */
	public static void registeInterceptor(Class<? extends RequestInterceptor> clazz){
		factory.registeInterceptor(clazz);
	}
}
