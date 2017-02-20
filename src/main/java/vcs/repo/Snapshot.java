package vcs.repo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Holds map of files and their hashes for revision.
 */
public class Snapshot implements Serializable {

    private final Map<String, String> fileMap = new HashMap<>();

    public void addFile(String file, String hash) {
        fileMap.put(file, hash);
    }

    public Set<String> keySet() {
        return fileMap.keySet();
    }

    public String get(String file) {
        return fileMap.get(file);
    }

    public boolean contains(String file) {
        return fileMap.containsKey(file);
    }
}
