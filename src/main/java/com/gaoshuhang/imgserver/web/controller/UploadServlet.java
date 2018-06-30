package com.gaoshuhang.imgserver.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaoshuhang.imgserver.dao.ImageDao;
import com.gaoshuhang.imgserver.domain.UploadRequestJsonBean;
import com.gaoshuhang.imgserver.domain.UploadResponseJsonBean;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MultipartConfig
@WebServlet(name = "UploadServlet", urlPatterns = "/upload")
public class UploadServlet extends HttpServlet
{
	private static Base64 base64 = new Base64();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setStatus(404);
		request.getRequestDispatcher("WEB-INF/not_found.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		ImageDao imageDao = ImageDao.getInstance();

		//使用json进行数据交互，图片数据格式为base64
		if("base64_json".equals(request.getParameter("req_type")))
		{
			ObjectMapper mapper = new ObjectMapper();

			try
			{
				//从输入流中读取请求JSON字符串并映射到实体类
				UploadRequestJsonBean reqJson = mapper.readValue(request.getInputStream(), UploadRequestJsonBean.class);
				String imageBase64 = reqJson.getData();
				byte[] imageBytes = base64.decode(imageBase64);
				String fileHash = imageDao.saveImage(imageBytes);

				//组装返回JSON信息
				UploadResponseJsonBean respJson = new UploadResponseJsonBean();
				respJson.setUpload_status("success");
				respJson.setFilehash(fileHash);

				response.getWriter().write(mapper.writeValueAsString(respJson));
			}
			catch (IOException e)
			{
				//ObjectMapper解析出错的情况
				response.getWriter().write("{\"upload_status\":\"failed\", \"filehash\":\"\"}");
			}
		}
		//使用enctype=multipart/form-data的表单进行数据交互
		else
		{
			String fileHash = imageDao.saveImage(request.getPart("file"));

			response.getWriter().write(fileHash);
		}
	}
}
