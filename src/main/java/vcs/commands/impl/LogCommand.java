package vcs.commands.impl;

import vcs.commands.Command;

import java.util.List;

/**
 * @author natalia on 25.09.16.
 */
public class LogCommand implements Command {
    public void execute(List<String> args) {
        if (args.size() > 0) {
            System.out.println("Command does not accept any arguments");
            return;
        }
    }
}
