package me.elieraad.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import me.elieraad.Main;
import me.elieraad.sql.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MainController {
    public ImageView gradesButton;
    public ImageView registerButton;
    public ImageView statButton;
    public ImageView searchButton;
    public Label studentCountLabel;
    public Label classCountLabel;
    private int studentCount;
    private int classCount;

    @FXML
    public void initialize() {
        setStudentCount();
        setClassCount();
        searchButton.setOnMouseClicked(event -> Main.changeScene("res/layout/search_section.fxml"));
        gradesButton.setOnMouseClicked(event -> Main.changeScene("res/layout/grades_section.fxml"));
        registerButton.setOnMouseClicked(event -> Main.changeScene("res/layout/registration_section.fxml"));
        statButton.setOnMouseClicked(event -> Main.changeScene("res/layout/statistics_section.fxml"));
    }

    private void setStudentCount() {
        String query = "SELECT COUNT(*) FROM STUDENT";
        try {
            ResultSet count = MySQL.getConn().createStatement().executeQuery(query);
            count.first();
            studentCount = count.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        studentCountLabel.setText("" + studentCount);
    }

    private void setClassCount() {
        String query = "SELECT COUNT(*) FROM CLASS";
        try {
            ResultSet count = MySQL.getConn().createStatement().executeQuery(query);
            count.first();
            classCount = count.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        classCountLabel.setText("" + classCount);
    }

}
