/**
 * 
 */
package com.david.web.rest.filter;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.david.web.rest.RequestInterceptor;
import com.david.web.rest.RestService;
import com.david.web.rest.UriHandlerMetaData;
import com.david.web.rest.util.PackageUtil;
import com.david.web.rest.util.RestUtil;
import com.david.web.rest.util.TextUtils;

/**
 * 功能类型于RestServlet
 * 但提供的不是基于Servlet的映射
 * 而是通过基于过滤器的配置
 * @author David
 */
public class RestFilter implements Filter {

	private static final Logger logger = Logger.getLogger("RestFilter");
	
	private FilterConfig filterConfig;
	
	public static ServletContext context;
	
	private static final Pattern pattern = Pattern.compile("\\.(js|css|png|gif|html|jpg|jpeg|xml)$", Pattern.CASE_INSENSITIVE);
	
	@Override
	public void destroy() {
		context = null;
	}

	@Override
	public void doFilter(ServletRequest req1, ServletResponse resp1,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest)req1;
		HttpServletResponse resp = (HttpServletResponse)resp1;
		
		String uri = req.getRequestURI().replaceAll(";jsessionid=.*?(?=\\?|$)", "");
		logger.info(String.format("doFilter uri-> %s", uri));
		
		if(pattern.matcher(uri).find()){//表示访问的是静态资源
			chain.doFilter(req, resp);
			return;
		}
		
		String contextPath = filterConfig.getServletContext().getContextPath();
		if(contextPath.length() > 1 && uri.length() > contextPath.length()){
			uri = uri.substring(contextPath.length());
		}
		//去除最后一个斜杠
		if(uri.length() > 1 && uri.lastIndexOf("/") == uri.length()-1){
			uri = uri.substring(0, uri.length()-1);
		}

		List<UriHandlerMetaData> handlers = RestService.factory().getHandlers(req.getMethod());
		UriHandlerMetaData handler = RestUtil.getHandler(handlers, uri);
		if(handler == null){
			//chain it
			chain.doFilter(req1, resp1);
		}else{
			RestUtil.handleRequest(null, req, resp, uri, handler);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String controllerPackageName = config.getInitParameter("controllers");
		logger.info("RestServlet init......."+controllerPackageName);
		if(!TextUtils.isNull(controllerPackageName))
			for(Class clazz : PackageUtil.scanPackage(controllerPackageName)){
				RestService.register(clazz);
			}
		String interceptorPackageName = config.getInitParameter("interceptors");
		if(!TextUtils.isNull(interceptorPackageName))
			for(Class clazz : PackageUtil.scanPackage(interceptorPackageName)){
				if(isRequestInterceptor(clazz))
					RestService.registeInterceptor(clazz);
			}
		filterConfig = config;
		context = config.getServletContext();
	}
	/**
	 * judge if a class implements clazz
	 * @param clazz
	 * @return
	 */
	protected static boolean isRequestInterceptor(Class clazz){
		for(Class is : clazz.getInterfaces()){
			if(is.toString().equals(RequestInterceptor.class.toString()))
				return true;
		}
		
		return false;
	}

}
