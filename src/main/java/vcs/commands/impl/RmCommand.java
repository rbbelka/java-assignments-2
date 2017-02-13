package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.util.Util;
import vcs.exceptions.VcsException;

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

    @Override
    public void execute(List<String> args) throws VcsException, IOException {
        if (args.size() == 0) {
            throw new WrongNumberOfArgumentsException("Files to delete are not specified");
        }
        for (String arg : args) {
            if (!Util.checkFile(arg))
                continue;

            boolean deleted;
            getRepo().getStorage().resetFile(arg);
            deleted = Files.deleteIfExists(Paths.get(userDir(), arg));

            if (deleted)
                System.out.println("Removed " + arg);
        }
    }
}
