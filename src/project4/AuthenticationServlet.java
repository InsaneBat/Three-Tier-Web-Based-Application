//Name:Axel Alvarado
//Course: CNT 4714 – Spring 2024 – Project Four
//Assignment title: A Three-Tier Distributed Web-Based Application
//Date: April 23, 2024

package project4;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/authenticate")
public class AuthenticationServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String inBoundUserName = request.getParameter("username");
        String inBoundPassword = request.getParameter("password");
        
        String credentialsSearchQuery = "SELECT * FROM usercredentials WHERE login_username = ? AND login_password = ?";
        
        boolean userCredentialsOK = false;
        
        Properties properties = new Properties();
        FileInputStream filein = null;
        Connection connection = null;
        ResultSet lookupResults = null;
        PreparedStatement pstatement = null;
        
        try {
            // Load properties from file
            filein = new FileInputStream("C:/Program Files/Apache Software Foundation/Tomcat 10.1/webapps/Project-4/WEB-INF/lib/systemapp.properties");
            properties.load(filein);
            
            // Establish connection to the database
            String dbUrl = properties.getProperty("MYSQL_DB_URL");
            String username = properties.getProperty("MYSQL_DB_USERNAME");
            String password = properties.getProperty("MYSQL_DB_PASSWORD");
            
            connection = DriverManager.getConnection(dbUrl, username, password);
            
            // Prepare and execute the query
            pstatement = connection.prepareStatement(credentialsSearchQuery);
            pstatement.setString(1,  inBoundUserName);
            pstatement.setString(2,  inBoundPassword);
            lookupResults = pstatement.executeQuery();
            
            // Check if the user credentials are valid
            if (lookupResults.next()) {
                userCredentialsOK = true;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (lookupResults != null) lookupResults.close();
                if (pstatement != null) pstatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        if (userCredentialsOK) {
            switch (inBoundUserName) {
                case "root":
                    request.getRequestDispatcher("rootHome.jsp").forward(request, response);
                    break;
                case "client":
                    request.getRequestDispatcher("clientHome.jsp").forward(request, response);
                    break;
                case "dataentryuser":
                    request.getRequestDispatcher("dataEntryHome.jsp").forward(request, response);
                    break;
                case "dataupdateuser":
                    request.getRequestDispatcher("dataUpdate.jsp").forward(request, response);
                    break;
                case "theaccountant":
                    request.getRequestDispatcher("theAccountant.jsp").forward(request, response);
                    break;
                default:
                    request.getRequestDispatcher("errorPage.html").forward(request, response);
                    break;
            }
        } else {
            request.getRequestDispatcher("errorPage.html").forward(request, response);
        }
    }
}
