package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.BranchNotSpecifiedException;
import vcs.exceptions.VcsException;
import vcs.repo.Repository;

import java.util.List;

public class BranchCommand implements Command {

    /**
     * Works with branches of {@link Repository}.
     *
     * If no args provided prints current branch.
     * If passed option -d deletes given branch,
     * else creates branch with given name.
     *
     * @throws BranchNotSpecifiedException if branch to delete is not specified
     */
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
