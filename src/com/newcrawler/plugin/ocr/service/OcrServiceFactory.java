package com.newcrawler.plugin.ocr.service;

import com.newcrawler.plugin.ocr.service.impl.OcrTdzywService;

public class OcrServiceFactory {
	

	private static OcrService ocrTdzywService;
	

	public static OcrService getOcrTdzywService() {
		if(ocrTdzywService==null){
			ocrTdzywService=new OcrTdzywService();
		}
		return ocrTdzywService;
	}
	
}
