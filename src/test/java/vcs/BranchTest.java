package vcs;

import org.junit.Before;
import org.junit.Test;
import vcs.exceptions.VcsException;
import vcs.repo.Repository;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BranchTest extends VcsTest {

    private static final String TEST = "test";

    @Before
    public void initCommit() throws IOException {
        repo.commit("init");
    }

    @Test
    public void testDefault() {
        assertEquals(1, repo.getBranches().size());
        assertEquals(Repository.DEFAULT_BRANCH, repo.getCurrentBranchName());
        assertEquals(1, repo.getCurrentRevision().getId());
    }

    @Test
    public void testCreate() throws VcsException, IOException {
        repo.createBranch(TEST);
        assertEquals(2, repo.getBranches().size());
        assertEquals(Repository.DEFAULT_BRANCH, repo.getCurrentBranchName());
        repo.checkoutBranch(TEST);
        assertEquals(TEST, repo.getCurrentBranchName());
    }

    @Test
    public void testDelete() throws VcsException {
        repo.createBranch(TEST);
        repo.deleteBranch(TEST);
        assertEquals(1, repo.getBranches().size());
    }

    @Test(expected = VcsException.class)
    public void testAlreadyAdded() throws VcsException {
        repo.createBranch(TEST);
        repo.createBranch(TEST);
    }

    @Test(expected = VcsException.class)
    public void testCantDelete() throws VcsException {
        repo.deleteBranch(TEST);
    }
}
