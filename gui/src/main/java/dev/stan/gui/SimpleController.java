package dev.stan.gui;


import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SimpleController {
    @FXML
    private Label label;

    @FXML
    private void initialize() {
        label.setText("Hello, JavaFX!");
    }
}