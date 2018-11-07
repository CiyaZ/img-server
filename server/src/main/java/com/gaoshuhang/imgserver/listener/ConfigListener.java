package com.gaoshuhang.imgserver.listener;

import com.gaoshuhang.imgserver.util.ConfLoader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author CiyaZ
 */
@WebListener()
public class ConfigListener implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent)
	{
		ConfLoader.loadConfig();
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
	}
}
