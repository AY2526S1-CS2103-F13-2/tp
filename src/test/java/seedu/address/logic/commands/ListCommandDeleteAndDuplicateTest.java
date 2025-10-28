package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.FeatureTag;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.TagFolder;

/** Covers ListCommand delete flow and duplicate-save guard. */
public class ListCommandDeleteAndDuplicateTest {

    /** Minimal stub we can program for the delete/duplicate scenarios. */
    private static class ProgrammableModelStub implements Model {
        private boolean hasFolder = false;
        private boolean removeReturn = false;
        private boolean addActiveCalled = false;
        private boolean addCompositeCalled = false;

        // --- hooks ListCommand touches ---
        @Override public boolean hasTagFolder(String name) {
            return hasFolder;
        }

        @Override public boolean removeTagFolderByName(String name) {
            return removeReturn;
        }

        @Override public void addActiveTagFoldersFromUser(List<String> tagNames) {
            addActiveCalled = true;
        }

        @Override public void addCompositeTagFolderFromUser(List<String> tagNames) {
            addCompositeCalled = true;
        }

        @Override public void updateFilteredPersonList(Predicate<Person> predicate) {
        }

        // --- the rest are harmless no-ops ---
        @Override public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        }
        @Override public ReadOnlyUserPrefs getUserPrefs() {
            return null;
        }
        @Override public GuiSettings getGuiSettings() {
            return null;
        }
        @Override public void setGuiSettings(GuiSettings guiSettings) {
        }
        @Override public Path getAddressBookFilePath() {
            return null;
        }
        @Override public void setAddressBookFilePath(Path addressBookFilePath) {
        }
        @Override public void setAddressBook(ReadOnlyAddressBook addressBook) {
        }
        @Override public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
        @Override public boolean hasPerson(Person person) {
            return false;
        }
        @Override public void deletePerson(Person target) {
        }
        @Override public void addPerson(Person person) {
        }
        @Override public void insertPerson(Index index, Person person) {
        }
        @Override public void setPerson(Person target, Person editedPerson) {
        }
        @Override public int getSize() {
            return 0;
        }
        @Override public ObservableList<Person> getFilteredPersonList() {
            return FXCollections.observableArrayList();
        }
        @Override public ObservableList<Person> getPersonListCopy() {
            return FXCollections.observableArrayList();
        }
        @Override public boolean isBizFeature(FeatureTag feature) {
            return false;
        }
        @Override public void addBizTags(FeatureTag feature, Set<Tag> tags) {
        }
        @Override public void removeBizFeature(FeatureTag feature) {
        }
        @Override public java.util.HashMap<FeatureTag, Set<Tag>> getBizTags() {
            return new java.util.HashMap<>();
        }
        @Override public void pushMutableCommandHistory(Command command) {
        }
        @Override public Optional<Command> popLastMutableCommand() {
            return Optional.empty();
        }
        @Override public void pushUndoCommandHistory(Command command) {
        }
        @Override public Optional<Command> popLastUndoCommand() {
            return Optional.empty();
        }
        @Override public ObservableList<TagFolder> getActiveTagFolders() {
            return FXCollections.observableArrayList();
        }
        @Override public void addActiveTagFolders(List<String> tagNames) {
        }
        @Override public void addCompositeTagFolder(List<String> tagNames) {
        }
        @Override public void refreshActiveTagFolderCounts() {
        }
    }

    @Test
    void delete_missingFolder_throws() {
        ProgrammableModelStub model = new ProgrammableModelStub();
        model.hasFolder = false; // simulate no such folder

        ListCommand cmd = new ListCommand(p -> true, List.of("friends"), false, true);

        assertThrows(CommandException.class, () -> cmd.execute(model));
    }

    @Test
    void delete_hasFolderButRemoveReturnsFalse_throws() {
        ProgrammableModelStub model = new ProgrammableModelStub();
        model.hasFolder = true;
        model.removeReturn = false;

        ListCommand cmd = new ListCommand(p -> true, List.of("friends"), false, true);

        assertThrows(CommandException.class, () -> cmd.execute(model));
    }

    @Test
    void delete_success_returnsDeletedMessage() throws Exception {
        ProgrammableModelStub model = new ProgrammableModelStub();
        model.hasFolder = true;
        model.removeReturn = true;

        ListCommand cmd = new ListCommand(p -> true, List.of("friends"), false, true);

        CommandResult r = cmd.execute(model);
        assertTrue(r.getFeedbackToUser().toLowerCase().contains("deleted folder"));
    }

    @Test
    void save_duplicateFolder_throwsAndDoesNotCallAdders() {
        ProgrammableModelStub model = new ProgrammableModelStub();
        model.hasFolder = true;

        ListCommand cmd = new ListCommand(p -> true, List.of("friends"), true, false);

        assertThrows(CommandException.class, () -> cmd.execute(model));
        // Make sure the adders were NOT called
        org.junit.jupiter.api.Assertions.assertFalse(model.addActiveCalled || model.addCompositeCalled);
    }
}
