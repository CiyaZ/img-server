package com.gaoshuhang.imgserver.web.controller;

import com.gaoshuhang.imgserver.conf.ImageServerConfig;
import com.gaoshuhang.imgserver.dao.ImageDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 下载控制器
 *
 * @author CiyaZ
 */
@WebServlet(name = "DownloadServlet", urlPatterns = "/download")
public class DownloadServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String fileHash = request.getParameter("filehash");
		if ("".equals(fileHash) || fileHash == null)
		{
			response.setStatus(404);
			request.getRequestDispatcher("WEB-INF/not_found.jsp").forward(request, response);
		}
		else
		{
			String scaleStr = request.getParameter("scale");
			float scale = 1f;
			if (scaleStr != null && !"".equals(scaleStr))
			{
				scale = Float.parseFloat(scaleStr);
			}

			if (scale <= 0f)
			{
				scale = 1f;
			}
			if (scale > ImageServerConfig.MAX_SCALE)
			{
				scale = ImageServerConfig.MAX_SCALE;
			}

			ImageDao imageDao = ImageDao.getInstance();
			boolean result = imageDao.outputImage(response, fileHash, scale);
			if (!result)
			{
				response.setStatus(404);
				request.getRequestDispatcher("WEB-INF/not_found.jsp").forward(request, response);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setStatus(400);
		request.getRequestDispatcher("WEB-INF/bad_request.jsp").forward(request, response);
	}
}
