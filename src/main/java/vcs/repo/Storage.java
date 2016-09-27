package vcs.repo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author natalia on 27.09.16.
 */
public class Storage implements Serializable {

    private final String repoDir;
    private final Set<String> files = new HashSet<>();

    public Storage(String dir) {
        repoDir = dir;
    }

    public String getRepoDir() {
        return repoDir;
    }

    public boolean addFile(String filename) {
        return files.add(filename);
    }

    public Set<String> getFiles() {
        return files;
    }
}
