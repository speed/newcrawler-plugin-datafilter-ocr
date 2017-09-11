package com.newcrawler.plugin.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.soso.plugin.DataFilterPlugin;
import com.soso.plugin.bo.DataFilterPluginBo;

public class TestPluginBySuning {

	/**
	 * @param args
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		test();
	}

	public static void test() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Map<String, String> properties = new HashMap<String, String>();
		Properties systemProp = new Properties();
		final InputStream resourceAsStream = TestPluginBySuning.class.getResourceAsStream("/system.properties");
		try {
			if (null != resourceAsStream) {
				systemProp.load(resourceAsStream);
			}
		} finally {
			if (resourceAsStream != null)
				resourceAsStream.close();
		}
		for (Object key : systemProp.keySet()) {
			properties.put(key.toString(), systemProp.get(key).toString());
		}

		ThreadTest test = new ThreadTest(properties, "http://price2.suning.cn/webapp/wcs/stores/prdprice/15042306_9017_10052_2-0.png");

		for (int i = 0; i < 10; i++) {
			Thread thread = new Thread(test);
			thread.start();
		}
	}

	static class ThreadTest implements Runnable {
		private Map<String, String> properties;
		private String value;

		public ThreadTest(Map<String, String> properties, String value) {
			this.properties = properties;
			this.value = value;
		}

		@Override
		public void run() {
			try {
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				if (classLoader == null)
					classLoader = TestPluginBySuning.class.getClassLoader();

				JarClassLoader jarClassLoader = new JarClassLoader(classLoader);
				Class<?> clazz = jarClassLoader.loadClass("com.newcrawler.plugin.datafilter.DataFilterPluginService");
				DataFilterPlugin dataFilterPlugin = (DataFilterPlugin) clazz.newInstance();
				DataFilterPluginBo datafilterpluginbo = new DataFilterPluginBo(properties, value, null);

				String result = dataFilterPlugin.execute(datafilterpluginbo);
				System.out.println(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
