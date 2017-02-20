package vcs;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import vcs.exceptions.VcsException;
import vcs.repo.Revision;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CommitTest extends VcsTest {

    @Test
    public void testCommit() throws IOException, VcsException {
        String name = "1";
        File file = folder.newFile(name);
        FileUtils.writeStringToFile(file, "changed");
        repo.getStorage().addFile(name);
        repo.commit("1");

        Revision revision = repo.getCurrentRevision();
        assertEquals("1", revision.getCommitMessage());
        assertEquals(1, revision.getId());
    }

    @Test
    public void testCommitNoChanges() throws IOException {
        repo.commit("1");
        repo.commit("1");
        Revision revision = repo.getCurrentRevision();
        assertEquals("1", revision.getCommitMessage());
        assertEquals(1, revision.getId());

    }
}
