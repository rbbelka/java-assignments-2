package vcs.commands.impl;

import vcs.commands.Command;
import vcs.util.Util;
import vcs.util.VcsException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static vcs.Main.getRepo;
import static vcs.util.Util.userDir;

/**
 * @author natalia on 28.09.16.
 */
public class RmCommand implements Command {
    private boolean deleted;

    @Override
    public void execute(List<String> args) throws VcsException {
        if (args.size() == 0) {
            System.out.println("Files to delete are not specified");
            return;
        }
        for (String arg : args) {
            if (! Util.checkFile(arg))
                continue;

            deleted = false;
            getRepo().getStorage().resetFile(arg);
            try {
                deleted = Files.deleteIfExists(Paths.get(userDir(), arg));
            } catch (IOException e) {
                throw new VcsException(e.getMessage());
            }

            if (deleted)
                System.out.println("Removed " + arg);
        }
    }
}
