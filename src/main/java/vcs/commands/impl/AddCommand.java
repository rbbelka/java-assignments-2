package vcs.commands.impl;

import vcs.commands.Command;

import java.util.List;

import static vcs.Main.getRepo;

/**
 * @author natalia on 25.09.16.
 */

public class AddCommand implements Command {
    public void execute(List<String> args) {
        for (String arg : args) {
            if (getRepo().addFile(arg))
                System.out.println("Added" + arg);
        }
    }
}
