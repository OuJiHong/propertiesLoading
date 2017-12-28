package com.properties.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

/**
 * redisson 工具
 * @author OJH
 *
 */
public class RedissonUtil {

	/**
	 * 默认数据编码集
	 */
	public static final String defaultEncoding = "UTF-8";
	
	/**
	 * 环境类型
	 * @author bomb
	 *
	 */
	public enum ENV {
		DEV("开发环境(DEV)", "/redisson-test.json"), //开发
		UAT("测试环境(UAT)", "/redisson-uat.json"); //UAT测试
        /**
         * 名称
         */
		private String name;
		/**
		 *配置文件路径 
		 */
		private String configPath;
		
		/**
		 * constructor
		 * @param name
		 * @param configPath
		 */
		private ENV(String name, String configPath) {
			this.name = name;
			this.configPath = configPath;
		}

		
		public String getName() {
			return name;
		}

		public String getConfigPath() {
			return configPath;
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
	public static RedissonClient createRedissonClient(ENV env) throws Exception {
		//处理文件路径
		System.out.println("读取配置文件：" + env.getConfigPath());
		InputStream input = RedissonUtil.class.getResourceAsStream(env.getConfigPath());
		Config config = Config.fromJSON(input);
		return Redisson.create(config);
	}
	
	
	
	/**
	 * 写入配置数据
	 * @param env ENV 
	 * @throws Exception
	 */
	public static void writeMapData(ENV env, String redisMapKey, File configDataFile) throws Exception {
		InputStream input = new FileInputStream(configDataFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.forName(defaultEncoding)));
		//读取配置文件，必须是.properties文件
		Properties property = new Properties();
		property.load(reader);
		input.close();
		
		//写入数据
		RedissonClient client = createRedissonClient(env);
		RMap<String, String> map = client.getMap(redisMapKey, StringCodec.INSTANCE);
		map.clear();
		Set<Object> keySet = property.keySet();
		for (Object key : keySet) {
			map.put(key.toString(), property.getProperty(key.toString()));
		}
		
		client.shutdown();
		
	}

	

	/**
	 * 读取配置数据
	 * @param env ENV
	 * @throws Exception
	 */
	public static String readMapData(ENV env, String redisMapKey, File configOutFile) throws Exception {

		StringBuilder dataStr = new StringBuilder();
		RedissonClient client = createRedissonClient(env);
		RMap<String, String> map = client.getMap(redisMapKey, StringCodec.INSTANCE);
		List<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys);
		
//		File tempFile = File.createTempFile("temp", ".pro");
//		FileOutputStream fos = new FileOutputStream(tempFile);

		
		PrintWriter pw = new PrintWriter(configOutFile, defaultEncoding);
		for (String key : keys) {
			String val = key + "=" + map.get(key);
			pw.println(val);
			dataStr.append(val + "\r\n");
		}
		pw.flush();
		pw.close();
		client.shutdown();

		//转码
		/*{
			String commondStr = "native2ascii.exe " + tempFile + " " + configOutFile.getAbsolutePath();
			Process process = Runtime.getRuntime().exec(commondStr);
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String outInfo = null;
			//显示进程处理信息
			while ((outInfo = in.readLine()) != null ) {
				System.out.println(outInfo);
			}
			in.close();
		}*/
		
		return dataStr.toString();
	}
	
	
	
	
}
