package com.newcrawler.plugin.ocr.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface OcrService {

	String recognition(List<String> list, int rate);
	void print(List<String> list);
	List<String> readTemp(byte[] imageInByte) throws IOException;
	byte[] readByByte(InputStream inStream) throws IOException;
	
	String query(Map<String, String> properties, String md5);
	boolean save(Map<String, String> properties, String md5, String value);
	
}
