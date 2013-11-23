/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package issuetrackinglite.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author anowak
 */
    // A Issue stub.
    public class IssueStub implements ObservableIssue {
        private final SimpleStringProperty id;
        private final SimpleStringProperty projectName;
        private final SimpleStringProperty title;
        private final SimpleStringProperty description;
        private final SimpleObjectProperty<Issue.IssueStatus> status =
                new SimpleObjectProperty<Issue.IssueStatus>(Issue.IssueStatus.NEW);

        IssueStub(String projectName, String id) {
            this(projectName, id, null);
        }
        IssueStub(String projectName, String id, String title) {
           // assert projectNames.contains(projectName);
            //assert ! projectsMap.get(projectName).contains(id);
            //assert ! issuesMap.containsKey(id);
            this.projectName = new SimpleStringProperty(projectName);
            this.id = new SimpleStringProperty(id);
            this.title = new SimpleStringProperty(title);
            this.description = new SimpleStringProperty("");
        }

        @Override
        public Issue.IssueStatus getStatus() {
            return status.get();
        }

        @Override
        public String getId() {
            return id.get();
        }

        @Override
        public String getProjectName() {
            return projectName.get();
        }

        @Override
        public String getSynopsis() {
            return title.get();
        }

        public void setSynopsis(String title) {
            this.title.set(title);
        }

        @Override
        public String getDescription() {
            return description.get();
        }

        public void setDescription(String description) {
            this.description.set(description);
        }

        public void setStatus(Issue.IssueStatus issueStatus) {
            this.status.set(issueStatus);
        }

        @Override
        public ObservableValue<String> idProperty() {
            return id;
        }

        @Override
        public ObservableValue<String> projectNameProperty() {
            return projectName;
        }

        @Override
        public ObservableValue<Issue.IssueStatus> statusProperty() {
            return status;
        }

        @Override
        public ObservableValue<String> synopsisProperty() {
            return title;
        }

        @Override
        public ObservableValue<String> descriptionProperty() {
            return description;
        }
    }