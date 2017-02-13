package vcs.commands;

import vcs.exceptions.VcsException;
import vcs.repo.Repository;

import java.io.IOException;
import java.util.List;

/**
 * @author natalia on 25.09.16.
 */

public interface Command {
    void execute(Repository repo, List<String> args) throws VcsException, IOException;
}
