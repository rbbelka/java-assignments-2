package vcs.repo;

import vcs.util.Util;
import vcs.util.VcsException;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author natalia on 27.09.16.
 */
public class Storage implements Serializable {

    private final String repoDir;
    private final String сurDir;

    // list of files under control
    private final Set<String> controlledFiles = new HashSet<>();
    private final Set<String> addedFiles = new HashSet<>();
    private final Set<String> modifiedFiles = new HashSet<>();
    private final Set<String> deletedFiles = new HashSet<>();

    public Storage(String repoDir, String сurDir) {
        this.repoDir = repoDir;
        this.сurDir = сurDir;
    }

    public String getRepoDir() {
        return repoDir;
    }

    public void addFile(String filename) throws VcsException {
        Util.copyFileToDir(filename, сurDir);
        addedFiles.add(filename);
        controlledFiles.add(filename);
    }

    public void resetFile(String filename) throws VcsException {
        Util.removeFileFromDir(filename, сurDir);
        addedFiles.remove(filename);
        controlledFiles.remove(filename);
    }


    public Set<String> getControlledFiles() {
        return controlledFiles;
    }

    private boolean isControlled(String file) {
        return controlledFiles.contains(file);
    }

    public Set<String> getAddedFiles() {
        return addedFiles;
    }

    public Set<String> getDeletedFiles() {
        return deletedFiles;
    }

    public Set<String> getModifiedFiles() {
        return modifiedFiles;
    }

    public List<String> getUntracked() {

        List<String> untracked = new ArrayList<>();
        Set<File> allFiles = Util.listFilesFromDir(repoDir);

        for (File file : allFiles) {
            String path = Paths.get(repoDir).relativize(file.toPath()).toString();
            if (!isControlled(path) && !path.startsWith(".vcs"))
                untracked.add(path);
        }
        return untracked;
    }

    public List<String> getModifiedNotStaged() throws VcsException {

        List<String> modified = new ArrayList<>();
        Set<File> allFiles = Util.listFilesFromDir(repoDir);

        for (File file : allFiles) {
            String path = Paths.get(repoDir).relativize(file.toPath()).toString();
            if (isControlled(path) && !Util.hashEqual(file, new File(сurDir, path)))
                modified.add(path);
        }
        return modified;
    }

    public List<String> getDeletedNotStaged() throws VcsException {
        List<String> deleted = new ArrayList<>();
        Set<String> allFiles =
                Util.listFilesFromDir(repoDir).stream()
                        .map(File::toPath)
                        .map(Paths.get(repoDir)::relativize)
                        .map(Path::toString)
                        .collect(Collectors.toSet());

        for (String filename : controlledFiles) {
            if (!allFiles.contains(filename))
                deleted.add(filename);
        }
        return deleted;
    }

}
