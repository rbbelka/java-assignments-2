package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.util.Util;
import vcs.exceptions.VcsException;

import java.io.IOException;
import java.util.List;

import static vcs.Main.getRepo;

/**
 * @author natalia on 28.09.16.
 */

public class ResetCommand  implements Command {
    public void execute(List<String> args) throws VcsException, IOException {
        if (args.size() == 0) {
            throw new WrongNumberOfArgumentsException("Files to reset are not specified");
        }
        for (String arg : args) {
            if (! Util.checkFile(arg))
                continue;

            getRepo().getStorage().resetFile(arg);
            System.out.println("Reset " + arg);
        }
    }
}
