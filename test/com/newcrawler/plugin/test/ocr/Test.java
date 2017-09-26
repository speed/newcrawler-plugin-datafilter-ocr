package com.newcrawler.plugin.test.ocr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.newcrawler.plugin.ocr.service.OcrService;
import com.newcrawler.plugin.ocr.service.OcrServiceFactory;

/**
 * 
 * @author Speed
 * @email k78@qq.com
 * @date 2017年9月13日 上午10:31:52
 * 
 */

public class Test {
	
	public static void main(String[] args) {
		try {

			String imageDir="F:\\ocr\\cdhrss\\test";
			String destDir=imageDir+"\\out";
			
			OcrService ocrService=OcrServiceFactory.getOcrService();
			Test.test(ocrService, imageDir, destDir);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveBitImg(OcrService ocrService, String fileDir, String outDir) throws IOException {
		File outputDir = new File(outDir);
		if(!outputDir.exists()) {
			outputDir.mkdirs();
		}
		File ocrFileDir = new File(fileDir);
		
		int border=1;
		int clean=2;
		int width=20;
		int offest=3;
		int rateFilter=80;
		
		for (File file : ocrFileDir.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}
			String name = file.getName();
			
			FileInputStream fin = new FileInputStream(file);
			System.out.print(name);
			
			File outputfile = new File(outDir, name);
			ocrService.gray(fin, outputfile, "png", border, clean, width, offest, rateFilter);
		}
	}
	
	public static void test(OcrService ocrService, String fileDir, String outDir) throws IOException {
		File ocrFileDir = new File(fileDir);
		
		int border=1;
		int clean=2;
		int width=20;
		int offest=3;
		int rateFilter=80;
		
		for (File file : ocrFileDir.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}
			String name = file.getName();
			
			FileInputStream fin = new FileInputStream(file);
			System.out.print(name);
			
			String data=ocrService.ocr(fin, border, clean, width, offest, rateFilter);
			
			String imageType="png";
			String newFileName=fileDir+"\\"+data+"."+imageType;
			File newFile=new File(newFileName);
			int i=0;
			while(newFile.exists()) {
				i++;
				newFileName=fileDir+"\\"+data+"("+i+")"+"."+imageType;
				newFile=new File(newFileName);
			}
			if(file.renameTo(newFile)) {
				System.out.println(file.getName() + " > " + newFileName +" success.");
		    } else {
		    	System.out.println(file.getName() + " > " + newFileName +" failure.");
		    }
		}
	}

}
