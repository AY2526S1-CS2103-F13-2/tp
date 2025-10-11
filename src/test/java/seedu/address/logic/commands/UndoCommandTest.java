package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Optional;
import java.util.Stack;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class UndoCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void undoCommand_noCommandsToUndo_returnsEmptyMessage() {
        UndoCommand undoCommand = new UndoCommand();
        // No mutable command executed, so undo should return MESSAGE_EMPTY
        assertCommandFailure(undoCommand, model, UndoCommand.MESSAGE_EMPTY);
    }

    @Test
    public void undoCommand_addCommand_success() throws CommandException {
        Person person = new PersonBuilder().build();
        AddCommand addCommand = new AddCommand(person);
        addCommand.execute(model);

        // Push the add command to the undo stack
        model.pushMutableCommandHistory(addCommand);

        UndoCommand undoCommand = new UndoCommand();
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        String expectedMessage = String.format(
                UndoCommand.MESSAGE_SUCCESS,
                String.format(AddCommand.MESSAGE_UNDO_SUCCESS, Messages.format(person)));
        assertCommandSuccess(undoCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void undoCommand_failure_noCommandToUndo() {
        UndoCommand undoCommand = new UndoCommand();
        // The model should remain unchanged and the correct message should be shown
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertCommandFailure(undoCommand, model, UndoCommand.MESSAGE_EMPTY);
        assertEquals(expectedModel.getAddressBook(), model.getAddressBook());
    }

    @Test
    public void undoCommand_failed_nonMutableCommand() throws CommandException {

        Command command = new Command() {

            @Override
            public CommandResult execute(Model model) throws CommandException {
                return new CommandResult("Non-mutable command executed");
            }

            @Override
            public boolean isMutable() {
                return false;
            }
        };

        UnrestrictedModelManager unrestrictedModel =
                new UnrestrictedModelManager(getTypicalAddressBook(), new UserPrefs());;

        unrestrictedModel.pushMutableCommandHistory(command);
        UndoCommand undoCommand = new UndoCommand();
        // Command fails as the last command is non-mutable
        assertCommandFailure(undoCommand, unrestrictedModel, UndoCommand.MESSAGE_NON_MUTABLE);
    }

    private class UnrestrictedModelManager extends ModelManager {

        // Stacks for undo and redo functionality
        private final Stack<Command> undoStack = new Stack<>();

        public UnrestrictedModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs) {
            super(addressBook, userPrefs);
        }

        @Override
        public void pushMutableCommandHistory(Command command) {
            // Remove checks to allow pushing non-mutable commands
            undoStack.push(command);
        }

        @Override
        public Optional<Command> popLastMutableCommand() {
            if (undoStack.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(undoStack.pop());
        }
    }
}
