package vcs.repo;

import vcs.util.Util;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author natalia on 27.09.16.
 */
public class Storage implements Serializable {

    private final String repoDir;
    private final String сurDir;

    // list of files under control
    private final Set<String> controlledFiles = new HashSet<>();

    public Storage(String repoDir, String сurDir) {
        this.repoDir = repoDir;
        this.сurDir = сurDir;
    }

    public String getRepoDir() {
        return repoDir;
    }

    public Set<String> getFiles() {
        return controlledFiles;
    }

    public void addFile(String filename) throws IOException {
        Util.copyFileToDir(filename, сurDir);
        controlledFiles.add(filename);
    }

    public boolean resetFile(String filename) {
        return controlledFiles.remove(filename);
    }
}
