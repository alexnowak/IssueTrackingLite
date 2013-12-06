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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class TrackingServiceStub {
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
                int projId = rs.getInt("Id");
                String projName = rs.getString("Name");
                Project proj = new Project(projId, projName);
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
            issuesMap.addListener(issuesMapChangeListener);
        }
        
        
    }


    public void deleteIssue(int issueId) {
        issuesMap.remove(issueId);
    }

    public ObservableList<String> getProjectNames() {
        ArrayList<String> projNames = new ArrayList<String>();
        
        for ( Project p : projectsMap.values() )
            projNames.add(p.getName());
        
        return FXCollections.observableArrayList(projNames);
    }

    public void saveIssue(String issueId, IssueStatus status,
            String synopsis, String description) {
        Issue issue = issuesMap.get(issueId);
        issue.setDescription(description);
        issue.setSynopsis(synopsis);
        issue.setStatus(status);
    }


    public ObservableList<String> getIssueIds(String projectName) throws SQLException {
        List<String> issues = new ArrayList<String>();
        try (Statement s = Database.getInstance().getConnection().createStatement() ) {
            ResultSet rs = s.executeQuery("select i.Id from Project p, Issue i where p.Name='"+projectName+"' and i.ProjId=p.Id");
            while (rs.next()) {
                issues.add("TT-"+rs.getInt("Id"));
            }
    }
        logger.info("Project \""+projectName+" has " + issues.size() + " issues.");
        return FXCollections.observableList(issues);
    }

    public ObservableList<Issue> getIssues(String projName) throws SQLException {
        List<Issue> issues = new ArrayList<Issue>();
        int projId = getProjectId(projName);
        try (Statement s = Database.getInstance().getConnection().createStatement() ) {
            ResultSet rs = s.executeQuery("select * from Issue where ProjId="+projId);
            while (rs.next()) {
                //        public Issue(int id, int projId, String projName, IssueStatus status, String synopsis, String description) {
 
                Issue issue = new Issue(
                        rs.getInt("Id"),
                        getProjectId(projName),
                        projName,
                        Issue.IssueStatus.values()[rs.getInt("Status")],
                        rs.getString("Synopsis"),
                        rs.getString("Synopsis"));
                issues.add(issue);
            }
    }
        logger.info("Project \""+projName+" has " + issues.size() + " issues.");
        return FXCollections.observableList(issues);
    }

    private int getProjectId(String projName) throws SQLException {
        int projId;
        try (Statement s = Database.getInstance().getConnection().createStatement()) {
            ResultSet rs = s.executeQuery("select Id from Project where Name='" + projName + "'");
            if (!rs.next()) {
                throw new SQLException("Project with name \"" + projName + "\" not found in database.");
            }

            projId = rs.getInt("Id");

            if (rs.next()) {
                throw new SQLException("Found more than one project with name \"" + projName + "\" in database.");
            }
        }

        logger.info("Project \"" + projName + " has ID " + projId);
        return projId;
    }

}
