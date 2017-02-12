package vcs.commands.impl;

import vcs.commands.Command;
import vcs.util.VcsException;

import java.io.IOException;
import java.util.List;

import static vcs.Main.getRepo;

/**
 * @author natalia on 25.09.16.
 */
public class CheckoutCommand implements Command {
    public void execute(List<String> args) throws VcsException, IOException {
        if (args.size() == 0) {
            System.out.println("Branch or revision is not specified");
            return;
        }
        if (args.get(0).equals("-r")) {
            if (args.size() > 1) {
                getRepo().checkoutRevision(args.get(1));
            } else {
                System.out.println("Revision is not specified");
            }
            return;
        }
        getRepo().checkoutBranch(args.get(0));
    }
}
