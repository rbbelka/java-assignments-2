package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.VcsException;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.repo.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MergeCommand implements Command {

    /**
     * Merges given branch into current branch
     *
     * @param args Branch to merge and commit message
     *
     * @throws WrongNumberOfArgumentsException if no arguments provided.
     */
    public void execute(Repository repo, List<String> args) throws VcsException, IOException {
        if (args.size() == 0) {
            throw new WrongNumberOfArgumentsException("Branch to merge is not specified");
        }
        String message = "";
        if (args.size() > 1) {
            message = args.stream().skip(1).collect(Collectors.joining(" "));
        }
        repo.merge(args.get(0), message);
    }
}
