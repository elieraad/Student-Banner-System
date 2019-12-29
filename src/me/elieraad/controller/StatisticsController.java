package me.elieraad.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import me.elieraad.sql.GradeStats;
import me.elieraad.Main;
import me.elieraad.sql.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StatisticsController {
    private int flag = 1;
    @FXML
    private Button menuButton;
    @FXML
    private TableView tView;
    @FXML
    private TableColumn col_one;
    @FXML
    private TableColumn col_two;
    @FXML
    private TableColumn col_three;
    @FXML
    private TableColumn col_four;
    @FXML
    private TableColumn col_five;
    @FXML
    private TableColumn col_six;
    @FXML
    private TableColumn col_seven;
    @FXML
    private TableColumn col_eight;


    private ObservableList<GradeStats> obsList = FXCollections.observableArrayList();
    private ObservableList<ArrayList<String>> obsList2 = FXCollections.observableArrayList();
    private String[] query = new String[8];
    private String grade;
    private String nbSections = "";
    private int acYear = MySQL.getAcademicYear();

    @FXML
    public void initialize() {

        tView.getItems().clear();
        query[0] = "SELECT DISTINCT classLevel AS grade, COUNT(classSection) AS nbSections " +
                "FROM CLASS C NATURAL JOIN(CLASS_ENROLLEMENT E) WHERE E.academicYear = '" + acYear + "' GROUP BY classLevel;";
        query[1] = "SELECT COUNT(DISTINCT classSection) FROM CLASS C GROUP BY classLevel;";
        query[2] = "SELECT C.classLevel, COUNT(DISTINCT S.studentID) AS males " +
                "FROM STUDENT S, CLASS C, CLASS_ENROLLEMENT E WHERE S.studentID = E.studentID AND E.classLevel = C.classLevel " +
                "AND studentGender = 'M' AND E.academicYear = '" + acYear + "' GROUP BY C.classLevel;";
        query[3] = "SELECT C.classLevel, COUNT(DISTINCT S.studentID) AS females " +
                "FROM STUDENT S, CLASS C, CLASS_ENROLLEMENT E WHERE E.academicYear = '" + acYear + "' AND S.studentID = E.studentID AND E.classLevel = C.classLevel " +
                "AND studentGender = 'F' GROUP BY C.classLevel;";
        query[4] = "SELECT C.classLevel, COUNT(DISTINCT S.studentID) AS lebanese " +
                "FROM STUDENT S, CLASS C, CLASS_ENROLLEMENT E WHERE E.academicYear = '" + acYear + "' AND S.studentID = E.studentID AND E.classLevel = C.classLevel " +
                "AND nationality = 'Lebanese' GROUP BY C.classLevel;";
        query[5] = "SELECT C.classLevel, COUNT(DISTINCT S.studentID) AS palestinian " +
                "FROM STUDENT S, CLASS C, CLASS_ENROLLEMENT E WHERE E.academicYear = '" + acYear + "' AND S.studentID = E.studentID AND E.classLevel = C.classLevel " +
                "AND nationality = 'Palestinian' GROUP BY C.classLevel;";
        query[6] = "SELECT C.classLevel, COUNT(DISTINCT S.studentID) AS syrian " +
                "FROM STUDENT S, CLASS C, CLASS_ENROLLEMENT E WHERE E.academicYear = '" + acYear + "' AND S.studentID = E.studentID AND E.classLevel = C.classLevel " +
                "AND nationality = 'Syrian' GROUP BY C.classLevel;";
        query[7] = "SELECT C.classLevel, COUNT(DISTINCT S.studentID) AS other " +
                "FROM STUDENT S, CLASS C, CLASS_ENROLLEMENT E WHERE E.academicYear = '" + acYear + "' AND S.studentID = E.studentID AND E.classLevel = C.classLevel " +
                "AND nationality NOT IN('Lebanese','Palestinian','Syrian') GROUP BY C.classLevel;";

        getData();


        menuButton.setOnMouseClicked(event -> Main.changeScene("res/layout/main_section.fxml"));
        col_one.setCellValueFactory(new PropertyValueFactory<>("grade"));
        col_two.setCellValueFactory(new PropertyValueFactory<>("nbSections"));
        col_three.setCellValueFactory(new PropertyValueFactory<>("males"));
        col_four.setCellValueFactory(new PropertyValueFactory<>("females"));
        col_five.setCellValueFactory(new PropertyValueFactory<>("lebanese"));
        col_six.setCellValueFactory(new PropertyValueFactory<>("palestinians"));
        col_seven.setCellValueFactory(new PropertyValueFactory<>("syrians"));
        col_eight.setCellValueFactory(new PropertyValueFactory<>("others"));

        tView.setRowFactory(tv -> {
            TableRow<GradeStats> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    if (flag <= 2) {
                        GradeStats rowData = row.getItem();
                        if (flag == 2) {
                            flag++;
                            expandRowNames(rowData);
                        }
                        if (flag == 1) {
                            flag = 2;
                            expandRow(rowData);

                        }

                    }
                }
            });
            return row;
        });
    }

    private void getData() {
        try {
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(query[0]);
            while (rs.next()) {
                grade = rs.getString("grade");
                nbSections = rs.getInt("nbSections") + "";
                obsList.add(new GradeStats(grade, nbSections, 0, 0, 0, 0,
                        0, 0));
            }
            rs = MySQL.getConn().createStatement().executeQuery(query[2]);
            int i = 0;
            int counter = 0;
            while (rs.next()) {
                String lev = obsList.get(i).getGrade();
                if (lev.equals(rs.getString(1))) {
                    obsList.get(i).setMales(rs.getInt(2));
                    counter++;
                } else {
                    obsList.get(i).setMales(0);
                    rs.beforeFirst();
                    for (int k = 0; k < counter; k++) rs.next();
                }
                i++;
            }
            rs = MySQL.getConn().createStatement().executeQuery(query[3]);
            i = 0;
            counter = 0;
            while (rs.next()) {
                String lev = obsList.get(i).getGrade();
                if (lev.equals(rs.getString(1))) {
                    obsList.get(i).setFemales(rs.getInt(2));
                    counter++;
                } else {
                    obsList.get(i).setFemales(0);
                    rs.beforeFirst();
                    for (int k = 0; k < counter; k++) rs.next();
                }
                i++;
            }
            rs = MySQL.getConn().createStatement().executeQuery(query[4]);
            i = 0;
            counter = 0;
            while (rs.next()) {
                String lev = obsList.get(i).getGrade();
                if (lev.equals(rs.getString(1))) {
                    obsList.get(i).setLebanese(rs.getInt(2));
                    counter++;
                } else {
                    obsList.get(i).setLebanese(0);
                    rs.beforeFirst();
                    for (int k = 0; k < counter; k++) rs.next();
                }
                i++;
            }
            rs = MySQL.getConn().createStatement().executeQuery(query[5]);
            i = 0;
            counter = 0;
            while (rs.next()) {
                String lev = obsList.get(i).getGrade();
                if (lev.equals(rs.getString(1))) {
                    obsList.get(i).setPalestinians(rs.getInt(2));
                    counter++;
                } else {
                    obsList.get(i).setPalestinians(0);
                    rs.beforeFirst();
                    for (int k = 0; k < counter; k++) rs.next();
                }
                i++;
            }
            rs = MySQL.getConn().createStatement().executeQuery(query[6]);
            i = 0;
            counter = 0;
            while (rs.next()) {
                String lev = obsList.get(i).getGrade();
                if (lev.equals(rs.getString(1))) {
                    obsList.get(i).setSyrians(rs.getInt(2));
                    counter++;
                } else {
                    obsList.get(i).setSyrians(0);
                    rs.beforeFirst();
                    for (int k = 0; k < counter; k++) rs.next();
                }
                i++;
            }
            rs = MySQL.getConn().createStatement().executeQuery(query[7]);
            i = 0;
            counter = 0;
            while (rs.next()) {
                String lev = obsList.get(i).getGrade();
                if (lev.equals(rs.getString(1))) {
                    obsList.get(i).setOthers(rs.getInt(2));
                    counter++;
                } else {
                    obsList.get(i).setOthers(0);
                    rs.beforeFirst();
                    for (int k = 0; k < counter; k++) rs.next();
                }
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tView.setItems(obsList);
    }

    private void getData2() {
        try {
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(query[0]);
            while (rs.next()) {
                grade = rs.getString(1);
                nbSections = rs.getString(2) + "";
                obsList.add(new GradeStats(grade, nbSections, 0, 0, 0, 0,
                        0, 0));
            }
            rs = MySQL.getConn().createStatement().executeQuery(query[2]);
            int i = 0;
            int counter = 0;
            while (rs.next()) {
                char sec = obsList.get(i).getNbSections().charAt(0);
                String lev = obsList.get(i).getGrade();
                if (sec == rs.getString(2).charAt(0) && lev.equals(rs.getString(1))) {
                    obsList.get(i).setMales(rs.getInt(3));
                } else {
                    obsList.get(i).setMales(0);
                    rs.beforeFirst();
                    for (int k = 0; k < counter; k++) rs.next();
                }
                i++;
            }

            rs = MySQL.getConn().createStatement().executeQuery(query[3]);
            i = 0;
            counter = 0;
            while (rs.next()) {
                char sec = obsList.get(i).getNbSections().charAt(0);
                String lev = obsList.get(i).getGrade();
                if (sec == rs.getString(2).charAt(0) && lev.equals(rs.getString(1))) {
                    obsList.get(i).setFemales(rs.getInt(3));
                } else {
                    obsList.get(i).setFemales(0);
                    rs.beforeFirst();
                    for (int k = 0; k < counter; k++) rs.next();
                }
                i++;
            }

            rs = MySQL.getConn().createStatement().executeQuery(query[4]);
            i = 0;
            counter = 0;
            while (rs.next()) {

                char sec = obsList.get(i).getNbSections().charAt(0);
                String lev = obsList.get(i).getGrade();
                if (sec == rs.getString(2).charAt(0) && lev.equals(rs.getString(1))) {
                    obsList.get(i).setLebanese(rs.getInt(3));
                } else {
                    obsList.get(i).setLebanese(0);
                    rs.beforeFirst();
                    for (int k = 0; k < counter; k++) rs.next();
                }
                i++;
            }

            rs = MySQL.getConn().createStatement().executeQuery(query[6]);
            i = 0;
            counter = 0;
            while (rs.next()) {
                char sec = obsList.get(i).getNbSections().charAt(0);
                String lev = obsList.get(i).getGrade();
                if (sec == rs.getString(2).charAt(0) && lev.equals(rs.getString(1))) {
                    obsList.get(i).setSyrians(rs.getInt(3));
                    counter++;
                } else {
                    obsList.get(i).setSyrians(0);
                    rs.beforeFirst();
                    for (int k = 0; k < counter; k++) rs.next();
                }
                i++;
            }
            rs = MySQL.getConn().createStatement().executeQuery(query[5]);
            i = 0;
            counter = 0;
            while (rs.next()) {
                char sec = obsList.get(i).getNbSections().charAt(0);
                String lev = obsList.get(i).getGrade();
                if (sec == rs.getString(2).charAt(0) && lev.equals(rs.getString(1))) {
                    obsList.get(i).setPalestinians(rs.getInt(3));
                } else {
                    obsList.get(i).setPalestinians(0);
                    rs.beforeFirst();
                    for (int k = 0; k < counter; k++) rs.next();
                }
                i++;
            }

            rs = MySQL.getConn().createStatement().executeQuery(query[7]);
            i = 0;
            counter = 0;
            while (rs.next()) {
                char sec = obsList.get(i).getNbSections().charAt(0);
                String lev = obsList.get(i).getGrade();
                if (sec == rs.getString(2).charAt(0) && lev.equals(rs.getString(1))) {
                    obsList.get(i).setOthers(rs.getInt(3));
                } else {
                    obsList.get(i).setOthers(0);
                    rs.beforeFirst();
                    for (int k = 0; k < counter; k++) rs.next();
                }
                i++;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        tView.setItems(obsList);
    }

    private void expandRow(GradeStats row) {

        col_two.setText("Section");

        String level = row.getGrade();
        query[0] = "SELECT classLevel AS grade, classSection AS section FROM CLASS WHERE classLevel = '" + level + "';";
        query[2] = "select  c.classLevel, c.classSection, count(*) as males from (class c natural join (student s natural join (class_enrollement e))) where e.academicYear = '" + acYear + "' AND c.classLevel = '" + level + "' and s.studentGender = 'm' group by c.classLevel, c.classSection;";
        query[3] = "select  c.classLevel, c.classSection, count(*) as females from (class c natural join (student s natural join (class_enrollement e))) where e.academicYear = '" + acYear + "' AND c.classLevel = '" + level + "' and s.studentGender = 'f' group by c.classLevel, c.classSection;";
        query[4] = "select  c.classLevel, c.classSection, count(*) as lebanese from (class c natural join (student s natural join (class_enrollement e))) where e.academicYear = '" + acYear + "' AND c.classLevel = '" + level + "' and s.nationality = 'lebanese' group by c.classLevel, c.classSection;";
        query[5] = "select  c.classLevel, c.classSection, count(*) as palestinian from (class c natural join (student s natural join (class_enrollement e))) where e.academicYear = '" + acYear + "' AND c.classLevel = '" + level + "' and s.nationality = 'palestinian' group by c.classLevel, c.classSection;";
        query[6] = "select  c.classLevel, c.classSection, count(*) as syrian from (class c natural join (student s natural join (class_enrollement e))) where e.academicYear = '" + acYear + "' AND c.classLevel = '" + level + "' and s.nationality = 'syrian' group by c.classLevel, c.classSection;";
        query[7] = "select  c.classLevel, c.classSection, count(*) as other from (class c natural join (student s natural join (class_enrollement e))) where e.academicYear = '" + acYear + "' AND c.classLevel = '" + level + "' and s.nationality = 'other' group by c.classLevel, c.classSection;";

        tView.getItems().clear();
        getData2();


    }

    private void expandRowNames(GradeStats row) {


        try {
            tView.getItems().clear();
            String level;
            char section;
            level = row.getGrade();
            section = row.getNbSections().charAt(0);
            col_one.setText("First Name");
            col_two.setText("Father Name");
            col_three.setText("Last Name");
            col_four.setText("Gender");
            col_five.setText("Date Of Birth");
            col_six.setText("Nationality");
            col_seven.setVisible(false);
            col_eight.setVisible(false);


            col_one.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ArrayList, String>, ObservableValue<String>>) param ->
                    new SimpleStringProperty(param.getValue().get(0).toString()));
            col_two.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ArrayList, String>, ObservableValue<String>>) param ->
                    new SimpleStringProperty(param.getValue().get(1).toString()));
            col_three.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ArrayList, String>, ObservableValue<String>>) param ->
                    new SimpleStringProperty(param.getValue().get(2).toString()));
            col_four.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ArrayList, String>, ObservableValue<String>>) param ->
                    new SimpleStringProperty(param.getValue().get(3).toString()));
            col_five.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ArrayList, String>, ObservableValue<String>>) param ->
                    new SimpleStringProperty(param.getValue().get(4).toString()));
            col_six.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ArrayList, String>, ObservableValue<String>>) param ->
                    new SimpleStringProperty(param.getValue().get(5).toString()));
            String query = "SELECT S.studentFirstName, P.parentFirstName, S.studentLastName,  S.studentGender, S.studentDateOfBirth, S.nationality " +
                    "FROM CLASS_ENROLLEMENT CE NATURAL JOIN (STUDENT S NATURAL JOIN(STUDENT_PARENT SP NATURAL JOIN (PARENT P))) where CE.academicYear = '" + acYear + "' AND P.parentGender = 'M' " +
                    "AND CE.classLevel = '" + level + "' AND CE.classSection = '" + section + "' ;";
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(query);
            int i = 0;
            while (rs.next()) {
                obsList2.add(new ArrayList<>());

                for (int j = 0; j < 6; j++) {
                    obsList2.get(i).add(j, rs.getString(j + 1) + "");


                }
                i++;
            }
            rs.beforeFirst();

            tView.setItems(obsList2);
        } catch (SQLException se) {
            se.printStackTrace();
        }


    }
}