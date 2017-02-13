package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.repo.Repository;
import vcs.exceptions.VcsException;

import java.io.File;
import java.io.IOException;
import java.util.List;



public class InitCommand implements Command {

    /**
     * Initializes repo
     * Creates necessary files and folders in working directory
     *
     * @throws WrongNumberOfArgumentsException if any arguments given.
     */
    public void execute(Repository repo, List<String> args) throws VcsException, IOException {
        if (args.size() > 0) {
            throw new WrongNumberOfArgumentsException("Command does not accept any arguments");
        }

        if (repo.checkInit()) {
            System.out.println("Repository has been already inited");
            return;
        }

        repo.createFileStructure();

        System.out.println("Succesful init");

    }
}
