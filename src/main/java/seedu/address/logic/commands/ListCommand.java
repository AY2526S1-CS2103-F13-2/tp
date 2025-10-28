package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.logic.commands.exceptions.CommandException;
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
            + "  list t/policyholder"
            + "  list t/friends t/colleagues s/";
    public static final String MESSAGE_LIST_BY_TAG_PREFIX = "Listed person(s) who ";
    public static final String MESSAGE_SUCCESS = "Listed all persons";
    public static final String MESSAGE_FOLDER_EXISTS = "Folder \"%s\" already exists";
    public static final String MESSAGE_FOLDER_CREATED = "Created folder \"%s\" and listed matching persons";
    public static final String MESSAGE_LISTED_MATCHING = "Listed persons with all of: %s";
    public static final String MESSAGE_FOLDER_DELETED = "Deleted folder \"%s\"";
    public static final String MESSAGE_FOLDER_MISSING = "Folder \"%s\" not found";

    public static final String MANUAL = String.join("\n",
            "NAME",
            "  list — Lists all persons in the address book.",
            "",
            "USAGE",
            "  list",
            "  list t/TAG",
            "  list t/TAG [t/TAG ...] [s/]",
            "  list t/TAG [t/TAG ...] [d/]",
            "",
            "EXAMPLES",
            "  list",
            "  list t/TAG",
            "  list t/friends t/colleagues s/",
            "  list t/friends t/colleagues d/",
            "",
            "SEE MORE",
            "  https://ay2526s1-cs2103-f13-2.github.io/tp/UserGuide.html#listing-all-persons--list"
    );

    private final Predicate<Person> predicate; // null => list all
    private final List<String> tagNamesForSidebar;
    private final boolean saveFolder;
    private final boolean deleteFolder;

    /** List all. */
    public ListCommand() {
        this.predicate = null;
        this.tagNamesForSidebar = java.util.Collections.emptyList();
        this.saveFolder = false;
        this.deleteFolder = false;
    }

    /** Old ctor (no sidebar updates). */
    public ListCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
        this.tagNamesForSidebar = Collections.emptyList();
        this.saveFolder = false;
        this.deleteFolder = false;
    }

    /** Predicate + tags + whether to save/delete a folder. */
    public ListCommand(Predicate<Person> predicate, List<String> tagNames,
                       boolean saveFolder, boolean deleteFolder) {
        this.predicate = predicate;
        this.tagNamesForSidebar = (tagNames == null) ? java.util.Collections.emptyList() : tagNames;
        this.saveFolder = saveFolder;
        this.deleteFolder = deleteFolder;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (predicate == null) {
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            return new CommandResult(MESSAGE_SUCCESS);
        }

        // filter list
        model.updateFilteredPersonList(predicate);

        // Delete flow
        if (deleteFolder) {
            final String folderName = deriveFolderName(tagNamesForSidebar);
            if (!model.hasTagFolder(folderName)) {
                throw new CommandException(String.format(MESSAGE_FOLDER_MISSING, folderName));
            }
            boolean removed = model.removeTagFolderByName(folderName);
            if (!removed) {
                throw new CommandException(String.format(MESSAGE_FOLDER_MISSING, folderName));
            }
            return new CommandResult(String.format(MESSAGE_FOLDER_DELETED, folderName));
        }

        // Only create a folder when user explicitly specified s/
        if (saveFolder && !tagNamesForSidebar.isEmpty()) {
            final String folderName = deriveFolderName(tagNamesForSidebar);

            // Guard against duplicates
            if (model.hasTagFolder(folderName)) {
                throw new CommandException(String.format(MESSAGE_FOLDER_EXISTS, folderName));
            }

            // Create the folder using your existing API
            if (tagNamesForSidebar.size() == 1) {
                model.addActiveTagFoldersFromUser(tagNamesForSidebar);
            } else {
                model.addCompositeTagFolderFromUser(tagNamesForSidebar);
            }

            return new CommandResult(String.format(MESSAGE_FOLDER_CREATED, folderName));
        }

        // No save requested → just report what we listed
        return new CommandResult(String.format(
                MESSAGE_LISTED_MATCHING, String.join(", ", tagNamesForSidebar)));
    }

    /** Build a stable, human-readable folder name like "friends & colleagues". */
    private static String deriveFolderName(List<String> tags) {
        // remove dupes, preserve order
        Set<String> unique = new LinkedHashSet<>(tags);
        return String.join(" & ", unique);
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
