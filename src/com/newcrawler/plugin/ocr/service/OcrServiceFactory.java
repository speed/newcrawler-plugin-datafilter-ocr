package com.newcrawler.plugin.ocr.service;

import com.newcrawler.plugin.ocr.service.impl.OcrServiceImpl;

public class OcrServiceFactory {
	

	private static OcrService ocrService;
	

	public static OcrService getOcrService() {
		if(ocrService==null){
			ocrService=new OcrServiceImpl();
		}
		return ocrService;
	}
	
}
