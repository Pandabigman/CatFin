package com.panda.utils;

//package panda.financeapp.utils;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox {
    static boolean answer;

    public static boolean display(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(350);
        window.setMinHeight(150);

        Label label = new Label(message);
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");
        

        yesButton.setOnAction(e -> {
            answer = true;
            window.close();
        });

        noButton.setOnAction(e -> {
            answer = false;
            window.close();
        });
        //
        VBox layout = new VBox(10);
        HBox buttons = new HBox(10);
        buttons.getChildren().addAll(yesButton, noButton);
        buttons.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(label, buttons);
        layout.setAlignment(Pos.CENTER);

        // Apply inline CSS styles
        layout.setStyle("-fx-background-color: #0e2433; -fx-padding: 20px;");
        label.setStyle("-fx-font-size: 16px; -fx-text-fill:#e0e0e0");
        buttons.setStyle("-fx-padding: 10px;");
        yesButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px; " +
                   "-fx-background-color: #3e8e41; -fx-text-fill: white; -fx-cursor: hand;");

        noButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10px; " +
                  "-fx-background-color: #dc143c; -fx-text-fill: white; -fx-cursor: hand;" );

        Scene scene = new Scene(layout);
        //scene.getStylesheets().add(getClass().getResource("../resources/styles/Blue.css").toExternalForm());
        window.setScene(scene);
        
        window.showAndWait();
        return answer;
    }
}
