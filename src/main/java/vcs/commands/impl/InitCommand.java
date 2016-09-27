package vcs.commands.impl;

import vcs.commands.Command;
import vcs.repo.Repository;
import vcs.util.VcsException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static vcs.Main.setRepo;
import static vcs.util.Util.currentDir;
import static vcs.util.Util.getInitFile;
import static vcs.util.Util.vcsDir;

/**
 * @author natalia on 25.09.16.
 */

public class InitCommand implements Command {

    public void execute(List<String> args) throws VcsException, IOException {

        File vcsDir = new File(vcsDir());
        File init = new File(getInitFile());

        if (vcs.Main.checkInit()) {
            System.out.println("Repository has been already inited");
            return;
        }

        if (!vcsDir.mkdirs()) {
            throw new VcsException("Can't create vcs folder");
        }

        if (!init.createNewFile()) {
            vcsDir.delete();
            throw new VcsException("Can't create init file");
        }

        setRepo(Repository.createRepository(currentDir()));

        System.out.println("Succesful init");

    }
}
