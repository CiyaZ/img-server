package com.gaoshuhang.imgserver.util;

import com.gaoshuhang.imgserver.conf.ImageServerConfig;
import org.apache.commons.codec.digest.DigestUtils;


public class HashUtil
{
	public static String getPathFromFileHash(String fileHash)
	{
		String firstDirectory = DigestUtils.md5Hex(fileHash.substring(0, 3));
		String secondDirectory = DigestUtils.md5Hex(fileHash.substring(3, 6));

		return ImageServerConfig.BASE_PATH + "/" + firstDirectory + "/" + secondDirectory + "/" + fileHash;
	}
}
