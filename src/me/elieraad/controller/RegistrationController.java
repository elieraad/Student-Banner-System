package me.elieraad.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import me.elieraad.Main;

public class RegistrationController {
    public Button backButton;
    public Button studentButton;
    public Button teacherButton;
    public Button classButton;
    public Button courseButton;

    @FXML
    public void initialize() {
        backButton.setOnMouseClicked(event -> Main.changeScene("res/layout/main_section.fxml"));
        studentButton.setOnMouseClicked(event -> Main.changeScene("res/layout/student_registration.fxml"));
        teacherButton.setOnMouseClicked(event -> Main.changeScene("res/layout/teacher_registration.fxml"));
        classButton.setOnMouseClicked(event -> Main.changeScene("res/layout/class_registration.fxml"));
        courseButton.setOnMouseClicked(event -> Main.changeScene("res/layout/course_registration.fxml"));
    }
}
