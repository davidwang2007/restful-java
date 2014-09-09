/**
 * 
 */
package com.david.web.rest.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author david
 *
 */
public class TextUtils {
	
	/**
	 * judge the str is null or not
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str){
		return str == null || str.length() == 0;
	}
	/**
	 * trim the string, and then judge if it's null
	 * @param str
	 * @return
	 */
	public static boolean isNullStrict(String str){
		return str == null || str.trim().length() == 0;
	}
	/**
	 * 
	 * @param strs
	 * @return
	 */
	public static String join(String... strs){
		return join(",",strs);
	}
	/**
	 * 
	 * @param strs
	 * @return
	 */
	public static String join(String seperator,String... strs){
		if(strs == null) strs = new String[]{};
		StringBuilder sb = new StringBuilder();
		for(String s : strs){
			sb.append(s).append(seperator);
		}
		if(sb.length()>0) sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	/**
	 * create default gson
	 * @return
	 */
	public static Gson createGson(){
		return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").create();
	}
}
