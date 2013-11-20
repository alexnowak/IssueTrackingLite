/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 */
package issuetrackinglite;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
    static Logger logger = Logger.getLogger(Main.class.getName());
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        logger.log(Level.INFO,"Launching JavaFX application.");
        Application.launch(Main.class, (java.lang.String[])null);
    }
    
    @Override
    public void stop() throws Exception{
        logger.log(Level.INFO,"Stopping JavaFX application.");
    }
    
    @Override
    public void start(Stage primaryStage) {
        logger.log(Level.INFO,"Starting JavaFX application.");
        try {
            AnchorPane page = (AnchorPane) FXMLLoader.load(Main.class.getResource("IssueTrackingLite.fxml"));
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Issue Tracking Lite Sample");
            primaryStage.show();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }
}