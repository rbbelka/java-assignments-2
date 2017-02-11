package vcs.repo;

import vcs.util.VcsException;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author natalia on 26.09.16.
 */
public class Repository implements Serializable {

    private static final String DEFAULT_BRANCH = "master";
    private Storage storage;

    private int currentRevision;
    private int nextRevisionNumber;
    private final Map<Integer, Revision> revisions;
    private Branch currentBranch;
    private final Map<String, Branch> branches;

    private Repository(String dir, String tempDir) throws VcsException {
        storage = new Storage(dir, tempDir);
        revisions =  new HashMap<>();
        currentBranch = new Branch(DEFAULT_BRANCH, 0);
        branches = new HashMap<>();
        branches.put(currentBranch.getName(), currentBranch);
    }

    public static Repository createRepository(String dir, String tempDir) throws VcsException {
        return new Repository(dir, tempDir);
    }

    public Storage getStorage() {
        return storage;
    }

    public String getCurrentBranchName() {
        return currentBranch.getName();
    }

    public void commit(String message) throws VcsException, IOException {
        if (currentBranch == null) {
            throw new VcsException("No tracked branch for commit");
        }
        int id = addRevision(message);
        System.out.println("Committed revision " + id + " to branch " + currentBranch.getName());
        storage.writeRevision(id);
    }

    private int addRevision(String message) {
        int id = nextRevisionNumber;
        Revision revision = new Revision(id, currentRevision, currentBranch.getName(), message);
        revisions.put(id, revision);
        currentRevision = id;
        currentBranch.setRevision(id);
        nextRevisionNumber++;
        return id;
    }

    public void checkout(String name) throws VcsException, IOException {
        Branch branch = branches.get(name);
        if (branch != null) {
            currentBranch = branch;
            currentRevision = branch.getRevision();
            System.out.println("Checked out branch " + currentBranch.getName());
        } else {
            try {
                Revision revision = revisions.get(Integer.parseInt(name));
                branch = branches.get(revision.getBranchName());
                currentRevision = revision.getId();
                currentBranch = branch;
                System.out.println("Checked out revision" + revision);
            } catch (Exception e) {
                throw new VcsException("Checkout failed: branch or revision not found");
            }
        }
        storage.checkoutRevision(currentRevision);
    }

    public void createBranch(String name) throws VcsException {
        Branch branch = new Branch(name, currentRevision);
        if (branches.get(name) == null) {
            branches.put(name, branch);
            System.out.println("Branch " + name + " created");
        } else {
            throw new VcsException("Branch already exists");
        }
    }

    public void deleteBranch(String name) throws VcsException {
        if (name.equals(currentBranch.getName())) {
            throw new VcsException("Can't delete current branch");
        }
        if (branches.remove(name) == null) {
            throw new VcsException("Branch not found");
        }
        System.out.println("Branch " + name + " deleted");
    }

}
