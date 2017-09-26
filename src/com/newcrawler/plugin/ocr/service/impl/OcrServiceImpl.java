package com.newcrawler.plugin.ocr.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import com.newcrawler.plugin.ocr.service.AbstractOcrService;
import com.newcrawler.plugin.ocr.service.ImageHelper;
import com.newcrawler.plugin.ocr.service.OcrService;

/**
 * 灰度公式是 gray = 0.212671 * R + 0.715160 * G + 0.072169 * B gray大于128，为白色，小于，为黑色
 * 
 * @author www.02ta.com
 *
 */
public class OcrServiceImpl extends AbstractOcrService implements OcrService {

	private final static Map<String, String> tempMap;
	static {
		try {
			Properties systemProp = new Properties();
			final InputStream resourceAsStream = OcrServiceImpl.class.getResourceAsStream("/ocr.properties");
			if (null != resourceAsStream) {
				systemProp.load(resourceAsStream);
			}
			tempMap = new HashMap<String, String>();
			for (Object obj : systemProp.keySet()) {
				tempMap.put(obj.toString(), systemProp.get(obj).toString());
			}
		} catch (final Exception e) {
			throw new RuntimeException("Not found tdzyw.properties");
		}
	}

	@Override
	protected Map<String, String> getTempMap() {
		return tempMap;
	}

	public void gray(InputStream fin, File outputfile, String format, int border, int clean, int width, int offest,
			int rateFilter) throws IOException {
		BufferedImage image = ImageIO.read(fin);
		fin.close();
		image = ImageHelper.cleanBorder(image, border);

		int pixels[] = ImageHelper.pixels(image);
		int threshold=128;
		//threshold = ImageHelper.ostu(pixels);
				
		ImageHelper.convertToGray(pixels, threshold);
		ImageHelper.convertToGray(pixels, threshold);
		
		int iw = image.getWidth();
		int ih = image.getHeight();
		BufferedImage bi = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);

		bi.setRGB(0, 0, iw, ih, pixels, 0, iw);

		ImageIO.write(bi, format, outputfile);
	}

	/**
	 * border 图片边框宽度 clean 清除噪点次数 width 图片切割宽度 offest 图片切割偏移量 rateFilter 模板匹配相似度过滤
	 */
	public String ocr(InputStream fin, int border, int clean, int width, int offest, int rateFilter)
			throws IOException {
		BufferedImage image = ImageIO.read(fin);
		fin.close();
		image = ImageHelper.cleanBorder(image, border);

		int pixels[] = ImageHelper.pixels(image);
		int threshold=128;
		//threshold = ImageHelper.ostu(pixels);

		ImageHelper.convertToGray(pixels, threshold);

		
		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();
		
		int matrix[][] = ImageHelper.getMatrix(pixels, imgWidth, imgHeight);
		ImageHelper.clean(matrix, clean);

		int startXPostion = 0;
		String data = "";

		int minw = width - offest;
		int maxw = width + offest;

		do {
			String result = "";
			int resultRate = 0;
			int resultWidth = 0;
			int index = startXPostion;
			int bestIndex = startXPostion;
			for (int j = -offest; j <= offest; j++) {
				index = startXPostion + j;
				if (index < 0) {
					index = 0;
				}
				for (int subWidth = minw; subWidth <= maxw && index + subWidth <= imgWidth; subWidth++) {
					int[][] smatrix = ImageHelper.getSubImg(matrix, index, subWidth, threshold);
					ImageHelper.clean(smatrix, clean);

					StringBuffer sb = ImageHelper.readTemplate(smatrix, threshold);

					Object[] resultObj = recognition(sb.toString(), rateFilter);
					if (resultObj != null) {

						String value = String.valueOf(resultObj[0]);
						Integer rate = (Integer) resultObj[1];

						// System.out.println(index+", "+i+", "+rate+", "+value);
						// System.out.println(sb);
						// System.out.println(sb.toString().replaceAll("\r\n", ""));

						if (resultRate < rate) {
							resultRate = rate;
							result = value;
							resultWidth = subWidth;
							bestIndex = index;
						}
					}
				}
				if (index == 0) {
					break;
				}
			}
			if (resultRate > 0) {
				data += result;
				startXPostion = bestIndex + resultWidth;
			} else {
				startXPostion = startXPostion + maxw;
			}
			startXPostion = ImageHelper.startXPostion(matrix, startXPostion, threshold);
		} while ((startXPostion + minw) <= imgWidth);

		return data;
	}
	
	
	public List<String> temp(InputStream fin, int border, int clean, int width, int offest)
			throws IOException {
		BufferedImage image = ImageIO.read(fin);
		fin.close();
		image = ImageHelper.cleanBorder(image, border);

		int pixels[] = ImageHelper.pixels(image);
		int threshold=128;
		//threshold = ImageHelper.ostu(pixels);

		ImageHelper.convertToGray(pixels, threshold);

		
		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();
		
		int matrix[][] = ImageHelper.getMatrix(pixels, imgWidth, imgHeight);
		ImageHelper.clean(matrix, clean);

		int startXPostion = 0;
		int minw = width - offest;
		int maxw = width + offest;
		List<String> list=new ArrayList<String>();
		do {
			int index = startXPostion;
			for (int j = -offest; j <= offest; j++) {
				index = startXPostion + j;
				if (index < 0) {
					index = 0;
				}
				for (int subWidth = minw; subWidth <= maxw && index + subWidth <= imgWidth; subWidth++) {
					int[][] smatrix = ImageHelper.getSubImg(matrix, index, subWidth, threshold);
					ImageHelper.clean(smatrix, clean);

					StringBuffer sb = ImageHelper.readTemplate(smatrix, threshold);

					//System.out.println(sb);
					//System.out.println(sb.toString().replaceAll("\r\n", ""));
					
					list.add(sb.toString());
				}
				if (index == 0) {
					break;
				}
			}
			startXPostion = startXPostion + maxw;
			startXPostion = ImageHelper.startXPostion(matrix, startXPostion, threshold);
		} while ((startXPostion + minw) <= imgWidth);

		return list;
	}
	
	
	public List<String> temp(InputStream fin, int border, int clean, int[]subWidth) throws IOException {
		BufferedImage image = ImageIO.read(fin);
		fin.close();
		image = ImageHelper.cleanBorder(image, border);

		int pixels[] = ImageHelper.pixels(image);
		int threshold=128;
		//threshold = ImageHelper.ostu(pixels);

		ImageHelper.convertToGray(pixels, threshold);

		List<String> list=new ArrayList<String>();
		
		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();
		
		int matrix[][] = ImageHelper.getMatrix(pixels, imgWidth, imgHeight);
		ImageHelper.clean(matrix, clean);

		int startXPostion = 0;
		for(int subw:subWidth) {
			int[][] smatrix = ImageHelper.getSubImg(matrix, startXPostion, subw, threshold);
			ImageHelper.clean(smatrix, clean);
			startXPostion+=subw;
			
			StringBuffer sb = ImageHelper.readTemplate(smatrix, threshold);

			
			list.add(sb.toString());
		}

		return list;
	}


}
