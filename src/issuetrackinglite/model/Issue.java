/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package issuetrackinglite.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author anowak
 */

    // A Issue.
    public class Issue  {
        public static enum IssueStatus {

            NEW, OPENED, FIXED, CLOSED
        }
        private SimpleIntegerProperty id;
        private int projId;
        private SimpleStringProperty synopsis;
        private SimpleStringProperty description;
        private SimpleObjectProperty<IssueStatus> status;

        /**
         * Constructor with Issue properties.
         * 
         * @param id
         * @param projId
         * @param status
         * @param synopsis
         * @param description 
         */
        public Issue(int id, int projId, IssueStatus status, String synopsis, String description) {
            this.id = new SimpleIntegerProperty(id);
            this.projId = projId;
            this.synopsis = new SimpleStringProperty(synopsis);
            this.description = new SimpleStringProperty(description);
            this.status = new SimpleObjectProperty<IssueStatus>(status); 
        }

        public Issue.IssueStatus getStatus() {
            return status.get();
        }

        public int getId() {
            return id.get();
        }

        public void setId(int id) {
            this.id.set(id);
        }
        
        public int getProjId() {
            return projId;
        }

        public void setProjId(int projId) {
            this.projId = projId;
        }

        public String getSynopsis() {
            return synopsis.get();
        }

        public void setSynopsis(String synopsis) {
            this.synopsis.set(synopsis);
        }

        public String getDescription() {
            return description.get();
        }

        public void setDescription(String description) {
            this.description.set(description);
        }

        public void setStatus(IssueStatus issueStatus) {
            this.status.set(issueStatus);
        }

        public ObservableIntegerValue idProperty() {
            return id;
        }

        public ObservableValue<IssueStatus> statusProperty() {
            return status;
        }

        public ObservableValue<String> synopsisProperty() {
            return synopsis;
        }

        public ObservableValue<String> descriptionProperty() {
            return description;
        }
        
        public String toString() {
            return "ID"+getId()+" Status: "+getStatus()+" Synopsis: "+getSynopsis() + " Description: " + getDescription();
        }
    }