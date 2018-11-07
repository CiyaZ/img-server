package com.gaoshuhang.imgserver.domain;

/**
 * 上传接口响应JSON
 *
 * @author CiyaZ
 */
public class UploadResponseJsonBean
{
	private String upload_status;
	private String filehash;

	public String getUpload_status()
	{
		return upload_status;
	}

	public void setUpload_status(String upload_status)
	{
		this.upload_status = upload_status;
	}

	public String getFilehash()
	{
		return filehash;
	}

	public void setFilehash(String filehash)
	{
		this.filehash = filehash;
	}
}
