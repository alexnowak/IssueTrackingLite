/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package issuetrackinglite.model;

/**
 *
 * @author Alex
 */
public class Project implements Comparable<Project> {

    public Project(int id, String name) {
        if (name == null)
            throw new NullPointerException("Person's name cannot be null.");
        this.id = id;
        this.name = name;
    }
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * <p>String representation of a Project.</p>
     * Note: toString() will be called by a JavaFX ListView to display the items
     * in a list.
     * 
     * @return The project name as it will displayed in a ListView
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Natural sort order is by project name
     * @param p Project to compare to
     * @return 0,1
     */
    @Override
    public int compareTo(Project p) {
        return getName().compareTo(p.getName());
    }
}
