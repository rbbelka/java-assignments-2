package vcs.repo;

import vcs.util.VcsException;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author natalia on 26.09.16.
 */
public class Repository implements Serializable {

    private Storage storage;

    private int currentRevision = 0;
    private int nextRevision;
    private final Map<Integer, Revision> revisions;

    private Repository(String dir, String tempDir) throws VcsException {
        storage = new Storage(dir, tempDir);
        revisions =  new HashMap<>();
    }

    public static Repository createRepository(String dir, String tempDir) throws VcsException {
        return new Repository(dir, tempDir);
    }

    public Storage getStorage() {
        return storage;
    }

    public void commit(String message) throws VcsException, IOException {
        int id = addRevision(message);
        System.out.println("Committed revision " + id);
        storage.writeRevision(id);
    }

    private int addRevision(String message) {
        int id = nextRevision;
        Revision revision = new Revision(id, currentRevision, message);
        revisions.put(id, revision);
        currentRevision = id;
        nextRevision++;
        return id;
    }

    public void checkout(String name) throws VcsException, IOException {
        try {
            int revision = Integer.parseInt(name);
            currentRevision = revisions.get(revision).getId();
        } catch (Exception e) {
            throw new VcsException("Checkout failed: revision not found");
        }
        storage.checkoutRevision(currentRevision);
    }
}
