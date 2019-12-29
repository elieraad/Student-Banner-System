package me.elieraad.controller;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import me.elieraad.Main;
import me.elieraad.sql.MySQL;

import java.sql.SQLException;
import java.util.Calendar;

public class ClassRegController {
    public ChoiceBox classLevel;
    public TextField classSection;
    public TextField floorNumber;
    public TextField floorLevel;
    public TextField capacity;
    public Button addClass;
    public Button backButton;

    public void initialize() {
        addClass.setOnMouseClicked(event -> {
            String[] classID = addClass();
            String[] roomID = addRoom();
            addClassLocation(classID, roomID);
        });

        backButton.setOnMouseClicked(event -> Main.changeScene("res/layout/registration_section.fxml"));
    }

    private void addClassLocation(String[] classID, String[] roomID) {
        String classLevel = classID[0];
        String classSection = classID[1];
        String roomNo = roomID[0];
        String roomLevel = roomID[1];
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

        String query = "INSERT INTO CLASS_LOCATION VALUES(" +
                "'" + classLevel + "', " +
                "'" + classSection + "', " +
                roomNo + ", " +
                "'" + roomLevel + "', " +
                "'" + year + "');";

        try {
            System.out.println(query);
            MySQL.getConn().createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String[] addRoom() {
        String roomNo = "LPAD('" + floorNumber.getText() + "', 2, '0')";
        String level = floorLevel.getText();
        String[] result = {roomNo, level};
        String query = "INSERT INTO ROOM VALUES(" +
                roomNo + ", " +
                "'" + level + "');";
        try {
            System.out.println(query);
            MySQL.getConn().createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String[] addClass() {
        String level = classLevel.getValue().toString();
        String section = classSection.getText();
        String[] result = {level, section};
        String query = "INSERT INTO CLASS VALUES(" +
                "'" + level + "', " +
                "'" + section + "', " +
                "'" + capacity.getText() + "');";
        try {
            System.out.println(query);
            MySQL.getConn().createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
