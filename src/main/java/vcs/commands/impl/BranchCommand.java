package vcs.commands.impl;

import vcs.commands.Command;
import vcs.util.VcsException;

import java.util.List;
import java.util.stream.Collectors;

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
                throw new VcsException("Not specified branch name");
            }
            return;
        }
        getRepo().createBranch(args.get(0));
    }
}
