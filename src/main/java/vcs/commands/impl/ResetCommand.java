package vcs.commands.impl;

import vcs.commands.Command;
import vcs.util.Util;
import vcs.util.VcsException;

import java.io.File;
import java.util.List;

import static vcs.Main.getRepo;

/**
 * @author natalia on 28.09.16.
 */

public class ResetCommand  implements Command {
    public void execute(List<String> args) throws VcsException {
        for (String arg : args) {
            if (! Util.checkFile(arg))
                continue;

            if (getRepo().getStorage().resetFile(arg))
                System.out.println("Reset " + arg);
        }
    }
}
