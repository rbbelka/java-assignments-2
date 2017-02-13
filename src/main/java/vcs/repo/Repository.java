package vcs.repo;


import vcs.exceptions.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author natalia on 26.09.16.
 */
public class Repository implements Serializable {

    private static final String DEFAULT_BRANCH = "master";
    private Storage storage;

    private int currentRevision = 0;
    private int nextRevisionNumber = 1;
    private final Map<Integer, Revision> revisions;
    private Branch currentBranch;
    private final Map<String, Branch> branches;

    private Repository(String dir, String tempDir) {
        storage = new Storage(dir, tempDir);
        revisions = new HashMap<>();
        currentBranch = new Branch(DEFAULT_BRANCH, 0);
        branches = new HashMap<>();
        branches.put(currentBranch.getName(), currentBranch);
    }

    public static Repository createRepository(String dir, String tempDir) {
        return new Repository(dir, tempDir);
    }

    public Storage getStorage() {
        return storage;
    }

    public String getCurrentBranchName() {
        return currentBranch.getName();
    }

    public Revision getCurrentRevision() {
        return getRevisionById(currentRevision);
    }

    public Revision getRevisionById(int previous) {
        return revisions.get(previous);
    }

    public void commit(String message) throws IOException {
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

    public void checkoutBranch(String name) throws VcsException, IOException {
        Branch branch = branches.get(name);
        if (branch == null) {
            throw new BranchNotFoundException("Checkout failed: branch not found");
        }
        currentBranch = branch;
        currentRevision = branch.getRevision();
        storage.checkoutRevision(currentRevision);
        System.out.println("Checked out branch " + currentBranch.getName());
    }

    public void checkoutRevision(String id) throws VcsException, IOException {
        Revision revision = revisions.get(Integer.parseInt(id));
        if (revision == null) {
            throw new RevisionNotFoundException("Checkout failed: revision not found");
        }
        Branch branch = branches.get(revision.getBranchName());
        if (branch == null) {
            throw new RevisionNotFoundException("Checkout failed: revision is incorrect");
        }
        currentRevision = revision.getId();
        currentBranch = branch;
        storage.checkoutRevision(currentRevision);
        System.out.println("Checked out revision " + revision.getId());
    }

    public void createBranch(String name) throws VcsException {
        if (branches.get(name) == null) {
            Branch branch = new Branch(name, currentRevision);
            branches.put(name, branch);
            System.out.println("Branch " + name + " created");
        } else {
            throw new BranchAlreadyExistsException("Branch creation failed : branch already exists");
        }
    }

    public void deleteBranch(String name) throws VcsException {
        if (name.equals(currentBranch.getName())) {
            throw new CurrentBranchDeletionException("Delete failed: can't delete current branch");
        }
        if (branches.remove(name) == null) {
            throw new BranchNotFoundException("Delete failed: branch not found");
        }
        System.out.println("Branch " + name + " deleted");
    }

    public void merge(String branchToMerge, String message) throws VcsException, IOException {
        Branch branch = branches.get(branchToMerge);
        if (branch == null) {
            throw new BranchNotFoundException("Merge failed: branch not found");
        }
        int fromId = branch.getRevision();
        int baseId = findLCA(fromId, currentBranch.getRevision());
        if (fromId <= baseId) {
            System.out.println("Nothing to merge: branch is up-to-date");
            return;
        }
        storage.merge(fromId, currentRevision, baseId, nextRevisionNumber);
        String mergeMessage = "Merged branch " + branch.getName() + " into " + currentBranch.getName();
        System.out.println(mergeMessage);
        addRevision(mergeMessage + message);
        storage.checkoutRevision(currentRevision);
    }

    private int findLCA(int fromId, int toId) {
        Revision from = getRevisionById(fromId);
        Revision to = getRevisionById(toId);
        while (from.getId() != to.getId()) {
            if (from.getId() > to.getId()) {
                from = getRevisionById(from.getPrevious());
            } else {
                to = getRevisionById(to.getPrevious());
            }
        }
        return from.getId();
    }
}
