package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.BranchNotSpecifiedException;
import vcs.exceptions.VcsException;
import vcs.repo.Repository;

import java.util.List;

/**
 * @author natalia on 25.09.16.
 */
public class BranchCommand implements Command {
    public void execute(Repository repo, List<String> args) throws VcsException {
        if (args.size() == 0) {
            System.out.println(repo.getCurrentBranchName());
            return;
        }
        if (args.get(0).equals("-d")) {
            if (args.size() > 1) {
                repo.deleteBranch(args.get(1));
            } else {
                throw new BranchNotSpecifiedException("Branch is not specified");
            }
            return;
        }
        repo.createBranch(args.get(0));
    }
}
