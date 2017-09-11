package com.newcrawler.plugin.common.cache;

import java.util.Map;

public class MemCache {
	private static final int maxCapacity=100;
	private volatile static MemCache memCache=null;
	private static Map<Object, Object> map=new LRULinkedHashMap<Object, Object>(maxCapacity);
	private MemCache(){
	}
	public static MemCache getInstance(){
		if(memCache==null){
			synchronized (MemCache.class) {
				if(memCache==null){
					memCache=new MemCache();
				}
			}
		}
		return memCache;
	}
	
	public Object get(Object key) {
		return map.get(key);
	}
	public Object put(Object key, Object value) {
		return map.put(key, value);
	}
	public void clear(){
		map.clear();
	}
	public boolean containsKey(Object key){
		return map.containsKey(key);
	}
	public Object remove(Object key){
		return map.remove(key);
	}
}
