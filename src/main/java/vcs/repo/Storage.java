package vcs.repo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author natalia on 27.09.16.
 */
public class Storage implements Serializable {

    private final String repoDir;

    // list of files under control
    private final Set<String> controlledFiles = new HashSet<>();

    public Storage(String dir) {
        repoDir = dir;
    }

    public String getRepoDir() {
        return repoDir;
    }

    public Set<String> getFiles() {
        return controlledFiles;
    }

    public boolean addFile(String filename) {
        return controlledFiles.add(filename);
    }

    public boolean resetFile(String filename) {
        return controlledFiles.remove(filename);
    }
}
