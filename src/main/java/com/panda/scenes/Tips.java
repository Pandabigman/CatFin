package com.panda.scenes;

import java.util.HashMap;
import java.util.Map;

//dao dependencies
import com.panda.controller.ChartsDataFetch;
import com.panda.controller.DAOManager;
import com.panda.utils.AlertBox;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class Tips {
    private Scene tipsScene;
    private Stage primaryStage;
    private DAOManager daoManager;
    private ChartsDataFetch dataFetch;
    
    //doesnt really need daoManager, included to be consistent
    public Tips(Stage primaryStage, DAOManager daoManager){
        this.primaryStage = primaryStage;
        this.daoManager = daoManager;
        //this.dataFetch = daoManager.getChartsData();
        initTipsScene();
    }

    private void initTipsScene(){
        VBox sceneLayout = new VBox();
        VBox tipsLayout = new VBox();
        tipsLayout.setPadding(new Insets(15, 15, 15, 15));
        tipsLayout.setSpacing(10);
         // Navigation Bar
        HBox navBar = new HBox(10);
        Button homeButton = new Button("Home");
        Button recordButton= new Button("Record");
        Button transactionsButton = new Button("Transactions");
        Button budgetButton = new Button("Budget");
        Button chartButton = new Button("Chart");
        Button uploadButton = new Button("Upload");
        //Button tipsButton = new Button("Tips");
        navBar.setPadding(new Insets(10, 10, 20, 10));
        navBar.getChildren().addAll(homeButton,recordButton, transactionsButton, budgetButton,chartButton,uploadButton);
        navBar.setAlignment(Pos.CENTER);
        navBar.setId("nav");
        // Navigation Button Actions
        homeButton.setOnAction(e -> {
            HomeScene homeScene = new HomeScene(primaryStage, daoManager);
            primaryStage.setScene(homeScene.getScene());
        });
        transactionsButton.setOnAction(e -> {
            TransactionsScene transactionsScene = new TransactionsScene(primaryStage, daoManager);
            primaryStage.setScene(transactionsScene.getScene());
        });
        recordButton.setOnAction(e -> {
            RecordScene record = new RecordScene(primaryStage, daoManager);
            primaryStage.setScene(record.getScene());
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
            UploadScene uploadScene = new UploadScene(primaryStage, daoManager);
            primaryStage.setScene(uploadScene.getScene());
         });
        tipsLayout.getChildren().addAll(navBar);
        
        //include page title
        Label titleLabel = new Label("");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        titleLabel.setAlignment(Pos.CENTER);
        tipsLayout.getChildren().addAll(titleLabel);
        // logic for code - create list to store tips returned from chartdao 
        //dont use daomanager - get null point exception
        dataFetch= new ChartsDataFetch();
        Map<String, String> categoryTips = new HashMap<>();
        try {
            categoryTips = dataFetch.getCategoryTips();
            titleLabel.setText("Tips");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Add category tips to the layout
        for (String category : categoryTips.keySet()) {
            Label tipLabel = new Label(categoryTips.get(category));
            // adding icons for decor
            Image iconImage = new Image(getClass().getResource("../resources/images/idea-bulb-icon.png").toExternalForm());
            ImageView iconView = new ImageView(iconImage);iconView.setFitWidth(16); 
            iconView.setFitHeight(16);
            tipLabel.setGraphic(iconView);
            tipLabel.setContentDisplay(ContentDisplay.LEFT); // Align the icon to the left of the label text
            tipsLayout.getChildren().add(tipLabel);
        }
        //insights
        VBox insightsLayout = new VBox(10);
        insightsLayout.setPadding(new Insets(15, 15, 15, 15));
        Label insightsLabel = new Label(" Insights");
        insightsLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        insightsLabel.setAlignment(Pos.CENTER);
        insightsLayout.getChildren().add(insightsLabel);
        // logic for code - create list to store insights returned from chartdao
        
        Map<String, String> insights=new HashMap<String, String>();
        try{
            insights = dataFetch.getTransactionInsights();
        }catch(Exception e){
            AlertBox.show("ERROR","Error fetching Insights");
            e.printStackTrace();
        }
        for(String insight:insights.keySet()){
            Label insightLabel = new Label(insights.get(insight));
            // adding icons for decor
            Image iconImage = new Image(getClass().getResource("../resources/images/light-lightbulb-icon.png").toExternalForm());
            ImageView iconView = new ImageView(iconImage);iconView.setFitWidth(16); 
            iconView.setFitHeight(16);
            insightLabel.setGraphic(iconView);
            insightLabel.setContentDisplay(ContentDisplay.LEFT); // Align the icon to the left of the label text
            insightsLayout.getChildren().add(insightLabel);
        }
        sceneLayout.getChildren().addAll(tipsLayout,insightsLayout);
        
        // Add tips to the layout
        tipsScene = new Scene(sceneLayout, 650, 500);
        tipsScene.getStylesheets().add(getClass().getResource("../resources/styles/modernTeal.css").toExternalForm());
        primaryStage.setTitle("CATFIN - Tips");
    }

    public Scene getScene(){
        return tipsScene;
    }
}
