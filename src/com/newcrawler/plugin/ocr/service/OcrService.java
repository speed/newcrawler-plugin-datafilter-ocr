package com.newcrawler.plugin.ocr.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface OcrService {

	public Object[] recognition(String str, int rateFilter);
	String recognition(List<String> list, int rate);
	
	String query(Map<String, String> properties, String md5);
	boolean save(Map<String, String> properties, String md5, String value);
	
	public String ocr(InputStream fin, int border, int clean, int width, int offest, int rateFilter)throws IOException;
	public void gray(InputStream fin, File outputfile, String format, int border, int clean, int width, int offest, int rateFilter) throws IOException;
	
	public List<String> temp(InputStream fin, int border, int clean, int width, int offest)
			throws IOException;
	public List<String> temp(InputStream fin, int border, int clean, int[]subWidth) throws IOException;
}
