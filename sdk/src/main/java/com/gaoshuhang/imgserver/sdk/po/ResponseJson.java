package com.gaoshuhang.imgserver.sdk.po;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseJson
{
	@JsonProperty("filehash")
	public String filehash;
	@JsonProperty("upload_status")
	public String uploadStatus;
}
