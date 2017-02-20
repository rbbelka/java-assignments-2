package vcs;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import vcs.exceptions.MergeConflictException;
import vcs.exceptions.VcsException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MergeTest extends VcsTest {

    @Test
    public void testMerge() throws IOException, VcsException {
        String name1 = "1";
        String name2 = "2";
        String name4 = "4";
        File file1 = folder.newFile(name1);
        File file2 = folder.newFile(name2);
        File file4 = folder.newFile(name4);
        String initial = "init 2";
        FileUtils.writeStringToFile(file2, initial);
        repo.getStorage().addFile(name1);
        repo.getStorage().addFile(name2);
        repo.getStorage().addFile(name4);
        repo.commit(initial);

        repo.createBranch("test");
        repo.checkoutBranch("test");
        String changed = "changed 2";
        FileUtils.writeStringToFile(file2, changed);
        FileUtils.deleteQuietly(file1);
        String name3 = "3";
        File file3 = folder.newFile(name3);
        repo.getStorage().addFile(name1);
        repo.getStorage().addFile(name2);
        repo.getStorage().addFile(name3);
        repo.commit(changed);

        repo.checkoutBranch("master");
        Assert.assertTrue(file1.exists());
        Assert.assertTrue(file2.exists());
        List<String> lines = FileUtils.readLines(file2);
        Assert.assertEquals(initial, lines.get(0));
        Assert.assertFalse(file3.exists());
        String changed4 = "changed 4";
        FileUtils.writeStringToFile(file4, changed4);
        repo.getStorage().addFile(name4);
        repo.commit(changed4);

        repo.merge("test", "");
        Assert.assertFalse(file1.exists());
        Assert.assertTrue(file2.exists());
        lines = FileUtils.readLines(file2);
        Assert.assertEquals(changed, lines.get(0));
        Assert.assertTrue(file3.exists());
        Assert.assertTrue(file4.exists());
        lines = FileUtils.readLines(file4);
        Assert.assertEquals(changed4, lines.get(0));
    }

    @Test(expected = MergeConflictException.class)
    public void testCantMerge() throws IOException, VcsException {
        String name = "1";
        File file = folder.newFile(name);
        FileUtils.writeStringToFile(file, "initial");

        repo.getStorage().addFile(name);
        repo.commit("1");

        repo.createBranch("test");
        repo.checkoutBranch("test");
        FileUtils.writeStringToFile(file, "changed in test");
        repo.getStorage().addFile(name);
        repo.commit("2");

        repo.checkoutBranch("master");
        FileUtils.writeStringToFile(file, "changed in master");
        repo.getStorage().addFile(name);
        repo.commit("3");
        repo.merge("test", "");
    }
}
