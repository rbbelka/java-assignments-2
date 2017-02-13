package vcs;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import vcs.exceptions.VcsException;
import vcs.repo.Revision;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LogTest extends VcsTest {
    @Test
    public void testLog() throws IOException, VcsException {
        repo.commit("empty");
        List<Revision> revisions = repo.getLog();
        Assert.assertEquals(revisions.size(), 1);
        Assert.assertEquals(revisions.get(0).getCommitMessage(), "empty");

        String name1 = "1";
        String name2 = "2";
        File file1 = folder.newFile(name1);
        File file2 = folder.newFile(name2);
        repo.getStorage().addFile(name1);
        repo.getStorage().addFile(name2);
        String added1 = "added 1 and 2";
        repo.commit(added1);

        repo.createBranch("test");
        repo.checkoutBranch("test");
        final String changed2 = "changed 2";
        FileUtils.writeStringToFile(file2, changed2);
        repo.getStorage().addFile(name2);
        repo.commit(changed2);
        revisions = repo.getLog();
        Assert.assertEquals(revisions.size(), 3);
        Assert.assertEquals(revisions.get(0).getCommitMessage(), changed2);

        repo.checkoutBranch("master");
        final String changed1 = "changed 1";
        FileUtils.writeStringToFile(file1, changed1);
        repo.commit(changed1);
        revisions = repo.getLog();
        Assert.assertEquals(revisions.size(), 3);
        Assert.assertEquals(revisions.get(0).getCommitMessage(), changed1);
    }
}
