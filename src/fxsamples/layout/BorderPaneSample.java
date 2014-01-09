/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fxsamples.layout;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Alex
 */
public class BorderPaneSample extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane border = new BorderPane();
        HBox hboxTop = addHBox("darkslateblue");
        HBox hboxButtom = addHBox("red");
        VBox vboxLeft = addVBox("green");
        VBox vboxRight = addVBox("yellow");
        
        border.setTop(hboxTop);
        border.setLeft(vboxLeft);
        border.setCenter(addGridPane());
        border.setRight(vboxRight);
        border.setBottom(hboxButtom);
              
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.show();
    }

    private HBox addHBox(String bgColor) {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15,12,15,12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: "+bgColor);
        
        Button buttonCurrent = new Button("Current");
        buttonCurrent.setPrefSize(100, 20);
        hbox.getChildren().add(buttonCurrent);
        
        Button buttonProjected = new Button("Projected");
        buttonProjected.setPrefSize(100, 20);
        hbox.getChildren().add(buttonProjected);

        return hbox;
    }

    private VBox addVBox(String bgColor) {
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-color: "+bgColor);
        vbox.setPadding(new Insets(15,12,15,12));
        vbox.setSpacing(20);
        
        Button b1 = new Button("Button 1");
        Button b2 = new Button("Button 2");
                
        vbox.getChildren().addAll(b1,b2);
        
        return vbox;
    }

    private GridPane addGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        // Category in column 2, row 1
        Text category = new Text("Sales:");
        category.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        grid.add(category, 1, 0);

        // Title in column 3, row 1
        Text chartTitle = new Text("Current Year");
        chartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        grid.add(chartTitle, 2, 0);

        // Subtitle in columns 2-3, row 2
        Text chartSubtitle = new Text("Goods and Services");
        grid.add(chartSubtitle, 1, 1, 2, 1);

        System.out.println("CWD: "+System.getProperty("user.dir"));
        // House icon in column 1, rows 1-2
        Image imgHouse = new Image("file:"+System.getProperty("user.dir")+"/res/house.png",100,100,true,true);
        System.out.println("Image isError="+imgHouse.isError());
        ImageView imageHouse = new ImageView(imgHouse );
        grid.add(imageHouse, 0, 0, 1, 2);
        
        // Left label in column 1 (bottom), row 3
        Text goodsPercent = new Text("Goods\n80%");
        GridPane.setValignment(goodsPercent, VPos.CENTER);
        grid.add(goodsPercent, 0, 2);
        
                

        // Chart in columns 2-3, row 3
        ImageView imageChart = new ImageView( new Image("file:"+System.getProperty("user.dir")+"/res/piechart.png"));
        grid.add(imageChart, 1, 2, 2, 1);

        // Right label in column 4 (top), row 3
        Text servicesPercent = new Text("Services\n20%");
        GridPane.setValignment(servicesPercent, VPos.BOTTOM);
        grid.add(servicesPercent, 3, 2);

        grid.setGridLinesVisible(true);

        return grid;
    }
    
    
    
}
