package com.gaoshuhang.imgserver.sdk;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageServerUtilTest
{
	private ImageServerUtil serverUtil = null;

	@Before
	public void initSDKUtil()
	{
		serverUtil = new ImageServerUtil("gLMhrQ27eaLdK8Eg", "127.0.0.1", null, 8080);
	}

	private byte[] loadImage() throws IOException
	{
		File file = new File("E:\\workspace\\github\\1.jpg");
		FileInputStream fileInputStream = new FileInputStream(file);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int n;
		while ((n = fileInputStream.read(buffer)) != -1)
		{
			byteArrayOutputStream.write(buffer, 0, n);
		}

		fileInputStream.close();
		return byteArrayOutputStream.toByteArray();
	}

	@Test
	public void testUploadImage()
	{
		try
		{
			String fileHash1 = serverUtil.uploadImage(loadImage());
			Base64 base64 = new Base64();
			String imageStr = base64.encodeToString(loadImage());
			String fileHash2 = serverUtil.uploadImageBase64(imageStr);
			Assert.assertEquals(fileHash1, fileHash2);

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testDownloadImage()
	{
		try
		{
			byte[] image1 = serverUtil.downloadImage("cd82513c34a9a41e3da5648a5649d92d");
			byte[] image2 = serverUtil.downloadImage("cd82513c34a9a41e3da5648a5649d92d");
			Assert.assertNotNull(image1);
			Assert.assertNotNull(image2);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testDownloadImageBase64()
	{
		try
		{
			String image1 = serverUtil.downloadImageBase64("cd82513c34a9a41e3da5648a5649d92d");
			String image2 = serverUtil.downloadImageBase64("cd82513c34a9a41e3da5648a5649d92d");
			Assert.assertNotNull(image1);
			Assert.assertNotNull(image2);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
