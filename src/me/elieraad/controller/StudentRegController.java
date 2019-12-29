package me.elieraad.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Callback;
import me.elieraad.Main;
import me.elieraad.sql.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StudentRegController {

    /*Student Info*/
    public TextField studentFName;
    public TextField studentLName;
    public ChoiceBox<String> studentGender;
    public DatePicker studentDOB;
    public TextField studentPOB;
    public TextField studentCurrCity;
    public TextField studentNationality;
    public TextField studentCity;
    public TextField studentCurrNeighborhood;
    public TextField studentDistrict;
    public TextField studentGovernorate;
    public TextField studentCRN;
    public ChoiceBox classBox;
    public ChoiceBox sectionBox;

    /*Mother Info*/
    public TextField motherFName;
    public TextField motherLName;
    public TextField motherPhoneNum;
    public CheckBox isMotherGuardian;

    /*Father Info*/
    public TextField fatherFName;
    public TextField fatherPhoneNum;
    public TextField fatherLName;
    public CheckBox isFatherGuardian;

    /*Guardian Info*/
    public Label guardianLabel;
    public TextField guardianFName;
    public TextField guardianLName;
    public TextField guardianPhoneNum;

    public Button submitButton;

    private static int studentCount;
    private static int guardianCount;
    private static int parentCount;
    public CheckBox isSiblingEnrolled;
    public Label fatherLabel;
    public Label motherLabel;
    public Label parentsLabel;

    public TextField siblingFName;
    public TextField siblingLName;
    public Button goButton;
    public TableView<ObservableList<String>> resultTable;
    public Button backButton;

    public void initialize() {
        guardianCount = getGuardianMaxID();
        studentCount = getStudentMaxID();
        parentCount = getParentMaxID();

        System.out.println(guardianCount);
        System.out.println(studentCount);
        System.out.println(parentCount);

        showSiblingSection(false);

        initClassBox();

        classBox.setOnAction(event -> {
            String selectedClass = classBox.getValue().toString();
            initSectionBox(selectedClass);
        });
        isMotherGuardian.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (isFatherGuardian.isSelected()) isFatherGuardian.setSelected(!newValue);
            handleGuardian();
        });

        isFatherGuardian.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (isMotherGuardian.isSelected()) isMotherGuardian.setSelected(!newValue);
            handleGuardian();
        });

        submitButton.setOnMouseClicked(event -> {
            if (isSiblingEnrolled.isSelected()) {
                String siblingID = getEnrolledSibling();
                String[] parentID = getExistingParent(siblingID);
                String guardianID = getExistingGuardian(siblingID);
                String studentID = addStudent(guardianID);
                addStudentParentRelation(studentID, parentID);
                String[] classID = enrollInClass(studentID);
                List<String> courses = getCourses(classID);
                registerCourses(studentID, courses);
            } else {
                String guardianID = addGuardian();
                String studentID = addStudent(guardianID);
                String fatherID = addFather();
                String motherID = addMother();
                String[] parentID = {fatherID, motherID};
                addStudentParentRelation(studentID, parentID);
                String[] classID = enrollInClass(studentID);
                List<String> courses = getCourses(classID);
                registerCourses(studentID, courses);
            }
        });

        goButton.setOnMouseClicked(event -> searchSibling());

        isSiblingEnrolled.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (isSiblingEnrolled.isSelected()) {
                showSiblingSection(true);
                disableParent(true);
                disableGuardian(true);
            } else {
                showSiblingSection(false);
                disableParent(false);
                disableGuardian(false);
            }
        }));

        backButton.setOnMouseClicked(event -> Main.changeScene("res/layout/registration_section.fxml"));
    }

    private void registerCourses(String studentID, List<String> courses) {
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        for (String course : courses) {
            String query = "INSERT INTO COURSE_REGISTRATION VALUES(" +
                    studentID + ", " +
                    "'" + course + "', " +
                    "'" + year + "', " +
                    "NULL, " +
                    "NULL, " +
                    "NULL, " +
                    "NULL, " +
                    "NULL, " +
                    "NULL);";

            try {
                System.out.println(query);
                MySQL.getConn().createStatement().execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getCourses(String[] classID) {
        List<String> courses = new ArrayList<>();
        String aClass = classID[0];
        String section = classID[1];
        String query = "SELECT courseID FROM TEACHING WHERE classLevel = '" + aClass + "' AND classSection = '" + section + "'";
        try {
            System.out.println(query);
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(query);
            while (rs.next()) {
                courses.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    private String[] enrollInClass(String studentID) {
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        String aClass = classBox.getValue().toString();
        String section = sectionBox.getValue().toString();
        String[] classID = {aClass, section};
        String query = "INSERT INTO CLASS_ENROLLEMENT VALUES(" +
                studentID + ", " +
                "'" + aClass + "', " +
                "'" + section + "', " +
                "'" + year + "');";

        try {
            System.out.println(query);
            MySQL.getConn().createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classID;
    }

    private void initSectionBox(String selectedClass) {
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
        sectionBox.setItems(results);
    }

    private void initClassBox() {
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
        classBox.setItems(results);
    }

    private void disableParent(boolean state) {
        if (state) {
            parentsLabel.setStyle("-fx-text-fill: GRAY;");
            fatherLabel.setStyle("-fx-text-fill: GRAY;");
            motherLabel.setStyle("-fx-text-fill: GRAY;");
        } else {
            parentsLabel.setStyle("-fx-text-fill: BLACK;");
            fatherLabel.setStyle("-fx-text-fill: BLACK;");
            motherLabel.setStyle("-fx-text-fill: BLACK;");
        }

        fatherFName.setDisable(state);
        fatherLName.setDisable(state);
        fatherPhoneNum.setDisable(state);
        isFatherGuardian.setDisable(state);

        motherFName.setDisable(state);
        motherLName.setDisable(state);
        motherPhoneNum.setDisable(state);
        isMotherGuardian.setDisable(state);
    }

    private void showSiblingSection(boolean state) {
        siblingFName.setVisible(state);
        siblingLName.setVisible(state);
        goButton.setVisible(state);
        resultTable.setVisible(state);
    }

    private String[] getExistingParent(String siblingID) {
        String[] parentID = new String[2];
        String getParentID = "SELECT parentID FROM STUDENT_PARENT where studentID = '" + siblingID + "'";
        try {
            System.out.println(getParentID);
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(getParentID);
            rs.first();
            parentID[0] = rs.getString(1);
            parentID[1] = rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parentID;
    }

    private String getExistingGuardian(String siblingID) {
        String getGuardianID = "SELECT guardianID FROM STUDENT WHERE studentID = '" + siblingID + "'";
        String guardianID = null;
        try {
            System.out.println(getGuardianID);
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(getGuardianID);
            rs.first();
            guardianID = rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guardianID;
    }

    private String getEnrolledSibling() throws NullPointerException {
        return resultTable.getSelectionModel().getSelectedItem().get(0);
    }

    private void searchSibling() {
        String fname = siblingFName.getText();
        String lname = siblingLName.getText();
        String query = "SELECT studentID, studentFirstName, studentLastName, studentPlaceOfBirth, studentDateOfBirth, nationality FROM STUDENT WHERE studentFirstName LIKE '%" + fname + "%' AND studentLastName LIKE '%" + lname + "%'";
        try {
            System.out.println(query);
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

    private String addFather() {
        String query = "INSERT INTO PARENT VALUES(";
        String fatherID = "CONCAT('p', LPAD('" + parentCount + "', 5, '0'))";
        String queryAddFather = fatherID + ", " +
                "'M', " +
                "'" + fatherFName.getText() + "', " +
                "'" + fatherLName.getText() + "', " +
                "'" + fatherPhoneNum.getText() + "')";
        try {
            System.out.println(query + queryAddFather);
            MySQL.getConn().createStatement().execute(query + queryAddFather);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        parentCount++;
        return fatherID;
    }

    private String addMother() {
        String query = "INSERT INTO PARENT VALUES(";
        String motherID = "CONCAT('p', LPAD('" + parentCount + "', 5, '0'))";
        String queryAddMother = motherID + ", " +
                "'F', " +
                "'" + motherFName.getText() + "', " +
                "'" + motherLName.getText() + "', " +
                "'" + motherPhoneNum.getText() + "')";
        try {
            System.out.println(query + queryAddMother);
            MySQL.getConn().createStatement().execute(query + queryAddMother);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        parentCount++;
        return motherID;
    }

    private void addStudentParentRelation(String studentID, String[] parentID) {
        if (!parentID[0].startsWith("CONCAT")) {
            parentID[0] = "'" + parentID[0] + "'";
            parentID[1] = "'" + parentID[1] + "'";
        }
        String query1 = "INSERT INTO STUDENT_PARENT VALUES(" + parentID[0] + ", " + studentID + ");";
        String query2 = "INSERT INTO STUDENT_PARENT VALUES(" + parentID[1] + ", " + studentID + ");";
        try {
            System.out.println(query1);
            MySQL.getConn().createStatement().execute(query1);
            System.out.println(query2);
            MySQL.getConn().createStatement().execute(query2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleGuardian() {
        if (isMotherGuardian.isSelected() || isFatherGuardian.isSelected()) {
            disableGuardian(true);
        } else {
            disableGuardian(false);
        }
    }

    private void disableGuardian(boolean state) {
        if (state)
            guardianLabel.setStyle("-fx-text-fill: GRAY;");
        else guardianLabel.setStyle("-fx-text-fill: BLACK");

        guardianFName.setDisable(state);
        guardianLName.setDisable(state);
        guardianPhoneNum.setDisable(state);
    }

    private String addGuardian() {
        String query = "INSERT INTO GUARDIAN VALUES(";
        String guardianID = "CONCAT('g', LPAD('" + guardianCount + "', 5, '0'))";
        if (isFatherGuardian.isSelected()) {
            query += guardianID + ", " +
                    "'" + fatherFName.getText() + "', " +
                    "'" + fatherLName.getText() + "', " +
                    "'" + fatherPhoneNum.getText() + "')";
        } else if (isMotherGuardian.isSelected()) {
            query += guardianID + ", " +
                    "'" + motherFName.getText() + "', " +
                    "'" + motherLName.getText() + "', " +
                    "'" + motherPhoneNum.getText() + "')";
        } else {
            query += guardianID + ", " +
                    "'" + guardianFName.getText() + "', " +
                    "'" + guardianLName.getText() + "', " +
                    "'" + guardianPhoneNum.getText() + "')";
        }

        try {
            System.out.println(query);
            MySQL.getConn().createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        guardianCount++;
        return guardianID;
    }

    private String addStudent(String guardianID) {
        if (!guardianID.startsWith("CONCAT"))
            guardianID = "'" + guardianID + "'";
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        String schoolID = "1234";
        String studentID = "CONCAT('" + schoolID + "', '" + year + "', LPAD('" + studentCount + "',5,'0'))";
        String addStudentQuery = "INSERT INTO STUDENT VALUES(" +
                studentID + ", " +
                "'" + studentFName.getText() + "', " +
                "'" + studentLName.getText() + "', " +
                "'" + LocalDate.now() + "', " +
                "'" + studentPOB.getText() + "', " +
                "'" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(studentDOB.getValue()) + "', " +
                "'" + studentGender.getValue().substring(0, 1) + "', " +
                "'" + studentNationality.getText() + "', " +
                "'" + studentGovernorate.getText() + "', " +
                "'" + studentDistrict.getText() + "', " +
                "'" + studentCity.getText() + "', " +
                "'" + studentCRN.getText() + "', " +
                "'" + studentCurrCity.getText() + "', " +
                "'" + studentCurrNeighborhood.getText() + "', " +
                "" + guardianID + ");";
        System.out.println(addStudentQuery);
        try {
            System.out.println(addStudentQuery);
            MySQL.getConn().createStatement().execute(addStudentQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        studentCount++;
        return studentID;
    }

    private int getStudentMaxID() {
        String query = "SELECT MAX(studentID) FROM STUDENT";
        int result = 0;
        try {
            System.out.println(query);
            ResultSet count = MySQL.getConn().createStatement().executeQuery(query);
            count.first();
            if (count.getString(1) != null)
                result = Integer.parseInt(count.getString(1).substring(8));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result + 1;
    }

    private int getGuardianMaxID() {
        String query = "SELECT MAX(guardianID) FROM GUARDIAN";
        int result = 0;
        try {
            System.out.println(query);
            ResultSet count = MySQL.getConn().createStatement().executeQuery(query);
            count.first();
            if (count.getString(1) != null)
                result = Integer.parseInt(count.getString(1).substring(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result + 1;
    }

    private int getParentMaxID() {
        String query = "SELECT MAX(parentID) FROM PARENT";
        int result = 0;
        try {
            System.out.println(query);
            ResultSet count = MySQL.getConn().createStatement().executeQuery(query);
            count.first();
            if (count.getString(1) != null)
                result = Integer.parseInt(count.getString(1).substring(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result + 1;
    }
}
