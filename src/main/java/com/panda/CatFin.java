package com.panda;


import com.panda.controller.SessionManager;
import com.panda.scenes.LoginScene;
import com.panda.utils.ConfirmBox;

import javafx.application.Application;
import javafx.scene.image.Image;

//
import javafx.stage.Stage;

public class CatFin extends Application {
    
    //private DAOmanager daoManager;
    
    
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        //daoManager =new DAOmanager();
        
        primaryStage.setOnCloseRequest(e->{
            e.consume();
            closeProgram(primaryStage);
        });
        primaryStage.setTitle("CATFIN");
        //setting icon of window
        Image icon = new Image(getClass().getResourceAsStream("resources/images/money-transfer-icon.png"));
        primaryStage.getIcons().add(icon);
        //initializing homeScene
        LoginScene loginScene = new LoginScene(primaryStage);
        //HomeScene homeScene = new HomeScene(primaryStage,daoManager);
        //TransactionsScene transactionsScene = new TransactionsScene(primaryStage, daoManager);
        primaryStage.setScene(loginScene.getScene());
        primaryStage.show();
    }
    
    

    

    public static void main(String[] args) {
        launch(args);
    }
    private void closeProgram(Stage primaryStage) {
        boolean result = ConfirmBox.display("Exit program", "Are you sure you want to exit program?");
        if(result) {
            SessionManager.stopSession();
            primaryStage.close();
        }
        
    }
      
}