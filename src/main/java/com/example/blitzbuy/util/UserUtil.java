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
 * 3. Support both Chinese and US phone number formats
 */

public class UserUtil {
    
    /**
     * Create users with Chinese mobile numbers (original format: 13500001001L + i)
     */
    public static void createCNPhone(int count) throws Exception {
        createUsers(count, false);
    }
    
    /**
     * Create users with US phone numbers (11-digit format with area codes)
     */
    public static void createUSPhone(int count) throws Exception {
        createUsers(count, true);
    }
    
    /**
     * Internal method to create users with specified phone number format
     */
    private static void createUsers(int count, boolean useUSFormat) throws Exception {
        List<User> users = new ArrayList<>(count);
        // count represents the number of users to create
        for (int i = 0; i < count; i++) {
            User user = new User();
            
            if (useUSFormat) {
                // Generate US phone numbers (11 digits with area codes)
                user.setId(generateUSPhoneNumber(i));
                user.setNickname("USUser" + i);
            } else {
                // Original Chinese mobile numbers
                user.setId(13500001001L + i);
                user.setNickname("CNUser" + i);
            }
            
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

    /**
     * Generate US phone numbers (11 digits)
     * Format: 1 + area code (3 digits) + exchange (3 digits) + number (4 digits)
     * @param index sequential index for generating unique numbers
     * @return US phone number as Long
     */
    private static Long generateUSPhoneNumber(int index) {
        // Common US area codes (avoiding invalid ones like 000, 111, etc.)
        String[] areaCodes = {
            "201", "202", "203", "205", "206", "207", "208", "209", "210", "212",
            "213", "214", "215", "216", "217", "218", "219", "224", "225", "228",
            "229", "231", "234", "239", "240", "248", "251", "252", "253", "254",
            "256", "260", "262", "267", "269", "270", "272", "276", "281", "301",
            "302", "303", "304", "305", "307", "308", "309", "310", "312", "313",
            "314", "315", "316", "317", "318", "319", "320", "321", "323", "325",
            "330", "331", "334", "336", "337", "339", "347", "351", "352", "360",
            "361", "364", "369", "380", "385", "386", "401", "402", "404", "405",
            "406", "407", "408", "409", "410", "412", "413", "414", "415", "417",
            "419", "423", "424", "425", "430", "432", "434", "435", "440", "442",
            "443", "445", "447", "458", "463", "464", "469", "470", "475", "478",
            "479", "480", "484", "501", "502", "503", "504", "505", "507", "508",
            "509", "510", "512", "513", "515", "516", "517", "518", "520", "530",
            "531", "534", "539", "540", "541", "551", "559", "561", "562", "563",
            "564", "567", "570", "571", "573", "574", "575", "580", "585", "586",
            "601", "602", "603", "605", "606", "607", "608", "609", "610", "612",
            "614", "615", "616", "617", "618", "619", "620", "623", "626", "628",
            "629", "630", "631", "636", "641", "646", "650", "651", "657", "660",
            "661", "662", "667", "669", "678", "681", "682", "689", "701", "702",
            "703", "704", "706", "707", "708", "712", "713", "714", "715", "716",
            "717", "718", "719", "720", "724", "725", "727", "731", "732", "734",
            "737", "740", "743", "747", "754", "757", "760", "762", "763", "765",
            "769", "770", "772", "773", "774", "775", "779", "781", "785", "786",
            "787", "801", "802", "803", "804", "805", "806", "808", "810", "812",
            "813", "814", "815", "816", "817", "818", "828", "830", "831", "832",
            "843", "845", "847", "848", "850", "856", "857", "858", "859", "860",
            "862", "863", "864", "865", "870", "872", "878", "901", "903", "904",
            "906", "907", "908", "909", "910", "912", "913", "914", "915", "916",
            "917", "918", "919", "920", "925", "928", "929", "930", "931", "934",
            "936", "937", "938", "940", "941", "947", "949", "951", "952", "954",
            "956", "959", "970", "971", "972", "973", "978", "979", "980", "984",
            "985", "989"
        };
        
        // Select area code based on index (cycle through available area codes)
        String areaCode = areaCodes[index % areaCodes.length];
        
        // Generate exchange (3 digits, cannot start with 0 or 1)
        int exchange = 200 + (index % 800); // 200-999
        
        // Generate number (4 digits)
        int number = 1000 + (index % 9000); // 1000-9999
        
        // Combine: 1 + areaCode + exchange + number (11 digits total)
        String phoneStr = "1" + areaCode + String.format("%03d", exchange) + String.format("%04d", number);
        
        return Long.parseLong(phoneStr);
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
        // Example usage:
        
        // Create 1000 users with Chinese mobile numbers (original format)
        // createCNPhone(1000);
        
        // Create 1000 users with US mobile numbers (original format)
        createUSPhone(1000);
        
        System.out.println("User creation completed!");
    }
}
