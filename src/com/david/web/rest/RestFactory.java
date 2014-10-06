/**
 * 
 */
package com.david.web.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.david.web.rest.annotation.Path;
import com.david.web.rest.annotation.RequestMethod;

/**
 * @author david
 */
public class RestFactory {
	
	private static final Logger logger = Logger.getLogger("RestFactory");
	
	/**
	 * store class to object map
	 */
	private Map<Class,Object> objectCacheMap = new HashMap<Class,Object>();
	
	/***
	 * a handler list for http get method
	 * */
	private List<UriHandlerMetaData> getHandlers = new ArrayList<UriHandlerMetaData>();
	
	/***
	 * a handler list for http post method
	 * */
	private List<UriHandlerMetaData> postHandlers = new ArrayList<UriHandlerMetaData>();
	
	/***
	 * a handler list for http put method
	 * */
	private List<UriHandlerMetaData> putHandlers = new ArrayList<UriHandlerMetaData>();
	
	/***
	 * a handler list for http delete method
	 * */
	private List<UriHandlerMetaData> deleteHandlers = new ArrayList<UriHandlerMetaData>();
	
	/***
	 * a handler list for http options method
	 * */
	private List<UriHandlerMetaData> optionsHandlers = new ArrayList<UriHandlerMetaData>();
	/***
	 * a handler list for http head method
	 * */
	private List<UriHandlerMetaData> headHandlers = new ArrayList<UriHandlerMetaData>();
	
	private Set<InterceptorHandlerMetaData> interceptorHandlers = new TreeSet<InterceptorHandlerMetaData>();
	
	/**
	 * register Class of Handlers
	 * @param clazz
	 */
	public <T> void register(Class<T> clazz){
		//logger.info("try to register class " + clazz);
		String pathPrefix = "";
		//if class with on @Path annotation, consider it's empty string
		if(clazz.isAnnotationPresent(Path.class)){
			logger.warning(clazz + " should have Path annotation!");
			pathPrefix = clazz.getAnnotation(Path.class).value();
		}
		//iterator the method
		for(Method method : clazz.getDeclaredMethods()){
			//如果此方法没有Path注解则证明此方法不是用来处理请求的
			if(!method.isAnnotationPresent(Path.class)) continue;
			Path path = method.getAnnotation(Path.class);
			UriHandlerMetaData metaData = new UriHandlerMetaData();
			metaData.setClazz(clazz);
			metaData.setMethod(method);
			metaData.setPath(String.format("%s%s", pathPrefix,path.value()).replaceAll("//", "/").replaceAll("/$",""));
			//
			String reqMethod = RequestMethod.GET;
			//if no @RequestMethod, consider it's HTTP GET
			if(method.isAnnotationPresent(RequestMethod.class)){
				reqMethod = method.getAnnotation(RequestMethod.class).value();
			}
			if(reqMethod.equalsIgnoreCase(RequestMethod.GET)){
				getHandlers.add(metaData);
			}else if(reqMethod.equalsIgnoreCase(RequestMethod.POST)){
				postHandlers.add(metaData);
			}else if(reqMethod.equalsIgnoreCase(RequestMethod.PUT)){
				putHandlers.add(metaData);
			}else if(reqMethod.equalsIgnoreCase(RequestMethod.DELETE)){
				deleteHandlers.add(metaData);
			}else if(reqMethod.equalsIgnoreCase(RequestMethod.OPTIONS)){
				optionsHandlers.add(metaData);
			}else if(reqMethod.equalsIgnoreCase(RequestMethod.HEAD)){
				headHandlers.add(metaData);
			}else{
				logger.log(Level.SEVERE, String.format("%s RequestMethod annotation error!",clazz));
			}
			
		}
		if(!objectCacheMap.containsKey(clazz)){
			try{
				objectCacheMap.put(clazz, clazz.newInstance());
			}catch(Exception ex){logger.info(clazz+ " new Instance Error!");}
		}
		//logger.info("register class " + clazz + " success!");
	}
	/**
	 * get handlers array list by http request method
	 * @param method
	 * @return
	 */
	public List<UriHandlerMetaData> getHandlers(String method){
		if(method.equalsIgnoreCase(RequestMethod.GET)){
			return getHandlers;
		}else if(method.equalsIgnoreCase(RequestMethod.POST)){
			return postHandlers;
		}else if(method.equalsIgnoreCase(RequestMethod.PUT)){
			return putHandlers;
		}else if(method.equalsIgnoreCase(RequestMethod.DELETE)){
			return deleteHandlers;
		}else if(method.equalsIgnoreCase(RequestMethod.OPTIONS)){
			return optionsHandlers;
		}else if(method.equalsIgnoreCase(RequestMethod.HEAD)){
			return headHandlers;
		}else{
			logger.log(Level.WARNING, String.format("Handler Collections Unfound with method : %s",method));
			return Collections.emptyList();
		}
	}
	/**
	 * 
	 * @param clazz
	 * @return
	 */
	public Object getObject(Class clazz){
		
		return objectCacheMap.get(clazz);
		
	}
	
	/**
	 * 注册过滤器
	 * @param clazz
	 */
	public void registeInterceptor(Class<? extends RequestInterceptor> clazz){
		interceptorHandlers.add(InterceptorHandlerMetaData.newInstance(clazz));
	}
	
	public Set<InterceptorHandlerMetaData> getInterceptorHandlers() {
		return interceptorHandlers;
	}
	
}
