package com.newcrawler.plugin.datafilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.newcrawler.plugin.common.utils.MD5Util;
import com.newcrawler.plugin.ocr.service.OcrService;
import com.newcrawler.plugin.ocr.service.OcrServiceFactory;
import com.soso.plugin.bo.DataFilterPluginBo;

public class DataFilterPluginService implements com.soso.plugin.DataFilterPlugin{
	private final static int MAX_RETRY=3;
	@Override
	public String execute(DataFilterPluginBo dataFilterPluginBo) {
		Map<String, String> properties=dataFilterPluginBo.getProperties();
		final String value=dataFilterPluginBo.getValue();
		
		OcrService ocrService=OcrServiceFactory.getOcrTdzywService();
		if(StringUtils.isEmpty(value)){
			return value;
		}
		byte[] imageInByte = getImage(value, 0);
		if(imageInByte==null){
			return null;
		}
		String strMd5=MD5Util.getMD5Str(imageInByte);
		String result=null;
		
		result=ocrService.query(properties, strMd5);
		if(StringUtils.isNotBlank(result)){
			return result;
		}
		
		List<String> list=null;
		try {
			list = ocrService.readTemp(imageInByte);
			result=ocrService.recognition(list,80);
			
			if(StringUtils.isNotBlank(result)){
				ocrService.save(properties, strMd5, result);
			}
			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	
	private byte[] getImage(String urlString, int retryTimes){
		byte[] imageInByte=null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setDefaultUseCaches(false);
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(10000);
			urlConnection.setReadTimeout(10000);
			
			InputStream in = urlConnection.getInputStream();
			imageInByte = readByByte(in);
			in.close();
		} catch (IOException e) {
			if(retryTimes>MAX_RETRY){
				return null;
			}else{
				retryTimes++;
				return getImage(urlString, retryTimes);
			}
		} finally{
			if (urlConnection != null)
				urlConnection.disconnect();
		}
		return imageInByte;
	}
	private static byte[] readByByte(InputStream inStream) throws IOException { 
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
