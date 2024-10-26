package com.panda.scenes;

//package panda.financeapp.scenes;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

//local
import com.panda.controller.CategoryDAO;
import com.panda.controller.DAOManager;
import com.panda.controller.TransactionDAO;
import com.panda.model.Transaction;
import com.panda.utils.AlertBox;
import com.panda.utils.AlertUtils;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.ObservableList;



public class RecordScene {
    private Scene recordScene;
    private Stage primaryStage;
    private TransactionDAO transactionDAO;
    private CategoryDAO categoryDAO;
    private DAOManager daoManager;
    private ObservableList<Transaction> transactionData;
    
    public RecordScene(Stage primaryStage,DAOManager daoManager){
        this.primaryStage = primaryStage;
        this.daoManager = daoManager;
        this.transactionDAO = daoManager.getTransactionDAO();
        this.categoryDAO = daoManager.getCategoryDAO();
        initRecordScene();
    }
    private void initRecordScene() {
        // Create the record Scene
        VBox recordLayout = new VBox(10);
        recordLayout.setPadding(new Insets(10, 10, 10, 10));

        // Navigation Bar
        HBox navBar = new HBox(10);
        Button homeButton = new Button("Home");
        //Button recordButton = new Button("Record");
        Button transactionsButton = new Button("Transactions");
        Button budgetButton = new Button("Budget");
        Button chartButton = new Button("Chart");
        Button uploadButton = new Button("Upload");
        Button tipsButton = new Button("Tips");

        navBar.getChildren().addAll(homeButton, transactionsButton, budgetButton,chartButton,tipsButton,uploadButton);
        navBar.setId("nav");
        navBar.setAlignment(Pos.CENTER);
        // Navigation Button Actions
        homeButton.setOnAction(e->{
            HomeScene homeScene = new HomeScene(primaryStage, daoManager);
            primaryStage.setScene(homeScene.getScene());
        });
        //recordButton.setOnAction(e -> primaryStage.setScene(recordScene));
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
            UploadScene uploadScene = new UploadScene(primaryStage, daoManager);
            primaryStage.setScene(uploadScene.getScene());
         });
         //doesnt really need daomanager as it can call it from its class, included to be consistent.
         tipsButton.setOnAction(e->{
            Tips tipsScene = new Tips(primaryStage,daoManager);
            primaryStage.setScene(tipsScene.getScene());
         });
        

        //To prevent null-point error when transaction data is called first time
        transactionData = FXCollections.observableArrayList();
        loadTransactions();

        // Form to add a new transaction
        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10, 10, 10, 10));
        inputGrid.setVgap(8);
        inputGrid.setHgap(10);
        Label descriptionLabel = new Label("Description:");
        GridPane.setConstraints(descriptionLabel, 0, 0);
        TextField descriptionField = new TextField();
        GridPane.setConstraints(descriptionField, 1, 0);

        Label amountLabel = new Label("Amount:");
        GridPane.setConstraints(amountLabel, 0, 1);
        TextField amountField = new TextField();
        amountField.setPromptText("£0.00");
        GridPane.setConstraints(amountField, 1, 1);

        Label categoryIdLabel = new Label("Category:");
        GridPane.setConstraints(categoryIdLabel, 0, 2);
        ComboBox<String> categoryComboBox = new ComboBox<>();
        GridPane.setConstraints(categoryComboBox, 1, 2);
        categoryComboBox.setPromptText("Select a category");
        populateCategories(categoryComboBox); 

        // New ComboBox for Transaction Type
        Label typeLabel = new Label("Type:");
        GridPane.setConstraints(typeLabel, 0, 3);
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.setItems(FXCollections.observableArrayList("Income", "Expense"));
        typeComboBox.setValue("Expense"); // Default value
        GridPane.setConstraints(typeComboBox, 1, 3);

        Button addTransactionButton = new Button("Add Transaction");
        GridPane.setConstraints(addTransactionButton, 1, 4);
        addTransactionButton.setOnAction(e -> {
            String description = descriptionField.getText();
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText());
            } catch (NumberFormatException ex) {
                AlertBox.show("Error", "Invalid amount format. Please enter a valid amount.");
                return;
            }
            String selectedCategory = categoryComboBox.getValue();
            String type = typeComboBox.getValue();
            if (description.isEmpty() || selectedCategory == null || amount <= 0) {
                AlertBox.show("Error", "Please fill in all required fields.");
                return;
            }
            else{
                int catId = getCategoryID(selectedCategory);
                handleTransaction(description,amount,catId,type);
                // Reset input values to prevent duplicate transactions mistake
                descriptionField.clear();
                amountField.clear();
                categoryComboBox.setValue(null);
                typeComboBox.setValue("Expense"); // Reset to default value
            }

            
        });

        inputGrid.getChildren().addAll(descriptionLabel, descriptionField, amountLabel, amountField, categoryIdLabel, categoryComboBox, typeLabel, typeComboBox, addTransactionButton);

        recordLayout.getChildren().addAll(navBar, inputGrid);
        recordScene = new Scene(recordLayout, 600, 400);
        recordScene.getStylesheets().add(getClass().getResource("../resources/styles/modernTeal.css").toExternalForm());
        primaryStage.setTitle("CATFIN - record");
    }
    public Scene getScene(){
        return recordScene;
    }

    private void handleTransaction(String description, double amount, int catId, String type) {
        Transaction transaction = new Transaction(0, description, amount, new Date(), catId, type);
                try {
                    transactionDAO.addTransaction(transaction);
                    transactionData.add(transaction);
                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction saved successfully ");
                    
                } catch (SQLException ex) {
                    AlertBox.show( "Error", "Failed to add transaction: " + ex.getMessage());
                }
    }
    private void populateCategories(ComboBox<String> categoryComboBox) {
        try {
            List<String> categories = categoryDAO.getAllCategoryNames();
            categoryComboBox.getItems().addAll(categories);
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.show( "ERROR", "Failed to load categories.");
        }
    }
    //load id from name
    private int getCategoryID(String categoryName) {
        try {
            return categoryDAO.getCategoryIdByName(categoryName);
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.show( "ERROR", "Failed to get category ID.");
            return -1;
        }
    }
    private void loadTransactions() {
        try {
            List<Transaction> transactions = transactionDAO.getAllUserTransactions();
            transactionData.addAll(transactions);
        } catch (SQLException ex) {
            AlertBox.show( "ERROR", "Failed to load transactions: " + ex.getMessage());
        }
    }
}