package com.panda.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
//import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.panda.controller.DAOManager;
import com.panda.model.Transaction;
import com.panda.utils.AlertUtils;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UploadScene {
    private Stage primaryStage;
    private DAOManager daoManager;
    private Scene uploadScene;
    private List<Transaction> transactions;
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MMM-yy");
    private static final int DEFAULT_CATEGORY_ID = 7;
    // private static final String TRANSACTION_PATTERN = 
    //     "(\\d{1,2}-\\w{3}-\\d{2})\\s+" +  // Date
    //     "(.+?)\\s+" +                      // Description (non-greedy match)
    //     "([A-Z]+)\\s+" +                   // Transaction type (DEB, DD, etc.)
    //     "((?:\\d{1,3},)?\\d+\\.\\d{2})\\s*"; // Amount (handles thousands with commas)

    public UploadScene(Stage primaryStage, DAOManager daoManager) {
        this.primaryStage = primaryStage;
        this.daoManager = daoManager;
        initUploadScene();
    }

    private void initUploadScene() {
        VBox uploadLayout = new VBox(10);
        uploadLayout.setPadding(new Insets(10));

        // Navigation Bar
        HBox navBar = new HBox(10);
        Button homeButton = new Button("Home");
        Button recordButton= new Button("Record");
        Button transactionsButton = new Button("Transactions");
        Button budgetButton = new Button("Budget");
        Button chartButton = new Button("Chart");
        //Button uploadButton = new Button("Upload");
        Button tipsButton = new Button("Tips");

        navBar.getChildren().addAll(homeButton,recordButton, transactionsButton, budgetButton,chartButton,tipsButton);
        navBar.setAlignment(Pos.CENTER);
        navBar.setId("nav");
        // Navigation Button Actions
        homeButton.setOnAction(e -> {
            HomeScene homeScene = new HomeScene(primaryStage, daoManager);
            primaryStage.setScene(homeScene.getScene());
        });
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
        //  uploadButton.setOnAction(e->{
        //     UploadScene upload = new UploadScene(primaryStage, daoManager);
        //     primaryStage.setScene(upload.getScene());
        //  });
         //doesnt really need daomanager as it can call it from its class, included to be consistent.
         tipsButton.setOnAction(e->{
            Tips tipsScene = new Tips(primaryStage,daoManager);
            primaryStage.setScene(tipsScene.getScene());
         });

        TextArea statusArea = new TextArea();
        statusArea.setEditable(false);
        statusArea.setPrefRowCount(15);
        statusArea.setText("No file selected");

        Button selectButton = new Button("Select Bank Statement PDF");
        
        selectButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    transactions = processPDF(selectedFile);
                    displayTransactions(transactions, statusArea);
                } catch (Exception ex) {
                    statusArea.setText("Error processing file: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        Button uploadButton = new Button("Upload transactions");
        
        uploadButton.setOnAction(e -> {
            if (transactions.isEmpty() || transactions.size() == 0 || transactions== null) {
                statusArea.setText("No transactions found in the uploaded file.");
                return;
            }
            else{
                handleUploadTransactions(transactions);
            }
            
        });

        uploadLayout.getChildren().addAll(navBar, selectButton, statusArea,uploadButton);
        uploadScene = new Scene(uploadLayout, 600, 400);
        uploadScene.getStylesheets().add(getClass().getResource("../resources/styles/modernTeal.css").toExternalForm());
        primaryStage.setTitle("CatFin Bank Statement Processor");
    }

    public Scene getScene() {
        return uploadScene;
    }

    private List<Transaction> processPDF(File pdfFile) throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            
            // Split text into lines for line-by-line processing
            String[] lines = text.split("\\r?\\n");
            boolean transactionsStarted = false;
            
            for (String line : lines) {
                // Skip lines until we find the transaction section
                if (line.contains("Your Transaction")) {
                    transactionsStarted = true;
                    continue;
                }
                
                if (!transactionsStarted || line.trim().isEmpty() || 
                    line.contains("STATEMENT") || line.contains("Balance")) {
                    continue;
                }
                
                Transaction transaction = parseTransactionLine(line);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }
        }
        
        return transactions;
    }

    private Transaction parseTransactionLine(String line) {
        try {
            // Split line by spaces while preserving quoted strings
            String[] parts = line.trim().split("\\s+");
            if (parts.length < 4) return null; // Skip invalid lines
            
            // Parse date
            String dateStr = parts[0];
            Date date = DATE_FORMATTER.parse(dateStr);
            
            // Parse transaction type
            //String type = parts[1];
            
            // Find amount and determine if it's money in or out
            double amount = 0.0;
            String transactionType = "expense";
            StringBuilder description = new StringBuilder();
            
            boolean foundAmount = false;
            for (int i = 2; i < parts.length; i++) {
                if (parts[i].matches("\\d+\\.\\d{2}")) {
                    // Found amount
                    amount = Double.parseDouble(parts[i]);
                    foundAmount = true;
                    
                    // Check if this is in the "Money In" column
                    if (i < parts.length - 1 && !parts[i + 1].matches("\\d+\\.\\d{2}")) {
                        transactionType = "income";
                    }
                    break;
                } else if (!foundAmount) {
                    // Add to description until we find the amount
                    if (description.length() > 0) description.append(" ");
                    description.append(parts[i]);
                }
            }
            
            return new Transaction(
                description.toString(),
                amount,
                date,
                DEFAULT_CATEGORY_ID,
                transactionType
            );

        } catch (ParseException | NumberFormatException e) {
            System.err.println("Error parsing line: " + line);
            System.err.println("Error details: " + e.getMessage());
            return null;
        }
    }
    
    private void displayTransactions(List<Transaction> transactions, TextArea statusArea) {
        StringBuilder display = new StringBuilder();
        display.append("Found ").append(transactions.size()).append(" transactions:\n\n");
        
        // Sort transactions by date
        transactions.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));
        
        double totalIncome = 0;
        double totalExpense = 0;
        
        for (Transaction t : transactions) {
            if (t.getType() == "income") {
                totalIncome += t.getAmount();
            } else {
                totalExpense += Math.abs(t.getAmount());
            }
            
            display.append(String.format("%s | %-40s | £%8.2f | %s%n",
                DATE_FORMATTER.format(t.getDate()),
                truncateString(t.getDescription(), 40),
                t.getAmount(),
                t.getType()));
        }
        
        display.append("\nSummary:\n");
        display.append(String.format("Total Income:  £%.2f%n", totalIncome));
        display.append(String.format("Total Expense: £%.2f%n", totalExpense));
        display.append(String.format("Net:          £%.2f%n", totalIncome - totalExpense));
        
        statusArea.setText(display.toString());
    }

    private String truncateString(String str, int length) {
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
    private void handleUploadTransactions(List<Transaction> transactions){
        
        int addCounter=0;
        for (Transaction transaction : transactions) {
            try{
            daoManager.getTransactionDAO().addTransaction(transaction);
            addCounter++;
            } catch (SQLException ex) {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Error uploading transactions", "Failed to add transaction: " + ex.getMessage());
                ex.printStackTrace();
                continue; // Skip this transaction and continue with the next one
            }
        }
        System.out.println("Added transactions "+addCounter );
        //check if all transactions were added successfully
        if (addCounter == transactions.size() - 1){
        AlertUtils.showAlert(Alert.AlertType.INFORMATION, "All Transactions saved successfully", "Added " 
        + transactions.size()+ " transactions to database successfully");
    }
        
    }
}