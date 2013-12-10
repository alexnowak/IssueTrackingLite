/**
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package issuetrackinglite;

import issuetrackinglite.model.Issue;
import issuetrackinglite.model.Project;
import issuetrackinglite.model.TrackingServiceStub;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
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
    TableView<Issue> table = new TableView<>();       // issue details table
    @FXML
    TableColumn<Issue, String> colName;   // Issue name
    @FXML
    TableColumn<Issue, Issue.IssueStatus> colStatus;    // issue status
    @FXML
    TableColumn<Issue, String> colSynopsis;       // issue synopsis
    @FXML
    ListView<Project> projList;  // project name projList
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
//    ObservableList<String> projectsView = FXCollections.observableArrayList();
    // An observable projList of project names obtained from the model.
    // This is a live projList, and we will react to its changes by removing
    // and adding project names to/from our projList widget.
//    private ObservableList<String> displayedProjectNames;
    // The projList of Issue IDs relevant to the selected project. Can be null
    // if no project is selected. This projList is obtained from the model.
    // This is a live projList, and we will react to its changes by removing
    // and adding Issue objects to/from our table widget.
    private ObservableList<String> displayedIssues;

    private ObservableList<Issue> tableContent = FXCollections.observableArrayList();

    TrackingServiceStub model = null;
    private TextField statusValue = new TextField();

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
        assert projList != null : "fx:id=\"list\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert newIssue != null : "fx:id=\"newIssue\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert saveIssue != null : "fx:id=\"saveIssue\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert synopsis != null : "fx:id=\"synopsis\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";

        logger.info("initialize: url=" + location);

        configureButtons();
        configureDetails();
        configureTable();
        try {
            connectToService();
            projList.setItems(model.getProjects());

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "FATAL ERROR: Exiting application. See exception below:", e);
            Platform.exit();
        }

        projList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        /**
         * Listen to changes in the project list selection, and updates the 
         * issue table widget and DeleteIssue and NewIssue buttons accordingly.
         */
        ChangeListener<Project> projChangeListener = new ChangeListener<Project>() {
            @Override
            public void changed(ObservableValue<? extends Project> observable, Project oldValue, Project newValue) {
                logger.finest("Selected Project: " + newValue);
                try {
                    updateIssueTable(newValue);
                    
                    newIssue.setDisable(false);

                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Database error: ", e);
                }
            }
        };

        projList.getSelectionModel().selectedItemProperty().addListener(projChangeListener);
    }

    /**
     * Called when the NewIssue button is fired.
     *
     * @param event the action event.
     */
    public void newIssueFired(ActionEvent event) {
        final Project proj = getSelectedProject();
        if (proj == null) {
            logger.warning("You found a bug! newIssueFired called but no project selected!");
            return;
        }

        Issue issue = new Issue(-1, proj.getId(), Issue.IssueStatus.NEW, "Enter Synopsis", "Enter Description");
        // Select the newly created issue.
        try {
            int newIssueId = model.saveNewIssue(issue);
            issue.setId(newIssueId);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Database Error:", ex);
        }
        table.getItems().add(issue);
        table.getSelectionModel().clearSelection();
        table.getSelectionModel().select(issue);
    }

    /**
     * Called when the DeleteIssue button is fired.
     *
     * @param event the action event.
     */
    public void deleteIssueFired(ActionEvent event) {
        logger.fine("You pressed DELETE ISSUE");
            // We create a copy of the current selection: we can't delete
        //    issue while looping over the live selection, since
        //    deleting selected issues will modify the selection.
        final List<Issue> selectedIssues = new ArrayList<>(table.getSelectionModel().getSelectedItems());
        table.getSelectionModel().clearSelection();
        try {
            for (Issue issue : selectedIssues) {
                // Delete isseu from table data
                table.getItems().remove(issue);
                // Delete issue from data base
                model.deleteIssue(issue);

            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting issue from database.", e);
        }
        details.setVisible(false);
    }

    /**
     * Called when the SaveIssue button is fired.
     *
     * @param event the action event.
     */
    public void saveIssueFired(ActionEvent event) {
        logger.severe("TODO: saveIssueFired called");

        Issue issue = getSelectedIssue();
        // this should not happen, but hey...
        if (issue == null) 
            return;

        try {
            // update selected issue with values from details view
            issue.setDescription(descriptionValue.getText());
            issue.setSynopsis(synopsis.getText());
            model.updateIssue(issue);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Unable to save issue: " + issue, e);
        }

        updateSaveIssueButtonState();
    }

    // Connect to the model, get the project's names projList, and listen to
    // its changes. Initializes the projList widget with retrieved project names.
    private void connectToService() throws SQLException {
        model = new TrackingServiceStub();
    }

    private static String nonNull(String s) {
        return s == null ? "" : s;
    }

    private void updateIssueDetails(Issue issue) throws SQLException {
        if (issue == null)
            return;
        displayedBugId = issue.getId();
        displayedBugProject = model.getProjectName(issue.getProjId());
        displayedIssueLabel.setText("ID" + displayedBugId + " / " + displayedBugProject);
        synopsis.setText(nonNull(issue.getSynopsis()));
        statusValue.setText(issue.getStatus().toString());
        descriptionValue.selectAll();
        descriptionValue.cut();
        descriptionValue.setText(issue.getDescription());
        details.setVisible(true);
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

    private boolean detailsChanged(Issue edited) {
        // If these fields differ, the issue needs saving.
        boolean synopsisChanged = synopsis.getText().compareTo(edited.getSynopsis()) != 0;
        logger.fine("Synopsis changed? " + synopsisChanged);
        if (synopsisChanged)
            return true;
        boolean descriptionChanged = descriptionValue.getText().compareTo(edited.getDescription()) != 0;
        logger.fine("Description changed? " + descriptionChanged);
        return descriptionChanged;
    }

    private void updateDeleteIssueButtonState() {
        final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
        logger.finest("nothingSelected: "+nothingSelected);
        deleteIssue.setDisable(nothingSelected);
    }

    private void updateSaveIssueButtonState() {
        final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
        logger.finest("nothingSelected: "+nothingSelected);
        if (nothingSelected) {
            saveIssue.setDisable(true);
            return;
        }

        boolean changed = detailsChanged(getSelectedIssue());
        saveIssue.setDisable(!changed);
    }

    /**
     * Return the name of the project currently selected, or null if no project
     * is currently selected.
     * 
     * @return Issue selected in issue table.
     *
     */
    public Project getSelectedProject() {
        final ObservableList<Project> selectedProjectItem = projList.getSelectionModel().getSelectedItems();
        final Project selectedProject = selectedProjectItem.get(0);
        return selectedProject;
    }

    public Issue getSelectedIssue() {
        List<Issue> selectedIssues = table.getSelectionModel().getSelectedItems();
        if (selectedIssues.size() == 1) {
            final Issue selectedIssue = selectedIssues.get(0);
            logger.fine("Selected issue: " + selectedIssue);
            return selectedIssue;
        }
        // This should not happen.  If save is pressed, an issue should be selected.
        logger.warning("You have selected " + selectedIssues.size() + " issues. You should select only one!");
        return null;
    }


    ListChangeListener<Issue> issueTableChangeListener = null;
    private void updateIssueTable(Project proj) throws SQLException {
        logger.fine("Updating issue table for selected project \"" + proj + "\"");



        ObservableList<Issue> issues = model.getIssues(proj);
        // avoid listener to fire when removing data model
        if (issueTableChangeListener!=null)
            table.getItems().removeListener(issueTableChangeListener);
        
        table.setItems(issues);

        // This listener will listen to changes in the displayedIssues projList,
        // and update our table widget in consequence.
        ListChangeListener<Issue> issueTableChangeListener = new ListChangeListener<Issue>() {
            @Override
            public void onChanged(Change<? extends Issue> c) {
                if (c == null) {
                    logger.warning("c == null!");
                    return;
                }
                while (c.next()) {
                    logger.finer("Change to IssueTable dected: wasAdded=" + c.wasAdded() + " wasReplaced=" + c.wasReplaced()
                            + " wasUpdated=" + c.wasUpdated() + " wasRemoved=" + c.wasRemoved() + " wasPermuted=" + c.wasPermutated());
                    if (c.wasAdded() || c.wasReplaced()) {
                        for (Issue i : c.getAddedSubList()) {
                            logger.fine("Issue " + i + " has been added or replaced.");
//                                table.getItems().add(model.getIssue(p));
                            table.getItems().add(i);
                        }
                    }
                    if (c.wasRemoved() || c.wasReplaced()) {

                        for (Issue i : c.getRemoved()) {
                            logger.fine("Issue " + i + " has been added or replaced.");
                            table.getItems().remove(i);

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

//        issues.addListener(issueTableChangeListener);

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
     * changed projListener.
     */
    private void configureTable() {
        // Setting Data Properties to Columns
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

        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Issue>() {
            @Override
            public void changed(ObservableValue<? extends Issue> ov, Issue oldValue, Issue newValue) {
                logger.fine("Selected Issue: OLD=" + oldValue + " NEW=" + newValue);
                if (newValue == null)
                    return;
                // Display issue details in the view below the table
                try {
                updateIssueDetails(newValue);
                } catch (SQLException e ) {
                    logger.log(Level.SEVERE,"Database error for issue: " + newValue,e);
                }
                updateDeleteIssueButtonState();
//                    updateBugDetails();
//                    updateSaveIssueButtonState();
            }
        });

    }

    private void configureDetails() {
        details.setVisible(false);

        details.addEventFilter(EventType.ROOT, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (event.getEventType() == MouseEvent.MOUSE_RELEASED
                        || event.getEventType() == KeyEvent.KEY_RELEASED) {
                    logger.info("Details event: "+event.getEventType() + " Event: "+event);
                    updateSaveIssueButtonState();
                }
            }
        });
    }
}
