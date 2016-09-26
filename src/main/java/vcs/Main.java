package vcs;

import vcs.commands.Command;
import vcs.commands.CommandFactory;
import vcs.util.VcsException;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws VcsException {

        if (args.length < 1) {
            inputError();
            return;
        }

        String commandName = args[0];
        Command command = CommandFactory.createCommand(commandName);
        if (command == null) {
            inputError();
            return;
        }

        List<String> cmdArgs = Arrays.asList(args).subList(1, args.length);
        command.execute(cmdArgs);

    }

    private static void inputError() {
        System.out.println("Incorrect input");
    }

}
