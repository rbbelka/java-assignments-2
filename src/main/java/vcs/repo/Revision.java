package vcs.repo;

import java.io.Serializable;

public class Revision implements Serializable {
    private final int id;
    private final int previous;
    private final String branchName;
    private final String commitMessage;

    public Revision(int id, int previous, String branchName, String commitMessage) {
        this.id = id;
        this.previous = previous;
        this.commitMessage = commitMessage;
        this.branchName = branchName;
    }

    public int getId() {
        return id;
    }

    public int getPrevious() {
        return previous;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Revision)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        Revision other = (Revision) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
