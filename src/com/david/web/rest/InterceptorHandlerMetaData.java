/**
 * 
 */
package com.david.web.rest;

import com.david.web.rest.annotation.RestInterceptor;

/**
 * 过滤器类上的注解为RestInterceptor
 * @author david
 */
public class InterceptorHandlerMetaData implements Comparable<InterceptorHandlerMetaData>{
	/**目录过滤器类*/
	private Class<? extends RequestInterceptor> clazz;
	
	private RequestInterceptor entity;//instance
	
	/**排序*/
	private Integer index;
	
	/**
	 * new instance
	 * @param clazz
	 * @return
	 */
	public static InterceptorHandlerMetaData newInstance(Class<? extends RequestInterceptor> clazz){
		int index = Short.MAX_VALUE;
		if(clazz.isAnnotationPresent(RestInterceptor.class)){
			index = clazz.getAnnotation(RestInterceptor.class).value();
		}
		InterceptorHandlerMetaData data = new InterceptorHandlerMetaData();
		data.setClazz(clazz);
		data.setIndex(index);
		try {
			data.setEntity(clazz.newInstance());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	public Class getClazz() {
		return clazz;
	}
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	public int getIndex() {
		return index;
	}
	public RequestInterceptor getEntity() {
		return entity;
	}
	public void setEntity(RequestInterceptor entity) {
		this.entity = entity;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	@Override
	public int compareTo(InterceptorHandlerMetaData o) {
		return index.compareTo(o.getIndex());
	}
}
