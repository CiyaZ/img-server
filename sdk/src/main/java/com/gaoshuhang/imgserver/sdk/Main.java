package com.gaoshuhang.imgserver.sdk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main
{
	public static void main(String[] args) throws Exception
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
		byte[] result = byteArrayOutputStream.toByteArray();

		ImageServerUtil imageServerUtil = new ImageServerUtil("gLMhrQ27eaLdK8Eg", "http://localhost:8080");
		String hash = imageServerUtil.uploadImage(result);
		System.out.println(hash);
	}
}
