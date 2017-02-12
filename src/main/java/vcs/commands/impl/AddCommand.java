package vcs.commands.impl;

import vcs.commands.Command;
import vcs.util.Util;
import vcs.util.VcsException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static vcs.Main.getRepo;

/**
 * @author natalia on 25.09.16.
 */

public class AddCommand implements Command {
    public void execute(List<String> args) throws VcsException {
        if (args.size() == 0) {
            System.out.println("Files to add are not specified");
            return;
        }
        for (String arg : args) {
            if (!Util.checkFile(arg))
                continue;

            getRepo().getStorage().addFile(arg);
            System.out.println("Added " + arg);
        }
    }
}
