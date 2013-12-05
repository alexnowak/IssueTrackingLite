/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 */
package issuetrackinglite.model;

import issuetrackinglite.db.Database;
import issuetrackinglite.model.Issue.IssueStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class TrackingServiceStub implements TrackingService {
    static final Logger logger = Logger.getLogger(TrackingServiceStub.class.getName());

    // You add a project by adding an entry with an empty observable array list
    // of issue IDs in the projects Map.
    ObservableMap<Integer, Project> projectsMap;
    ObservableMap<Integer, Issue> issuesMap;
//    ObservableList<String> projectNames;

    AtomicInteger issueCounter = new AtomicInteger(0);

    
    public TrackingServiceStub() throws SQLException {
        
        final TreeMap<Integer,Project> projects = new TreeMap<Integer,Project>();
        projectsMap = FXCollections.observableMap(projects);
        
        // retrieve projects from db
        Database db=null;
        db = Database.getInstance();
        try (Statement s = db.getConnection().createStatement() ) {
            ResultSet rs = s.executeQuery("select * from Project");
            int nProj=0;
            while (rs.next()) {
                Integer projId = rs.getInt("Id");
                Project proj = new Project(projId, rs.getString("Name"));
                projectsMap.put(projId, proj);
                logger.fine("\t["+ (nProj++) + "] Id" + proj.getId() + proj.getName());
            }
            logger.info("Number of projects in db: "+nProj);
 
        
    //        projectNames = FXCollections.<String>observableArrayList();
    //        projectNames.addAll(projectsMap.keySet());

                // The projectNames list is kept in sync with the project's map by observing
            // the projectsMap and modifying the projectNames list in consequence.
            MapChangeListener<Integer,Project> projectsMapChangeListener = new MapChangeListener<Integer,Project>() {
                @Override
                public void onChanged(Change<? extends Integer, ? extends Project> change) {
                    if (change.wasAdded()) {
                        //Integer projId = change.getKey()+1;
                        //Project proj = new Project(projId, projName);
                        // TODO: Persist new project
                        logger.info("Adding project: " + change.getValueAdded());
                                
                        projectsMap.put(change.getValueAdded().getId(),change.getValueAdded());
                    }
                    if (change.wasRemoved()) {
                        projectsMap.remove(change.getKey());
                        // TODO: Remove project
                        // projectNames.remove(change.getKey());
                        logger.severe("TODO: Remove project from map and database.");
                    }
                }
            };

            projectsMap.addListener(projectsMapChangeListener);

            Map<Integer, Issue> issues = new TreeMap<Integer, Issue>();
            issuesMap = FXCollections.observableMap(issues);


    // You create new issue by adding a Issue instance to the issuesMap.
            // the new id will be automatically added to the corresponding list in
            // the projectsMap.
            //
            MapChangeListener<Integer, Issue> issuesMapChangeListener = new MapChangeListener<Integer, Issue>() {
                @Override
                public void onChanged(Change<? extends Integer, ? extends Issue> change) {
                    if (change.wasAdded()) {
                        Issue val = change.getValueAdded();
                        //projectsMap.get(val.getProjectName()).add(val.getId());
                        logger.severe("TODO: ADD Issue - "+val);
                    }
                    if (change.wasRemoved()) {
                        Issue val = change.getValueRemoved();
                        logger.severe("TODO: REMOVE Issue - "+val);
                    }
                }
            };
            
            issuesMap.addListener(issuesMapChangeListener);

            rs = s.executeQuery("select * from Issue");
            while (rs.next()) {
                int id = rs.getInt("Id");
                int projId = rs.getInt("projId");
                int status = rs.getInt("Status");
                String synopsis = rs.getString("Synopsis");
                String description = rs.getString("Description");
                
//                    + "Id INTEGER PRIMARY KEY, "
//                    + "ProjectId INTEGER, " // foreign key to project related to issue
//                    + "Status INTEGER, "
//                    + "Synopsis VARCHAR(1024), "
//                    + "Description VARCHAR(32000) "  // TODO: this should be probably a blob or clob? max length is 32672
                
                Issue issue = new Issue(id, projId, projectsMap.get(projId).getName(), IssueStatus.NEW,synopsis, description);
                issuesMap.put(issue.getId(), issue);
            }
        }
        
        
    }


    @Override
    public void deleteIssue(int issueId) {
        issuesMap.remove(issueId);
    }

    @Override
    public ObservableList<String> getProjectNames() {
        return null;
    }

    @Override
    public void saveIssue(String issueId, IssueStatus status,
            String synopsis, String description) {
        Issue issue = issuesMap.get(issueId);
        issue.setDescription(description);
        issue.setSynopsis(synopsis);
        issue.setStatus(status);
    }


    @Override
    public ObservableList<String> getIssueIds(String projectName) {
        return null;
    }
}
