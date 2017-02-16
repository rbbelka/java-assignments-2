package vcs;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import vcs.exceptions.VcsException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ResetTest extends VcsTest {

    @Test
    public void testReset() throws IOException, VcsException {
        String name1 = "1";
        String name2 = "2";
        File file1 = folder.newFile(name1);
        File file2 = folder.newFile(name2);
        FileUtils.writeStringToFile(file1, "initial");
        repo.getStorage().addFile(name1);
        repo.getStorage().addFile(name2);
        Assert.assertEquals(repo.getStorage().getControlledFiles().size(), 2);

        repo.commit("1");
        List<String> lines = FileUtils.readLines(file1);
        Assert.assertEquals("initial", lines.get(0));

        FileUtils.writeStringToFile(file1, "changed1");
        FileUtils.writeStringToFile(file2, "changed2");
        repo.getStorage().resetFile(name1, repo.getCurrentRevision().getId());

        Assert.assertTrue(file1.exists());
        lines = FileUtils.readLines(file1);
        Assert.assertEquals("initial", lines.get(0));
        lines = FileUtils.readLines(file2);
        Assert.assertEquals("changed2", lines.get(0));

        Assert.assertEquals(repo.getStorage().getControlledFiles().size(), 2);
    }

    @Test
    public void testResetAbsent() throws IOException, VcsException {
        repo.commit("1");
        String name1 = "1";
        File file1 = new File(folder.getRoot(), name1);
        Assert.assertFalse(file1.exists());
        repo.getStorage().resetFile(name1, repo.getCurrentRevision().getId());
    }
}
