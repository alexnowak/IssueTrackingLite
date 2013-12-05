/**
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package issuetrackinglite;

import issuetrackinglite.model.Issue;
import issuetrackinglite.model.TrackingService;
import issuetrackinglite.model.TrackingServiceStub;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform;

public class IssueTrackingLiteController {

    static final Logger logger = Logger.getLogger(IssueTrackingLiteController.class.getName());

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    Button newIssue;
    @FXML
    Button deleteIssue;
    @FXML
    Button saveIssue;
    @FXML
    TableView<Issue> table;       // issue details table
    @FXML
    TableColumn<Issue, String> colName;   // Issue name
    @FXML
    TableColumn<Issue, Issue.IssueStatus> colStatus;    // issue status
    @FXML
    TableColumn<Issue, String> colSynopsis;       // issue synopsis
    @FXML
    ListView<String> list;  // project name list
    @FXML
    TextField synopsis;
    @FXML
    Label displayedIssueLabel;          // the displayedIssueLabel will contain a concatenation of the 
    // the project name and the bug id.
    @FXML
    AnchorPane details;
    @FXML
    TextArea descriptionValue;

    private int displayedBugId;      // the id of the bug displayed in the details section.
    private String displayedBugProject; // the name of the project of the bug displayed in the detailed section.

    // Observeable properties  -----------------------------------------------------
    ObservableList<String> projectsView = FXCollections.observableArrayList();

    // An observable list of project names obtained from the model.
    // This is a live list, and we will react to its changes by removing
    // and adding project names to/from our list widget.
    private ObservableList<String> displayedProjectNames;

    // The list of Issue IDs relevant to the selected project. Can be null
    // if no project is selected. This list is obtained from the model.
    // This is a live list, and we will react to its changes by removing
    // and adding Issue objects to/from our table widget.
    private ObservableList<String> displayedIssues;

    private ObservableList<Issue> tableContent = FXCollections.observableArrayList();

    TrackingService model = null;
    private TextField statusValue = new TextField();

    // This listener will listen to changes in the displayedIssues list,
    // and update our table widget in consequence.
    private ListChangeListener<String> projectIssuesListener = null;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize() {
        assert colName != null : "fx:id=\"colName\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert colStatus != null : "fx:id=\"colStatus\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert colSynopsis != null : "fx:id=\"colSynopsis\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert deleteIssue != null : "fx:id=\"deleteIssue\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert descriptionValue != null : "fx:id=\"descriptionValue\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert details != null : "fx:id=\"details\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert displayedIssueLabel != null : "fx:id=\"displayedIssueLabel\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert list != null : "fx:id=\"list\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert newIssue != null : "fx:id=\"newIssue\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert saveIssue != null : "fx:id=\"saveIssue\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert synopsis != null : "fx:id=\"synopsis\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";

        logger.info("initialize: url=" + location);
        
        try {
        configureButtons();
        configureDetails();
        configureTable();
        connectToService();
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"FATAL ERROR: Exiting application. See exception below:",e);
            Platform.exit();
        }
        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        /**
         * Listen to changes in the list selection, and updates the table widget
         * and DeleteIssue and NewIssue buttons accordingly.
         */
        ChangeListener<String> projectItemSelected = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                projectUnselected(oldValue);
                projectSelected(newValue);
            }
        };

        list.getSelectionModel().selectedItemProperty().addListener(projectItemSelected);

        // This listener will listen to changes in the displayedProjectNames list,
        // and update our list widget in consequence.
        ListChangeListener<String> projectNamesListener = new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                while (c.next()) {
                    if (c.wasAdded() || c.wasReplaced()) {
                        for (String p : c.getAddedSubList()) {
                            projectsView.add(p);
                        }
                    }
                    if (c.wasRemoved() || c.wasReplaced()) {
                        for (String p : c.getRemoved()) {
                            projectsView.remove(p);
                        }
                    }
                }
                FXCollections.sort(projectsView);
            }
        };
        displayedProjectNames.addListener(projectNamesListener);
    }

    /**
     * Called when the NewIssue button is fired.
     *
     * @param event the action event.
     */
    public void newIssueFired(ActionEvent event) {
        final String selectedProject = getSelectedProject();
        if (model != null && selectedProject != null) {
            Issue issue = new Issue(1,2,"aaa",Issue.IssueStatus.NEW,"bla","bla");
            // Select the newly created issue.
            table.getSelectionModel().clearSelection();
            table.getSelectionModel().select(issue);
        }
    }

    /**
     * Called when the DeleteIssue button is fired.
     *
     * @param event the action event.
     */
    public void deleteIssueFired(ActionEvent event) {
        final String selectedProject = getSelectedProject();
        if (model != null && selectedProject != null && table != null) {
            // We create a copy of the current selection: we can't delete
            //    issue while looping over the live selection, since
            //    deleting selected issues will modify the selection.
            final List<?> selectedIssue = new ArrayList<Object>(table.getSelectionModel().getSelectedItems());
            for (Object o : selectedIssue) {
                if (o instanceof Issue) {
                    model.deleteIssue(((Issue) o).getId());
                }
            }
            table.getSelectionModel().clearSelection();
        }
    }

    /**
     * Called when the SaveIssue button is fired.
     *
     * @param event the action event.
     */
    public void saveIssueFired(ActionEvent event) {
        logger.severe("TODO: saveIssueFired called");
        
        /********
        final Issue ref = getSelectedIssue();
        DetailsData data = new DetailsData();
        Issue edited = new Issue(444,2,data.getProjectName(),data.getStatus(),data.getSynopsis(),data.getDescription());
        SaveState saveState = computeSaveState(edited, ref);
        if (saveState == SaveState.UNSAVED) {
            model.saveIssue(ref.getId(), edited.getStatus(),
                    edited.getSynopsis(), edited.getDescription());
        }
        // We refresh the content of the table because synopsis and/or description
        // are likely to have been modified by the user.
        int selectedRowIndex = table.getSelectionModel().getSelectedIndex();
        table.getItems().clear();
        displayedIssues = model.getIssueIds(getSelectedProject());
        for (String id : displayedIssues) {
            final Issue issue = model.getIssue(id);
            table.getItems().add(issue);
        }
        table.getSelectionModel().select(selectedRowIndex);

        updateSaveIssueButtonState();
        ************/ 
    }

    // Connect to the model, get the project's names list, and listen to
    // its changes. Initializes the list widget with retrieved project names.
    private void connectToService() throws SQLException {
        if (model == null) {
            model = new TrackingServiceStub();
            displayedProjectNames = model.getProjectNames();
        }
        projectsView.clear();
        List<String> sortedProjects = new ArrayList<String>(displayedProjectNames);
        Collections.sort(sortedProjects);
        projectsView.addAll(sortedProjects);
        list.setItems(projectsView);
    }


    private static String nonNull(String s) {
        return s == null ? "" : s;
    }

    private void updateBugDetails() {
        final Issue selectedIssue = getSelectedIssue();
        if (details != null && selectedIssue != null) {
            if (displayedIssueLabel != null) {
                displayedBugId = selectedIssue.getId();
                displayedBugProject = selectedIssue.getProjectName();
                displayedIssueLabel.setText(displayedBugId + " / " + displayedBugProject);
            }
            if (synopsis != null) {
                synopsis.setText(nonNull(selectedIssue.getSynopsis()));
            }
            if (statusValue != null) {
                statusValue.setText(selectedIssue.getStatus().toString());
            }
            if (descriptionValue != null) {
                descriptionValue.selectAll();
                descriptionValue.cut();
                descriptionValue.setText(selectedIssue.getDescription());
            }
        } else {
            displayedIssueLabel.setText("");
            displayedBugId = -1;
            displayedBugProject = null;
        }
        if (details != null) {
            details.setVisible(selectedIssue != null);
        }
    }

    private boolean isVoid(Object o) {
        if (o instanceof String) {
            return isEmpty((String) o);
        } else {
            return o == null;
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean equal(Object o1, Object o2) {
        if (isVoid(o1)) {
            return isVoid(o2);
        }
        return o1.equals(o2);
    }

    private static enum SaveState {
        INVALID, UNSAVED, UNCHANGED
    }

    private final class DetailsData {

        public int getId() {
            if (displayedBugId == -1 || isEmpty(displayedIssueLabel.getText())) {
                return -1;
            }
            return displayedBugId;
        }

        public Issue.IssueStatus getStatus() {
            if (statusValue == null || isEmpty(statusValue.getText())) {
                return null;
            }
            return Issue.IssueStatus.valueOf(statusValue.getText().trim());
        }

        public String getProjectName() {
            if (displayedBugProject == null || isEmpty(displayedIssueLabel.getText())) {
                return null;
            }
            return displayedBugProject;
        }

        public String getSynopsis() {
            if (synopsis == null || isEmpty(synopsis.getText())) {
                return "";
            }
            return synopsis.getText();
        }

        public String getDescription() {
            if (descriptionValue == null || isEmpty(descriptionValue.getText())) {
                return "";
            }
            return descriptionValue.getText();
        }
    }

    private SaveState computeSaveState(Issue edited, Issue issue) {
        try {
            // These fields are not editable - so if they differ they are invalid
            // and we cannot save.
            if (!equal(edited.getId(), issue.getId())) {
                return SaveState.INVALID;
            }
            if (!equal(edited.getProjectName(), issue.getProjectName())) {
                return SaveState.INVALID;
            }

            // If these fields differ, the issue needs saving.
            if (!equal(edited.getStatus(), issue.getStatus())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getSynopsis(), issue.getSynopsis())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getDescription(), issue.getDescription())) {
                return SaveState.UNSAVED;
            }
        } catch (Exception x) {
            // If there's an exception, some fields are invalid.
            return SaveState.INVALID;
        }
        // No field is invalid, no field needs saving.
        return SaveState.UNCHANGED;
    }

    private void updateDeleteIssueButtonState() {
        final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
        deleteIssue.setDisable(nothingSelected);
    }

    private void updateSaveIssueButtonState() {
        final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
        if (nothingSelected) {
            saveIssue.setDisable(true);
            return;
        }
        
        logger.severe("TODO: computeSaveState");

        boolean disable = false; //computeSaveState(new DetailsData(), getSelectedIssue()) != SaveState.UNSAVED;
        saveIssue.setDisable(disable);
    }

    /**
     * Return the name of the project currently selected, or null if no project
     * is currently selected.
     *
     */
    public String getSelectedProject() {
        final ObservableList<String> selectedProjectItem = list.getSelectionModel().getSelectedItems();
        final String selectedProject = selectedProjectItem.get(0);
        return selectedProject;
    }

    public Issue getSelectedIssue() {
        List<Issue> selectedIssues = table.getSelectionModel().getSelectedItems();
        if (selectedIssues.size() == 1) {
            final Issue selectedIssue = selectedIssues.get(0);
            return selectedIssue;
        }
        return null;
    }

    // Called when a project is unselected.
    private void projectUnselected(String oldProjectName) {
        logger.finest("projectUnselected called! oldProjectName="+oldProjectName);
        if (oldProjectName != null) {
            displayedIssues.removeListener(projectIssuesListener);
            displayedIssues = null;
            table.getSelectionModel().clearSelection();
            table.getItems().clear();
            if (newIssue != null) {
                newIssue.setDisable(true);
            }
            if (deleteIssue != null) {
                deleteIssue.setDisable(true);
            }
        }
    }

    // Called when a project is selected.
    private void projectSelected(String newProjectName) {
        logger.finest("projectSelected called! newProjectName="+newProjectName);
        
        if (newProjectName != null) {
            table.getItems().clear();
            displayedIssues = model.getIssueIds(newProjectName);
            
            logger.fine("Project \""+newProjectName+"\" has " + displayedIssues.size() + " issue(s).");
            

// TODO: Finish this...           
//            for (String id : displayedIssues) {
//                Issue issue = model.getIssue(id);
//                table.getItems().add(issue);
//            }

            projectIssuesListener = new ListChangeListener<String>() {
                @Override
                public void onChanged(Change<? extends String> c) {
                    while (c.next()) {
                        if (c.wasAdded() || c.wasReplaced()) {
                            logger.finest("Item " + c + " has been added or replaced.");
                            for (String p : c.getAddedSubList()) {
//                                table.getItems().add(model.getIssue(p));
                                table.getItems().add(new Issue(-1,-1,"bla",Issue.IssueStatus.NEW,"bla","bla"));
                            }
                        }
                        if (c.wasRemoved() || c.wasReplaced()) {
                            for (String p : c.getRemoved()) {
                                Issue removed = null;
                                // Issue already removed:
                                // we can't use model.getIssue(issueId) to get it.
                                // we need to loop over the table content instead.
                                // Then we need to remove it - but outside of the for loop
                                // to avoid ConcurrentModificationExceptions.
//                                for (Issue t : table.getItems()) {
//                                    if (t.getId().equals(p)) {
//                                        removed = t;
//                                        break;
//                                    }
//                                }
//                                if (removed != null) {
//                                    table.getItems().remove(removed);
//                                }
                            }
                        }
                    }
                }
            };

            displayedIssues.addListener(projectIssuesListener);
            newIssue.setDisable(false);

            updateDeleteIssueButtonState();
            updateSaveIssueButtonState();
        }
    }

    /**
     * Initialize New/Save/Del buttons. Disable all.
     */
    private void configureButtons() {
        newIssue.setDisable(true);
        saveIssue.setDisable(true);
        deleteIssue.setDisable(true);
    }

    /**
     * Configure the table widget: set up its column, and register the selection
     * changed listener.
     */
    private void configureTable() {
        colName.setCellValueFactory(new PropertyValueFactory<Issue, String>("id"));
        colSynopsis.setCellValueFactory(new PropertyValueFactory<Issue, String>("synopsis"));
        colStatus.setCellValueFactory(new PropertyValueFactory<Issue, Issue.IssueStatus>("status"));

        // In order to limit the amount of setup in Getting Started we set the width
        // of the 3 columns programmatically but one can do it from SceneBuilder.
        colName.setPrefWidth(75);
        colStatus.setPrefWidth(75);
        colSynopsis.setPrefWidth(443);

        colName.setMinWidth(75);
        colStatus.setMinWidth(75);
        colSynopsis.setMinWidth(443);

        colName.setMaxWidth(750);
        colStatus.setMaxWidth(750);
        colSynopsis.setMaxWidth(4430);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setItems(tableContent);
        assert table.getItems() == tableContent;

        ObservableList<Issue> tableSelection = table.getSelectionModel().getSelectedItems();

        // This listener listen to changes in the table widget selection and
        // update the DeleteIssue button state accordingly.
        ListChangeListener<Issue> tableSelectionChanged = new ListChangeListener<Issue>() {
                @Override
                public void onChanged(Change<? extends Issue> c) {
                    
                    logger.fine("Table selection changed.");
//                    updateDeleteIssueButtonState();
//                    updateBugDetails();
//                    updateSaveIssueButtonState();
                }
            };

        tableSelection.addListener(tableSelectionChanged);
    }

    private void configureDetails() {
        details.setVisible(false);

        details.addEventFilter(EventType.ROOT, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (event.getEventType() == MouseEvent.MOUSE_RELEASED || 
                    event.getEventType() == KeyEvent.KEY_RELEASED) {
                    updateSaveIssueButtonState();
                }
            }
        });
    }
}