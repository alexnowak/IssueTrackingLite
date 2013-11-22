/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 */
package issuetrackinglite;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.Enumeration;
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
/**********    
    public static void main(String[] args) throws IOException {
        LogManager.getLogManager().readConfiguration();
        
        logger.severe("SEVERE");
        logger.warning("WARNING");
        logger.info("INFO");
        logger.config("CONFIG");
        logger.fine("FINE");
        logger.finer("FINER");
        logger.finest("FINEST");
        
        logger.info("Launching JavaFX application.");
        logger.info("CWD: "+System.getProperty("user.dir"));

        System.out.println("LogLevel: "+logger.getLevel());
        for ( Enumeration p = System.getProperties().elements(); p.hasMoreElements();) 
            System.out.println(p.nextElement());
        System.out.println("Classpath: " + System.getProperty("java.class.path"));
        System.out.println("java.util.logging.config.file: "+System.getProperty("java.util.logging.config.file"));
        
        Handler handlers[] = logger.getHandlers();
        System.out.println("Found "+handlers.length+" logging handlers.");
        for (int i=0; i<handlers.length; i++)
            System.out.println("["+i+"] "+handlers[i]);

        
        System.out.println("LogManager props: "+LogManager.getLogManager().getProperty("handlers"));
        

        Application.launch(Main.class, (java.lang.String[])args);
    }
   
 ****/
    
    @Override
    public void stop() throws Exception{
        logger.log(Level.INFO,"Stopping JavaFX application.");
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
        LogManager.getLogManager().readConfiguration();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        logger.log(Level.INFO,"Starting JavaFX application.");
        
        logger.severe("SEVERE");
        logger.warning("WARNING");
        logger.info("INFO");
        logger.config("CONFIG");
        logger.fine("FINE");
        logger.finer("FINER");
        logger.finest("FINEST");
        
        logger.info("Launching JavaFX application.");
        logger.info("CWD: "+System.getProperty("user.dir"));

        System.out.println("LogLevel: "+logger.getLevel());
        System.out.println("Classpath: " + System.getProperty("java.class.path"));
        System.out.println("java.util.logging.config.file: "+System.getProperty("java.util.logging.config.file"));
        System.getProperties().list(System.out);
        //for ( Enumeration<String,String> p = (Enumeration<String,String>) System.getProperties().elements(); key.hasMoreElements();) 
        //    System.out.println(key + ": "+System.getProperty(key.nextElement()));
        
        Handler handlers[] = logger.getHandlers();
        System.out.println("Found "+handlers.length+" logging handlers.");
        for (int i=0; i<handlers.length; i++)
            System.out.println("["+i+"] "+handlers[i]);

        
        System.out.println("LogManager props: "+LogManager.getLogManager().getProperty("handlers"));

        
        
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