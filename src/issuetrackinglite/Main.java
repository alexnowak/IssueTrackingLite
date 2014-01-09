/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 */
package issuetrackinglite;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Handler;
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
        logger.info("Starting JavaFX application. Logging Level: " + logger.getLevel());
        logger.info("Logger Name: " + logger.getName());
        logger.info("Number of logging handlers: " + logger.getParent().getHandlers().length);
        for (Handler h : logger.getParent().getHandlers()) {
            logger.info(" - "+h.getClass().getName() + " - "+h.getLevel());
        }
        logger.severe("--SEVERE");
        logger.warning("WARNING");
        logger.info("INFO");
        logger.config("CONFIG");
        logger.fine("FINE");
        logger.finer("FINER");
        logger.finest("FINEST");

  //      System.getProperties().list(System.out);

        try {
            AnchorPane page = (AnchorPane) FXMLLoader.load(Main.class.getResource("IssueTrackingLite.fxml"));
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Issue Tracking Lite");
            primaryStage.show();
        } catch (IOException ex) {
            logger.log(Level.SEVERE,"JavaFX Error", ex);
        } catch (RuntimeException re ) {
            logger.log(Level.SEVERE,"Dude!!",re);
        }
    }

    @Override
    public void stop() throws Exception {
        logger.info("Stopping JavaFX application.");
    }

}
