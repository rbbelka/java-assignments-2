package vcs.commands;

import vcs.commands.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author natalia on 25.09.16.
 */
public class CommandFactory {
    private static final Map<String, Command> supportedCommands = new HashMap<>();

    static {
        supportedCommands.put("add", new AddCommand());
        supportedCommands.put("branch", new BranchCommand());
        supportedCommands.put("checkout", new CheckoutCommand());
        supportedCommands.put("commit", new CommitCommand());
        supportedCommands.put("init", new InitCommand());
        supportedCommands.put("log", new LogCommand());
        supportedCommands.put("merge", new MergeCommand());
        supportedCommands.put("status", new StatusCommand());
    }

    public static Command createCommand(String commandName) {
        return supportedCommands.get(commandName);
    }
}
