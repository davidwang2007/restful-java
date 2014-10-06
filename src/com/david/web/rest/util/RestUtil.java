/**
 * 
 */
package com.david.web.rest.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.david.web.rest.InterceptorHandlerMetaData;
import com.david.web.rest.RequestInterceptor;
import com.david.web.rest.RestService;
import com.david.web.rest.UriHandlerMetaData;
import com.david.web.rest.annotation.CookieValue;
import com.david.web.rest.annotation.DefaultValue;
import com.david.web.rest.annotation.PathParam;
import com.david.web.rest.annotation.RequestHeader;
import com.david.web.rest.annotation.RequestJson;
import com.david.web.rest.annotation.RequestParam;
import com.david.web.rest.annotation.ResponseType;
import com.google.gson.Gson;

/**
 * @author David
 *
 */
public class RestUtil {
	public static final Logger logger = Logger.getLogger("RestUtil");
	/**
	 * WRITE JSON TO BROWSER
	 * @param obj
	 * @param resp
	 * @throws IOException
	 */
	public static void writeJson(Object obj,HttpServletResponse resp){
		Gson gson = TextUtils.createGson();
		String line = gson.toJson(obj);
		byte[] all = new byte[0];
		try {
			all = line.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		resp.setHeader("Content-Type", "application/json; charset=utf-8");
		resp.setHeader("Content-Length", String.valueOf(all.length));

		try {
			resp.getOutputStream().write(all);
			resp.getOutputStream().flush();
			resp.getOutputStream().close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
	}
	

	/**
	 * WRITE JSON TO BROWSER
	 * @param obj
	 * @param resp
	 * @throws IOException
	 */
	public static void writeText(String text,HttpServletResponse resp){
		byte[] all = new byte[0];
		try {
			all = text.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		resp.setHeader("Content-Type", "text/plain; charset=utf-8");
		resp.setHeader("Content-Length", String.valueOf(all.length));

		try {
			resp.getOutputStream().write(all);
			resp.getOutputStream().flush();
			resp.getOutputStream().close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
	}
	/**
	 * judge if a class implements clazz
	 * @param clazz
	 * @return
	 */
	public static boolean isRequestInterceptor(Class clazz){
		for(Class is : clazz.getInterfaces()){
			if(is.toString().equals(RequestInterceptor.class.toString()))
				return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param es
	 * @return
	 */
	public static String enumToString(Enumeration<String> es){
		StringBuilder sb = new StringBuilder();
		while(es.hasMoreElements()){
			sb.append(es.nextElement()).append(",");
		}
		if(sb.length()>0){
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}

	/**
	 * parse annotation
	 * @param <T>
	 * @param anno
	 * @param clazz
	 * @return
	 */
	public static <T> T parseAnnotation(Annotation anno,Class<T> clazz){
		return (T)(anno);
	}
	/**
	 * 将JSON请求体包装为实体类
	 * @param request
	 * @param clazz
	 */
	public static <T> T readJson(HttpServletRequest request,Class<? extends T> clazz) throws IOException{
		/*
		String type = request.getHeader("Content-Type");
		BufferedReader reader = new BufferedReader(request.getReader());
		StringBuilder sb = new StringBuilder();
		String line = null;
		try{
			while((line = reader.readLine()) != null){
				sb.append(line);
			}
		}finally{
			reader.close();
		}
		 */
		return TextUtils.createGson().fromJson(request.getReader(), clazz);
	}


	/**
	 * get request headers
	 * @param req
	 * @param key
	 * @return
	 */
	public static Enumeration<String> getHeaders(HttpServletRequest req,String key){
		return req.getHeaders(key);
	}

	/**
	 * GET COOKIE VALUE
	 * @param req
	 * @param key
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest req,String key){
		Cookie[] cookies = req.getCookies();
		if(cookies == null) return null;
		for(Cookie cookie : cookies){
			if(cookie.getName().equals(key))
				return cookie.getValue();
		}
		return null;
	}


	/**
	 * 过滤出属性此uri处理器
	 * @param handlers handler collections
	 * @param uri 处理过的uri
	 * @return
	 */
	public static UriHandlerMetaData getHandler(List<UriHandlerMetaData> handlers,String uri){
		for(UriHandlerMetaData handler : handlers){
			if(handler.matchUri(uri)) return handler;
		}
		return null;
	}
	
	/**
	 * 
	 * @param servlet if is servlet,
	 * @param req HttpRequest
	 * @param resp HttpResponse 
	 * @param uri
	 * @param handler
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void handleRequest(HttpServlet servlet,HttpServletRequest req, HttpServletResponse resp,String uri,UriHandlerMetaData handler)
			throws ServletException, IOException{
		//modified by www 修改添加过滤器  started
		for(InterceptorHandlerMetaData data : RestService.factory().getInterceptorHandlers()){
			if(!data.getEntity().intercept(handler, req, resp))
				return;
		}

		//modified by www 修改添加过滤器  end

		//先解析出PathParams
		Map<String,String> pathParams = new HashMap<String,String>();
		if(handler.isUseRegexp()){//表示有路径参数
			Matcher paramsMatcher= Pattern.compile("\\{(\\w+)\\}").matcher(handler.getPath());
			Matcher valuesMatcher = Pattern.compile(handler.getPath().replaceAll("\\{\\w+\\}", "(\\\\w+)")).matcher(uri);
			if(valuesMatcher.find()){
				int index = 0;
				while(paramsMatcher.find()){
					//print(String.format("key[%s]: value[%s]",paramsMatcher.group(1),valuesMatcher.group(++index)));
					pathParams.put(paramsMatcher.group(1), valuesMatcher.group(++index));
				}
			}
		}
		//组装参数
		Class[] parameterTypes = handler.getParameterTypes();
		Object[] parameters = new Object[parameterTypes.length];
		Method method = handler.getMethod();
		for(int i = 0; i < parameterTypes.length; i++){
			Class parameterType = parameterTypes[i];
			Annotation[] parameterAnnotations = handler.getParameterAnnotations()[i];
			String paramValue = null;
			String defaultValue = null;
			boolean jsonFlag = false;
			//先分析有注解的，没有注解的参数还可有Object[用于json的],HttpServletRequest,HttpServletResponse,HttpSession,ServletContext,ServletConfig
			/**
			 *  参数中的注解可有
				 DefaultValue PathParam RequestParam RequestJson CookieValue
			 * */
			for(Annotation anno : parameterAnnotations){
				if(anno instanceof DefaultValue){
					defaultValue = parseAnnotation(anno, DefaultValue.class).value();
				}else if(anno instanceof RequestParam){
					paramValue = TextUtils.join(req.getParameterValues(parseAnnotation(anno, RequestParam.class).value()));
				}else if(anno instanceof CookieValue){
					paramValue = getCookieValue(req, parseAnnotation(anno, CookieValue.class).value());
				}else if(anno instanceof RequestHeader){
					paramValue = enumToString(getHeaders(req, parseAnnotation(anno, RequestHeader.class).value()));
				}else if(anno instanceof PathParam){
					paramValue = pathParams.get(parseAnnotation(anno, PathParam.class).value());
				}else if(anno instanceof RequestJson){
					jsonFlag = true;
					try{
						parameters[i] = readJson(req, parameterType);
					}catch(Exception ex){logger.log(Level.SEVERE, ex.getMessage(),ex);}
					break;//NOT NECESSARY
				}
			}
			if(jsonFlag) continue;//如果是JSON的话已经赋值了
			if(TextUtils.isNull(paramValue) && !TextUtils.isNull(defaultValue))
				paramValue = defaultValue;
			if(parameterType.equals(Integer.TYPE) || parameterType.equals(Integer.class)){
				parameters[i] = Integer.valueOf(paramValue);
			}else if(parameterType.equals(Long.TYPE) || parameterType.equals(Long.class)){
				parameters[i] = Long.valueOf(paramValue);
			}else if(parameterType.equals(Short.TYPE) || parameterType.equals(Short.class)){
				parameters[i] = Short.valueOf(paramValue);
			}else if(parameterType.equals(Float.TYPE)||parameterType.equals(Float.class)){
				parameters[i] = Float.valueOf(paramValue);
			}else if(parameterType.equals(Double.TYPE) || parameterType.equals(Double.class)){
				parameters[i] = Double.valueOf(paramValue);
			}else if(parameterType.equals(Boolean.TYPE) || parameterType.equals(Boolean.class)){
				parameters[i] = Boolean.valueOf(paramValue);
			}else if(parameterType.equals(Byte.TYPE) || parameterType.equals(Byte.class)){
				parameters[i] = Byte.valueOf(paramValue);
			}else if(parameterType.equals(String.class)){
				parameters[i] = paramValue;
			}else if(parameterType.equals(HttpServletRequest.class)){
				parameters[i] = req;
			}else if(parameterType.equals(HttpServletResponse.class)){
				parameters[i] = resp;
			}else if(parameterType.equals(HttpSession.class)){
				parameters[i] = req.getSession();
			}else if(parameterType.equals(ServletContext.class)){
				parameters[i] = req.getServletContext();
			}else if(parameterType.equals(ServletConfig.class)){
				parameters[i] = servlet.getServletConfig();
			}else if(parameterType.isArray()){
				String[] paramsValues = TextUtils.isNull(paramValue) ? new String[]{} : paramValue.split(",");
				Class componentType = parameterType.getComponentType();
				if(componentType.equals(String.class)){//如果是String组的话
					parameters[i] = paramsValues;
					continue;
				}else if(componentType.equals(Integer.TYPE))
					componentType = Integer.class;
				else if(componentType.equals(Long.TYPE))
					componentType = Long.class;
				else if(componentType.equals(Short.TYPE))
					componentType = Short.class;
				else if(componentType.equals(Byte.TYPE))
					componentType = Byte.class;
				else if(componentType.equals(Float.TYPE))
					componentType = Float.class;
				else if(componentType.equals(Double.TYPE))
					componentType = Double.class;
				else if(componentType.equals(Boolean.TYPE))
					componentType = Boolean.class;

				try {
					Method componentMethod = componentType.getMethod("valueOf", String.class);
					parameters[i] = Array.newInstance(parameterType.getComponentType(), paramsValues.length);
					for(int j = 0; j < paramsValues.length; j++)
						try{
							Array.set(parameters[i], j, componentMethod.invoke(componentType, paramsValues[j]));
						}catch(Exception ex){ }

				} catch (Exception e) { }


			}else{
				logger.info("Unknown Parameter Type " + parameterType +" for Method " + method);
			}

		}
		//组装好 参数数组了
		Object invokeResult = null;
		try {
			invokeResult = method.invoke(RestService.factory().getObject(handler.getClazz()), parameters);
		} catch (Exception e) {
			resp.setStatus(500);
			resp.getWriter().println("Server Error!"+e.getMessage());
		}
		if(invokeResult == null) return;
		if(method.isAnnotationPresent(ResponseType.class)){//看是否定义的输出类型，如果没有默认为JSON
			String responseType = method.getAnnotation(ResponseType.class).value();
			if(responseType.equals(ResponseType.JSON))
				writeJson(invokeResult, resp);
			else if(responseType.equals(ResponseType.TEXT))
				writeText(String.valueOf(invokeResult), resp);
			else if(responseType.equals(ResponseType.JSP))
				req.getRequestDispatcher(String.valueOf(invokeResult)).forward(req, resp);
			else if(responseType.equals(ResponseType.REDIRECT))
				resp.sendRedirect(String.valueOf(invokeResult));
			else
				logger.warning("Unknown response type: " + responseType);
		}else{
			writeJson(invokeResult, resp);
		}
	}
}
