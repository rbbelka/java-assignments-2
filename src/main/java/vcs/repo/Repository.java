package vcs.repo;


import vcs.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class represents logic of repository.
 * Holds and changes info about branches and revisions.
 */
public class Repository implements Serializable {

    public static final String DEFAULT_BRANCH = "master";
    private Storage storage;

    private int currentRevision = 0;
    private int nextRevisionNumber = 1;
    private final Map<Integer, Revision> revisions;
    private Branch currentBranch;
    private final Map<String, Branch> branches;

    private Repository(String dir) {
        storage = new Storage(dir);
        revisions = new HashMap<>();
        currentBranch = new Branch(DEFAULT_BRANCH, 0);
        branches = new HashMap<>();
        branches.put(currentBranch.getName(), currentBranch);
    }

    public static Repository createRepository(String dir) {
        return new Repository(dir);
    }

    public String getInitFile() {
        return getVcsDir() + "/init";
    }

    public Storage getStorage() {
        return storage;
    }

    public Map<String, Branch> getBranches() {
        return branches;
    }

    public String getCurrentBranchName() {
        return currentBranch.getName();
    }

    public Revision getCurrentRevision() {
        return getRevisionById(currentRevision);
    }

    public int getCurrentRevisionId() {
        return currentRevision;
    }

    public boolean checkInit() {
        return new File(getInitFile()).exists();
    }

    public void commit(String message) throws IOException {
        if (storage.writeRevision(nextRevisionNumber, currentRevision)) {
            addRevision(message);
            System.out.println("Committed revision " + currentRevision + " to branch " + currentBranch.getName());
        } else {
            System.out.println("No changes to commit");
        }
    }

    public void checkoutBranch(String name) throws IOException, BranchNotFoundException {
        Branch branch = branches.get(name);
        if (branch == null) {
            throw new BranchNotFoundException("Checkout failed: branch not found");
        }
        currentBranch = branch;
        currentRevision = branch.getRevision();
        storage.checkoutRevision(currentRevision);
        System.out.println("Checked out branch " + currentBranch.getName());
    }

    public void checkoutRevision(String id) throws IOException, RevisionNotFoundException {
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

    public void createBranch(String name) throws RevisionNotFoundException, BranchAlreadyExistsException {
        if (currentRevision == 0) {
            throw new RevisionNotFoundException("There is no commit to create a branch from");
        }
        if (branches.get(name) == null) {
            Branch branch = new Branch(name, currentRevision);
            branches.put(name, branch);
            System.out.println("Branch " + name + " created");
        } else {
            throw new BranchAlreadyExistsException("Branch creation failed : branch already exists");
        }
    }

    public void deleteBranch(String name) throws CurrentBranchDeletionException, BranchNotFoundException {
        if (name.equals(currentBranch.getName())) {
            throw new CurrentBranchDeletionException("Delete failed: can't delete current branch");
        }
        if (branches.remove(name) == null) {
            throw new BranchNotFoundException("Delete failed: branch not found");
        }
        System.out.println("Branch " + name + " deleted");
    }

    public void merge(String branchToMerge, String message) throws IOException, BranchNotFoundException, MergeConflictException {
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

    public void createFileStructure() throws IOException {
        File vcsDir = new File(getVcsDir());
        File curDir = new File(getStorage().getCurDir());
        File init = new File(getInitFile());

        if (!vcsDir.mkdirs()) {
            throw new IOException("Can't create vcs folder");
        }

        if (!curDir.mkdirs()) {
            vcsDir.delete();
            throw new IOException("Can't create temp folder");
        }


        if (!init.createNewFile()) {
            vcsDir.delete();
            throw new IOException("Can't create init file");
        }
    }

    public List<Revision> getLog() {
        List<Revision> revisions = new ArrayList<>();
        Revision revision = getCurrentRevision();
        if (revision == null) {
            return revisions;
        }
        while (revision != null) {
            revisions.add(revision);
            revision = getRevisionById(revision.getPrevious());
        }
        return revisions;
    }

    private Revision getRevisionById(int previous) {
        return revisions.get(previous);
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

    private int addRevision(String message) {
        int id = nextRevisionNumber;
        Revision revision = new Revision(id, currentRevision, currentBranch.getName(), message);
        revisions.put(id, revision);
        currentRevision = id;
        currentBranch.setRevision(id);
        nextRevisionNumber++;
        return id;
    }

    private String getVcsDir() {
        return getStorage().getRepoDir() + "/.vcs";
    }
}
