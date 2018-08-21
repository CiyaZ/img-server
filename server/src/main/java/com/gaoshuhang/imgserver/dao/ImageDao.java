package com.gaoshuhang.imgserver.dao;

import com.gaoshuhang.imgserver.cache.LRULinkedHashMap;
import com.gaoshuhang.imgserver.conf.ImageServerConfig;
import com.gaoshuhang.imgserver.util.HashUtil;
import com.gaoshuhang.imgserver.util.ImageScaleUtil;
import com.gaoshuhang.imgserver.util.LRUCacheUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageDao
{
	private static ImageDao imageDao = new ImageDao();
	private LRULinkedHashMap<String, byte[]> lruLinkedHashMap;

	private ImageDao()
	{
	}

	public static ImageDao getInstance()
	{
		return imageDao;
	}

	/**
	 * 上传（和缓存无关），文件不存在动态计算文件的散列值并写入磁盘返回MD5，存在直接返回MD5
	 *
	 * @param part 上传的文件
	 * @return 文件MD5
	 */
	public String saveImage(Part part) throws IOException
	{
		try
		{
			MessageDigest MD5 = MessageDigest.getInstance("MD5");

			InputStream inputStream = part.getInputStream();
			byte[] buffer = new byte[8192];
			int n;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			while ((n = inputStream.read(buffer)) != -1)
			{
				byteArrayOutputStream.write(buffer, 0, n);
				MD5.update(buffer, 0, n);
			}
			byte[] imageData = byteArrayOutputStream.toByteArray();
			String fileHash = new String(Hex.encodeHex(MD5.digest()));

			String path = HashUtil.getPathFromFileHash(fileHash);
			File file = new File(path);
			if (!file.exists())
			{
				FileUtils.writeByteArrayToFile(file, imageData);
			}
			return fileHash;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 上传，适用于小文件，全部数据都在内存byte[]中
	 *
	 * @param imageData 图片数据的字节数组
	 * @return 图片MD5
	 */
	public String saveImage(byte[] imageData) throws IOException
	{
		try
		{
			MessageDigest MD5 = MessageDigest.getInstance("MD5");
			String fileHash = new String(Hex.encodeHex(MD5.digest(imageData)));
			String path = HashUtil.getPathFromFileHash(fileHash);
			File file = new File(path);
			if (!file.exists())
			{
				FileUtils.writeByteArrayToFile(new File(path), imageData);
			}
			return fileHash;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 下载，如果缓存里有读缓存，没有不读缓存读文件并写入缓存，如果该文件不存在不会向response写入数据
	 *
	 * @param response 响应对象
	 * @param fileHash 文件MD5
	 * @return 向response写入了图片数据返回true，否则返回false
	 */
	public boolean outputImage(HttpServletResponse response, String fileHash, float scale) throws IOException
	{
		if (this.lruLinkedHashMap == null)
			this.lruLinkedHashMap = LRUCacheUtil.getLRULinkedHashMap();

		byte[] imageData = this.lruLinkedHashMap.get(fileHash);

		if (ImageServerConfig.USE_CACHE)//如果启用了图片访问缓存
		{

			if (imageData == null)//缓存中没有这个图片的hash索引
			{
				String path = HashUtil.getPathFromFileHash(fileHash);//尝试从文件系统中读取图片
				File imageFile = new File(path);
				if (!imageFile.exists())//文件系统中也不存在该图片
				{
					return false;
				}
				else//文件系统中存在该图片
				{
					imageData = FileUtils.readFileToByteArray(imageFile);
					this.lruLinkedHashMap.put(fileHash, imageData);
					writeImageDataToResponse(response, imageData, scale);
					return true;
				}
			}
			else//缓存中存在该图片
			{
				writeImageDataToResponse(response, imageData, scale);
				return true;
			}
		}
		else
		{
			String path = HashUtil.getPathFromFileHash(fileHash);
			File imageFile = new File(path);
			if (!imageFile.exists())
			{
				return false;
			}
			else
			{
				writeImageDataToResponse(response, imageData, scale);
				return true;
			}
		}
	}

	private void writeImageDataToResponse(HttpServletResponse response,byte[] imageData, float scale) throws IOException
	{
		OutputStream outputStream = response.getOutputStream();
		if (scale != 1f)
			imageData = ImageScaleUtil.scaleImage(imageData, scale);
		outputStream.write(imageData);
	}
}
