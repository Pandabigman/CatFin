package com.panda.utils;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlertBox {
    public static void show(String title, String message) {
        // Create a new Stage (window)
        Stage alertStage = new Stage();
        alertStage.initModality(Modality.APPLICATION_MODAL);
        alertStage.setTitle(title);
        alertStage.initStyle(StageStyle.UTILITY);
        alertStage.setMinWidth(300);
        alertStage.setMinHeight(150);

        // Create UI components
        Label messageLabel = new Label(message);
        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> alertStage.close());

        // Layout setup
        VBox layout = new VBox(20);
        layout.getChildren().addAll(messageLabel, closeButton);
        layout.setAlignment(Pos.CENTER);

        // Create and set the Scene
        Scene scene = new Scene(layout);
        alertStage.setScene(scene);

        // Apply custom CSS if needed
        scene.getStylesheets().add(AlertBox.class.getResource("../resources/styles/modernTealAlert.css").toExternalForm());

        // Show the Alert window and wait until it's closed
        alertStage.showAndWait();
    }
}
