package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.repo.Repository;
import vcs.exceptions.VcsException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author natalia on 25.09.16.
 */

public class InitCommand implements Command {

    public void execute(Repository repo, List<String> args) throws VcsException, IOException {
        if (args.size() > 0) {
            throw new WrongNumberOfArgumentsException("Command does not accept any arguments");
        }

        File vcsDir = new File(repo.getVcsDir());
        File curDir = new File(repo.getStorage().getCurDir());
        File init = new File(repo.getInitFile());

        if (repo.checkInit()) {
            System.out.println("Repository has been already inited");
            return;
        }

        if (!vcsDir.mkdirs()) {
            throw new IOException("Can't create vcs folder");
        }

        if (!curDir.mkdirs()) {
            vcsDir.delete();
            throw new IOException("Can't create temp folder");
        }

        if (!init.createNewFile()) {
            vcsDir.delete();
            throw new IOException("Can't create init file");
        }

        System.out.println("Succesful init");

    }
}
