package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.BranchNotSpecifiedException;
import vcs.exceptions.VcsException;

import java.util.List;

import static vcs.Main.getRepo;

/**
 * @author natalia on 25.09.16.
 */
public class BranchCommand implements Command {
    public void execute(List<String> args) throws VcsException {
        if (args.size() == 0) {
            System.out.println(getRepo().getCurrentBranchName());
            return;
        }
        if (args.get(0).equals("-d")) {
            if (args.size() > 1) {
                getRepo().deleteBranch(args.get(1));
            } else {
                throw new BranchNotSpecifiedException("Branch is not specified");
            }
            return;
        }
        getRepo().createBranch(args.get(0));
    }
}
