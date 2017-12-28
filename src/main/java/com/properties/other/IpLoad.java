/**
 * 
 */
package com.properties.other;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import net.shop.mongo.mapper.ip.CityIp;
import net.shop.mongo.mapper.ip.ProvinceIp;

/**
 * @author bomb
 *
 */
public class IpLoad {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		MongoClient mongoClient = getMongoClient();
		Datastore ds = getDatastore(mongoClient);
		String fileName = "e:/ips.json";
		loadIPData(ds, fileName);
		mongoClient.close();
		System.exit(0);
	}

	public static MongoClient getMongoClient() {
		MongoClientOptions options = MongoClientOptions.builder().cursorFinalizerEnabled(false).connectionsPerHost(100)
				.connectTimeout(5000).heartbeatConnectTimeout(5000).heartbeatFrequency(5000)
				.heartbeatSocketTimeout(5000).maxConnectionIdleTime(60000).maxConnectionLifeTime(0)
				.minConnectionsPerHost(50)

				.minHeartbeatFrequency(5000).requiredReplicaSetName("rslog").socketKeepAlive(true).socketTimeout(5000)
				.sslEnabled(false).build();
		MongoCredential credential = MongoCredential.createScramSha1Credential("express", "admin",
				"express123".toCharArray());

		MongoClient mongoClient = new MongoClient(
				Arrays.asList(new ServerAddress("192.168.3.71", 27017), new ServerAddress("192.168.3.72", 27017)),
				Arrays.asList(credential), options);
		return mongoClient;
	}

	public static Datastore getDatastore(MongoClient client) {
		Morphia morphia = new Morphia();
		morphia.mapPackage("main.mapper");
		Datastore ds = morphia.createDatastore(client, "express");
		ds.ensureIndexes();
		ds.ensureCaps();
		return ds;
	}

	public static void loadIPData(Datastore ds, String dataFile) throws Exception {
		FileInputStream fis = new FileInputStream(dataFile);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(fis);
		Iterator<Entry<String, JsonNode>> rootIt = root.fields();
		while (rootIt.hasNext()) {
			Entry<String, JsonNode> entry = rootIt.next();
			if ("provinces".equals(entry.getKey())) {
				Iterator<Entry<String, JsonNode>> subIt = entry.getValue().fields();
				ProvinceIp ip = null;
				while (subIt.hasNext()) {

					Entry<String, JsonNode> subEntry = subIt.next();
					if (subEntry.getValue() instanceof ArrayNode) {
						ArrayNode arrayNode = (ArrayNode) subEntry.getValue();
						int length = arrayNode.size();
						for (int i = 0; i < length; i++) {
							ip = new ProvinceIp();
							ip.setName(subEntry.getKey().trim().replaceAll("\"", ""));
							ip.setIp(arrayNode.get(i).toString().trim().replaceAll("\"", ""));
							ds.save(ip);
						}
					}

				}
			} else if ("cities".equals(entry.getKey())) {

				CityIp ip = null;
				Iterator<Entry<String, JsonNode>> subIt = entry.getValue().fields();
				while (subIt.hasNext()) {

					Entry<String, JsonNode> subEntry = subIt.next();
					if (subEntry.getValue() instanceof ArrayNode) {
						ArrayNode arrayNode = (ArrayNode) subEntry.getValue();
						int length = arrayNode.size();
						for (int i = 0; i < length; i++) {
							ip = new CityIp();
							ip.setName(subEntry.getKey().trim().replaceAll("\"", ""));
							ip.setIp(arrayNode.get(i).toString().trim().replaceAll("\"", ""));
							ds.save(ip);
						}
					}

				}
			} else {
				continue;
			}
		}
		if (fis != null) {
			fis.close();
		}
	}

}
