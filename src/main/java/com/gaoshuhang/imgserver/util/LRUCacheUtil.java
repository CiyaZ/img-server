package com.gaoshuhang.imgserver.util;

import com.gaoshuhang.imgserver.cache.LRULinkedHashMap;
import com.gaoshuhang.imgserver.conf.ImageServerConfig;

public class LRUCacheUtil
{
	private static LRULinkedHashMap<String, byte[]> lruLinkedHashMap;

	public static LRULinkedHashMap<String, byte[]> getLRULinkedHashMap()
	{
		if(LRUCacheUtil.lruLinkedHashMap == null)
		{
			LRUCacheUtil.lruLinkedHashMap = new LRULinkedHashMap<>(ImageServerConfig.CACHE_SIZE);
		}
		return LRUCacheUtil.lruLinkedHashMap;
	}
}
