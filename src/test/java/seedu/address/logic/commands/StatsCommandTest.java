package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains unit tests for StatsCommand.
 */
public class StatsCommandTest {
    @Test
    public void execute_stats_success() {
        StatsCommand statsCommand = new StatsCommand();
        assertEquals(new CommandResult("Number of Customers: 0"),
                statsCommand.execute(new ModelStub()));

    }

    /**
     * A Stub for Model class for unit testing of StatsCommand.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {

        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            return null;
        }

        @Override
        public GuiSettings getGuiSettings() {
            return null;
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {

        }

        @Override
        public Path getAddressBookFilePath() {
            return null;
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {

        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook addressBook) {

        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return null;
        }

        @Override
        public boolean hasPerson(Person person) {
            return false;
        }

        @Override
        public void deletePerson(Person target) {

        }

        @Override
        public void addPerson(Person person) {

        }

        @Override
        public void insertPerson(Index index, Person person) {
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {

        }

        @Override
        public int getSize() {
            return 0;
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return null;
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {

        }

        @Override
        public void pushMutableCommandHistory(Command command) {
        }

        @Override
        public Optional<Command> popLastMutableCommand() {
            return Optional.empty();
        }

        @Override
        public void pushUndoCommandHistory(Command command) {
        }

        @Override
        public Optional<Command> popLastUndoCommand() {
            return Optional.empty();
        }
    }
}

