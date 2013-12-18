/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package issuetrackinglite.model;

import java.text.NumberFormat;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

/**
 *
 * @author Alex
 */
public class ProjectCell extends ListCell<Project> {
        private TextField textField;

        @Override
        public void startEdit() {
            super.startEdit();

            System.out.println("startEdit called");

          if (textField == null) {
              createTextField();
          }
         
          setGraphic(textField);
          setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
          textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            System.out.println("cancelEdit called.");

            setText(getItem().toString());
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        

        @Override
        public void updateItem(Project item, boolean empty) {
            super.updateItem(item, empty);
            System.out.println("updateItem: project=" + item + " empty=" + empty);

            setText(item == null ? "" : item.getName() );

//            if (item != null) {
//                double value = item.doubleValue();
//
//                setTextFill(value == 0 ? Color.BLACK
//                        : value < 0 ? Color.RED : Color.GREEN);
//            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {

                @Override
                public void handle(KeyEvent t) {
                    System.out.println("handle KeyEvent called: " + t);

                    if (t.getCode() == KeyCode.ENTER) {
                        commitEdit( new Project(textField.getText()));
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    
}
