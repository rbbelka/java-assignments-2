package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.VcsException;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.repo.Repository;

import java.io.IOException;
import java.util.List;

public class ResetCommand  implements Command {

    /**
     * Reset given files state in {@link Repository},
     * removes them from controlled.
     *
     * @throws WrongNumberOfArgumentsException if no arguments provided.
     */
    public void execute(Repository repo, List<String> args) throws VcsException, IOException {
        if (args.size() == 0) {
            throw new WrongNumberOfArgumentsException("Files to reset are not specified");
        }
        for (String arg : args) {
            boolean reset = repo.getStorage().resetFile(arg);
            if (reset)
                System.out.println("Reset " + arg);
        }
    }
}
