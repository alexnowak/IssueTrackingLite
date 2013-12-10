/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 */
package issuetrackinglite.model;

import issuetrackinglite.db.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TrackingServiceStub {
    static final Logger logger = Logger.getLogger(TrackingServiceStub.class.getName());

    
    public TrackingServiceStub() {
        
    }


    public ObservableList<Project> getProjects() throws SQLException {
        ArrayList<Project> projs = new ArrayList<>();
        
        Database db = Database.getInstance();
        try (Statement s = db.getConnection().createStatement() ) {
            ResultSet rs = s.executeQuery("select * from Project");
            int nProj=0;
            while (rs.next()) {
                Project proj = new Project(rs.getInt("Id"), rs.getString("Name"));
                projs.add(proj);
                logger.fine("\t["+ (nProj++) + "] Id" + proj.getId() + " - "+proj.getName());
            }
            logger.info("Number of projects in db: "+nProj);
        }
        
        Collections.sort(projs);
        return FXCollections.observableList(projs);
    }

    public ObservableList<Issue> getIssues(Project proj) throws SQLException {
        List<Issue> issues = new ArrayList<>();
        try (Statement s = Database.getInstance().getConnection().createStatement() ) {
            ResultSet rs = s.executeQuery("select * from Issue where ProjId="+proj.getId());
            while (rs.next()) {
                //        public Issue(int id, int projId, String projName, IssueStatus status, String synopsis, String description) {
                 Issue issue = new Issue(
                        rs.getInt("Id"),
                        proj.getId(),
                        Issue.IssueStatus.values()[rs.getInt("Status")],
                        rs.getString("Synopsis"),
                        rs.getString("Description"));
                issues.add(issue);
            }
    }
        logger.info("Project ID"+proj.getId()+" \"" + proj.getName() + "\" has " + issues.size() + " issues.");
        return FXCollections.observableList(issues);
    }

    public int getProjectId(String projName) throws SQLException {
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
         // logger.finest("Project \"" + projName + " has ID " + projId);
        return projId;
    }

    public String getProjectName(int projId) throws SQLException {
        String projName="";
        try (Statement s = Database.getInstance().getConnection().createStatement()) {
            ResultSet rs = s.executeQuery("select Name from Project where Id=" + projId);
            if (!rs.next()) {
                throw new SQLException("Project with ID" + projId + " not found in database.");
            }

            projName = rs.getString("Name");

            if (rs.next()) {
                throw new SQLException("Found more than one project with ID" + projId + " in database.");
            }
        }
         // logger.finest("Project \"" + projName + " has ID " + projId);
        return projName;
    }

    public int saveNewIssue(Issue issue) throws SQLException {
        try (Statement s = Database.getInstance().getConnection().createStatement()) {
            int result = s.executeUpdate(
                    "insert into Issue values("
                    + "DEFAULT, " + issue.getProjId() + ", "
                    + Issue.IssueStatus.NEW.ordinal() + ", "
                    + "'" + issue.getSynopsis() + "', "
                    + "'" + issue.getDescription() + "'"
                    + ")");
            // get id of the saved issue
            int newId = getLastIssueId();
            issue.setId(newId);
           logger.info("New issue saved successfully (SQLCode="+result+") Issue: "+issue);
           return newId;
        }
    }

    public int saveNewProject(Project proj) throws SQLException {
        try (Statement s = Database.getInstance().getConnection().createStatement()) {
            int result = s.executeUpdate(
                    "insert into Project values("
                    + "DEFAULT, " 
                    + "'" + proj.getName() + "'"
                    + ")" );
            // get id of the saved issue
            int newId = getLastProjectId();
            proj.setId(newId);
           logger.info("New projet saved successfully (SQLCode="+result+") Project: "+proj);
           return newId;
        }
    }

    public void updateIssue(Issue issue) throws SQLException {
        try (Statement s = Database.getInstance().getConnection().createStatement()) {
           int result = s.executeUpdate("update Issue set "
                   +"Synopsis='"+issue.getSynopsis()+"',"
                   +"Description='" + issue.getDescription() + "' "
                   +"where Id="+issue.getId());
           logger.info("Database update completed successfully (SQLCode="+result+") Issue: "+issue);
        }
    }


    public void deleteIssue(Issue issue) throws SQLException {
        try (Statement s = Database.getInstance().getConnection().createStatement()) {
           int result = s.executeUpdate("delete from Issue where Id="+issue.getId());
           logger.info("Deleted from database: (SQLCode="+result+") Issue: "+issue);
        }
    }

    public int getLastIssueId() throws SQLException {
        return getLastId("Issue");
    }
    public int getLastProjectId() throws SQLException {
        return getLastId("Project");
    }

    private int getLastId(String tableName) throws SQLException {
        int lastId = -1;
        try (Statement s = Database.getInstance().getConnection().createStatement()) {
            ResultSet rs = s.executeQuery("select max(id) from "+tableName);
            while (rs.next()) {
                lastId = rs.getInt(1);
            }
        }
        logger.fine("Table: " + tableName + " MaxId: "+lastId);
        return lastId;
    }

    
}
