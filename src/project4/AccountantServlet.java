//Name:Axel Alvarado
//Course: CNT 4714 – Spring 2024 – Project Four
//Assignment title: A Three-Tier Distributed Web-Based Application
//Date: April 23, 2024


package project4;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/accountantServlet")
public class AccountantServlet extends HttpServlet {

    private Connection connection;
    private Statement statement;

    @Override
    public void init() throws ServletException {
        super.init();
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("C:/Program Files/Apache Software Foundation/Tomcat 10.1/webapps/Project-4/WEB-INF/lib/accountant.properties"));

            Class.forName(props.getProperty("MYSQL_DB_DRIVER_CLASS"));
            String dbUrl = props.getProperty("MYSQL_DB_URL");
            String username = props.getProperty("MYSQL_DB_USERNAME");
            String password = props.getProperty("MYSQL_DB_PASSWORD");

            connection = DriverManager.getConnection(dbUrl, username, password);
            statement = connection.createStatement();
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (connection == null) {
            connectToDatabase(); // Reconnect if not connected
        }

        String message = "";
        String sqlStatement = "";

        try {
            // Get the selected option from the client
            int selectedOption = Integer.parseInt(request.getParameter("option"));

            // Determine the SQL command based on the selected option
            switch (selectedOption) {
                case 1:
                    sqlStatement = "call Get_The_Maximum_Status_Of_All_Suppliers";
                    break;
                case 2:
                    sqlStatement = "call Get_The_Sum_Of_All_Parts_Weights";
                    break;
                case 3:
                    sqlStatement = "call Get_The_Total_Number_Of_Shipments";
                    break;
                case 4:
                    sqlStatement = "call Get_The_Name_And_Number_Of_Workers_Of_The_Job_With_The_Most_Workers";
                    break;
                case 5:
                    sqlStatement = "call List_The_Name_And_Status_Of_All_Suppliers";
                    break;
                default:
                    sqlStatement = "{call ERROR()}";
                    break;
            }

            message = executeSQL(sqlStatement);
        } catch (SQLException e) {
            message = "<tr bgcolor='#ff0000'><td><font color='#ffffff'><br>Error executing the SQL statement:<br>" + e.getMessage() + "</font></td></tr>";
        }

        request.setAttribute("message", message);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/theAccountant.jsp");
        dispatcher.forward(request, response);
    }

    private String executeSQL(String sqlStatement) throws SQLException {
        StringBuilder result = new StringBuilder("<div><div><div><table><thead><tr>");
        String closeTable = "</table></div></div></div>";

        ResultSet resultSet = statement.executeQuery(sqlStatement);
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            result.append("<th scope='col'>").append(metaData.getColumnLabel(i)).append("</th>");
        }
        result.append("</tr></thead><tbody>");

        while (resultSet.next()) {
            result.append("<tr>");
            for (int i = 1; i <= columnCount; i++) {
                if (i != 1) {
                    result.append("<td>").append(resultSet.getString(i)).append("</td>");
                } else {
                    result.append("<td scope='row'>").append(resultSet.getString(i)).append("</td>");
                }
            }
            result.append("</tr>");
        }
        result.append("</tbody>").append(closeTable);

        return result.toString();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
