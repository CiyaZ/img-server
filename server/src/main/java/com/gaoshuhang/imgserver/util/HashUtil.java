package com.gaoshuhang.imgserver.util;

import com.gaoshuhang.imgserver.conf.ImageServerConfig;
import org.apache.commons.codec.digest.DigestUtils;


/**
 * @author CiyaZ
 */
public class HashUtil
{
	/**
	 * 从filehash参数获取图片的绝对路径
	 *
	 * @param fileHash 下载请求传来的参数
	 * @return 绝对路径字符串
	 */
	public static String getPathFromFileHash(String fileHash)
	{
		String firstDirectory = DigestUtils.md5Hex(fileHash.substring(0, 3));
		String secondDirectory = DigestUtils.md5Hex(fileHash.substring(3, 6));

		return ImageServerConfig.BASE_PATH + "/" + firstDirectory + "/" + secondDirectory + "/" + fileHash;
	}
}
