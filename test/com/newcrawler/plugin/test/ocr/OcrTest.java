package com.newcrawler.plugin.test.ocr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import com.newcrawler.plugin.ocr.service.OcrService;
import com.newcrawler.plugin.ocr.service.OcrServiceFactory;

public class OcrTest {
	/**
	 * 测试
	 * @throws IOException 
	 */
	public static void main(String args[]) throws IOException {
		/*String urlString="http://price1.suning.cn/webapp/wcs/stores/prdprice/19794384_9017_10000_9-1.png";
		OcrService ocrService=OcrServiceFactory.getOcrServiceSuning();
		test2(urlString, ocrService);*/
		String urlString="http://files.tdzyw.com//txtimage//4//d9ebBwUCAlJWBQIEB1YBBAcMUVJQAgVVXFAHXFcEVgNSUwABAQsHWw";
		OcrService ocrService=OcrServiceFactory.getOcrTdzywService();
		//testWriteFile(urlString, ocrService);
		test2(urlString, ocrService);
	}
	public static void testWriteFile(String urlString, OcrService ocrService) throws IOException{
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		InputStream in = urlConnection.getInputStream();
		//FileInputStream in=new FileInputStream("D:/test/gp1004499573,3.png");
		
		byte[] imageInByte = readByByte(in);
		InputStream temp = new ByteArrayInputStream(imageInByte);
		BufferedImage image = ImageIO.read(temp);
		
		try {
			FileOutputStream fileOuputStream = new FileOutputStream("f:/test/testocr.jpg"); 
			ImageIO.write(image, "jpg", fileOuputStream);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 打印模板
	 * @throws IOException
	 */
	public static void test1(String urlString, OcrService ocrService) throws IOException{
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		InputStream in = urlConnection.getInputStream();
		//FileInputStream in=new FileInputStream("D:/test/gp1004499573,3.png");
		
		byte[] imageInByte = readByByte(in);
		List<String> list=null;
		try {
			list = ocrService.readTemp(imageInByte);
			ocrService.print(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 识别测试
	 * @throws IOException
	 */
	public static void test2(String urlString, OcrService ocrService) throws IOException{
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		InputStream in = urlConnection.getInputStream();
		//FileInputStream in=new FileInputStream("D:/test/gp1004499573,3.png");
		byte[] imageInByte = readByByte(in);
		List<String> list=null;
		try {
			list = ocrService.readTemp(imageInByte);
			
			String result=ocrService.recognition(list,80);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
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
