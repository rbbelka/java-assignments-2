package vcs;

import org.junit.Assert;
import org.junit.Test;
import vcs.exceptions.VcsException;

import java.io.File;
import java.io.IOException;

public class CheckoutTest extends VcsTest {

    @Test
    public void testCheckout() throws IOException, VcsException {

        repo.commit("1");

        String name1 = "1";
        File file1 = folder.newFile(name1);
        repo.createBranch("test1");
        repo.checkoutBranch("test1");
        repo.getStorage().addFile(name1);
        repo.commit("2");

        repo.checkoutBranch("master");
        String name2 = "2";
        File file2 = folder.newFile(name2);
        repo.createBranch("test2");
        repo.checkoutBranch("test2");
        repo.getStorage().addFile(name2);
        repo.commit("3");

        repo.checkoutBranch("master");
        Assert.assertFalse(file1.exists());
        Assert.assertFalse(file2.exists());

        repo.checkoutBranch("test1");
        Assert.assertTrue(file1.exists());
        Assert.assertFalse(file2.exists());

        repo.checkoutBranch("test2");
        Assert.assertFalse(file1.exists());
        Assert.assertTrue(file2.exists());
    }
}
