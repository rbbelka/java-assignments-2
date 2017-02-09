package vcs.commands.impl;

import vcs.commands.Command;
import vcs.util.VcsException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static vcs.Main.getRepo;

/**
 * @author natalia on 25.09.16.
 */
public class CommitCommand implements Command {

    public void execute(List<String> args) throws VcsException, IOException {
        String message = args.stream().collect(Collectors.joining(" "));
        getRepo().commit(message);
        //  TODO deal with added, modified, deleted files
    }
}
