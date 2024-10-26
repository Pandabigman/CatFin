package com.panda.scenes;

//package panda.financeapp.scenes;

import java.sql.SQLException;
import java.util.List;

import com.panda.controller.BudgetDAO;
import com.panda.controller.CategoryDAO;
import com.panda.controller.DAOManager;
import com.panda.model.Budget;
import com.panda.utils.AlertBox;
import com.panda.utils.AlertUtils;

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

public class BudgetScene {
    private Scene budgetScene;
    private Stage primaryStage;
    private DAOManager daoManager;
    private CategoryDAO categoryDAO;

    public BudgetScene(Stage primaryStage, DAOManager daoManager) {
        this.primaryStage = primaryStage;
        this.daoManager = daoManager;
        this.categoryDAO = daoManager.getCategoryDAO();
        // this.budgetDAO = daoManager.getBudgetDAO();

        initBudgetScene();

    }

    private void initBudgetScene() {
        // Create the Budget Scene (Placeholder for now)
        VBox budgetLayout = new VBox(10);
        budgetLayout.setPadding(new Insets(10, 10, 10, 10));

        HBox budgetHeader = new HBox(10);

        // Navigation Bar
        HBox navBar = new HBox(10);
        Button homeButton = new Button("Home");
        Button recordButton = new Button("Record");
        Button transactionsButton = new Button("Transactions");
        // Button budgetButton = new Button("Budget");
        Button chartButton = new Button("Chart");
        Button tipsButton = new Button("Tips");
        Button uploadButton = new Button("Upload");

        navBar.getChildren().addAll(homeButton, recordButton, transactionsButton, chartButton, tipsButton,
                uploadButton);
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
        // budgetButton.setOnAction(e ->primaryStage.setScene(budgetScene));
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
        // put all the buttons not related to grid here
        budgetHeader.getChildren().addAll(navBar);
        budgetHeader.setAlignment(Pos.CENTER);

        VBox budgetDetailsSection = new VBox(10);
        budgetDetailsSection.setPadding(new Insets(10, 10, 10, 10));
        // creating a form to edit budgets or add to budgets
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // adding input for total income will compare against total expenses in charts

        // Category label and input
        Label categoryLabel = new Label("Category:");
        GridPane.setConstraints(categoryLabel, 0, 0);
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.setPromptText("Select a category");
        populateCategories(categoryComboBox); // Populate ComboBox with categories from DB
        GridPane.setConstraints(categoryComboBox, 1, 0);
        // Budget amount label and input
        Label amountLabel = new Label("Budget Amount (£):");
        GridPane.setConstraints(amountLabel, 0, 1);
        TextField amountInput = new TextField();
        amountInput.setPromptText("Enter budget amount");
        GridPane.setConstraints(amountInput, 1, 1);

        // Submit button
        Button submitButton = new Button("Save Budget");
        GridPane.setConstraints(submitButton, 1, 2);
        // submitButton.setId("submit-button");
        submitButton.setOnAction(e -> {
            String selectedCategory = categoryComboBox.getValue();
            String amount = amountInput.getText();

            if (selectedCategory != null && !amount.isEmpty()) {
                try {
                    double budgetAmount = Double.parseDouble(amount);

                    // Fetch the category_id from the database based on the category name
                    int categoryId = getCategoryID(selectedCategory); // Assume this method is implemented

                    if (categoryId != -1) {
                        // Create a Budget object
                        Budget budget = new Budget(budgetAmount, categoryId);

                        // Use BudgetDAO to save the budget (insert or update)
                        BudgetDAO budgetDAO = new BudgetDAO();
                        budgetDAO.saveUserBudget(budget);

                        AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Success",
                                "Budget saved successfully for " + selectedCategory);

                        // Refresh the budget details section to reflect the updated data
                        refreshBudgetDetailsSection(budgetDetailsSection);
                    } else {
                        AlertBox.show("Category Not Found",
                                "The selected category does not exist.");
                    }
                } catch (NumberFormatException ex) {
                    AlertBox.show("Invalid Amount",
                            "Please enter a valid number for the budget amount.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                AlertUtils.showAlert(Alert.AlertType.WARNING, "Incomplete Data", "Please fill in all fields.");
            }
        });

        // initialize on first request
        refreshBudgetDetailsSection(budgetDetailsSection);
        // Add everything to the grid
        grid.getChildren().addAll(categoryLabel, categoryComboBox, amountLabel, amountInput, submitButton);

        budgetLayout.getChildren().addAll(budgetHeader, grid, budgetDetailsSection);
        budgetScene = new Scene(budgetLayout, 600, 450);
        budgetScene.getStylesheets().add(getClass().getResource("../resources/styles/modernTeal.css").toExternalForm());
        primaryStage.setTitle("CATFIN - Budget");
    }

    public Scene getScene() {
        return budgetScene;
    }

    // load without id
    // This method populateCategories is responsible for populating a JavaFX
    // ComboBox with category
    // names retrieved from the database using the CategoryDAO.
    private void populateCategories(ComboBox<String> categoryComboBox) {
        try {
            List<String> categories = categoryDAO.getAllCategoryNames();
            categoryComboBox.getItems().addAll(categories);
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.show("Error", "Failed to load categories.");
        }
    }

    // load id from name
    private int getCategoryID(String categoryName) {
        try {
            return categoryDAO.getCategoryIdByName(categoryName);
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.show("Error", "Failed to get category ID.");
            return -1;
        }
    }

    /**
     * The `refreshBudgetDetailsSection` method clears existing budget details and
     * displays current
     * budget information for each category fetched from the database.
     * 
     * @param budgetDetailsSection The `budgetDetailsSection` parameter is a VBox
     *                             (Vertical Box) in
     *                             JavaFX that is used to display the budget details
     *                             in a vertical layout. The method
     *                             `refreshBudgetDetailsSection` clears any existing
     *                             content in the VBox and then populates it with
     *                             current budget information fetched from the
     *                             database for each
     */
    private void refreshBudgetDetailsSection(VBox budgetDetailsSection) {
        budgetDetailsSection.getChildren().clear(); // Clear existing budget details

        try {
            Label budInfo = new Label("Current Budget information");
            budInfo.setStyle("-fx-font-size: 20px;");
            budgetDetailsSection.getChildren().add(budInfo);

            // Fetch and display budget details for each category
            List<Budget> budgets = new BudgetDAO().getUserBudgets();
            for (Budget budget : budgets) {
                String categoryName = categoryDAO.getCategoryNamebyID(budget.getCategoryId());

                Label catLabel = new Label(categoryName + ":");
                Label budgetAmountLabel = new Label("Budget Amount: £" + budget.getAmount());

                HBox budgetDetailsRow = new HBox(10);
                budgetDetailsRow.getChildren().addAll(catLabel, budgetAmountLabel);
                budgetDetailsRow.setAlignment(Pos.CENTER_LEFT);

                budgetDetailsSection.getChildren().add(budgetDetailsRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBox.show("Error", "Failed to load budget details.");
        }
    }
}