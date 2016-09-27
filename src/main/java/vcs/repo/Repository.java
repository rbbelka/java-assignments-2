package vcs.repo;

import vcs.util.VcsException;

import java.io.Serializable;

/**
 * @author natalia on 26.09.16.
 */
public class Repository implements Serializable {

    private Storage storage;

    private Repository(String dir) throws VcsException {
        storage = new Storage(dir);
    }

    public String getDir() {
        return storage.getRepoDir();
    }

    public static Repository createRepository(String dir) throws VcsException {
        return new Repository(dir);
    }

    public boolean addFile(String filename) {
        return storage.addFile(filename);
    }

    public Storage getStorage() {
        return storage;
    }
}
