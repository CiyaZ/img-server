# 图片服务器

使用Servlet实现的图片服务器。图片按MD5命名，分两级目录存储，使用LRU缓存，上传和下载可限制权限，下载具有图片缩放功能，能够整体缩放和按长宽缩放。

图片格式支持PNG、JPG、GIF，通过校验其二进制内容，非法格式会被拦截，缩放后输出格式与原格式相同。

## server 服务器部分

* `src/main/resources/imgserver.properties`为配置文件
	* `basePath`:图片存放路径，必须
	* `token`：用于上传和下载的身份验证口令，必须配置至少一个，多个用逗号隔开
	* `upload_check`:上传是否检查身份，默认true，非必须，一般要开上传验证
	* `download_check`:下载是否检查身份，默认false，非必须，一般不要开下载验证
	* `use_cache`：是否用缓存，默认true，非必须，访问量很少就不用开了，占内存
	* `cache_size`:缓存大小（图片张数），默认100，非必须，根据图片大小进行调整
	* `use_scale`：是否启用缩放，默认false，如果图片很大，启动缩放功能会十分占内存
	* `max_scale`：最大的缩放尺寸，比如max_scale=5表示最大的输出图片为原来长宽的5倍（最小尺寸为1px*1px）

* `/upload?token=xxx` POST 上传，表单enctype="multipart/form-data"，上传成功返回文件MD5值
* `/upload?token=xxx&req_type=base64_json` POST 使用JSON字符串上传，图片是BASE64格式编码，不要包含HTML显示用的头信息
* `/download?filehash=xxx&scale=xxx&xscale=xxx&yscale=xxx` GET 下载，filehash是上传后返回的MD5值，必须参数，scale、xscale、yscale是缩放比例浮点值，都是可选参数，整体缩放和长宽缩放如果同时使用，会导致效果叠加，如果未启用缩放，这些缩放参数不会起作用
* 其余请求一概拦截，转向404错误页

使用json上传时请求和响应的json字符串：

请求：
```json
{
	"data":"图片的base64编码"
}
```

响应：
```json
{
	"filehash":"图片的md5值",
	"upload_status":"success表示上传成功，failed表示上传失败，上传失败filehash为空字符串"
}
```

## sdk 接口封装

封装了HTTP客户端部分，实现图片的上传和下载，用于嵌入Java程序中和服务器交互。

### demo

#### 初始化

```java
public void initSDKUtil()
{
	serverUtil = new ImageServerUtil("gLMhrQ27eaLdK8Eg", "127.0.0.1", "/imgserver", 8080);
}
```

#### 上传

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

#### 下载

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

### 安装到本地仓库

```
mvn clean install
```

### 部署

修改配置文件，打包后放进Tomcat的webapps即可。
