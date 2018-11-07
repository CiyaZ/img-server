package com.gaoshuhang.imgserver.util;

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
	public static byte[] scaleImage(byte[] srcImageBytes, float scale) throws IOException
	{
		//将原始图片的二进制数据读入BufferedImage
		ByteArrayInputStream bis = new ByteArrayInputStream(srcImageBytes);
		BufferedImage srcImage = ImageIO.read(bis);
		bis.close();

		//图片缩放
		BufferedImage resultImage = scaleImage(srcImage, scale);

		//将BufferedImage输出到byte[]并返回
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(resultImage, "png", bos);
		return bos.toByteArray();
	}

	private static BufferedImage scaleImage(BufferedImage srcImage, float scale)
	{
		//获取原始图像的宽度和高度
		int width = (int) (srcImage.getWidth() * scale);
		int height = (int) (srcImage.getHeight() * scale);

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
