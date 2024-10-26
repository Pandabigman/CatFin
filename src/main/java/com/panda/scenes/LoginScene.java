package com.panda.scenes;

//package panda.financeapp.scenes;

import java.sql.SQLException;

//local used classes
import com.panda.controller.DAOManager;
import com.panda.controller.SessionManager;
import com.panda.controller.UserDAO;
import com.panda.model.User;
import com.panda.utils.AlertBox;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScene {
    private Scene loginScene;
    private Stage primaryStage;
    private DAOManager daoManager;
    private UserDAO userDAO;

    public LoginScene(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.daoManager = new DAOManager();
        this.userDAO = new UserDAO();
        initLoginScene();
    }

    private void initLoginScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        VBox headingBox = new VBox(10);
        Label titleLabel = new Label("Login Form");
        headingBox.getChildren().add(titleLabel);
        headingBox.setAlignment(Pos.CENTER);
        titleLabel.setId("title-label");
        layout.getChildren().add(titleLabel);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        Button signUpButton = new Button("Sign Up");
        // signUpButton.setStyle("-fx-background-color: #439fe0;\r\n" + //
        // " -fx-text-fill: white;")
        ;
        loginButton.setOnAction(e -> {
            try {
                handleLogin(emailField.getText(), passwordField.getText());
            } catch (Exception er) {
                AlertBox.show("Error", "Invalid username or password");
                er.printStackTrace();
            }
        });
        signUpButton.setOnAction(e -> {
            SignUpScene signUpScene = new SignUpScene(primaryStage);
            primaryStage.setScene(signUpScene.getScene());
        });
        VBox buttonsBox = new VBox(10);
        buttonsBox.getChildren().addAll(loginButton, signUpButton);
        buttonsBox.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(headingBox, emailLabel, emailField, passwordLabel, passwordField, buttonsBox);
        loginScene = new Scene(layout, 400, 300);
        loginScene.getStylesheets().add(getClass().getResource("../resources/styles/modernTeal.css").toExternalForm());
    }

    public Scene getScene() {
        return loginScene;
    }

    private void handleLogin(String email, String password) throws SQLException {
        if (email.isEmpty() || password.isEmpty()) {
            // AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Please enter both email
            // and password");
            AlertBox.show("Error", "Please enter both email and password");
            return;
        }

        // UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByEmailAndPassword(email, password);
        if (user != null) {
            SessionManager.startSession(user.getId());
            // DAOManager daoManager = new DAOManager();
            HomeScene homeScene = new HomeScene(primaryStage, daoManager);
            primaryStage.setScene(homeScene.getScene());
        } else {
            AlertBox.show("Login Failed", "Please click sign Up to create a new account");
        }
    }
}
