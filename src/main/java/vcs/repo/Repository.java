package vcs.repo;

import vcs.util.VcsException;

import java.io.Serializable;

/**
 * @author natalia on 26.09.16.
 */
public class Repository implements Serializable {

    private Storage storage;

    private Repository(String dir, String tempDir) throws VcsException {
        storage = new Storage(dir, tempDir);
    }

    public String getDir() {
        return storage.getRepoDir();
    }

    public static Repository createRepository(String dir, String tempDir) throws VcsException {
        return new Repository(dir, tempDir);
    }

    public Storage getStorage() {
        return storage;
    }

}
