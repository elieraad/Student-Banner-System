package me.elieraad.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import me.elieraad.Main;
import me.elieraad.sql.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GradesController {
    public Button backButton;
    public ChoiceBox classLevel;
    public ChoiceBox sectionLevel;
    public ChoiceBox subjectList;
    public Button goButton;
    public TableView gradeTable;
    public Button update;
    public TextField exam1grade;
    public TextField exam2grade;
    public TextField exam3grade;
    public TextField exam4grade;
    public TextField exam5grade;
    public TextField exam6grade;
    public Button done;

    @FXML
    public void initialize() {
        initClasses();
        classLevel.setOnAction(event -> {
            String selectedClass = classLevel.getValue().toString();
            initSections(selectedClass);
            initSubjects(selectedClass);
        });

        goButton.setOnMouseClicked(event -> {
            String courseID = getCourseID();
            searchStudents(courseID);
        });

        backButton.setOnMouseClicked(event -> Main.changeScene("res/layout/main_section.fxml"));
        final String[] studentId = {""};
        update.setOnMouseClicked(event -> studentId[0] = updateGrade());
        done.setOnMouseClicked(event -> updateQuery(studentId[0]));
    }

    private void updateQuery(String studentId) {
        String query = "UPDATE COURSE_REGISTRATION " +
                "SET exam1Grade = '" + exam1grade.getText() + "', exam2Grade = '" + exam2grade.getText() + "', exam3Grade = '" + exam3grade.getText() + "', exam4Grade = '" + exam4grade.getText() + "', exam5Grade = '" + exam5grade.getText() + "', exam6Grade = '" + exam6grade.getText() + "' " +
                "WHERE studentID = '" + studentId + "';";
        try {
            System.out.println(query);
            MySQL.getConn().createStatement().execute(query);
            String courseID = getCourseID();
            searchStudents(courseID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private String updateGrade() {
        ObservableList selectedRow = (ObservableList) gradeTable.getSelectionModel().getSelectedItem();
        String studentID = selectedRow.get(0).toString();
        exam1grade.setText(selectedRow.get(3).toString());
        exam2grade.setText(selectedRow.get(4).toString());
        exam3grade.setText(selectedRow.get(5).toString());
        exam4grade.setText(selectedRow.get(6).toString());
        exam5grade.setText(selectedRow.get(7).toString());
        exam6grade.setText(selectedRow.get(8).toString());
        return studentID;
    }


    private void searchStudents(String courseID) {
        String selectdClass = classLevel.getValue().toString();
        String selectedSection = sectionLevel.getValue().toString();
        String query = "SELECT studentID, studentFirstName, studentLastName, exam1Grade, exam2Grade, exam3Grade, exam4Grade, exam5Grade, exam6Grade " +
                "FROM STUDENT NATURAL JOIN COURSE_REGISTRATION NATURAL JOIN COURSE NATURAL JOIN TEACHING " +
                "WHERE courseID = '" + courseID + "' AND classLevel = '" + selectdClass + "' AND classSection = '" + selectedSection + "'";
        try {
            System.out.println(query);
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(query);
            List<String> header = MySQL.getHeader(rs);
            gradeTable.getColumns().clear();

            for (int i = 0; i < header.size(); i++) {
                TableColumn col = new TableColumn(header.get(i));
                int finalI = i;
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param ->
                        new SimpleStringProperty(param.getValue().get(finalI).toString()));


                gradeTable.getColumns().addAll(col);

            }
            ObservableList table = MySQL.formTable(rs);

            gradeTable.setItems(table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String getCourseID() {
        String courseID = "";
        String courseName = subjectList.getValue().toString();
        String courseIdQuery = "SELECT courseID FROM COURSE WHERE courseName = '" + courseName + "'";
        try {
            System.out.println(courseIdQuery);
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(courseIdQuery);
            rs.first();
            courseID = rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseID;
    }

    private void initClasses() {
        ObservableList<String> results = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT classLevel FROM CLASS";
        try {
            System.out.println(query);
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(query);
            while (rs.next()) {
                results.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        classLevel.setItems(results);
    }

    private void initSections(String selectedClass) {
        ObservableList<String> results = FXCollections.observableArrayList();
        String query = "SELECT classSection FROM CLASS WHERE classLevel = '" + selectedClass + "';";
        try {
            System.out.println(query);
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(query);
            while (rs.next()) {
                results.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sectionLevel.setItems(results);
    }

    private void initSubjects(String selectedClass) {
        String query = "SELECT courseName FROM COURSE NATURAL JOIN TEACHING WHERE classLevel = '" + selectedClass + "';";
        ObservableList<String> results = FXCollections.observableArrayList();
        try {
            System.out.println(query);
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(query);
            while (rs.next()) {
                results.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        subjectList.setItems(results);
    }

}


