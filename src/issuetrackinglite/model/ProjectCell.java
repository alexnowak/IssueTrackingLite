/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package issuetrackinglite.model;

import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Alex
 */
public class ProjectCell extends ListCell<Project> {
    static final Logger logger = Logger.getLogger(ProjectCell.class.getName());

    private TextField textField;
    //private Project oldProject;

    public ProjectCell(Project p) {
        setItem(p);
        setText(p.getName());
    }

    @Override
    public void startEdit() {
        super.startEdit();

        logger.fine("startEdit called: Item="+getItem().getName()+" Id="+getItem().getProjId());

        if (textField == null) {
            createTextField();
        }

        setGraphic(textField);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.selectAll();
    }
    
    @Override
    public void commitEdit(Project newValue) {
        super.commitEdit(newValue);
        logger.fine("commitEdit: newValue="+newValue + " ID="+newValue.getProjId());
        
        if (newValue.getName().equals(getItem().getName())) {
            logger.fine("Project name has not changed.");
            return;
        }
       
        logger.fine("Renaming Project FROM \""+ getItem().getName() + "\" TO \"" + newValue.getName() +"\"...");
                
    }
    
    @Override
    public void cancelEdit() {
        super.cancelEdit();

        logger.fine("cancelEdit called: Item="+getItem());

        setText(getItem().toString());
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    
    @Override
    public void updateItem(Project item, boolean empty) {
        super.updateItem(item, empty);
        
        if (item==null || empty)
            return;
        logger.fine("updateItem: project=" + item + " empty=" + empty);
        setText(item.getName());

    }
    
    
    private void createTextField() {
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                logger.finer("handle KeyEvent called: " + t);

                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(new Project(textField.getText()));
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
