package com.gaoshuhang.imgserver.util;

import org.apache.tika.Tika;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 图片缩放工具
 *
 * @author CiyaZ
 */
public class ImageScaleUtil
{
	/**
	 * 缩放图片工具类，可以整体缩放和宽高缩放，两个效果会叠加
	 *
	 * @param srcImageBytes 原始图片数据
	 * @param scale         整体缩放
	 * @param xScale        宽缩放
	 * @param yScale        高缩放
	 * @return 返回整体缩放和宽高缩放后的结果
	 * @throws IOException IO错误
	 */
	public static byte[] scaleImage(byte[] srcImageBytes, float scale, float xScale, float yScale) throws IOException
	{
		if(scale == 1f && xScale == 1f && yScale == 1f)
		{
			// 如果不缩放就不要读ImageIO，内存消耗太严重，低内存直接OOM
			return srcImageBytes;
		}

		// 将原始图片的二进制数据读入BufferedImage
		ByteArrayInputStream bis = new ByteArrayInputStream(srcImageBytes);
		BufferedImage srcImage = ImageIO.read(bis);
		bis.close();

		BufferedImage resultImage = null;
		// 图片整体缩放
		if (scale != 1f)
		{
			resultImage = scale(srcImage, scale, scale);
		}
		// 图片长宽单独缩放
		if (xScale != 1f || yScale != 1f)
		{
			if (resultImage != null)
			{
				resultImage = scale(resultImage, xScale, yScale);
			}
			else
			{
				resultImage = scale(srcImage, xScale, yScale);
			}
		}

		// 将BufferedImage输出到byte[]并返回
		if (resultImage != null)
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			// 根据源文件的格式输出图片
			Tika tika = new Tika();
			String format = tika.detect(srcImageBytes);
			if("image/png".equals(format))
			{
				ImageIO.write(resultImage, "png", bos);
			}
			else if("image/jpg".equals(format) || "image/jpeg".equals(format))
			{
				ImageIO.write(resultImage, "jpg", bos);
			}
			else if("image/gif".equals(format))
			{
				ImageIO.write(resultImage, "gif", bos);
			}

			return bos.toByteArray();
		}
		else
		{
			return srcImageBytes;
		}
	}

	private static BufferedImage scale(BufferedImage srcImage, float xScale, float yScale)
	{
		//获取原始图像的宽度和高度
		int width = (int) (srcImage.getWidth() * xScale);
		int height = (int) (srcImage.getHeight() * yScale);

		//不能低于输出图片的下限大小1px*1px
		if (width <= 0)
		{
			width = 1;
		}
		if (height <= 0)
		{
			height = 1;
		}

		//生成新图片
		BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		resultImage.getGraphics().drawImage(srcImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
		return resultImage;
	}
}
