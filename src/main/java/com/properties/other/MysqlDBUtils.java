/**
 * 
 */
package com.properties.other;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author bomb
 *
 */
public class MysqlDBUtils {
	
	private static String userName = "shop";
	
	private static String userPwd = "shop";
	
	private static String jdbcURL = "jdbc:mysql://192.168.3.71:3306/shop?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
	
	static{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
		}
	}
	
	public static Connection getConn() throws SQLException{
		 Connection conn =  DriverManager.getConnection(jdbcURL , userName , userPwd ) ;
		 return conn;
	}
	
	public static Long getProvinceId(Connection conn,String provName) throws SQLException{
		Long id = null;
		String sql = "SELECT id,name FROM shop_area sa where parent is null and name like ? "; 
		PreparedStatement sm =  conn.prepareStatement(sql);
		sm.setString(1, provName+"%");
		ResultSet rs =  sm.executeQuery();
		if(rs.next()){
			id = rs.getLong(1);
			System.err.println(id+ " : " + rs.getString(2));
		}
		rs.close();
		sm.close();
		return id; 
	}
	
	public static Long getCityId(Connection conn,String cityName) throws SQLException{
		Long id = null;
		String sql = "SELECT id,name FROM shop_area sa where name like ? "; 
		PreparedStatement sm =  conn.prepareStatement(sql);
		sm.setString(1, cityName+"%");
		ResultSet rs =  sm.executeQuery();
		if(rs.next()){
			id = rs.getLong(1);
			System.err.println(id+ " : " + rs.getString(2));
		}
		rs.close();
		sm.close();
		return id; 
	}
	
	
	public static void main(String[] args)throws Exception{
		Connection conn = getConn();
		getProvinceId(conn, "北京");
		getCityId(conn, "长沙");
		conn.close();
		System.exit(0);
	}

}
