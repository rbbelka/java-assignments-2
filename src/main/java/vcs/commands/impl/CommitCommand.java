package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.VcsException;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.repo.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author natalia on 25.09.16.
 */
public class CommitCommand implements Command {

    /**
     * Commits staged changes
     *
     * @param args - commit message
     *
     * @throws WrongNumberOfArgumentsException if no arguments provided.
     */
    public void execute(Repository repo, List<String> args) throws VcsException, IOException {
        if (args.size() == 0) {
            throw new WrongNumberOfArgumentsException("Commit message is not entered");
        }
        String message = args.stream().collect(Collectors.joining(" "));
        repo.commit(message);
    }
}
