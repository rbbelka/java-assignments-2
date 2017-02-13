package vcs.repo;

import org.apache.commons.io.FileUtils;
import vcs.exceptions.MergeConflictException;
import vcs.util.Util;
import vcs.exceptions.VcsException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author natalia on 27.09.16.
 */
public class Storage implements Serializable {

    private final String repoDir;
    private final String curDir;

    // list of files under control
    private final Set<String> controlledFiles = new HashSet<>();
    private final Set<String> addedFiles = new HashSet<>();
    private final Set<String> modifiedFiles = new HashSet<>();
    private final Set<String> deletedFiles = new HashSet<>();

    private final Map<Integer, Snapshot> snapshots = new HashMap<>();

    public Storage(String repoDir, String curDir) {
        this.repoDir = repoDir;
        this.curDir = curDir;
    }

    public String getRepoDir() {
        return repoDir;
    }

    public void addFile(String filename) throws IOException {
        Util.copyFileAndHashToCurrentDir(filename);
        addedFiles.add(filename);
        controlledFiles.add(filename);
    }

    public void resetFile(String filename) throws IOException {
        Util.removeFileAndHashFromCurrentDir(filename);
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

    public List<String> getModifiedNotStaged() throws IOException {

        List<String> modified = new ArrayList<>();
        Set<File> allFiles = Util.listFilesFromDir(repoDir);

        for (File file : allFiles) {
            String path = Paths.get(repoDir).relativize(file.toPath()).toString();
            if (isControlled(path) && !Util.hashEqual(file, new File(curDir, path)))
                modified.add(path);
        }
        return modified;
    }

    public List<String> getDeletedNotStaged() {
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

    public void writeRevision(int revision) throws IOException {
        Snapshot snapshot = new Snapshot();
        final String storageDir = Util.storageDir();
        for (String filePath : controlledFiles) {
            File file = new File(curDir, filePath);
            if (file.exists()) {
                String hash = Util.getMD5(file);
                snapshot.addFile(filePath, hash);
                File storedFile = new File(storageDir, hash);
                if (!storedFile.exists()) {
                    FileUtils.copyFile(file, storedFile);
                }
            }
        }
        snapshots.put(revision, snapshot);
    }

    public void checkoutRevision(int revision) throws IOException {
        final String storageDir = Util.storageDir();
        for (String file : controlledFiles) {
            FileUtils.deleteQuietly(new File(repoDir, file));
        }
        FileUtils.cleanDirectory(new File(curDir));
        Snapshot snapshot = getSnapshot(revision);
        controlledFiles.clear();
        controlledFiles.addAll(snapshot.keySet());
        for (String file : controlledFiles) {
            String hash = snapshot.get(file);
            FileUtils.copyFile(new File(storageDir, hash), new File(repoDir, file));
            FileUtils.copyFile(new File(storageDir, hash), new File(curDir, file));
        }
    }

    private Snapshot getSnapshot(int revision) {
        return snapshots.get(revision);
    }

    public void merge(int fromRevision, int toRevision, int baseRevision, int nextRevision) throws MergeConflictException {
        Snapshot from = getSnapshot(fromRevision);
        Snapshot to = getSnapshot(toRevision);
        Snapshot base = getSnapshot(baseRevision);

        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(from.keySet());
        allFiles.addAll(to.keySet());
        allFiles.addAll(base.keySet());

        Snapshot result = new Snapshot();
        for (String file : allFiles) {
            final String fromHash = from.get(file);
            final String toHash = to.get(file);
            final String baseHash = base.get(file);

            boolean changedFrom = !Objects.equals(fromHash, baseHash);
            boolean changedTo = !Objects.equals(toHash, baseHash);
            boolean changeDiffers = !Objects.equals(fromHash, toHash);

            if (changedFrom && changedTo && changeDiffers) {
                throw new MergeConflictException("Merge conflict: file " + file + " differs");
            }
            if (changedFrom) {
                if (from.contains(file)) {
                    result.addFile(file, fromHash);
                }
            } else if (to.contains(file)) {
                result.addFile(file, toHash);
            }
        }
        snapshots.put(nextRevision, result);
    }

}
