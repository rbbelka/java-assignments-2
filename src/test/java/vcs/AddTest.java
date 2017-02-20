package vcs;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import vcs.exceptions.VcsException;

import java.io.File;
import java.io.IOException;

public class AddTest extends VcsTest {

    @Test
    public void testAdd() throws IOException, VcsException {
        String name1 = "1";
        File file1 = folder.newFile(name1);
        repo.getStorage().addFile(name1);
        Assert.assertTrue(file1.exists());
        Assert.assertEquals(repo.getStorage().getControlledFiles().size(), 1);
    }

    @Test
    public void testAddAbsent() throws IOException, VcsException {
        String name1 = "1";
        File file1 = new File(folder.getRoot(), name1);
        Assert.assertFalse(file1.exists());
        repo.getStorage().addFile(name1);
        Assert.assertEquals(repo.getStorage().getControlledFiles().size(), 0);
    }

    @Test
    public void testAddDeleted() throws IOException, VcsException {
        String name1 = "1";
        File file1 = folder.newFile(name1);
        Assert.assertTrue(file1.exists());
        repo.getStorage().addFile(name1);
        File fileCur = new File(repo.getStorage().getCurDir(), name1);
        Assert.assertTrue(fileCur.exists());
        Assert.assertEquals(repo.getStorage().getControlledFiles().size(), 1);

        FileUtils.deleteQuietly(file1);
        Assert.assertFalse(file1.exists());
        repo.getStorage().addFile(name1);
        Assert.assertFalse(fileCur.exists());
        Assert.assertEquals(repo.getStorage().getControlledFiles().size(), 1);
    }
}
