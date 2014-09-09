/**
 * 
 */
package com.david.web.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * response type annotation
 * @date 2014-8-12 14:49:13
 * @author david
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseType {
	/**application json*/
	public static final String JSON = "json";
	/**text plain*/
	public static final String TEXT = "text";
	/**forward dispatcher to jsp file*/
	public static final String JSP = "jsp";
	/**send redirect*/
	public static final String REDIRECT = "redirect";
	
	String value();
}
