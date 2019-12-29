package me.elieraad.sql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MySQL {


    private static Connection conn;

    public static int getAcademicYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        if(month < 6)
            year--;
        return year;
    }

    public static void connect(String username, String password) {

        String url = "jdbc:mysql://localhost:3306/SCHOOL?useSSL=false&allowPublicKeyRetrieval=true";
        conn = null;

        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println("Access Denied");
            e.printStackTrace();
            return;
        }

        System.out.println("Access Granted");
    }

    public static List<String> getHeader(ResultSet rs) {
        List<String> header = new ArrayList<>();
        if (rs == null)
            return null;

        ResultSetMetaData rsmd;
        try {
            rsmd = rs.getMetaData();
            int NumOfCol = rsmd.getColumnCount();
            for (int i = 1; i <= NumOfCol; i++) {
                header.add(rsmd.getColumnName(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return header;
    }

    public static ObservableList formTable(ResultSet rs) {
        ObservableList table = FXCollections.observableArrayList();

        if (rs == null)
            return null;

        ResultSetMetaData rsmd;
        try {

            rsmd = rs.getMetaData();
            int NumOfCol = rsmd.getColumnCount();

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= NumOfCol; i++) {
                    row.add(rs.getString(i));
                }
                table.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return table;
    }

    public static Connection getConn() {
        return conn;
    }
}
