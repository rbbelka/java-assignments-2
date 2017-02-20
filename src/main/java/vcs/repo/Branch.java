package vcs.repo;

import java.io.Serializable;

/**
 * Class represents branch.
 * Identified by name and holds last branch commit.
 */
public class Branch implements Serializable {

    private final String name;
    private int revision;

    public Branch(String name, int revision) {
        this.name = name;
        this.revision = revision;
    }

    public String getName() {
        return name;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Branch) && ((Branch) obj).name.equals(name);
    }
}