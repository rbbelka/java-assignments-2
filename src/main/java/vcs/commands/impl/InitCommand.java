package vcs.commands.impl;

import vcs.commands.Command;
import vcs.repo.Repository;
import vcs.util.VcsException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static vcs.Main.setRepo;
import static vcs.util.Util.*;

/**
 * @author natalia on 25.09.16.
 */

public class InitCommand implements Command {

    public void execute(List<String> args) throws VcsException {
        if (args.size() > 0) {
            System.out.println("Command does not accept any arguments");
            return;
        }

        File vcsDir = new File(vcsDir());
        File curDir = new File(curDir());
        File init = new File(getInitFile());

        if (vcs.Main.checkInit()) {
            System.out.println("Repository has been already inited");
            return;
        }

        if (!vcsDir.mkdirs()) {
            throw new VcsException("Can't create vcs folder");
        }

        if (!curDir.mkdirs()) {
            throw new VcsException("Can't create temp folder");
        }

        try {
            if (!init.createNewFile()) {
                vcsDir.delete();
                throw new VcsException("Can't create init file");
            }
        } catch (IOException e) {
            throw new VcsException(e.getMessage());
        }

        setRepo(Repository.createRepository(userDir(), curDir()));

        System.out.println("Succesful init");

    }
}
