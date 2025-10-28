package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Lists all persons in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all persons, or only those with the given tag.\n"
            + "Examples:\n"
            + "  list\n"
            + "  list t/policyholder";
    public static final String MESSAGE_LIST_BY_TAG_PREFIX = "Listed person(s) who ";
    public static final String MESSAGE_SUCCESS = "Listed all persons";

    public static final String MANUAL = String.join("\n",
            "NAME",
            "  list — Lists all persons in the address book.",
            "",
            "USAGE",
            "  list",
            "  list t/TAG",
            "",
            "EXAMPLES",
            "  list",
            "  list t/TAG",
            "",
            "SEE MORE",
            "  https://ay2526s1-cs2103-f13-2.github.io/tp/UserGuide.html#listing-all-persons--list"
    );

    private final Predicate<Person> predicate; // null => list all
    private final List<String> tagNamesForSidebar;
    private final boolean saveFolder;

    /** List all. */
    public ListCommand() {
        this.predicate = null;
        this.tagNamesForSidebar = java.util.Collections.emptyList();
        this.saveFolder = false;
    }

    /** Old ctor (no sidebar updates). */
    public ListCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
        this.tagNamesForSidebar = Collections.emptyList();
        this.saveFolder = false;
    }

    /** Predicate + tags + whether to save a folder. */
    public ListCommand(Predicate<Person> predicate, List<String> tagNames, boolean saveFolder) {
        this.predicate = predicate;
        this.tagNamesForSidebar = (tagNames == null) ? Collections.emptyList() : tagNames;
        this.saveFolder = saveFolder;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        if (predicate == null) {
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            return new CommandResult(MESSAGE_SUCCESS);
        }

        // filter list
        model.updateFilteredPersonList(predicate);

        // Only SAVE to the sidebar when user explicitly added the trailing 's'
        if (saveFolder && !tagNamesForSidebar.isEmpty()) {
            if (tagNamesForSidebar.size() == 1) {
                // single-tag folder (create if missing)
                model.addActiveTagFolders(tagNamesForSidebar);
            } else {
                // multi-tag composite folder
                model.addCompositeTagFolder(tagNamesForSidebar);
            }
        }

        return new CommandResult(MESSAGE_LIST_BY_TAG_PREFIX + predicate);
    }

    @Override
    public String man() {
        return MANUAL;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof ListCommand
                && ((this.predicate == null && ((ListCommand) other).predicate == null)
                || (this.predicate != null && this.predicate.equals(((ListCommand) other).predicate))));
    }
}
