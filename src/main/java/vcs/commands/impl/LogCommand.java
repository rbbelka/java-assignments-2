package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.VcsException;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.repo.Repository;
import vcs.repo.Revision;

import java.util.List;

/**
 * @author natalia on 25.09.16.
 */
public class LogCommand implements Command {
    public void execute(Repository repo, List<String> args) throws VcsException {
        if (args.size() > 0) {
            throw new WrongNumberOfArgumentsException("Command does not accept any arguments");
        }
        List<Revision> revisions = repo.getLog();
        if (revisions.size() == 0) {
            System.out.println("Current branch does not have any commits yet");
        }

        System.out.println("Current branch: " + revisions.get(0).getBranchName());
        for (Revision revision : revisions) {
            System.out.println(revision.getId() + ": " + revision.getCommitMessage());
        }
    }
}
