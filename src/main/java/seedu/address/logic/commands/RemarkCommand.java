package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Remark;

import java.util.List;

/**
 * Changes the remark of an existing person in the address book. (Currently not
 * implemented — throws to indicate WIP.)
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the remark of the person identified "
            + "by the index number used in the last person listing. "
            + "Existing remark will be overwritten by the input.\n" + "Parameters: INDEX (must be a positive integer) "
            + "r/[REMARK]\n" + "Example: " + COMMAND_WORD + " 1 " + "r/Likes to swim.";

    public static final String MESSAGE_NOT_IMPLEMENTED_YET = "Remark command not implemented yet";

    public static final String MESSAGE_ARGUMENTS = "Index: %1$d, Remark: %2$s";
    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to Person: %1$s";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from Person: %1$s";

    private final Index index;
    private final Remark remark;

    /**
     * @param index of the person in the filtered list whose remark to edit
     * @param remark new remark text
     */
    public RemarkCommand(Index index, Remark remark) {
        requireAllNonNull(index, remark);
        this.index = index;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(seedu.address.model.Model model) throws CommandException {
        requireAllNonNull(model);

        List<seedu.address.model.person.Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        seedu.address.model.person.Person personToEdit = lastShownList.get(index.getZeroBased());

        // create a new Person with same fields but new remark
        seedu.address.model.person.Person editedPerson = new seedu.address.model.person.Person(personToEdit.getName(),
                personToEdit.getPhone(), personToEdit.getEmail(), personToEdit.getAddress(), personToEdit.getTags(),
                remark // <-- new remark
        );

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RemarkCommand)) {
            return false;
        }
        RemarkCommand e = (RemarkCommand) other;
        return index.equals(e.index) && remark.equals(e.remark);
    }

    /**
     * Generates a command execution success message based on whether the remark
     * is added to or removed from {@code personToEdit}.
     */
    private String generateSuccessMessage(seedu.address.model.person.Person personToEdit) {
        String message = !remark.value.isEmpty() ? MESSAGE_ADD_REMARK_SUCCESS : MESSAGE_DELETE_REMARK_SUCCESS;
        return String.format(message, seedu.address.logic.Messages.format(personToEdit));
    }
}
