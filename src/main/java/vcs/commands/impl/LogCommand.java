package vcs.commands.impl;

import vcs.commands.Command;
import vcs.repo.Repository;
import vcs.repo.Revision;

import java.util.List;

import static vcs.Main.getRepo;

/**
 * @author natalia on 25.09.16.
 */
public class LogCommand implements Command {
    public void execute(List<String> args) {
        if (args.size() > 0) {
            System.out.println("Command does not accept any arguments");
            return;
        }
        Repository repo = getRepo();
        String branchName = repo.getCurrentBranchName();
        Revision revision = repo.getCurrentRevision();
        if (revision == null) {
            System.out.println("Current branch " + branchName + " does not have any commits yet");
            return;
        }
        System.out.println("Current branch: " + branchName);
        while (revision != null) {
            System.out.println(revision.getId() + ": " + revision.getCommitMessage());
            revision = repo.getRevisionById(revision.getPrevious());
        }
    }
}
