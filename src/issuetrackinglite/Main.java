/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 */
package issuetrackinglite;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
    static final Logger logger = Logger.getLogger(Main.class.getName());
    
    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting JavaFX application.");
        logger.severe("SEVERE");
        logger.warning("WARNING");
        logger.info("INFO");
        logger.config("CONFIG");
        logger.fine("FINE");
        logger.finer("FINER");
        logger.finest("FINEST");
        
        System.getProperties().list(System.out);
      
        try {
            AnchorPane page = (AnchorPane) FXMLLoader.load(Main.class.getResource("IssueTrackingLite.fxml"));
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Issue Tracking Lite Sample");
            primaryStage.show();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "JavaFX Error", ex);
            if (ex instanceof InvocationTargetException) {
                logger.severe("InvocationTargetException: Cause: "+ ((InvocationTargetException)ex).getCause());
            }
        }
    }

    @Override
    public void stop() throws Exception{
        logger.log(Level.INFO,"Stopping JavaFX application.");
    }
 
}