package com.panda.scenes;

//package panda.financeapp.scenes;


//import panda.financeapp.controller.DAOmanager;
//import panda.financeapp.model.User;

import java.sql.SQLException;

import com.panda.controller.DAOManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;


public class HomeScene {
    private Scene homeScene;
    private Stage primaryStage;
    
    private DAOManager daoManager;
    

    public HomeScene(Stage primaryStage,DAOManager daoManager){
        this.primaryStage = primaryStage;
        this.daoManager = daoManager;
        
        initHomeScene();
    }

    
    private void initHomeScene() {
        // Create the Home Scene
        VBox homeLayout = new VBox(10);
        homeLayout.setPadding(new Insets(10, 10, 10, 10));

        // Navigation Bar
        HBox navBar = new HBox(10);
        Button homeButton = new Button("Home");
        Button recordButton= new Button("Record");
        Button transactionsButton = new Button("Transactions");
        Button budgetButton = new Button("Budget");
        Button chartButton = new Button("Chart");
        Button uploadButton = new Button("Upload");
        Button tipsButton = new Button("Tips");

        navBar.getChildren().addAll(homeButton,recordButton, transactionsButton, budgetButton,chartButton,tipsButton,uploadButton);
        navBar.setAlignment(Pos.CENTER);
        navBar.setId("nav");
        homeButton.setId("active");
        // Navigation Button Actions
        homeButton.setOnAction(e -> primaryStage.setScene(homeScene));
        recordButton.setOnAction(e -> {
            RecordScene recordScene = new RecordScene(primaryStage, daoManager);
            primaryStage.setScene(recordScene.getScene());
        });
        transactionsButton.setOnAction(e -> {
            TransactionsScene transactionsScene = new TransactionsScene(primaryStage, daoManager);
            primaryStage.setScene(transactionsScene.getScene());
        });
        budgetButton.setOnAction(e ->{
            BudgetScene budgetScene = new BudgetScene(primaryStage, daoManager);
            primaryStage.setScene(budgetScene.getScene());
        });
         chartButton.setOnAction(e -> {
            ChartsScene chartScene = new ChartsScene(primaryStage, daoManager);
            primaryStage.setScene(chartScene.getScene());
         });
         uploadButton.setOnAction(e->{
            UploadScene upload = new UploadScene(primaryStage, daoManager);
            primaryStage.setScene(upload.getScene());
         });
         //doesnt really need daomanager as it can call it from its class, included to be consistent.
         tipsButton.setOnAction(e->{
            Tips tipsScene = new Tips(primaryStage,daoManager);
            primaryStage.setScene(tipsScene.getScene());
         });
         //inner layout inside homelayout 
        VBox welcomeLayout = new VBox(10);
        try {
            
            String username= daoManager.getUserDAO().getUserName();
            double balance = daoManager.getUserDAO().getBalance();
            Label welcomeMsg = new Label("Welcome " + username);
            welcomeMsg.setStyle("-fx-font-size: 30px;");
            // Create separate Text objects for the label and the balance amount
            Text labelText = new Text("Your current balance is £");
            Text balanceText = new Text(String.format("%.2f", balance));
            labelText.setFill(Color.WHITE);
            // Apply the color to only the balance amount based on its value
            if (balance < 0) {
                balanceText.setFill(Color.RED);
            } else {
                balanceText.setFill(Color.WHITESMOKE);
            }
            //additional properties
            labelText.setStyle("-fx-font-size: 20px; "); 
            balanceText.setStyle("-fx-font-size: 20px;"); 
            TextFlow balanceLabel = new TextFlow(labelText, balanceText);
            welcomeLayout.getChildren().addAll(welcomeMsg, balanceLabel);

        } catch (SQLException e1) {
            // 
            Label errorLabel = new Label("Could not fetch your details, please restart app");
            welcomeLayout.getChildren().addAll(errorLabel);
            e1.printStackTrace();
        }
        homeLayout.getChildren().addAll(navBar, welcomeLayout);
        homeScene = new Scene(homeLayout, 650, 400);
        homeScene.getStylesheets().add(getClass().getResource("../resources/styles/modernTeal.css").toExternalForm());
        primaryStage.setTitle("CATFIN - Home");
    }

    public Scene getScene(){
        return homeScene;
    }


    
    
    
}
