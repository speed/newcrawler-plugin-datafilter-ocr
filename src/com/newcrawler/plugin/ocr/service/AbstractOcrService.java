package com.newcrawler.plugin.ocr.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.newcrawler.plugin.common.cache.MemCache;
import com.newcrawler.plugin.common.db.BaseDao;
import com.newcrawler.plugin.common.utils.Distance;
import com.newcrawler.plugin.ocr.bo.OCRCacheBo;

/**
 * 
 * @author Speed
 *
 */
public abstract class AbstractOcrService {
	private static Log logger = LogFactory.getLog(AbstractOcrService.class);
	protected abstract Map<String, String> getTempMap();
	
	public String query(Map<String, String> properties, String md5){
		String value=(String)MemCache.getInstance().get(md5);
		if(value!=null){
			return value;
		}
		String sql="select * from plugin_ocr_cache where md5=?";
		List<Object> parameters=new ArrayList<Object>();
		parameters.add(md5);
		
		List<?> list=null;
		try {
			list = BaseDao.query(properties, OCRCacheBo.class, sql, parameters);
		} catch (Exception e) {
			logger.error(e);
		}
		if(list!=null && !list.isEmpty()){
			value=((OCRCacheBo)list.get(0)).getValue();
			MemCache.getInstance().put(md5, value);
			return value;
		}
		return null;
	}
	
	public boolean save(Map<String, String> properties, String md5, String value){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String create_time=String.valueOf(sdf.format(new Date()));
		
		List<Object> parameters=new ArrayList<Object>();
		parameters.add(md5);
		parameters.add(value);
		parameters.add(create_time);
		
		String sql="INSERT INTO `plugin_ocr_cache` (`md5`, `value`, `create_time`) VALUES (?, ?, ?);";
		BaseDao.saveOrUpdate(properties, sql, parameters);
		MemCache.getInstance().put(md5, value);
		return true;
	}
	
	/**
	 * 识别数字
	 * @param list
	 * @return
	 */
	public String recognition(List<String> list, int rate){
		String result="";
		Map<String,String> map=getTempMap();
		for(String str:list){
			str=str.replaceAll("\r\n", "");
			if(map.containsKey(str)){
				String v=map.get(str);
				result+=v;
				//System.out.println(v);
			}else{
				int maxRate=0;
				String v="";
				for(String temp:map.keySet()){
					int t=Distance.similarRate(temp, str);
					if(t>maxRate){
						maxRate=t;
						v=map.get(temp);
						//System.out.println(v+">"+maxRate);
					}
				}
				if(maxRate>rate){
					//System.out.println(v);
					result+=v;
				}
			}
		}
		return result;
	}
	
	public Object[] recognition(String str, int rateFilter){
		str=str.replaceAll("\r\n", "");
		
		int maxRate=0;
		String result="";
		Map<String,String> map=getTempMap();
		if(map.containsKey(str)){
			result=map.get(str);
			maxRate=100;
		}else{
			for(String temp:map.keySet()){
				int rate=Distance.similarRate(temp, str);
				if(rate>maxRate){
					maxRate=rate;
					result=map.get(temp);
				}
			}
		}
		Object[] resultObj=null;
		if(maxRate>rateFilter){
			resultObj=new Object[2];
			resultObj[0]=result;
			resultObj[1]=maxRate;
		}
		return resultObj;
	}
	
	
}
