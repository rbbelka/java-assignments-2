package vcs.commands.impl;

import vcs.commands.Command;
import vcs.exceptions.WrongNumberOfArgumentsException;
import vcs.exceptions.VcsException;
import vcs.repo.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author natalia on 25.09.16.
 */
public class MergeCommand implements Command {
    public void execute(Repository repo, List<String> args) throws VcsException, IOException {
        if (args.size() == 0) {
            throw new WrongNumberOfArgumentsException("Branch to merge is not specified");
        }
        String message = "";
        if (args.size() > 1) {
            message = args.stream().skip(1).collect(Collectors.joining(" "));
        }
        repo.merge(args.get(0), message);
    }
}
