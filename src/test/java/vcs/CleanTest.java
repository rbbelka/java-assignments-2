package vcs;

import org.junit.Assert;
import org.junit.Test;
import vcs.exceptions.VcsException;

import java.io.File;
import java.io.IOException;

public class CleanTest extends VcsTest {
    @Test
    public void testClean() throws IOException, VcsException {
        String name1 = "1";
        String name2 = "2";
        File file1 = folder.newFile(name1);
        File file2 = folder.newFile(name2);
        repo.getStorage().addFile(name1);

        repo.getStorage().clean();
        Assert.assertTrue(file1.exists());
        Assert.assertFalse(file2.exists());
    }
}
