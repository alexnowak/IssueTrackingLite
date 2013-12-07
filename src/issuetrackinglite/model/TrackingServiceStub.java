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
                logger.fine("\t["+ (nProj++) + "] Id" + proj.getId() + proj.getName());
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
                        proj.getName(),
                        Issue.IssueStatus.values()[rs.getInt("Status")],
                        rs.getString("Synopsis"),
                        rs.getString("Description"));
                issues.add(issue);
            }
    }
        logger.info("Project ID"+proj.getId()+" \"" + proj.getName() + "\" has " + issues.size() + " issues.");
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
         // logger.finest("Project \"" + projName + " has ID " + projId);
        return projId;
    }

    public void saveIssue(Issue issue) {
        logger.severe("TODO: Implement saveIssue: Issue: "+issue);
    }


    public void deleteIssue(Issue issue) throws SQLException {
        try (Statement s = Database.getInstance().getConnection().createStatement()) {
           int result = s.executeUpdate("delete from Issue where Id="+issue.getId());
           logger.info("Deleted from database: (SQLCode="+result+") Issue: "+issue);
        }
        
    }

    
}
