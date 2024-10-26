package com.panda.scenes;

import java.util.Map;

//DAO interactions
import com.panda.controller.ChartsDataFetch;
import com.panda.controller.DAOManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
//import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
// barChart Structure
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class ChartsScene {
    private Scene chartScene;
    private Stage primaryStage;
    private DAOManager daoManager;

    public ChartsScene(Stage primaryStage, DAOManager daoManager) {
        this.primaryStage = primaryStage;
        this.daoManager = daoManager;

        initChartsScene();
    }

    // charts scene to show user expenses so far against how much is budgeted
    private void initChartsScene() {
        VBox chartLayout = new VBox(10);
        chartLayout.setPadding(new Insets(10, 10, 10, 10));

        HBox chartHeader = new HBox(20);

        // Label chartsLabel = new Label("Chart");

        // Navigation Bar
        HBox navBar = new HBox(10);
        Button homeButton = new Button("Home");
        Button recordButton = new Button("Record");
        Button transactionsButton = new Button("Transactions");
        Button budgetButton = new Button("Budget");
        // Button chartButton = new Button("Chart");
        Button tipsButton = new Button("Tips");
        Button refreshBtn = new Button("Refresh Chart");
        Button uploadButton = new Button("Upload ");

        // navBar.setPadding(new Insets(10, 10, 50, 10));
        navBar.getChildren().addAll(homeButton, recordButton, transactionsButton, budgetButton, tipsButton, refreshBtn, uploadButton);
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
        budgetButton.setOnAction(e -> {
            BudgetScene budgetScene = new BudgetScene(primaryStage, daoManager);
            primaryStage.setScene(budgetScene.getScene());
        });
        // chartButton.setOnAction(e -> primaryStage.setScene(chartScene));
        tipsButton.setOnAction(e -> {
            Tips tipsScene = new Tips(primaryStage, daoManager);
            primaryStage.setScene(tipsScene.getScene());
        });
        uploadButton.setOnAction(e -> {
            UploadScene uploadScene = new UploadScene(primaryStage, daoManager);
            primaryStage.setScene(uploadScene.getScene());
        });
        // building the bar chart of budget against expenses
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        refreshBtn.setOnAction(e -> refreshChart(barChart));
        // title
        barChart.setTitle("Budget vs Expenses by Category");
        // barChart.setStyle("-fx-chart-title-text: white; -fx-font-weight: bold;");
        xAxis.setLabel("Category");
        yAxis.setLabel("Amount");
        // barChart.setStyle("-fx-background-color: #FFFFFF;");
        // Create the data series

        refreshChart(barChart);

        // chartHeader.getChildren().addAll(chartsLabel,refreshBtn);
        chartLayout.getChildren().addAll(chartHeader, navBar, barChart);

        chartScene = new Scene(chartLayout, 800, 600);
        chartScene.getStylesheets().add(getClass().getResource("../resources/styles/chartstyle.css").toExternalForm());
        primaryStage.setTitle("CATFIN - Chart");
    }

    public Scene getScene() {
        return chartScene;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    /// helper functions and methods
    // refresh chart
    @SuppressWarnings("unchecked")
    private void refreshChart(BarChart<String, Number> barChart) {
        // Clear existing data
        barChart.getData().clear();
        try {
            ChartsDataFetch dataFetcher = new ChartsDataFetch();
            Map<String, Double> budgetData = dataFetcher.getBudgetData();
            Map<String, Double> expenseData = dataFetcher.getExpenseData();

            XYChart.Series<String, Number> budgetSeries = new XYChart.Series<>();
            budgetSeries.setName("Budget");

            XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
            expenseSeries.setName("Expenses");

            // Debugging
            // System.out.println("Budget Data: " + budgetData);
            // System.out.println("Expense Data: " + expenseData);

            // Populate budget series
            for (Map.Entry<String, Double> entry : budgetData.entrySet()) {
                String categoryName = entry.getKey();
                double budgetAmount = entry.getValue();
                budgetSeries.getData().add(new XYChart.Data<>(categoryName, budgetAmount));
            }

            // Populate expense series
            for (Map.Entry<String, Double> entry : expenseData.entrySet()) {
                String categoryName = entry.getKey();
                double expenseAmount = entry.getValue();
                expenseSeries.getData().add(new XYChart.Data<>(categoryName, expenseAmount));
            }

            // Add the series to the bar chart
            barChart.getData().addAll(budgetSeries, expenseSeries);
            barChart.getStylesheets()
                    .add(getClass().getResource("../resources/styles/chartstyle.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}