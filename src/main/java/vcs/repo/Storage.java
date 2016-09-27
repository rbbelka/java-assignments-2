package vcs.repo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author natalia on 27.09.16.
 */
public class Storage implements Serializable {

    private final String repoDir;
    private final List<String> files = new ArrayList<>();

    public Storage(String dir) {
        repoDir = dir;
    }

    public String getRepoDir() {
        return repoDir;
    }

    public boolean addFile(String filename) {
        // TODO: implement adding
        return false;
    }
}
