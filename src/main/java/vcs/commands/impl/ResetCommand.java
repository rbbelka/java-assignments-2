package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.RevisionNotFoundException;
import vcs.exceptions.VcsException;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.repo.Repository;

import java.io.IOException;
import java.util.List;

public class ResetCommand  implements Command {

    /**
     * Reset given files content to latest revision.
     *
     * @throws WrongNumberOfArgumentsException if no arguments provided.
     */
    public void execute(Repository repo, List<String> args) throws VcsException, IOException {
        if (args.size() == 0) {
            throw new WrongNumberOfArgumentsException("Files to reset are not specified");
        }
        int currentRevision = repo.getCurrentRevisionId();
        if (currentRevision == 0) {
            throw new RevisionNotFoundException("No revision to reset file");
        }
        for (String arg : args) {
            boolean reset = repo.getStorage().resetFile(arg, currentRevision);
            if (reset)
                System.out.println("Reset " + arg);
        }
    }
}
