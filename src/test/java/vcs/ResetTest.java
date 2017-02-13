package vcs;

import org.junit.Assert;
import org.junit.Test;
import vcs.exceptions.VcsException;

import java.io.File;
import java.io.IOException;

public class ResetTest extends VcsTest {

    @Test
    public void testReset() throws IOException, VcsException {
        String name1 = "1";
        String name2 = "2";
        File file1 = folder.newFile(name1);
        File file2 = folder.newFile(name2);
        repo.getStorage().addFile(name1);
        repo.getStorage().addFile(name2);
        Assert.assertEquals(repo.getStorage().getControlledFiles().size(), 2);

        repo.getStorage().resetFile(name1);

        Assert.assertTrue(file1.exists());
        Assert.assertEquals(repo.getStorage().getControlledFiles().size(), 1);
    }

    @Test
    public void testResetAbsent() throws IOException, VcsException {
        String name1 = "1";
        File file1 = new File(folder.getRoot(), name1);
        Assert.assertFalse(file1.exists());
        repo.getStorage().resetFile(name1);
    }
}
