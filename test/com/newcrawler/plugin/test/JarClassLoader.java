package com.newcrawler.plugin.test;


public class JarClassLoader extends ClassLoader {
	public JarClassLoader(){
	}
	public JarClassLoader(ClassLoader parent){
		super(parent);
	}
	public Class<?> findClass(String name){
		// 必需的步骤1：如果类已经在系统缓冲之中不必再次装入它
		Class<?> clazz = findLoadedClass(name);
		if (clazz != null)
			return clazz;
		// 尝试用默认的ClassLoader装入它
		try{
			clazz = findSystemClass(name);
		}catch(ClassNotFoundException e){}
		if (clazz != null)
			return clazz;
		return null;
	}

}