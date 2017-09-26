package com.newcrawler.plugin.test.ocr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
* @author	Speed
* @email		k78@qq.com
* @date		2017年9月12日 下午2:46:17
* 
*/

public class OcrReadTemplate {

	public static void main(String[] args) throws IOException {
		String imageDir="F:\\ocr\\cdhrss\\test";
		String destDir=imageDir+"\\out";
		
		OcrService ocrService=OcrServiceFactory.getOcrService();
		OcrReadTemplate.readTemp(ocrService, imageDir, destDir);
	}
	
	public static void readTemp(OcrService ocrService, String fileDir, String outDir) throws IOException {
		File ocrFileDir = new File(fileDir);
		int border=1;
		int clean=0;
		int width=9;
		int offest=0;
		
		Map<String, List<String>> tempMap=new HashMap<String, List<String>>();
		
		for (File file : ocrFileDir.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}
			String name = file.getName();
			
			FileInputStream fin = new FileInputStream(file);
			System.out.println(name);
			
			List<String> list=ocrService.temp(fin, border, clean, width, offest);
			
			
			for(int i=0, len=list.size(); i<len; i++) {
				String num=name.substring(i, i+1);
				List<String> tempValues=null;
				if(tempMap.containsKey(num)) {
					tempValues=tempMap.get(num);
				}else {
					tempValues=new ArrayList<String>();
					tempMap.put(num, tempValues);
				}
				if(!tempValues.contains(list.get(i))) {
					tempValues.add(list.get(i));
				}
				
			}
			if(tempMap.size()==10) {
				break;
			}
		}
		
		Set<String> keys=tempMap.keySet();
		List<String> list=new ArrayList<String>();
		list.addAll(keys);
		Collections.sort(list);
		System.out.println("#total:"+list.size());
		for(String key:list) {
			List<String> values=tempMap.get(key);
			for(String v:values) {
				System.out.println(v+"="+key);
				
				v=v.replaceAll("\r\n", "");
				System.out.println(v+"="+key);
			}
		}
	}
	

}
