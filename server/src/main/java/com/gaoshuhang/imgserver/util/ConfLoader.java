package com.gaoshuhang.imgserver.util;

import com.gaoshuhang.imgserver.conf.ImageServerConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * 设置加载工具
 *
 * @author CiyaZ
 */
public class ConfLoader
{
	public static void loadConfig()
	{
		InputStream inputStream = null;
		try
		{
			Properties properties = new Properties();
			inputStream = ConfLoader.class.getClassLoader().getResourceAsStream("imgserver.properties");
			properties.load(inputStream);

			ImageServerConfig.BASE_PATH = properties.getProperty("base_path");
			String tokens = properties.getProperty("token");
			if (tokens == null)
			{
				throw new RuntimeException("配置文件中缺少必要的配置项目");
			}
			String[] tokensArray = tokens.split(",");
			ImageServerConfig.TOKEN.addAll(Arrays.asList(tokensArray));

			if (ImageServerConfig.BASE_PATH == null)
			{
				throw new RuntimeException("配置文件中缺少必要的配置项目");
			}

			String uploadCheckStr = properties.getProperty("upload_check");
			if (uploadCheckStr != null)
			{
				ImageServerConfig.UPLOAD_CHECK = Boolean.parseBoolean(uploadCheckStr);
			}
			String downloadCheckStr = properties.getProperty("download_check");
			if (downloadCheckStr != null)
			{
				ImageServerConfig.DOWNLOAD_CHECK = Boolean.parseBoolean(downloadCheckStr);
			}
			String useCacheStr = properties.getProperty("use_cache");
			if (useCacheStr != null)
			{
				ImageServerConfig.USE_CACHE = Boolean.parseBoolean(useCacheStr);
			}
			String cacheSizeStr = properties.getProperty("cache_size");
			if (cacheSizeStr != null)
			{
				ImageServerConfig.CACHE_SIZE = Integer.parseInt(cacheSizeStr);
			}
			String maxScaleStr = properties.getProperty("max_scale");
			if (maxScaleStr != null)
			{
				ImageServerConfig.MAX_SCALE = Float.parseFloat(maxScaleStr);
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException("找不到配置文件");
		}
		finally
		{
			try
			{
				if (inputStream != null)
				{
					inputStream.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}
}
