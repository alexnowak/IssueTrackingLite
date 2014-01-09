/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fxsamples;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
 
class TFListCell extends ListCell<String> {
 
    private TextField textField;
 
    @Override
    public void startEdit() {
        if (!isEditable() || !getListView().isEditable()) {
            return;
        }
        super.startEdit();
 
        if (isEditing()) {
            if (textField == null) {
                textField = new TextField(getItem());
                textField.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        commitEdit(textField.getText());
                    }
                });
            }
        }
 
        textField.setText(getItem());
        setText(null);
 
        setGraphic(textField);
        textField.selectAll();
    }
 
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        
        if (isEmpty()) {
            setText(null);
            setGraphic(null);
        } else {
            if (!isEditing()) {
                if (textField != null) {
                    setText(textField.getText());
                } else {
                    setText(item);
                }
                setGraphic(null);
            }
        }
    }
}
 
public class ListViewEditSample extends Application {
 
    @Override
    public void start(Stage stage) {
        ObservableList<String> items = FXCollections.observableArrayList(
                "A", "B", "C", "D", "E"
        );
        items.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends String> change) {
                System.out.println(change);
            }
        });
 
        StackPane root = new StackPane();
        ListView<String> list = new ListView<>(items);
        list.setEditable(true);
        list.setCellFactory(new Callback<ListView<String>, 
            ListCell<String>>() {
                @Override 
                public ListCell<String> call(ListView<String> list) {
                    return new TFListCell();
                }
            }
        );
        root.getChildren().add(list);
 
        Scene scene = new Scene(root, 200, 200);
 
        stage.setScene(scene);
        stage.show();
    }
 
    public static void main(String... args) {
        launch(args);
    }
}