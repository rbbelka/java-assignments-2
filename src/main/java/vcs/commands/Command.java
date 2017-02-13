package vcs.commands;

import vcs.exceptions.VcsException;
import vcs.repo.Repository;

import java.io.IOException;
import java.util.List;

public interface Command {

    /**
     * @param repo repository to execute command on
     * @param args arguments for command
     */
    void execute(Repository repo, List<String> args) throws VcsException, IOException;
}
