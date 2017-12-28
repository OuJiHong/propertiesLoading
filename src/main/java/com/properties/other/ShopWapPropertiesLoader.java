/**
 * 
 */
package com.properties.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

/**
 * @author bomb
 *
 */
public class ShopWapPropertiesLoader {

	/**
	 * redis config file map
	 */
	private static final Map<ENV, String> redissonConfMap = new HashMap<ENV, String>();
    /**
     * shop project properties file map
     */
	private static final Map<ENV, String> propertyFileMap = new HashMap<ENV, String>();

	/**
	 * redis key
	 */
	private static final String redis_system_map_key = "SHOP_WAP_WAP_SYSTEM_PROPERTY_CONFIG_MAP";

	/**
	 * temp propertie file path
	 */
	private static String tempFile = null;
	
	/**
	 * main method
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//generateProp(ENV.PRO);
		configParams(ENV.DEV);
		System.exit(0);
	}

	static {
		redissonConfMap.put(ENV.DEV, "C:/Users/Administrator/shop/redisson.json");
		redissonConfMap.put(ENV.UAT, "C:/Users/Administrator/shop-uat/redisson.json");
		redissonConfMap.put(ENV.PRO, "C:/Users/Administrator/shop-pro/redisson.json");

		propertyFileMap.put(ENV.DEV, "C:/Users/Administrator/shop-wap/shop-wap.properties");
		propertyFileMap.put(ENV.UAT, "C:/Users/Administrator/shop-wap-uat/shop-wap.properties");
		propertyFileMap.put(ENV.PRO, "C:/Users/Administrator/shop-wap-pro/shop-wap.properties");

		tempFile = System.getenv("user.home") + System.getenv("file.separator") + "shop-wap_temp.properties";
	}

	/**
	 * 环境类型
	 * @author bomb
	 *
	 */
	public enum ENV {
		DEV("dev"), //开发
		UAT("uat"), //UAT测试
		PRO("pro"); //生产
        /**
         * 名称
         */
		private String name;

		/**
		 * constructor
		 * @param name
		 */
		private ENV(String name) {
			this.name = name;
		}

		/**
		 * toString()
		 */
		public String toString() {
			return this.name;
		}

	};

	

	/**
	 * fetch the redisson client object
	 * @param confFile redission.json file
	 * @return Redisson client
	 * @throws Exception
	 */
	private static RedissonClient getRedissonClient(String confFile) throws Exception {
		Config config = Config.fromJSON(new File(confFile));
		return Redisson.create(config);
	}

	/**
	 * configure propertie file to redis
	 * @param env ENV 
	 * @throws Exception
	 */
	public static void configParams(ENV env) throws Exception {
		String redissonConfFile = redissonConfMap.get(env);
		String redisMapKey = redis_system_map_key;
		String propertiesFile = propertyFileMap.get(env);
		InputStream is = new FileInputStream(propertiesFile);
		Properties property = new Properties();
		property.load(is);

		RedissonClient client = getRedissonClient(redissonConfFile);
		RMap<String, String> map = client.getMap(redisMapKey, StringCodec.INSTANCE);
		map.clear();
		Set<Object> keySet = property.keySet();
		for (Object key : keySet) {
			map.put(key.toString(), property.getProperty(key.toString()));
		}
		client.shutdown();
		is.close();
	}

	/**
	 * generate properties from redis
	 * @param env ENV
	 * @throws Exception
	 */
	public static void generateProp(ENV env) throws Exception {
		String redissonConfFile = redissonConfMap.get(env);
		String redisMapKey = redis_system_map_key;
		String propertiesUtf8File = tempFile;
		String propertiesFile = propertyFileMap.get(env);

		RedissonClient client = getRedissonClient(redissonConfFile);
		RMap<String, String> map = client.getMap(redisMapKey, StringCodec.INSTANCE);
		List<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys);

		FileOutputStream fos = new FileOutputStream(propertiesUtf8File);

		PrintWriter pw = new PrintWriter(fos);
		for (String key : keys) {
			pw.println(key + "=" + map.get(key));
		}
		pw.flush();
		pw.close();
		client.shutdown();

		String commondStr = "native2ascii.exe " + propertiesUtf8File + " " + propertiesFile;

		Process process = Runtime.getRuntime().exec(commondStr);

		BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

		while (true) {
			String str = in.readLine();
			if (str != null) {
				System.out.println(str);
			} else {
				break;
			}
		}
		in.close();
	}
}