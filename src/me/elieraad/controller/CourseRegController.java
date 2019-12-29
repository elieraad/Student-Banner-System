package me.elieraad.controller;

import javafx.scene.control.*;
import me.elieraad.Main;
import me.elieraad.sql.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseRegController {
    public TextField courseName;
    public ChoiceBox courseLevel;
    public TextField coefficient;
    public TextField hoursPerWeek;
    public TextField hoursPerDay;

    public Button addCourse;
    public Button backButton;

    private int courseCount;

    public void initialize() {
        courseCount = getMaxCourseID();
        addCourse.setOnMouseClicked(event -> addCourse());
        backButton.setOnMouseClicked(event -> Main.changeScene("res/layout/registration_section.fxml"));
    }

    private void addCourse() {
        String courseID = "CONCAT('crs', LPAD('" + courseCount + "', 2, '0'))";
        String query = "INSERT INTO COURSE VALUES(" +
                courseID + ", " +
                "'" + courseName.getText() + "', " +
                "'" + courseLevel.getValue() + "', " +
                "'" + coefficient.getText() + "', " +
                "'" + hoursPerWeek.getText() + "', " +
                "'" + hoursPerDay.getText() + "');";

        try {
            System.out.println(query);
            MySQL.getConn().createStatement().execute(query);
            courseCount++;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getMaxCourseID() {
        String query = "SELECT MAX(courseID) FROM COURSE";
        int result = 0;
        try {
            System.out.println(query);
            ResultSet count = MySQL.getConn().createStatement().executeQuery(query);
            count.first();
            if (count.getString(1) != null)
                result = Integer.parseInt(count.getString(1).substring(3));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result + 1;
    }
}
