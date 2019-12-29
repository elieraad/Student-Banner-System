package me.elieraad.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import me.elieraad.Main;
import me.elieraad.sql.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TeacherRegController {
    //Teacher Info
    public TextField teacherFName;
    public TextField teacherLName;
    public DatePicker teacherDOB;
    public TextField teacherPhoneNum;

    //Contract Info
    public DatePicker contractStart;
    public DatePicker contractEnd;
    public ChoiceBox contractType;

    //Degree Info
    public TextField university;
    public ChoiceBox degreeLevel;
    public TextField major;
    public TextField gradYear;

    //Employment Record
    public TextField schoolName;
    public MenuButton subjectList;
    public MenuButton classList;
    public DatePicker recordStart;
    public DatePicker recordEnd;

    //Buttons
    public Button addEmployment;
    public Button addDegree;
    public Button addTeacher;
    public Button backButton;

    public ChoiceBox assignedClass;
    public ChoiceBox assignedSubject;
    public MenuButton assignedSections;
    public Button assignCourse;

    private String teacherID;
    private String degreeID;

    private int degreeCount;
    private int teacherCount;
    private int empRecCount;


    public void initialize() {
        degreeCount = getDegreeMaxID();
        teacherCount = getTeacherMaxID();
        empRecCount = getEmpRecMaxID();
        addDegree.setVisible(false);
        addEmployment.setVisible(false);
        assignCourse.setVisible(false);

        initAssignedClasses();

        assignedClass.setOnAction(event -> {
            String selectedClass = assignedClass.getValue().toString();
            initAssignedSections(selectedClass);
            initAssignedSubjects(selectedClass);
            attachListener();
        });


        addTeacher.setOnMouseClicked(event -> {
            String contractID = getContractID();
            teacherID = addTeacher(contractID);
            addEmployment(teacherID);
            degreeID = addDegree();
            addTeacherDegreeRelation(degreeID, teacherID);
            String courseID = getCourseID();
            addTeachingRelation(courseID, teacherID);
            addDegree.setVisible(true);
            addEmployment.setVisible(true);
            assignCourse.setVisible(true);

        });

        addEmployment.setOnMouseClicked(event -> addEmployment(teacherID));

        addDegree.setOnMouseClicked(event -> {
            degreeID = addDegree();
            addTeacherDegreeRelation(degreeID, teacherID);
        });

        assignCourse.setOnMouseClicked(event -> {
            String courseID = getCourseID();
            addTeachingRelation(courseID, teacherID);
        });

        subjectList.getItems().forEach(menuItem -> menuItem.setOnAction(event -> {
            final String[] subjectListText = {""};
            subjectList.getItems().forEach(item -> {
                if (((CheckMenuItem) item).isSelected())
                    subjectListText[0] += item.getText() + ", ";

            });
            if (subjectListText[0].isEmpty())
                subjectList.setText("Select Subjects");
            else
                subjectList.setText(subjectListText[0].substring(0, subjectListText[0].length() - 2));
        }));

        classList.getItems().forEach(menuItem -> menuItem.setOnAction(event -> {
            final String[] classListText = {""};
            classList.getItems().forEach(item -> {
                if (((CheckMenuItem) item).isSelected())
                    classListText[0] += item.getText() + ", ";

            });
            if (classListText[0].isEmpty())
                classList.setText("Select Classes");
            else
                classList.setText(classListText[0].substring(0, classListText[0].length() - 2));
        }));

        backButton.setOnMouseClicked(event -> Main.changeScene("res/layout/registration_section.fxml"));

    }

    private void attachListener() {
        assignedSections.getItems().forEach(menuItem -> menuItem.setOnAction(event -> {
            final String[] sectionListText = {""};
            assignedSections.getItems().forEach(item -> {
                if (((CheckMenuItem) item).isSelected())
                    sectionListText[0] += item.getText() + ", ";

            });
            if (sectionListText[0].isEmpty())
                assignedSections.setText("Select Sections");
            else
                assignedSections.setText(sectionListText[0].substring(0, sectionListText[0].length() - 2));
        }));
    }

    private String getCourseID() {
        String courseID = "";
        String courseName = assignedSubject.getValue().toString();
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

    private void addTeachingRelation(String courseID, String teacherID) {
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        String classLevel = assignedClass.getValue().toString();
        ArrayList<String> sections = new ArrayList<>();
        assignedSections.getItems().forEach(menuItem -> {
            if (((CheckMenuItem) menuItem).isSelected())
                sections.add(menuItem.getText());
        });
        for (String section : sections) {
            String query = "INSERT INTO TEACHING VALUES(" +
                    "'" + courseID + "', " +
                    teacherID + ", " +
                    "'" + classLevel + "', " +
                    "'" + section + "', " +
                    "'" + year + "')";
            try {
                System.out.println(query);
                MySQL.getConn().createStatement().execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void initAssignedSubjects(String selectedClass) {
        String query = "SELECT courseName FROM COURSE WHERE courseLevel = '" + selectedClass + "';";
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
        assignedSubject.setItems(results);
    }

    private void initAssignedSections(String selectedClass) {
        List<CheckMenuItem> results = new ArrayList<>();
        String query = "SELECT classSection FROM CLASS WHERE classLevel = '" + selectedClass + "';";
        try {
            System.out.println(query);
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(query);
            while (rs.next()) {
                results.add(new CheckMenuItem(rs.getString(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObservableList items = assignedSections.getItems();
        items.clear();
        items.addAll(results);
    }

    private void initAssignedClasses() {
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
        assignedClass.setItems(results);
    }

    private String getContractID() {
        String contractID = "";
        String type = contractType.getValue().toString();
        String query = "SELECT contractID FROM CONTRACT WHERE contractType = '" + type + "'";
        try {
            System.out.println(query);
            ResultSet rs = MySQL.getConn().createStatement().executeQuery(query);
            rs.first();
            contractID = rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contractID;
    }

    private String addTeacher(String contractID) {
        String teacherID = "CONCAT('t', LPAD('" + teacherCount + "', '5', '0'))";
        String query = "INSERT INTO TEACHER VALUES(" +
                teacherID + ", " +
                "'" + teacherFName.getText() + "', " +
                "'" + teacherLName.getText() + "', " +
                "'" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(teacherDOB.getValue()) + "', " +
                "'" + teacherPhoneNum.getText() + "', " +
                "'" + LocalDate.now() + "', " +
                "'" + contractID + "', " +
                "'" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(contractStart.getValue()) + "', " +
                "'" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(contractEnd.getValue()) + "'" +
                ");";
        try {
            System.out.println(query);
            MySQL.getConn().createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        teacherCount++;
        return teacherID;
    }

    private void addEmployment(String teacherID) {
        List<String> selectedSubjects = new ArrayList<>();
        List<String> selectedClasses = new ArrayList<>();

        subjectList.getItems().forEach(menuItem -> {
            if (((CheckMenuItem) menuItem).isSelected())
                selectedSubjects.add(menuItem.getText());
        });

        classList.getItems().forEach(menuItem -> {
            if (((CheckMenuItem) menuItem).isSelected())
                selectedClasses.add(menuItem.getText());
        });

        for (String subject : selectedSubjects) {
            for (String Aclass : selectedClasses) {
                String empID = "CONCAT('emp', LPAD('" + empRecCount + "', '5', 0))";
                String query = "INSERT INTO EMPLOYMENT_RECORD VALUES(" +
                        empID + ", " +
                        "'" + schoolName.getText() + "', " +
                        "'" + Aclass + "', " +
                        "'" + subject + "', " +
                        "'" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(recordStart.getValue()) + "', " +
                        "'" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(recordEnd.getValue()) + "', " +
                        teacherID + ");";

                try {
                    System.out.println(query);
                    MySQL.getConn().createStatement().execute(query);
                    empRecCount++;
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private String addDegree() {
        String degreeID = "CONCAT('d', LPAD('" + degreeCount + "', '5', '0'))";
        String query = "INSERT INTO DEGREE VALUES(" +
                degreeID + ", " +
                "'" + university.getText() + "', " +
                "'" + degreeLevel.getValue() + "', " +
                "'" + major.getText() + "'" +
                ");";
        try {
            System.out.println(query);
            MySQL.getConn().createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        degreeCount++;
        return degreeID;
    }

    private void addTeacherDegreeRelation(String degreeID, String teacherID) {
        String query = "INSERT INTO DEGREE_TEACHER VALUES(" +
                teacherID + ", " +
                degreeID + ", " +
                "'" + gradYear.getText() + "'" +
                ");";
        try {
            System.out.println(query);
            MySQL.getConn().createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getTeacherMaxID() {
        String query = "SELECT MAX(teacherID) FROM TEACHER";
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

    private int getDegreeMaxID() {
        String query = "SELECT MAX(degreeID) FROM DEGREE";
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

    private int getEmpRecMaxID() {
        String query = "SELECT MAX(employmentRecordID) FROM EMPLOYMENT_RECORD";
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
