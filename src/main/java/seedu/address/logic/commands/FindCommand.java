package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.ClientMatchesPredicate;

/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case-insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all clients whose "
            + "name, phone number, or email contain any of the specified keywords "
            + "(case-insensitive, partial match allowed), and displays them as a list "
            + "with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " alice 9123 gmail";

    public static final String MANUAL = String.join("\n",
            "NAME",
            "  find — Finds all persons whose names, phone numbers, "
                    + "or email addresses contain any of the given keywords.",
            "",
            "USAGE",
            "  find KEYWORD [MORE_KEYWORDS]",
            "",
            "PARAMETERS",
            "  • Searches are performed across NAME, PHONE, and EMAIL fields.",
            "  • Keywords are case-insensitive; order doesn’t matter.",
            "  • Partial matches are supported (e.g., 'Al' matches 'Alice').",
            "",
            "EXAMPLES",
            "  find Alice",
            "  find Alice Bob",
            "  find 9876",
            "  find example.com",
            "",
            "SEE MORE",
            "  https://ay2526s1-cs2103-f13-2.github.io/tp/UserGuide.html#locating-persons-by-name-find"
    );

    private static final Logger logger = LogsCenter.getLogger(FindCommand.class);

    private final ClientMatchesPredicate predicate;

    /**
     * Creates a FindCommand to filter persons based on the given predicate.
     *
     * @param predicate the predicate to filter persons
     * @throws NullPointerException if predicate is null
     */
    public FindCommand(ClientMatchesPredicate predicate) {
        this.predicate = predicate;
        logger.fine("Created FindCommand with predicate: " + predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model, "Model cannot be null");

        logger.info("Executing find command with predicate: " + predicate);

        // Get count before filtering for logging purposes
        int totalPersons = model.getFilteredPersonList().size();

        model.updateFilteredPersonList(predicate);

        int matchedPersons = model.getFilteredPersonList().size();

        logger.info(String.format("Find command completed. Found %d person(s) matching criteria (from %d total)",
                matchedPersons, totalPersons));

        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, matchedPersons));
    }

    @Override
    public String man() {
        return MANUAL;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
