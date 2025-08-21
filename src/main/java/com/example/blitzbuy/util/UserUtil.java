package com.example.blitzbuy.util;

import com.fasterxml.jackson.databind.ObjectMapper;


import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Heather
 * @version 1.0
 * UserUtil: Generate multiple user test scripts
 * 1. Create multiple users and save to user table
 * 2. Simulate HTTP requests to generate JMeter load testing scripts
 */

public class UserUtil {
    public static void create(int count) throws Exception {
        List<User> users = new ArrayList<>(count);
        // count represents the number of users to create
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13500001001L + i);
            user.setNickname("testUser" + i);
            // Generate a random 8-character salt (numbers and letters)
            user.setSalt(RandomUtil.randomString(8));// Salt for user data table, set by programmer
            // User's original password is 123456
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            users.add(user);
        }
        System.out.println("create user");
        // Insert user data into user table
        Connection connection = getConn();
        String sql = "insert into user(nickname,salt,password,id) values(?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            preparedStatement.setString(1, user.getNickname());
            preparedStatement.setString(2, user.getSalt());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setLong(4, user.getId());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        preparedStatement.clearParameters();// Close
        connection.close();
        System.out.println("insert to do");
        // Simulate login, send login request to get userTicket
        // Specify the login interface address
        String urlStr = "http://localhost:9090/login/doLogin";
        // Specify file path: on Mac, the file path is /Users/heatherwang/Desktop/config.txt
        File file = new File("/Users/heatherwang/Desktop/config.txt");

        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            // Request
            URL url = new URL(urlStr);
            // Use HttpURLConnection to send HTTP request
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            // Set output to webpage (equivalent to output to page)
            co.setDoOutput(true);
            OutputStream outputStream = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToMidPass("123456");
            outputStream.write(params.getBytes());
            outputStream.flush();
            // Get webpage output (get input stream, get result, output to ByteArrayOutputStream)
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(bytes)) >= 0) {
                bout.write(bytes, 0, len);
            }
            inputStream.close();
            bout.close();
            // Convert ByteArrayOutputStream content to respBean object
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            // Get userTicket
            String userTicket = (String) respBean.getObject();
            System.out.println("create userTicket" + userTicket);
            String row = user.getId() + "," + userTicket;
            // Write to specified file
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file:" + user.getId());
        }
        raf.close();
        System.out.println("over");
    }

    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://localhost:3306/blitzbuy?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "123456";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) throws Exception {
        create(2000);
    }
}
