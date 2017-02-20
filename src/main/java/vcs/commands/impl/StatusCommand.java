package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.VcsException;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.repo.Repository;

import java.io.IOException;
import java.util.List;

public class StatusCommand implements Command {

    /**
     * Prints out unstaged changes in working directory.
     *
     * @throws WrongNumberOfArgumentsException if any arguments given.
     */
    public void execute(Repository repo, List<String> args) throws VcsException, IOException {
        if (args.size() > 0) {
            throw new WrongNumberOfArgumentsException("Command does not accept any arguments");
        }

        List<String> untracked = repo.getStorage().getUntracked();
        List<String> modified = repo.getStorage().getModifiedNotStaged();
        List<String> deleted = repo.getStorage().getDeletedNotStaged();

        printInfo(untracked, "Untracked");
        printInfo(modified, "Modified");
        printInfo(deleted, "Deleted");
    }

    private void printInfo(List<String> files, String fileType) {
        if (files.size() > 0)
            System.out.println(fileType + " files:");
        for (String filename : files)
            System.out.println(filename);
    }
}
