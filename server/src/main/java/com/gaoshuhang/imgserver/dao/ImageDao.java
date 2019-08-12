package com.gaoshuhang.imgserver.dao;

import com.gaoshuhang.imgserver.cache.LruLinkedHashMap;
import com.gaoshuhang.imgserver.conf.ImageServerConfig;
import com.gaoshuhang.imgserver.util.HashUtil;
import com.gaoshuhang.imgserver.util.ImageScaleUtil;
import com.gaoshuhang.imgserver.util.LruCacheUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 图片读写
 *
 * @author CiyaZ
 */
public class ImageDao
{
	private static ImageDao imageDao = new ImageDao();
	private LruLinkedHashMap<String, byte[]> lruLinkedHashMap;

	private static final Logger logger = LoggerFactory.getLogger(imageDao.getClass());

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
			MessageDigest md5 = MessageDigest.getInstance("MD5");

			InputStream inputStream = part.getInputStream();
			byte[] buffer = new byte[8192];
			int n;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			while ((n = inputStream.read(buffer)) != -1)
			{
				byteArrayOutputStream.write(buffer, 0, n);
				md5.update(buffer, 0, n);
			}
			byte[] imageData = byteArrayOutputStream.toByteArray();
			String fileHash = new String(Hex.encodeHex(md5.digest()));

			String path = HashUtil.getPathFromFileHash(fileHash);
			File file = new File(path);
			if (!file.exists())
			{
				FileUtils.writeByteArrayToFile(file, imageData);
				logger.info("Image " + fileHash + " saved at " + path + ".");
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
	 * 上传
	 *
	 * @param imageData 图片数据的字节数组
	 * @return 图片MD5
	 */
	public String saveImage(byte[] imageData) throws IOException
	{
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			String fileHash = new String(Hex.encodeHex(md5.digest(imageData)));
			String path = HashUtil.getPathFromFileHash(fileHash);
			File file = new File(path);
			if (!file.exists())
			{
				FileUtils.writeByteArrayToFile(new File(path), imageData);
				logger.info("Image " + fileHash + " saved at " + path + ".");
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
	 * @param response  响应对象
	 * @param fileHash  文件MD5
	 * @param fullScale 整体缩放值
	 * @param xScale    宽缩放值
	 * @param yScale    高缩放值
	 * @return 向response写入了图片数据返回true，否则返回false
	 */
	public boolean outputImage(HttpServletResponse response, String fileHash, float fullScale, float xScale, float yScale) throws IOException
	{
		//如果启用了图片访问缓存
		if (ImageServerConfig.USE_CACHE)
		{
			if (this.lruLinkedHashMap == null)
			{
				this.lruLinkedHashMap = LruCacheUtil.getLRULinkedHashMap();
			}

			byte[] imageData = this.lruLinkedHashMap.get(fileHash);

			//缓存中没有这个图片的hash索引
			if (imageData == null)
			{
				logger.info("Image " + fileHash + " cache not exist, try to read from file system.");
				//尝试从文件系统中读取图片
				String path = HashUtil.getPathFromFileHash(fileHash);
				File imageFile = new File(path);
				//文件系统中也不存在该图片
				if (!imageFile.exists())
				{
					logger.info("Image " + fileHash + " not exist.");
					return false;
				}
				//文件系统中存在该图片
				else
				{
					imageData = FileUtils.readFileToByteArray(imageFile);
					this.lruLinkedHashMap.put(fileHash, imageData);
					writeImageDataToResponse(response, imageData, fullScale, xScale, yScale);
					logger.info("Image " + fileHash + " served from file system.");
					return true;
				}
			}
			//缓存中存在该图片
			else
			{
				writeImageDataToResponse(response, imageData, fullScale, xScale, yScale);
				logger.info("Image " + fileHash + " served from cache.");
				return true;
			}
		}
		// 不启用缓存
		else
		{
			String path = HashUtil.getPathFromFileHash(fileHash);
			File imageFile = new File(path);
			if (!imageFile.exists())
			{
				// 文件系统中不存在该图片
				logger.info("Image " + fileHash + " not exist.");
				return false;
			}
			else
			{
				// 读取文件
				byte[] imageData = FileUtils.readFileToByteArray(imageFile);
				writeImageDataToResponse(response, imageData, fullScale, xScale, yScale);
				logger.info("Image " + fileHash + " served from file system.");
				return true;
			}
		}
	}

	private void writeImageDataToResponse(HttpServletResponse response, byte[] imageData, float fullScale, float xScale, float yScale) throws IOException
	{
		OutputStream outputStream = response.getOutputStream();

		imageData = ImageScaleUtil.scaleImage(imageData, fullScale, xScale, yScale);
		outputStream.write(imageData);
	}
}
