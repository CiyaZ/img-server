package com.gaoshuhang.imgserver.web.controller;

import com.gaoshuhang.imgserver.dao.ImageDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "DownloadServlet", urlPatterns = "/download")
public class DownloadServlet extends HttpServlet
{
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String fileHash = request.getParameter("filehash");
		if("".equals(fileHash) || fileHash == null)
		{
			response.setStatus(404);
			request.getRequestDispatcher("WEB-INF/not_found.jsp").forward(request, response);
		}
		else
		{
			ImageDao imageDao = ImageDao.getInstance();
			boolean result = imageDao.outputImage(response, fileHash);
			if(!result)
			{
				response.setStatus(404);
				request.getRequestDispatcher("WEB-INF/not_found.jsp").forward(request, response);
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setStatus(404);
		request.getRequestDispatcher("WEB-INF/not_found.jsp").forward(request, response);
	}
}
