/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package issuetrackinglite;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 *
 * @author anowak
 */
public class IssueTrackingLiteController implements Initializable {
    
    @FXML
    private Label messageBar;
    
    @FXML
    private void newIssueFired(ActionEvent event) {
        System.out.println("You clicked newIssue!");
        messageBar.setText("You clicked newIssue!");
    }
    
    @FXML
    private void saveIssueFired(ActionEvent event) {
        System.out.println("You clicked saveIssue!");
        messageBar.setText("You clicked saveIssue!");
    }
    
    @FXML
    private void deleteIssueFired(ActionEvent event) {
        System.out.println("You clicked deleteIssue!");
        messageBar.setText("You clicked deleteIssue!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}
