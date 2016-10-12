package vcs.repo;

public class Revision {
    private final int id;
    private final int previous;
    private final String commitMessage;

    public Revision(int id, int previous, String commitMessage) {
        this.id = id;
        this.previous = previous;
        this.commitMessage = commitMessage;
    }

    public int getId() {
        return id;
    }

    public int getPrevious() {
        return previous;
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
