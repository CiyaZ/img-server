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
			String fullScaleStr = request.getParameter("scale");
			String xScaleStr = request.getParameter("xscale");
			String yScaleStr = request.getParameter("yscale");

			float fullScale = 1f;
			float xScale = 1f;
			float yScale = 1f;

			if (fullScaleStr != null && !"".equals(fullScaleStr))
			{
				fullScale = scaleValueCheckConvert(fullScaleStr);
			}
			if (xScaleStr != null && !"".equals(xScaleStr))
			{
				xScale = scaleValueCheckConvert(xScaleStr);
			}
			if (yScaleStr != null && !"".equals(yScaleStr))
			{
				yScale = scaleValueCheckConvert(yScaleStr);
			}

			if (fullScale != -1f && xScale != -1f && yScale != -1f)
			{
				// 所有参数类型转换成功
				fullScale = scaleValueNormalize(fullScale);
				xScale = scaleValueNormalize(xScale);
				yScale = scaleValueNormalize(yScale);

				ImageDao imageDao = ImageDao.getInstance();
				boolean result = imageDao.outputImage(response, fileHash, fullScale, xScale, yScale);
				if (!result)
				{
					response.setStatus(404);
					request.getRequestDispatcher("WEB-INF/not_found.jsp").forward(request, response);
				}
			}
			else
			{
				// 参数类型错误的情况
				response.setStatus(400);
				request.getRequestDispatcher("WEB-INF/bad_request.jsp").forward(request, response);
			}
		}
	}

	private float scaleValueCheckConvert(String scaleStr)
	{
		try
		{
			return Float.parseFloat(scaleStr);
		}
		catch (NumberFormatException ignored)
		{
			return -1f;
		}
	}

	private float scaleValueNormalize(float scale)
	{
		if (scale <= 0f)
		{
			scale = 1f;
		}
		if (scale > ImageServerConfig.MAX_SCALE)
		{
			scale = ImageServerConfig.MAX_SCALE;
		}

		return scale;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setStatus(400);
		request.getRequestDispatcher("WEB-INF/bad_request.jsp").forward(request, response);
	}
}
