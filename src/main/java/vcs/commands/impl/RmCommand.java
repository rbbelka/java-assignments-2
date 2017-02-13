package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.VcsException;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.repo.Repository;

import java.io.IOException;
import java.util.List;

public class RmCommand implements Command {

    /**
     * Removes given files from working directory
     * and from {@link Repository}.
     *
     * @throws WrongNumberOfArgumentsException if no arguments provided.
     */
    public void execute(Repository repo, List<String> args) throws VcsException, IOException {
        if (args.size() == 0) {
            throw new WrongNumberOfArgumentsException("Files to delete are not specified");
        }
        for (String arg : args) {
            boolean deleted = repo.getStorage().removeFile(arg);

            if (deleted)
                System.out.println("Removed " + arg);
        }
    }
}
