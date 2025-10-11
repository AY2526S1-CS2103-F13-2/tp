package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {

    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    public abstract CommandResult execute(Model model) throws CommandException;

    /**
     * Returns true if the command is mutable, false otherwise.
     * A mutable command is one that changes the state of the application.
     */
    public boolean isMutable() {
        return false;
    }

    /**
     * Undoes the command, reverting the model to its previous state.
     * This method should only be called if the command is mutable.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the undo operation result for display
     * @throws UnsupportedOperationException if the command is not mutable.
     */

    public String undo(Model model) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This command does not support undo.");
    }

}
