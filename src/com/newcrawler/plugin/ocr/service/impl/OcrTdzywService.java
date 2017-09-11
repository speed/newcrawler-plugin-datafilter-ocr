package com.newcrawler.plugin.ocr.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.newcrawler.plugin.ocr.service.AbstractOcrService;
import com.newcrawler.plugin.ocr.service.OcrService;

/**
 * 灰度公式是 gray = 0.212671 * R + 0.715160 * G + 0.072169 * B
 * gray大于128，为白色，小于，为黑色
 * @author www.02ta.com
 *
 */
public class OcrTdzywService extends AbstractOcrService implements OcrService{
	
	private final static Map<String, String> tempMap;
	static{
		try {
			Properties systemProp = new Properties();
	        final InputStream resourceAsStream = OcrTdzywService.class.getResourceAsStream("/tdzyw.properties");
	        if (null != resourceAsStream) {
	        	systemProp.load(resourceAsStream);
	        }
	        tempMap=new HashMap<String, String>();
			for(Object obj:systemProp.keySet()){
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
	
}
