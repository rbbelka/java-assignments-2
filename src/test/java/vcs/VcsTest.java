package vcs;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import vcs.repo.Repository;

import java.io.IOException;

import static vcs.repo.Repository.createRepository;

public class VcsTest {
    protected Repository repo;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        repo = createRepository(folder.getRoot().getAbsolutePath());
        repo.createFileStructure();
    }
}
