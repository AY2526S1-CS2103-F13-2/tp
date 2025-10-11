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
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class RedoCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void redoCommand_noCommandsToRedo_returnsEmptyMessage() {
        RedoCommand redoCommand = new RedoCommand();
        // No command has been undone, so redo should return MESSAGE_EMPTY
        assertCommandFailure(redoCommand, model, RedoCommand.MESSAGE_EMPTY);
    }

    @Test
    public void redoCommand_addCommand_success() throws CommandException {
        Person person = new PersonBuilder().build();
        AddCommand addCommand = new AddCommand(person);

        // Add the AddCommand to the undo-ed stack
        model.pushUndoCommandHistory(addCommand);

        RedoCommand redoCommand = new RedoCommand();
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.addPerson(person);
        String expectedMessage = String.format(
                RedoCommand.MESSAGE_SUCCESS,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(person)));
        assertCommandSuccess(redoCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void redoCommand_failure_noCommandToRedo() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        RedoCommand redoCommand = new RedoCommand();
        // The model should remain unchanged and the correct message should be shown
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertCommandFailure(redoCommand, model, RedoCommand.MESSAGE_EMPTY);
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
                new UnrestrictedModelManager(getTypicalAddressBook(), new UserPrefs());

        unrestrictedModel.pushUndoCommandHistory(command);
        RedoCommand redoCommand = new RedoCommand();
        // Command fails as the last command is non-mutable
        assertCommandFailure(redoCommand, unrestrictedModel, RedoCommand.MESSAGE_NON_MUTABLE);
    }

    private class UnrestrictedModelManager extends ModelManager {

        // Stacks for undo and redo functionality
        private final Stack<Command> redoStack = new Stack<>();

        public UnrestrictedModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
            super(addressBook, userPrefs);
        }

        @Override
        public void pushUndoCommandHistory(Command command) {
            // Remove checks to allow pushing non-mutable commands
            redoStack.push(command);
        }

        @Override
        public Optional<Command> popLastUndoCommand() {
            if (redoStack.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(redoStack.pop());
        }
    }
}
