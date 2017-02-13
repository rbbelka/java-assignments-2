package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.RevisionNotSpecifiedException;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.exceptions.VcsException;
import vcs.repo.Repository;

import java.io.IOException;
import java.util.List;


/**
 * @author natalia on 25.09.16.
 */
public class CheckoutCommand implements Command {
    public void execute(Repository repo, List<String> args) throws IOException, VcsException {
        if (args.size() == 0) {
            throw new WrongNumberOfArgumentsException("Branch or revision is not specified");
        }
        if (args.get(0).equals("-r")) {
            if (args.size() > 1) {
                repo.checkoutRevision(args.get(1));
            } else {
                throw new RevisionNotSpecifiedException("Revision is not specified");
            }
            return;
        }
        repo.checkoutBranch(args.get(0));
    }
}
