package vcs.commands;

import vcs.commands.impl.*;

import java.util.HashMap;
import java.util.Map;


public class CommandFactory {
    private static final Map<String, Command> supportedCommands = new HashMap<>();

    static {
        supportedCommands.put("add", new AddCommand());
        supportedCommands.put("branch", new BranchCommand());
        supportedCommands.put("checkout", new CheckoutCommand());
        supportedCommands.put("clean", new CleanCommand());
        supportedCommands.put("commit", new CommitCommand());
        supportedCommands.put("init", new InitCommand());
        supportedCommands.put("log", new LogCommand());
        supportedCommands.put("merge", new MergeCommand());
        supportedCommands.put("reset", new ResetCommand());
        supportedCommands.put("rm", new RmCommand());
        supportedCommands.put("status", new StatusCommand());
    }

    /**
     * Provides {@link Command} by given name.
     *
     * @param commandName name of command to get
     */
    public static Command createCommand(String commandName) {
        return supportedCommands.get(commandName);
    }
}
