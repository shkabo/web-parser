package com.shkabo.minions;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

public class Wpwoo {

    private Connection conn;

    public Wpwoo(String url, String username, String password) {
        try {
            this.conn = DriverManager.getConnection(url, username,password);
            System.out.println("Connected to the database");
        } catch (SQLException ex) {
            System.out.println("SQL Exception: "+ ex.getMessage());
        }
    }


    public int addCollection(String name, String slug) throws SQLException {

        String query = "INSERT INTO wp_terms (name, slug, term_group) values (" + name +", " + slug + ", 0)";
        int collection_id = 0;

        try (
                PreparedStatement stmt = this.conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
                ){

            int affected_rows = stmt.executeUpdate();

            if (affected_rows == 0) {
                throw new SQLException("Creating collection failed, no rows affected");
            }
            try (
                    ResultSet rs = stmt.getGeneratedKeys()
                    ) {

                if (rs.next()) {
                    collection_id = rs.getInt(1);

                }
            }
            return collection_id;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return collection_id;
    }

    public void addImages(LinkedHashMap image_map) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);

        String query = "INSERT INTO wp_posts (" +
                "post_author, " +
                "post_date, " +
                "post_date_gmt, " +
                "post_title, " +
                "post_status, " +
                "comment_status, " +
                "ping_status, " +
                "post_name, " +
                "post_modified, " +
                "post_modified_gmt, " +
                "post_parent, " +
                "menu_order, " +
                "post_type, " +
                "post_mime_type, " +
                "comment_count)" +
                "VALUES (" +
                "1, " +
                strDate + ", " +
                strDate + ", " +
                image_map.get("post_title");

    }

}
