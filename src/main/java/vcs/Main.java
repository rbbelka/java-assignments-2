package vcs;

import vcs.commands.Command;
import vcs.commands.CommandFactory;
import vcs.exceptions.VcsException;
import vcs.repo.Repository;
import vcs.util.Serializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static vcs.repo.Repository.createRepository;


/**
 * Entry point of app
 */
public class Main {

    public static void main(String[] args) {

        if (args.length < 1) {
            usage();
            return;
        }

        String commandName = args[0];
        Command command = CommandFactory.createCommand(commandName);
        if (command == null) {
            usage();
            return;
        }

        String userDir = System.getProperty("user.dir");
        Repository repo = createRepository(userDir);
        try {
            if (repo.checkInit()) {
                repo = Serializer.deserialize(repo.getInitFile());
            } else if (!commandName.equals("init")) {
                initError();
                return;
            }

            List<String> cmdArgs = Arrays.asList(args).subList(1, args.length);
            try {
                command.execute(repo, cmdArgs);
            } catch (VcsException e) {
                System.out.println(e.getMessage());
            }

        Serializer.serialize(repo, repo.getInitFile());

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void usage() {
        System.out.println("Incorrect input.");
        System.out.println("Usage: <cmd> <args>");
        System.out.println("List of commands: init, status, clean, log, add, reset, rm, commit, checkout, branch, merge.");
    }

    private static void initError() {
        System.out.println("Not a repository");
    }

}
