package com.gaoshuhang.imgserver.conf;

import java.util.HashSet;
import java.util.Set;

/**
 * 配置信息类
 *
 * @author CiyaZ
 */
public class ImageServerConfig
{
	public static String BASE_PATH;
	public static Set<String> TOKEN = new HashSet<>();
	public static boolean UPLOAD_CHECK = true;
	public static boolean DOWNLOAD_CHECK = false;
	public static boolean USE_CACHE = true;
	public static int CACHE_SIZE = 100;
	public static float MAX_SCALE = 5;
}
