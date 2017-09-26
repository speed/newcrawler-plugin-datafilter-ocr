package com.newcrawler.plugin.test.ocr;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
* 
* @author	Speed
* @email		k78@qq.com
* @date		2017年9月12日 下午4:03:14
* 
*/

public class OcrDownloadImg {

	public static void main(String[] args) throws InterruptedException {
		OcrDownloadImg test=new OcrDownloadImg();
		for(int i=0; i<200; i++) {
			String fileName="F:\\ocr\\cdhrss\\ocr-"+i+".png";
			String img=null;
			try {
				img=test.downloadImg(fileName);
			} catch (IOException e) {
				System.out.println("error:");
				System.out.print(img);
				System.out.println(e.getMessage());
			}
			Thread.sleep(1000l);
		}
		
	}
	
	public String downloadImg(String fileName) throws IOException {
		String content=new String(download("http://jypt.cdhrss.gov.cn:8048/portal.php?id=1"));
		//System.out.println(content);
		String img=null;
		String imgRegex="<img src=\"(/images/qrcode/.*?)\" />";
		
		Pattern p = Pattern.compile(imgRegex);//. represents single character  
		Matcher m = p.matcher(content);  
		if(m.find()) {
			//http://jypt.cdhrss.gov.cn:8048/images/qrcode/qr_26994_1505203368.png?t=1505203368
			img="http://jypt.cdhrss.gov.cn:8048"+m.group(1);
			System.out.println(img);
			
			byte[] imgByte = download(img);
			//BufferedImage imgBi = ImageIO.read(new ByteArrayInputStream(imgByte));
			
			OutputStream out = null;

			try {
			    out = new BufferedOutputStream(new FileOutputStream(fileName));
			    out.write(imgByte);
			} finally {
			    if (out != null) out.close();
			}
		}
		return img;
	}
	public byte[] download(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		InputStream in = urlConnection.getInputStream();
		//FileInputStream in=new FileInputStream("D:/test/gp1004499573,3.png");
		byte[] dataByte=null;
		try {
			dataByte = readByByte(in);
		} finally {
		    if (in != null) in.close();
		    urlConnection.disconnect();
		}
		return dataByte;
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
