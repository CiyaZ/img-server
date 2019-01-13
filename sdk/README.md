# image-server sdk

封装了HTTP客户端代码，用于嵌入工程中和服务器交互。

## demo

### 初始化

```java
public void initSDKUtil()
{
	serverUtil = new ImageServerUtil("gLMhrQ27eaLdK8Eg", "127.0.0.1", null, 8080);
}
```

### 上传

```java
public void testUploadImage()
{
	try
	{
		String fileHash = serverUtil.uploadImage(byte[] imageData);

	}
	catch (IOException e)
	{
		e.printStackTrace();
	}
}
```

### 下载

```java
public void testDownloadImage()
{
	try
	{
		byte[] image = serverUtil.downloadImage("cd82513c34a9a41e3da5648a5649d92d");
	catch (IOException e)
	{
		e.printStackTrace();
	}

}
```

## 安装到本地仓库

```
mvn clean install
```