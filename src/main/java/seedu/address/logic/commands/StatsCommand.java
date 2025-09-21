package seedu.address.logic.commands;

import seedu.address.model.Model;

public class StatsCommand extends Command{
    public static final String COMMAND_WORD = "stats";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(
                "------------------------------------------\n" +
                "Insurance Packages           No. of Customers\n" +
                "Package A                              138\n" +
                "------------------------------------------\n");
    }
}
