package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.VcsException;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.repo.Repository;
import vcs.repo.Revision;

import java.util.List;

public class LogCommand implements Command {

    /**
     * Prints out commits in current branch
     * from newest to oldest
     *
     * @throws WrongNumberOfArgumentsException if any arguments given.
     */
    public void execute(Repository repo, List<String> args) throws VcsException {
        if (args.size() > 0) {
            throw new WrongNumberOfArgumentsException("Command does not accept any arguments");
        }
        List<Revision> revisions = repo.getLog();
        if (revisions.size() == 0) {
            System.out.println("Current branch does not have any commits yet");
        }

        System.out.println("Current branch: " + repo.getCurrentBranchName());
        for (Revision revision : revisions) {
            System.out.println(revision.getId() + ": " + revision.getCommitMessage());
        }
    }
}
