package vcs;

import vcs.commands.Command;
import vcs.commands.CommandFactory;
import vcs.repo.Repository;
import vcs.util.Serializer;
import vcs.exceptions.VcsException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static vcs.util.Util.getInitFile;

public class Main {

    private static Repository repo;
    private static final String initFile = getInitFile();

    public static void main(String[] args) {

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

        try {
        if (checkInit()) {
                repo = Serializer.deserialize(initFile);
        } else if (!commandName.equals("init")) {
            initError();
            return;
        }

        List<String> cmdArgs = Arrays.asList(args).subList(1, args.length);
        try {
            command.execute(cmdArgs);
        } catch (VcsException e) {
            System.out.println(e.getMessage());
        }

        Serializer.serialize(repo, initFile);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Repository getRepo() {
        return repo;
    }

    public static void setRepo(Repository newRepo) {
        if (newRepo != null)
            repo = newRepo;
    }

    public static boolean checkInit() {
        return (new File(getInitFile()).exists());
    }

    private static void inputError() {
        System.out.println("Incorrect input");
    }

    private static void initError() {
        System.out.println("Not a repository");
    }

}
