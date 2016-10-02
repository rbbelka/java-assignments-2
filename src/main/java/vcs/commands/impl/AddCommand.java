package vcs.commands.impl;

import vcs.commands.Command;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static vcs.Main.getRepo;

/**
 * @author natalia on 25.09.16.
 */

public class AddCommand implements Command {
    public void execute(List<String> args) throws IOException {
        for (String arg : args) {
            File f = new File(arg);
            if(!f.isFile() || !f.canRead()) {
                System.out.println("Incorrect path: " + arg);
                continue;
            }

            getRepo().getStorage().addFile(arg);
            System.out.println("Added " + arg);
        }
    }
}
