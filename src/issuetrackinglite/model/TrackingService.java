/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 */
package issuetrackinglite.model;

import issuetrackinglite.model.Issue.IssueStatus;
import javafx.collections.ObservableList;

public interface TrackingService {

    public ObservableList<String> getIssueIds(String projectName);
    public ObservableList<String> getProjectNames();
    public void deleteIssue(int issueId);
    public void saveIssue(String issueId, IssueStatus status,
            String synopsis, String description);
}
