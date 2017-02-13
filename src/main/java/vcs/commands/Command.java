package vcs.commands;

import vcs.exceptions.VcsException;

import java.io.IOException;
import java.util.List;

/**
 * @author natalia on 25.09.16.
 */

public interface Command {
    void execute(List<String> args) throws VcsException, IOException;
}
