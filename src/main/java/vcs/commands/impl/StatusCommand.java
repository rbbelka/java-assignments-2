package vcs.commands.impl;

import vcs.commands.Command;
import vcs.util.VcsException;

import java.util.List;

import static vcs.Main.getRepo;

/**
 * @author natalia on 25.09.16.
 */
public class StatusCommand implements Command {
    public void execute(List<String> args) throws VcsException {
        if (args.size() > 0) {
            System.out.println("Command does not accept any arguments");
            return;
        }

        List<String> untracked = getRepo().getStorage().getUntracked();
        List<String> modified = getRepo().getStorage().getModifiedNotStaged();
        List<String> deleted = getRepo().getStorage().getDeletedNotStaged();

        printInfo(untracked, "Untracked");
        printInfo(modified, "Modified");
        printInfo(deleted, "Deleted");
    }

    private void printInfo(List<String> files, String fileType) {
        if (files.size() > 0)
            System.out.println(fileType + " files:");
        for (String filename : files)
            System.out.println(filename);
    }
}
