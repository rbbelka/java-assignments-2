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
        final String id = args.get(0);
        getRepo().checkout(id);
    }
}
