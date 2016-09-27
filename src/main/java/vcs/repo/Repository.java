package vcs.repo;

import vcs.util.VcsException;

import java.io.Serializable;

/**
 * @author natalia on 26.09.16.
 */
public class Repository implements Serializable {

    private static Storage storage;

//    private Branch currentBranch = new Branch("master");
//    private final List<Branch> branches = new ArrayList<>();

    private Repository(String dir) throws VcsException {
        storage = new Storage(dir);
    }

    public static Repository createRepository(String dir) throws VcsException {
        return new Repository(dir);
    }

//    public Branch createBranch(String name) throws VcsException {
//        Branch branch = new Branch(name);
//        if (!branches.add(branch))
//            throw new VcsException("Branch already exists");
//        return branch;
//    }

}
