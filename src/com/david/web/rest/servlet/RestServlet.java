/**
 * 
 */
package com.david.web.rest.servlet;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.david.web.rest.InterceptorHandlerMetaData;
import com.david.web.rest.RestService;
import com.david.web.rest.UriHandlerMetaData;
import com.david.web.rest.annotation.CookieValue;
import com.david.web.rest.annotation.DefaultValue;
import com.david.web.rest.annotation.PathParam;
import com.david.web.rest.annotation.RequestHeader;
import com.david.web.rest.annotation.RequestJson;
import com.david.web.rest.annotation.RequestParam;
import com.david.web.rest.annotation.ResponseType;
import com.david.web.rest.util.PackageUtil;
import com.david.web.rest.util.RestUtil;
import com.david.web.rest.util.TextUtils;

/**
 * @author david
 */
public class RestServlet extends HttpServlet {
	private static final long serialVersionUID = 6181298780133095607L;
	private static final Logger logger = Logger.getLogger("RestServlet");
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String controllerPackageName = config.getInitParameter("controllers");
		logger.info("RestServlet init......."+controllerPackageName);
		if(!TextUtils.isNull(controllerPackageName))
			for(Class clazz : PackageUtil.scanPackage(controllerPackageName)){
				RestService.register(clazz);
			}
		String interceptorPackageName = config.getInitParameter("interceptors");
		if(!TextUtils.isNull(interceptorPackageName))
			for(Class clazz : PackageUtil.scanPackage(interceptorPackageName)){
				if(RestUtil.isRequestInterceptor(clazz))
					RestService.registeInterceptor(clazz);
			}
	}

	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uri = req.getRequestURI();
		logger.info(String.format("uri-> %s", uri));
		String contextPath = getServletContext().getContextPath();
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
			resp.setStatus(404);
			resp.getWriter().println(uri+" Unknown Uri Location! (Powered By D.W. RESTFul)");
		}else{
			RestUtil.handleRequest(this, req, resp, uri, handler);
		}

	}
}
