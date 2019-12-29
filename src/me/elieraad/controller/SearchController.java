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
import java.util.List;

public class SearchController {

    public TextField Search_by;
    public Button backButton;
    public ChoiceBox<String> category;
    public ChoiceBox<String> criteria;
    public ListView<String> list;
    public Label Search_label;
    public Button search_button;
    public TableView<ObservableList<String>> resultTable;


    @FXML
    public void initialize() {
        category.valueProperty().addListener(e -> setCriteria());
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        criteria.valueProperty().addListener(e -> {
            String item = criteria.getValue();
            Search_label.setText("Search by " + item);
        });

        backButton.setOnMouseClicked(event -> Main.changeScene("res/layout/main_section.fxml"));
        search_button.setOnMouseClicked(e -> {
            String targetClause = handle_list();
            search(targetClause);
        });
    }

    private String handle_list() {
        StringBuilder target_clause = new StringBuilder();
        ObservableList<String> selectedItems = list.getSelectionModel().getSelectedItems();
        for (String s : selectedItems) {
            target_clause.append(s).append(", ");
        }
        target_clause = new StringBuilder(target_clause.substring(0, target_clause.length() - 2) + " ");
        return target_clause.toString();
    }


    private void setCriteria() {

        ObservableList studentCriteria = FXCollections.observableArrayList("studentFirstName", "studentLastName", "dateOfRegistration",
                "studentPlaceOfBirth", "studentDateOfBirth", "studentGender", "nationality", "governate", "district", "city",
                "civilRecordNumber", "currentCity", "parentFirstName", "parentLastName", "parentPhoneNumber", "guardianFirstName",
                "guardianLastName", "guardianPhoneNumber", "courseName", "classLevel", "classSection", "academicYear");

        ObservableList teacherCriteria = FXCollections.observableArrayList("teacherFirstName", "teacherLastName",
                "teacherDateOfBirth", "teacherPhoneNumber", "dateStarted", "contractType", "degreeLevel", "major", "courseName", "academicYear");

        String selectedCategory = category.getValue();

        if (selectedCategory.equals("Student")) {
            criteria.setItems(studentCriteria);
            list.setItems(studentCriteria);
        } else if (selectedCategory.equals("Teacher")) {
            criteria.setItems(teacherCriteria);
            list.setItems(teacherCriteria);
        }

    }


    private void search(String targetClause) {
        int year = MySQL.getAcademicYear();
        String item = criteria.getValue();
        String range_clause = "STUDENT NATURAL JOIN STUDENT_PARENT NATURAL JOIN PARENT NATURAL JOIN GUARDIAN NATURAL JOIN COURSE_REGISTRATION"
                + " NATURAL JOIN COURSE NATURAL JOIN TEACHING NATURAL JOIN TEACHER NATURAL JOIN EMPLOYMENT_RECORD"
                + " NATURAL JOIN DEGREE_TEACHER NATURAL JOIN DEGREE NATURAL JOIN CLASS NATURAL JOIN CLASS_ENROLLEMENT"
                + " NATURAL JOIN CONTRACT";
        String qualification_clause = item + " LIKE '%" + Search_by.getText() + "%' AND ";

        if (item.equals("academicYear")) {
            year = Integer.parseInt(Search_by.getText());
            qualification_clause = "";
        }

        String query = "SELECT DISTINCT " + targetClause + " FROM " + range_clause + " WHERE " + qualification_clause + " academicYear = '" + year + "';";
        System.out.println(query);
        try {
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(query);
            List<String> header = MySQL.getHeader(rs);
            resultTable.getColumns().clear();
            for (int i = 0; i < header.size(); i++) {
                TableColumn col = new TableColumn(header.get(i));
                int finalI = i;
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param ->
                        new SimpleStringProperty(param.getValue().get(finalI).toString()));
                resultTable.getColumns().addAll(col);
            }

            ObservableList table = MySQL.formTable(rs);

            resultTable.setItems(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

