package com.newcrawler.plugin.ocr.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Speed
 * @email k78@qq.com
 * @date 2017年9月13日 下午3:16:30
 * 
 */

public final class ImageHelper {
	
	
	public static BufferedImage getBufferedImage(int pixels[], int iw, int ih) {
		BufferedImage bi = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);

		bi.setRGB(0, 0, iw, ih, pixels, 0, iw);
		return bi;
	}
	public static BufferedImage getBufferedImage(int matrix[][]) {

		int iw=matrix[0].length;
		int ih=matrix.length;
		
		BufferedImage bi = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
		for (int i=0; i<iw; i++) {
			for (int j=0; j<ih; j++) {
				int rgb = matrix[j][i];
				bi.setRGB(i, j, rgb);
			}
		}
		return bi;
	}
	
	public static int startXPostion(int [][] matrix, int index, int threshold){
		int iw=matrix[0].length;
		int ih=matrix.length;
		
		for (int i=index; i<iw; i++) {
			for (int j=0; j<ih; j++) {
				int colorInt = matrix[j][i];
				Color color = new Color(colorInt);
				if (isBlack(threshold, color.getRed(), color.getGreen(), color.getBlue())) {
					return i;
				}
			}
		}
		return index;
	}
	
	public static int[][] getSubImg(int [][] matrix, int sx, int iw, int threshold){
		int ih=matrix.length;
		for (int i=iw; i>0; i--) {
			int w=i-1;
			boolean isAvailabile=false;
			for (int j=0; j<ih; j++) {
				int colorInt = matrix[j][sx+w];
				Color color = new Color(colorInt);
				if (isBlack(threshold, color.getRed(), color.getGreen(), color.getBlue())) {
					isAvailabile=true;
					break;
				}
			}
			if(isAvailabile) {
				iw=i;
				break;
			}
		}
		
		int [][] smatrix=new int[ih][iw];
		for (int i=0; i<iw; i++) {
			for (int j=0; j<ih; j++) {
				smatrix[j][i]=matrix[j][sx+i];
			}
		}
		return smatrix;
	}
	
	public static StringBuffer readTemplate(int [][] matrix, int threshold){
		StringBuffer sb=new StringBuffer();
		int ih=matrix.length;
		int iw=matrix[0].length;
		for (int i=0;i<ih;i++) {
			boolean isEmpty=true;
			String temp="";
			for (int j=0;j<iw;j++) {
				int colorInt = matrix[i][j];
				Color color = new Color(colorInt);
				if (isBlack(threshold, color.getRed(), color.getGreen(), color.getBlue())) {
					isEmpty=false;
					temp+="1";
				}else {
					temp+="0";
				}
			}
			if(isEmpty){
				continue;
			}
			sb.append(temp);
			sb.append("\r\n");
		}
		return sb;
	}
	
	public static BufferedImage cleanBorder(BufferedImage image, int border) throws IOException {
		int[] cut = getSize(image, border);

		if (cut != null && cut.length == 4) {
			// x, y, width, height
			//System.out.println("w:" + cut[2] + ", h:" + cut[3] + ", ");
			image = image.getSubimage(cut[0], cut[1], cut[2], cut[3]);
		}
		return image;
	}
	
	/**
	 * http://www.labbookpages.co.uk/software/imgProc/otsuThreshold.html
	 * @param srcData
	 * @return
	 */
	public static int ostu(int[] srcData) {
		int threshold=0;
		int[] histData=new int[srcData.length];
		// Calculate histogram
		int ptr = 0;
		while (ptr < srcData.length) {
		   int h = 0xFF & srcData[ptr];
		   histData[h] ++;
		   ptr ++;
		}

		// Total number of pixels
		int total = srcData.length;

		float sum = 0;
		for (int t=0 ; t<256 ; t++) sum += t * histData[t];

		float sumB = 0;
		int wB = 0;
		int wF = 0;

		float varMax = 0;
		threshold = 0;

		for (int t=0 ; t<256 ; t++) {
		   wB += histData[t];               // Weight Background
		   if (wB == 0) continue;

		   wF = total - wB;                 // Weight Foreground
		   if (wF == 0) break;

		   sumB += (float) (t * histData[t]);

		   float mB = sumB / wB;            // Mean Background
		   float mF = (sum - sumB) / wF;    // Mean Foreground

		   // Calculate Between Class Variance
		   float varBetween = (float)wB * (float)wF * (mB - mF) * (mB - mF);

		   // Check if new maximum found
		   if (varBetween > varMax) {
		      varMax = varBetween;
		      threshold = t;
		   }
		}
		return threshold;
	}
	public static int[] pixels(final BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		return pixels;
	}
	
	
	public static void convertToGray(int[] pixels, int threshold) {
		for (int i = 0; i < pixels.length; i++) {
			int r = (pixels[i] >> 16) & 0xff; 
			int g = (pixels[i] >> 8) & 0xff; 
			int b = (pixels[i]) & 0xff;
			
			pixels[i] = getGray(threshold, r,g,b);
		}
	}

	public static int[][] getMatrix(int[] pixels, int iw, int ih){
		int matrix[][] = new int[ih][iw];
		for (int i = 0, x = 0, y = 0, len = pixels.length; i < len; i++) {
			matrix[y][x] = pixels[i];
			if ((i + 1) % iw == 0) {
				y++;
				x = 0;
			} else {
				x++;
			}
		}
		return matrix;
	}
	/**
	 * 清除噪点
	 * 
	 * @param matrix
	 */
	public static void clean(int matrix[][], int cleanTimes) {
		
		for (int n = 0; n < cleanTimes; n++) {
			int w = matrix[0].length;
			int h = matrix.length;
			for (int y = 1; y < h - 1; y++) {
				for (int x = 1; x < w - 1; x++) {
					boolean flag = false;

					if (matrix[y][x] != -1 && matrix[y][x] != 0) {
						// isBlack

						// 左右均为空时，去掉此点
						if ((matrix[y][x - 1] == -1 || matrix[y][x - 1] == 0)
								&& (matrix[y][x + 1] == -1 || matrix[y][x + 1] == 0)) {
							flag = true;
						}
						// 上下均为空时，去掉此点
						if ((matrix[y - 1][x] == -1 || matrix[y - 1][x] == 0)
								&& (matrix[y + 1][x] == -1 || matrix[y + 1][x] == 0)) {
							flag = true;
						}

						// 斜上下为空时，去掉此点
						if ((matrix[y + 1][x - 1] == -1 || matrix[y + 1][x - 1] == 0)
								&& (matrix[y - 1][x + 1] == -1 || matrix[y - 1][x + 1] == 0)) {
							flag = true;
						}
						if ((matrix[y + 1][x + 1] == -1 || matrix[y + 1][x + 1] == 0)
								&& (matrix[y - 1][x - 1] == -1 || matrix[y - 1][x - 1] == 0)) {
							flag = true;
						}

						//
						/*
						 * if ((matrix[y-1][x] == -1 || matrix[y-1][x] == 0) && (matrix[y-1][x+1] == -1
						 * || matrix[y-1][x+1] == 0) && (matrix[y][x+1] == -1 || matrix[y][x+1] == 0) &&
						 * (matrix[y+1][x+1] == -1 || matrix[y+1][x+1] == 0)){ flag = true; }
						 */
						if (flag) {
							matrix[y][x] = -1;
						}
					}

				}
			}
		}
	}

	public static int[] getSize(BufferedImage image, int border) {
		int iw = image.getWidth();
		int ih = image.getHeight();

		int[] cut = new int[4];

		int x1 = 0, x2 = 0, y1 = 0, y2 = 0;

		for (int y = border; y < ih - border; y++) {
			for (int x = border; x < iw - border; x++) {
				int colorInt = image.getRGB(x, y);
				Color color = new Color(colorInt);
				if (isBlack(128, color.getRed(), color.getGreen(), color.getBlue())) {
					if (x1 == 0 || x1 > x) {
						x1 = x;
					}
					if (x2 == 0 || x2 < x) {
						x2 = x;
					}

					if (y1 == 0 || y1 > y) {
						y1 = y;
					}
					if (y2 == 0 || y2 < y) {
						y2 = y;
					}
				}
			}
		}
		// x, y, w, h
		cut[0] = x1;
		cut[1] = y1;
		cut[2] = x2 - x1 + 1;
		cut[3] = y2 - y1 + 1;
		return cut;
	}
	/**
	 *  1.浮点算法：Gray=R*0.3+G*0.59+B*0.11
	 *  2.整数方法：Gray=(R*30+G*59+B*11)/100
	 * 	3.移位方法：Gray =(R*77+G*151+B*28)>>8;
	 * 	4.平均值法：Gray=（R+G+B）/3;
	 * 	5.仅取绿色：Gray=G；
	 * 	通过上述任一种方法求得Gray后，将原来的RGB(R,G,B)中的R,G,B统一用Gray替换，形成新的颜色RGB(Gray,Gray,Gray)，用它替换原来的RGB(R,G,B)就是灰度图了。
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	private static int getGray(int threshold, int r, int g, int b) {
		int gray =(r*77+g*151+b*28)>>8;
		if(gray<threshold) {
			gray=0;//黑
		}else {
			gray=255;//白
		}
		Color color = new Color(gray, gray, gray);
		return color.getRGB();
	}
	
	private static boolean isBlack(int threshold, int r, int g, int b) {
		// Gray=R×0.299+G×0.587+B×0.114
		int gray =(r*77+g*151+b*28)>>8;
		if (gray > 128) {
			return false;
		} else {
			return true;
		}
	}

	public static byte[] readByByte(InputStream inStream) throws IOException {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outstream.write(buffer, 0, len);
		}
		outstream.close();
		return outstream.toByteArray();
	}

}
