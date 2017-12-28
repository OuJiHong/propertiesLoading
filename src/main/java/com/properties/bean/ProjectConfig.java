package com.properties.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.properties.util.RedissonUtil.ENV;

/**
 * 配置
 * @author OJH
 *
 */
public class ProjectConfig implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 初始化
	 * 
	 */
	private static final String userHome = System.getProperty("user.home");
	
	/**
	 * redis key
	 */
	private static final String redis_shop_map_key = "SHOP_SYSTEM_PROPERTY_CONFIG_MAP";
	
	private static final String redis_common_service_key = "COMMON_SERVICE_WAP_SYSTEM_PROPERTY_CONFIG_MAP";
	
	private String redisKey;
	
	private Map<ENV,String> data = new HashMap<ENV,String>();

	
	public String getRedisKey() {
		return redisKey;
	}

	public void setRedisKey(String redisKey) {
		this.redisKey = redisKey;
	}

	public Map<ENV, String> getData() {
		return data;
	}

	public void setData(Map<ENV, String> data) {
		this.data = data;
	}

	
	/**
	 * 添加配置
	 * @param env
	 * @param outFile
	 */
	public void addOutFile(ENV env, String outFile){
		this.data.put(env, outFile);
	}
	
	/**
	 * 根据环境获取配置的输出地址
	 * @param env
	 */
	public String getOutFile(ENV env){
		return data.get(env);
	}

	/**
	 * 创建shop配置
	 * @return
	 */
	public static ProjectConfig createShopProjectConfig(){
		ProjectConfig config = new ProjectConfig();
		config.setRedisKey(redis_shop_map_key);
		config.addOutFile(ENV.DEV, userHome + "/shop/shop.properties");
		config.addOutFile(ENV.UAT, userHome + "/shop-uat/shop.properties");

		return config;
	}
	
	/**
	 * 创建common配置
	 * @return
	 */
	public static ProjectConfig createCommonProjectConfig(){
		ProjectConfig config = new ProjectConfig();
		config.setRedisKey(redis_common_service_key);
		config.addOutFile(ENV.DEV, userHome + "/common-service/common-service.properties");
		config.addOutFile(ENV.UAT, userHome + "/common-service-uat/common-service.properties");
		
		return config;
	}
	

}
