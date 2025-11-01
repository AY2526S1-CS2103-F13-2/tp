package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MANUAL = String.join("\n",
            "NAME",
            "  delete — Deletes a person identified by index from the address book.",
            "",
            "USAGE",
            "  delete INDEX",
            "",
            "PARAMETERS",
            "  • INDEX: positive integer referring to the displayed list index",
            "",
            "EXAMPLES",
            "  delete 3",
            "",
            "SEE MORE",
            "  https://ay2526s1-cs2103-f13-2.github.io/tp/UserGuide.html#deleting-a-person--delete"
    );

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_UNDO_SUCCESS = "Restored last removed person: %1$s";
    public static final String MESSAGE_UNDO_FAILED = "No person to restore.";

    private final Index targetIndex;

    private Person personToDelete = null;
    private ReadOnlyAddressBook currentAddressBook = null;

    public DeleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (personToDelete == null && targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        currentAddressBook = new AddressBook(model.getAddressBook().getPersonList());
        personToDelete = personToDelete == null ? lastShownList.get(targetIndex.getZeroBased()) : personToDelete;
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public String man() {
        return MANUAL;
    }

    public boolean isMutable() {
        return true;
    }

    /**
     * Re-inserts the deleted person back into the model at the original index.
     * Assumes that the person to delete is not null and that the target index is valid.
     *
     * @param model The model to revert the deletion in.
     * @return A message indicating the person that was restored.
     * @throws IllegalStateException if there is no person to revert to.
     */
    @Override
    public String undo(Model model) {
        requireNonNull(model);

        if (personToDelete != null) {
            // Re-insert the deleted person at the original index
            model.setAddressBook(currentAddressBook);
            return String.format(MESSAGE_UNDO_SUCCESS, Messages.format(personToDelete));
        } else {
            throw new IllegalStateException(MESSAGE_UNDO_FAILED);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }

        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return targetIndex.equals(otherDeleteCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
