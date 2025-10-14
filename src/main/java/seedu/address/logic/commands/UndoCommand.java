package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Clears the address book.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_EMPTY = "No commands to undo.";
    public static final String MESSAGE_NON_MUTABLE = "Last command is not mutable, cannot undo.";
    public static final String MESSAGE_SUCCESS = "Undo successful.\n%1$s";


    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // get the last command from the undo stack and execute its undo method
        Optional<Command> lastCommand = model.popLastMutableCommand();

        if (lastCommand.isPresent()) {
            Command command = lastCommand.get();
            // Check if the command is mutable before redoing
            if (command.isMutable()) {
                String undoMessage = command.undo(model);
                // After undoing, add the command back to the redo stack
                model.pushUndoCommandHistory(command);
                return new CommandResult(String.format(MESSAGE_SUCCESS, undoMessage));
            }
            throw new CommandException(MESSAGE_NON_MUTABLE);

        } else {
            throw new CommandException(MESSAGE_EMPTY);
        }
    }
}
