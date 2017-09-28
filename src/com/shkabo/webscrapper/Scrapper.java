package com.shkabo.webscrapper;

import com.shkabo.minions.Minion;
import com.shkabo.minions.Wpwoo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

public class Scrapper {

	public static void main(String[] args) {
		String[] links = new String[] {
//				"http://newwalls.as-creation.com/en/collections/372400.html", //happy spring
//				"http://newwalls.as-creation.com/en/collections/354700.html", //x-ray
//				"http://newwalls.as-creation.com/en/collections/377400.html", //best of brands
				"http://newwalls.as-creation.com/en/collections/371700.html", //graze - 12 items
//                "http://newwalls.as-creation.com/en/collections/371800.html", //april
//                "http://newwalls.as-creation.com/en/collections/363700.html", //chateau 5

		};

		for(String link : links) {
			Minion edd = new Minion(link);
		}
		// mysql connection strings
//        LinkedHashMap connection_string = new LinkedHashMap();
//        connection_string.put("url", "jdbc:mysql://localhost:3306/wordpress");
//        connection_string.put("mysql_username", "root");
//        connection_string.put("mysql_password", "bosko123");
//        try {
//            Connection conn = new Wpwoo(url, username, password).getConnection();
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery("select * from wp_posts where id = 8");
//            rs.first();
//            System.out.println(rs.getString("post_title"));
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

    }
}