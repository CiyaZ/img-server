package com.gaoshuhang.imgserver.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaoshuhang.imgserver.sdk.po.RequestJson;
import com.gaoshuhang.imgserver.sdk.po.ResponseJson;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageServerUtil
{
	private String token;
	private String serverUrl;

	/**
	 * 初始化SDK工具类
	 * @param token 操作口令
	 * @param serverUrl 服务器URL
	 */
	public ImageServerUtil(String token, String serverUrl)
	{
		this.token = token;
		this.serverUrl = serverUrl;
	}

	/**
	 * 获取已配置的操作口令
	 * @return 操作口令字符串
	 */
	public String getToken()
	{
		return token;
	}

	/**
	 * 获取已配置的服务器URL
	 * @return 服务器URL字符串
	 */
	public String getServerUrl()
	{
		return serverUrl;
	}

	/**
	 * 下载图片
	 * @param fileHash 图片hash值
	 * @return 图片二进制数据
	 * @throws IOException IO错误
	 */
	public byte[] downloadImage(String fileHash) throws IOException
	{
		String urlStr = serverUrl + "/download?filehash=" + fileHash;
		return httpImageDownload(urlStr);
	}

	/**
	 * 下载图片
	 * @param fileHash 图片散列值
	 * @param scale 缩放浮点值
	 * @return 图片二进制数据
	 * @throws IOException IO错误
	 */
	public byte[] downloadImage(String fileHash, float scale) throws IOException
	{
		String urlStr = serverUrl + "/download?filehash=" + fileHash + "&scale=" + scale;
		return httpImageDownload(urlStr);
	}

	/**
	 * 以Base64形式下载图片
	 * @param fileHash 图片hash值
	 * @return 图片base64字符串
	 * @throws IOException IO错误
	 */
	public String downloadImageBase64(String fileHash) throws IOException
	{
		String urlStr = serverUrl + "/download?filehash=" + fileHash;
		byte[] result = httpImageDownload(urlStr);
		if (result != null)
		{
			Base64 base64 = new Base64();
			return base64.encodeToString(result);
		}
		else
		{
			return null;
		}
	}

	/**
	 * 下载图片base64值
	 * @param fileHash 图片hash值
	 * @param scale 缩放浮点值
	 * @return 图片base64字符串值
	 * @throws IOException IO错误
	 */
	public String downloadImageBase64(String fileHash, float scale) throws IOException
	{
		String urlStr = serverUrl + "/download?filehash=" + fileHash + "&scale=" + scale;
		byte[] result = httpImageDownload(urlStr);
		if (result != null)
		{
			Base64 base64 = new Base64();
			return base64.encodeToString(result);
		}
		else
		{
			return null;
		}
	}

	private byte[] httpImageDownload(String urlStr) throws IOException
	{
		byte[] result = null;
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect();
		if (connection.getResponseCode() == 200)
		{
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			InputStream inputStream = connection.getInputStream();

			byte[] buffer = new byte[1024];
			int n;
			while ((n = inputStream.read(buffer)) != -1)
			{
				byteArrayOutputStream.write(buffer, 0, n);
			}

			inputStream.close();
			result = byteArrayOutputStream.toByteArray();
		}
		return result;
	}

	/**
	 * 上传图片
	 * @param imageData 图片二进制数据
	 * @return 上传成功为图片的hash值，失败为null或空字符串
	 * @throws IOException IO错误
	 */
	public String uploadImage(byte[] imageData) throws IOException
	{
		String urlStr = serverUrl + "/upload?token=" + token + "&req_type=base64_json";
		RequestJson requestJson = new RequestJson();
		Base64 base64 = new Base64();
		requestJson.data = base64.encodeToString(imageData);
		ResponseJson responseJson = httpImageUpload(urlStr, requestJson);
		if(responseJson != null && responseJson.uploadStatus.equals("success"))
		{
			return responseJson.filehash;
		}
		else
		{
			return null;
		}
	}

	/**
	 * 上传图片
	 * @param imageDataBase64 图片的Base64值
	 * @return 上传成功为图片的hash值，失败为null或空字符串
	 * @throws IOException IO错误
	 */
	public String uploadImageBase64(String imageDataBase64) throws IOException
	{
		String urlStr = serverUrl + "/upload?token=" + token + "&req_type=base64_json";
		RequestJson requestJson = new RequestJson();
		requestJson.data = imageDataBase64;
		ResponseJson responseJson = httpImageUpload(urlStr, requestJson);
		if(responseJson != null && responseJson.uploadStatus.equals("success"))
		{
			return responseJson.filehash;
		}
		else
		{
			return null;
		}
	}

	private ResponseJson httpImageUpload(String urlStr, RequestJson requestJson) throws IOException
	{
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		//获取请求体输出流
		connection.setDoOutput(true);
		DataOutput dataOutput = new DataOutputStream(connection.getOutputStream());
		//向请求体写入数据
		ObjectMapper mapper = new ObjectMapper();
		String reqStr = mapper.writeValueAsString(requestJson);
		connection.connect();
		dataOutput.write(reqStr.getBytes());
		((DataOutputStream) dataOutput).flush();

		if (connection.getResponseCode() == 200)
		{
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			InputStream inputStream = connection.getInputStream();

			byte[] buffer = new byte[1024];
			int n;
			while ((n = inputStream.read(buffer)) != -1)
			{
				byteArrayOutputStream.write(buffer, 0, n);
			}

			inputStream.close();
			byte[] result = byteArrayOutputStream.toByteArray();
			return mapper.readValue(new String(result), ResponseJson.class);
		}
		return null;
	}
}
