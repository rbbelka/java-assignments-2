package vcs.commands.impl;

import vcs.commands.Command;
import vcs.util.VcsException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static vcs.Main.getRepo;
import static vcs.util.Util.userDir;

/**
 * @author natalia on 28.09.16.
 */
public class CleanCommand implements Command {
    @Override
    public void execute(List<String> args) throws VcsException {
        List<String> untracked = getRepo().getStorage().getUntracked();

        try {
        for (String path : untracked)
                Files.deleteIfExists(Paths.get(userDir(), path));
        } catch (IOException e) {
            throw new VcsException(e.getMessage());
        }

        System.out.println("Repository cleaned");
    }
}
