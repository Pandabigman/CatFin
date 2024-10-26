package com.panda.utils;

import javafx.scene.control.Alert;
//import javafx.stage.Stage;

public class AlertUtils {
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        
        // Load the CSS for modern teal styling
        alert.getDialogPane().getStylesheets().add(AlertUtils.class.getResource("../resources/styles/modernTealAlert.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-pane");

        // Show the alert
        alert.showAndWait();
    }
}