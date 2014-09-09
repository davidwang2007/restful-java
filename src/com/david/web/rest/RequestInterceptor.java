/**
 * 
 */
package com.david.web.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 过滤器接口
 * @author david
 */
public interface RequestInterceptor {
	/**
	 * 过滤请求
	 * @param handler controller处理器原数据
	 * @param req 请求
	 * @param resp 响应
	 * @return true 继续下一过滤 false终止当前过滤【这种情况下时使用了resp输出了响应】
	 */
	boolean intercept(UriHandlerMetaData handler,HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException  ;
}
