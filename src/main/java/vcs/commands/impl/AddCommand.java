package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.VcsException;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.repo.Repository;

import java.io.IOException;
import java.util.List;


public class AddCommand implements Command {

    /**
     * Adds files to repository
     * Stages changes(including deletion of file) in files for commit
     *
     * @param args list of paths of files to add
     *
     * @throws WrongNumberOfArgumentsException if no arguments provided
     */
    public void execute(Repository repo, List<String> args) throws IOException, VcsException {
        if (args.size() == 0) {
            throw new WrongNumberOfArgumentsException("Files to add are not specified");
        }
        for (String arg : args) {
            boolean added = repo.getStorage().addFile(arg);
            if (added)
                System.out.println("Added " + arg);
        }
    }
}
