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

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SupplierRecord extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection databaseConnection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        databaseConnection = initializeDatabaseConnection();
    }

    private Connection initializeDatabaseConnection() throws ServletException {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("C:/Program Files/Apache Software Foundation/Tomcat 10.1/webapps/Project-4/WEB-INF/lib/dataentry.properties"));

            String dbUrl = props.getProperty("MYSQL_DB_URL");
            String username = props.getProperty("MYSQL_DB_USERNAME");
            String password = props.getProperty("MYSQL_DB_PASSWORD");

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(dbUrl, username, password);
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new UnavailableException("Failed to initialize database connection: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handle GET request
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String Snum = request.getParameter("Snum_Supplier");
        String Snam = request.getParameter("Sname_Supplier");
        String Stat = request.getParameter("Stat");
        String Scity = request.getParameter("Scity");
        
        String result = null;

        try {
            result = doUpdate(Snum, Snam, Stat, Scity);
        } catch(SQLException e) {
            result = "<span>" + e.getMessage() + "</span>";
            e.printStackTrace();
        }
        
        HttpSession session = request.getSession();
        session.setAttribute("result", result);
        session.setAttribute("textbox", Snum);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/dataEntryHome.jsp");
        dispatcher.forward(request, response);
    }

    private String doUpdate(String snum, String sname, String status, String city) throws SQLException {
        String res_out = "<div> The statement executed successfully.</div><div>";
        int rowUpdates = 0;
        PreparedStatement statement = null;

        try {
            String sql = "INSERT INTO suppliers VALUES (?, ?, ?, ?)";
            statement = databaseConnection.prepareStatement(sql);
            statement.setString(1, snum);
            statement.setString(2, sname);
            statement.setString(3, status);
            statement.setString(4, city);

            rowUpdates = statement.executeUpdate();
            res_out += rowUpdates + " row(s) affected</div><div>";
        } finally {
            if (statement != null) {
                statement.close();
            }
        }

        return res_out;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (databaseConnection != null) {
            try {
                databaseConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
