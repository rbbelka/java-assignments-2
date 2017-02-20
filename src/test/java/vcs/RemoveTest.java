package vcs;

import org.junit.Assert;
import org.junit.Test;
import vcs.exceptions.VcsException;

import java.io.File;
import java.io.IOException;

public class RemoveTest extends VcsTest {

    @Test
    public void testRemove() throws IOException, VcsException {
        String name1 = "1";
        File file1 = folder.newFile(name1);
        repo.getStorage().addFile(name1);
        Assert.assertEquals(repo.getStorage().getControlledFiles().size(), 1);
        repo.getStorage().removeFile(name1);
        Assert.assertFalse(file1.exists());
        Assert.assertEquals(repo.getStorage().getControlledFiles().size(), 0);
    }

    @Test
    public void testRemoveAbsent() throws IOException, VcsException {
        String name1 = "1";
        File file1 = new File(folder.getRoot(), name1);
        Assert.assertFalse(file1.exists());
        repo.getStorage().removeFile(name1);
    }
}
