package com.gaoshuhang.imgserver.util;

import com.gaoshuhang.imgserver.cache.LruLinkedHashMap;
import com.gaoshuhang.imgserver.conf.ImageServerConfig;

/**
 * 缓存处理工具类
 *
 * @author CiyaZ
 */
public class LruCacheUtil
{
	private static LruLinkedHashMap<String, byte[]> lruLinkedHashMap;

	public static LruLinkedHashMap<String, byte[]> getLRULinkedHashMap()
	{
		if (LruCacheUtil.lruLinkedHashMap == null)
		{
			LruCacheUtil.lruLinkedHashMap = new LruLinkedHashMap<>(ImageServerConfig.CACHE_SIZE);
		}
		return LruCacheUtil.lruLinkedHashMap;
	}
}
