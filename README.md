# 图片服务器

使用Servlet实现的图片服务器。图片按MD5命名，分两级目录存储，使用LRU缓存，上传和下载可限制权限。

* `src/main/resources/imgserver.properties`为配置文件
	* `basePath`:图片存放路径，必须
	* `token`：用于上传和下载的身份验证口令，必须配置至少一个，多个用逗号隔开
	* `upload_check`:上传是否检查身份，默认true，非必须，一般要开上传验证
	* `download_check`:下载是否检查身份，默认false，非必须，一般不要开下载验证
	* `use_cache`：是否用缓存，默认true，非必须，访问量很少就不用开了，占内存
	* `cache_size`:缓存大小（图片张数），默认100，非必须，根据图片大小进行调整

* `/upload?token=xxx` POST 上传，表单enctype="multipart/form-data"，上传成功返回文件MD5值
* `upload?token=xxx&req_type=base64_json` POST 使用JSON字符串上传，图片是BASE64格式编码，不要包含HTML显示用的头信息
* `/download?filehash=` GET 下载
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