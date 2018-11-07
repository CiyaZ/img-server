package com.gaoshuhang.imgserver.web.filter;

import com.gaoshuhang.imgserver.conf.ImageServerConfig;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 访问控制过滤器
 *
 * @author CiyaZ
 */
@WebFilter(filterName = "AccessFilter", urlPatterns = "/*")
public class AccessFilter implements Filter
{
	@Override
	public void destroy()
	{
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException
	{
		HttpServletRequest httpServletRequest = (HttpServletRequest) req;
		HttpServletResponse httpServletResponse = (HttpServletResponse) resp;

		if (httpServletRequest.getRequestURI().equals(httpServletRequest.getContextPath() + "/upload"))
		{
			if (ImageServerConfig.UPLOAD_CHECK)
			{
				Set<String> tokenSet = ImageServerConfig.TOKEN;
				String requestToken = httpServletRequest.getParameter("token");
				if (isStringInSet(tokenSet, requestToken))
				{
					chain.doFilter(httpServletRequest, httpServletResponse);
				}
				else
				{
					httpServletResponse.setStatus(401);
					httpServletRequest.getRequestDispatcher("WEB-INF/unauthorized.jsp").forward(httpServletRequest, httpServletResponse);
				}
			}
			else
			{
				chain.doFilter(httpServletRequest, httpServletResponse);
			}
		}
		else if (httpServletRequest.getRequestURI().equals(httpServletRequest.getContextPath() + "/download"))
		{
			if (ImageServerConfig.DOWNLOAD_CHECK)
			{
				Set<String> tokenSet = ImageServerConfig.TOKEN;
				String requestToken = httpServletRequest.getParameter("token");
				if (isStringInSet(tokenSet, requestToken))
				{
					chain.doFilter(httpServletRequest, httpServletResponse);
				}
				else
				{
					httpServletResponse.setStatus(401);
					httpServletRequest.getRequestDispatcher("WEB-INF/unauthorized.jsp").forward(httpServletRequest, httpServletResponse);
				}
			}
			else
			{
				chain.doFilter(httpServletRequest, httpServletResponse);
			}
		}
		else
		{
			httpServletResponse.setStatus(404);
			httpServletRequest.getRequestDispatcher("WEB-INF/not_found.jsp").forward(httpServletRequest, httpServletResponse);
		}
	}

	@Override
	public void init(FilterConfig config)
	{
	}

	/**
	 * 遍历判断某个字符串的值是否在Set中存在
	 *
	 * @param stringSet 包含若干字符串的集合
	 * @param str       字符串
	 * @return 存在返回true，否则返回false
	 */
	private boolean isStringInSet(Set<String> stringSet, String str)
	{
		for (String s : stringSet)
		{
			if (s.equals(str))
			{
				return true;
			}
		}
		return false;
	}
}
