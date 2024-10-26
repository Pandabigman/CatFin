package com.panda.scenes;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

//local utils used
import com.panda.controller.CategoryDAO;
import com.panda.controller.DAOManager;
import com.panda.controller.TransactionDAO;
import com.panda.model.Category;
import com.panda.model.Transaction;
import com.panda.utils.AlertUtils;
import com.panda.utils.ConfirmBox;

///----------------------------------------------//
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
//import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TransactionsScene {
    private Scene transactionsScene;
    private Stage primaryStage;
    private DAOManager daoManager;
    private TransactionDAO transactionDAO;
    private CategoryDAO categoryDAO;

    private ObservableList<Transaction> transactionData;
    private ObservableList<Category> categoryData;
    private TableView<Transaction> transactionTable;

    public TransactionsScene(Stage primaryStage, DAOManager daoManager) {
        this.primaryStage = primaryStage;
        this.daoManager = daoManager;
        this.transactionDAO = daoManager.getTransactionDAO();
        this.categoryDAO = daoManager.getCategoryDAO();

        initTransactionsScene();
    }

    @SuppressWarnings("unchecked")
    private void initTransactionsScene() {
        // Create the All Transactions Scene
        VBox transactionsLayout = new VBox(10);
        transactionsLayout.setPadding(new Insets(10, 10, 10, 10));

        // Label transactionsLabel = new Label("All Transactions");

        // Navigation Bar
        HBox navBar = new HBox(10);
        Button homeButton = new Button("Home");
        Button recordButton = new Button("Record");
        Button budgetButton = new Button("Budget");
        Button chartButton = new Button("Chart");
        Button tipsButton = new Button("Tips");
        Button uploadButton = new Button("Upload");

        navBar.getChildren().addAll(homeButton, recordButton, budgetButton, chartButton, tipsButton, uploadButton);
        navBar.setAlignment(Pos.CENTER);
        navBar.setId("nav");
        
        homeButton.setOnAction(e -> {
            HomeScene homeScene = new HomeScene(primaryStage, daoManager);
            primaryStage.setScene(homeScene.getScene());
        });
        recordButton.setOnAction(e -> {
            RecordScene recordScene = new RecordScene(primaryStage, daoManager);
            primaryStage.setScene(recordScene.getScene());
        });
        // transactionsButton.setOnAction(e ->
        // primaryStage.setScene(transactionsScene));
        budgetButton.setOnAction(e -> {
            BudgetScene budgetScene = new BudgetScene(primaryStage, daoManager);
            primaryStage.setScene(budgetScene.getScene());
        });
        chartButton.setOnAction(e -> {
            ChartsScene chartScene = new ChartsScene(primaryStage, daoManager);
            primaryStage.setScene(chartScene.getScene());
        });
        tipsButton.setOnAction(e -> {
            Tips tipsScene = new Tips(primaryStage, daoManager);
            primaryStage.setScene(tipsScene.getScene());
        });
        uploadButton.setOnAction(e -> {
            UploadScene uploadScene = new UploadScene(primaryStage, daoManager);
            primaryStage.setScene(uploadScene.getScene());
        });

        // table displays data fetched from database
        transactionTable = new TableView<>();
        transactionData = FXCollections.observableArrayList();
        transactionTable.setItems(transactionData);
        // initialising colums for each data type
        TableColumn<Transaction, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

        TableColumn<Transaction, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getAmount()));

        TableColumn<Transaction, Date> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDate()));

        TableColumn<Transaction, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> {
            int categoryId = cellData.getValue().getCategoryId();
            String categoryName = categoryData.stream()
                    .filter(cat -> cat.getId() == categoryId)
                    .map(Category::getName)
                    .findFirst()
                    .orElse("Unknown");
            return new javafx.beans.property.SimpleStringProperty(categoryName);
        });

        TableColumn<Transaction, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));

        // delete individual transactions from table
        TableColumn<Transaction, Void> delColumn = new TableColumn<>("Action");

        // This Callback is used to create a custom cell factory for the "Action" column
        // in the TableView
        // of transactions.
        Callback<TableColumn<Transaction, Void>, TableCell<Transaction, Void>> cellFactory = new Callback<TableColumn<Transaction, Void>, TableCell<Transaction, Void>>() {
            @Override
            public TableCell<Transaction, Void> call(final TableColumn<Transaction, Void> param) {
                return new TableCell<Transaction, Void>() {
                    private final Button btn = new Button("Delete");

                    {
                        btn.setOnAction(e -> {
                            boolean result = ConfirmBox.display("Delete Transaction",
                                    "Are you sure you want to delete this transaction?");
                            if (result) {
                                Transaction transaction = getTableView().getItems().get(getIndex());
                                getTableView().getItems().remove(transaction); // Remove transaction from view
                                try {
                                    transactionDAO.deleteTransaction(transaction);
                                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Success",
                                            "Transaction deleted successfully");
                                } catch (SQLException ex) {
                                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                                            "Failed to delete transaction: " + ex.getMessage());
                                }
                            }
                        });
                    }

                    // not really needed could just loadtransactions after each delete to update,
                    // but would increase queries
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        delColumn.setCellFactory(cellFactory);

        transactionTable.getColumns().addAll(descriptionColumn, amountColumn, dateColumn, categoryColumn, typeColumn,
                delColumn);
        loadCategories();
        loadTransactions();
        Button deleteBtn = new Button("Delete All Transactions");
        deleteBtn.setStyle("-fx-background-color: #ff0000;");
        deleteBtn.setAlignment(Pos.CENTER);
        deleteBtn.setOnAction(e -> {
            boolean result = ConfirmBox.display("Delete All Transactions",
                    "Are you sure you want to delete all transactions. This is irreversible");
            if (result) {
                try {
                    transactionDAO.deleteAllTransactions();
                    loadTransactions();
                } catch (SQLException ex) {
                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Error",
                            "Failed to delete all transactions: " + ex.getMessage());
                }
            }
        });

        transactionsLayout.getChildren().addAll(navBar, transactionTable, deleteBtn);
        transactionsScene = new Scene(transactionsLayout, 800, 500);
        transactionsScene.getStylesheets().add(getClass().getResource("../resources/styles/modernTeal.css").toExternalForm());
        primaryStage.setTitle("CATFIN - Transactions");
    }

    public Scene getScene() {
        return transactionsScene;
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryDAO.getAllCategories();
            categoryData = FXCollections.observableArrayList(categories);
        } catch (SQLException ex) {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Failed to load categories: " + ex.getMessage());
        }
    }

    private void loadTransactions() {
        // System.out.println("Loading transactions");
        // if (transactionDAO == null) {
        // System.out.println("transactionDAO is null");
        // }

        if (transactionTable == null) {
            System.out.println("transactionTable is null");
        }
        try {
            List<Transaction> transactions = transactionDAO.getAllUserTransactions();
            transactionData.setAll(transactions);
        } catch (SQLException ex) {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Error", "Failed to load transactions: " + ex.getMessage());
        }
    }

}