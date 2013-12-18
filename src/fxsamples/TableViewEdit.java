/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fxsamples;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
 
/**
*
* @web http://java-buddy.blogspot.com/
*/
public class TableViewEdit extends Application {
 
  public class Record{
      private SimpleStringProperty fieldMonth;
      private SimpleDoubleProperty fieldValue;
     
      Record(String fMonth, double fValue){
          this.fieldMonth = new SimpleStringProperty(fMonth);
          this.fieldValue = new SimpleDoubleProperty(fValue);
      }
     
      public String getFieldMonth() {
          return fieldMonth.get();
      }
     
      public double getFieldValue() {
          return fieldValue.get();
      }
     
      public void setFieldMonth(String fMonth) {
          fieldMonth.set(fMonth);
      }
     
      public void setFieldValue(Double fValue) {
          fieldValue.set(fValue);
      }
     
  }
 
  private TableView<Record> tableView = new TableView<>();
 
  private ObservableList<Record> dataList =
          FXCollections.observableArrayList(          
              new Record("January", 100),
//              new Record("February", 200),
//              new Record("March", 50),
//              new Record("April", 75),
//              new Record("May", 110),
//              new Record("June", 300),
//              new Record("July", 111),
//              new Record("August", 30),
//              new Record("September", 75),
//              new Record("October", 55),
              new Record("November", 225),
              new Record("December", 99));
 
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
      launch(args);
  }
 
  @Override
  public void start(Stage primaryStage) {
      primaryStage.setTitle("java-buddy.blogspot.com");
     
      Group root = new Group();
     
      tableView.setEditable(true);
      Callback<TableColumn, TableCell> cellFactory =
              new Callback<TableColumn, TableCell>() {
                  public TableCell call(TableColumn p) {
                      System.out.println("call called. TableCol \""+p.getText()+"\"");
                      return new EditingCell();
                  }
              };
 
      TableColumn columnMonth = new TableColumn("Month");
      columnMonth.setCellValueFactory(
              new PropertyValueFactory<Record,String>("fieldMonth"));
 
      TableColumn columnValue = new TableColumn("Value");
      columnValue.setCellValueFactory(
              new PropertyValueFactory<Record,Double>("fieldValue"));
     
      //--- Add for Editable Cell of Value field, in Double
      columnValue.setCellFactory(cellFactory);
      columnValue.setOnEditCommit(
              new EventHandler<TableColumn.CellEditEvent<Record, Double>>() {
                  @Override 
                  public void handle(TableColumn.CellEditEvent<Record, Double> t) {
                        System.out.println("handle called.");

                      ((Record)t.getTableView().getItems().get(
                              t.getTablePosition().getRow())).setFieldValue(t.getNewValue());
                  }
              });
      //---
     
      tableView.setItems(dataList);
      tableView.getColumns().addAll(columnMonth, columnValue);
     
      VBox vBox = new VBox();
      vBox.setSpacing(10);
      vBox.getChildren().add(tableView);
 
      root.getChildren().add(vBox);
     
      primaryStage.setScene(new Scene(root, 300, 250));
      primaryStage.show();
  }
 
  class EditingCell extends TableCell<Record, Double> {
 
      private TextField textField;
     
      public EditingCell() {}
     
      @Override
      public void startEdit() {

          super.startEdit();
          System.out.println("startEdit called.");
         
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
          
          setText(String.valueOf(getItem()));
          setContentDisplay(ContentDisplay.TEXT_ONLY);
      }
 
      @Override
      public void updateItem(Double item, boolean empty) {
          super.updateItem(item, empty);
          
          System.out.println("updateItem called. item="+item+" empty="+empty);
          

          if (empty) {
              setText(null);
              setGraphic(null);
          } else {
              if (isEditing()) {
                  if (textField != null) {
                      textField.setText(getString());
                  }
                  setGraphic(textField);
                  setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
              } else {
                  setText(getString());
                  setContentDisplay(ContentDisplay.TEXT_ONLY);
              }
          }
      }
 
      private void createTextField() {
          textField = new TextField(getString());
          textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()*2);
          textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
             
              @Override
              public void handle(KeyEvent t) {
                   System.out.println("handle KeyEvent called: " + t);

                  if (t.getCode() == KeyCode.ENTER) {
                      commitEdit(Double.parseDouble(textField.getText()));
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
 
}