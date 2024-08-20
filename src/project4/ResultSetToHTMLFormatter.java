//Name:Axel Alvarado
//Course: CNT 4714 – Spring 2024 – Project Four
//Assignment title: A Three-Tier Distributed Web-Based Application
//Date: April 23, 2024

package project4;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetToHTMLFormatter {
    public static synchronized String getHtmlRows(ResultSet results) throws SQLException {
        StringBuffer htmlRows = new StringBuffer();
        ResultSetMetaData metaData = results.getMetaData();
        int columnCount = metaData.getColumnCount(); // count of columns in the ResultSet object

        // Debug: Print column names
        System.out.println("Column Count: " + columnCount);
        for (int i = 1; i <= columnCount; i++) {
            System.out.println("Column " + i + " Name: " + metaData.getColumnLabel(i));
        }

        // set the table header row
        // add <tr> element for header row of the outbound table
        htmlRows.append("<tr bgcolor=\"red\">");

        // loop across all the columns to print the column header data
        for (int i = 1; i <= columnCount; i++) {
            htmlRows.append("<th>");
            htmlRows.append(metaData.getColumnLabel(i));
            htmlRows.append("</th>");
        }
        htmlRows.append("</tr>");

        int count = 0;
        while (results.next()) {
            if (count % 2 == 0) {
                htmlRows.append("<tr bgcolor=\"gray\">");
            } else {
                htmlRows.append("<tr bgcolor=\"white\">");
            }
            for (int i = 1; i <= columnCount; i++) {
                // Debug: Print data for each column
                System.out.println("Column " + i + " Data: " + results.getString(i));
                
                htmlRows.append("<td>");
                htmlRows.append(results.getString(i));
                htmlRows.append("</td>");
            }
            htmlRows.append("</tr>");
            count++;
        }
        return htmlRows.toString();
    }
}
