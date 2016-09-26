package vcs.commands.impl;

import vcs.commands.Command;
import vcs.util.VcsException;

import java.io.File;
import java.util.List;

import static vcs.util.Util.vcsDir;

/**
 * @author natalia on 25.09.16.
 */

public class InitCommand implements Command {

    public void execute(List<String> args) throws VcsException {

        File vcsDir = new File(vcsDir());

        if (vcsDir.isDirectory()) {
            System.out.println("Repository has been already inited");
            return;
        }

        if (vcsDir.mkdirs())
            System.out.println("Succesfully inited");
        else
            throw new VcsException("Can't create vs folder");
    }
}
