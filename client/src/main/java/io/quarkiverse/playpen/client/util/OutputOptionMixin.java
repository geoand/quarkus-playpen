package io.quarkiverse.playpen.client.util;

import static io.quarkiverse.playpen.client.util.MessageIcons.ERROR_ICON;
import static io.quarkiverse.playpen.client.util.MessageIcons.WARN_ICON;

import java.io.PrintWriter;
import java.util.List;

import io.quarkiverse.playpen.client.PlaypenCommand;
import picocli.CommandLine;
import picocli.CommandLine.Help.ColorScheme;
import picocli.CommandLine.Model.CommandSpec;

public class OutputOptionMixin implements MessageWriter {

    static final boolean picocliDebugEnabled = "DEBUG".equalsIgnoreCase(System.getProperty("picocli.trace"));

    boolean verbose = false;

    @CommandLine.Option(names = { "-e", "--errors" }, description = "Print more context on errors and exceptions.")
    boolean showErrors;

    @CommandLine.Spec(CommandLine.Spec.Target.MIXEE)
    CommandSpec mixee;

    ColorScheme scheme;
    PrintWriter out;
    PrintWriter err;

    ColorScheme colorScheme() {
        ColorScheme colors = scheme;
        if (colors == null) {
            colors = scheme = mixee.commandLine().getColorScheme();
        }
        return colors;
    }

    public PrintWriter out() {
        PrintWriter o = out;
        if (o == null) {
            o = out = mixee.commandLine().getOut();
        }
        return o;
    }

    public PrintWriter err() {
        PrintWriter e = err;
        if (e == null) {
            e = err = mixee.commandLine().getErr();
        }
        return e;
    }

    public boolean isShowErrors() {
        return showErrors || picocliDebugEnabled;
    }

    private static OutputOptionMixin getOutput(CommandSpec commandSpec) {
        return ((PlaypenCommand) commandSpec.root().userObject()).getOutput();
    }

    @CommandLine.Option(names = { "--verbose" }, description = "Verbose mode.")
    public void setVerbose(boolean verbose) {
        getOutput(mixee).verbose = verbose;
    }

    public boolean getVerbose() {
        return getOutput(mixee).verbose;
    }

    public boolean isVerbose() {
        return getVerbose() || picocliDebugEnabled;
    }

    public boolean isAnsiEnabled() {
        return CommandLine.Help.Ansi.AUTO.enabled();
    }

    public void printText(String... text) {
        for (String line : text) {
            out().println(colorScheme().ansi().new Text(line, colorScheme()));
        }
    }

    public void printErrorText(String[] text) {
        for (String line : text) {
            err().println(colorScheme().errorText(line));
        }
    }

    public void printStackTrace(Exception ex) {
        if (isShowErrors()) {
            err().println(colorScheme().stackTraceText(ex));
        }
    }

    public void info(String msg) {
        out().println(colorScheme().ansi().new Text(msg, colorScheme()));
    }

    public void error(String msg) {
        out().println(colorScheme().errorText(ERROR_ICON + " " + msg));
    }

    public boolean isDebugEnabled() {
        return isVerbose();
    }

    public void debug(String msg) {
        if (isVerbose()) {
            out().println(colorScheme().ansi().new Text("@|faint [DEBUG] " + msg + "|@", colorScheme()));
        }
    }

    @Override
    public void warn(String msg) {
        out().println(colorScheme().ansi().new Text("@|yellow " + WARN_ICON + " " + msg + "|@", colorScheme()));
    }

    // CommandLine must be passed in (forwarded commands)
    public void throwIfUnmatchedArguments(CommandLine cmd) {
        List<String> unmatchedArguments = cmd.getUnmatchedArguments();
        if (!unmatchedArguments.isEmpty()) {
            throw new CommandLine.UnmatchedArgumentException(cmd, unmatchedArguments);
        }
    }

    public int handleCommandException(Exception ex, String message) {
        CommandLine cmd = mixee.commandLine();
        printStackTrace(ex);
        if (ex instanceof CommandLine.ParameterException) {
            CommandLine.UnmatchedArgumentException.printSuggestions((CommandLine.ParameterException) ex, out());
        }
        error(message);
        return cmd.getExitCodeExceptionMapper() != null ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : mixee.exitCodeOnInvalidInput();
    }

    @Override
    public String toString() {
        return "OutputOptions [showErrors=" + showErrors
                + ", verbose=" + getVerbose() + "]";
    }

}
