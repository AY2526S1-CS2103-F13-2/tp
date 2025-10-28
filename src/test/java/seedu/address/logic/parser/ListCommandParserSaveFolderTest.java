package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.FeatureTag;
import seedu.address.model.tag.Tag;

/**
 * Tests the trailing 's' behaviour and tag parsing for ListCommandParser.
 */
public class ListCommandParserSaveFolderTest {

    /** Minimal Model stub that captures sidebar interactions. */
    private static class CaptureModelStub implements Model {
        private Predicate<Person> lastPredicate;
        private boolean addActiveCalled;
        private boolean addCompositeCalled;
        private List<String> capturedTags = List.of();

        @Override public void addActiveTagFolders(List<String> tagNames) {
            addActiveCalled = true;
            capturedTags = List.copyOf(tagNames);
        }
        @Override public void addCompositeTagFolder(List<String> tagNames) {
            addCompositeCalled = true;
            capturedTags = List.copyOf(tagNames);
        }
        @Override public void updateFilteredPersonList(Predicate<Person> predicate) {
            lastPredicate = predicate;
        }

        @Override
        public boolean isBizFeature(FeatureTag feature) {
            return false;
        }

        @Override
        public void addBizTags(FeatureTag feature, Set<Tag> tags) {

        }

        @Override
        public void removeBizFeature(FeatureTag feature) {

        }

        @Override
        public HashMap<FeatureTag, Set<Tag>> getBizTags() {
            return null;
        }

        // --- harmless stubs ---
        @Override public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
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
        @Override public javafx.collections.ObservableList<seedu.address.model.tag.TagFolder> getActiveTagFolders() {
            return FXCollections.observableArrayList();
        }

        @Override public void refreshActiveTagFolderCounts() {
        }

        @Override
        public boolean hasTagFolder(String name) {
            return false;
        }

        @Override
        public void addActiveTagFoldersFromUser(List<String> tagNames) {

        }

        @Override
        public void addCompositeTagFolderFromUser(List<String> tagNames) {

        }

        @Override
        public boolean removeTagFolderByName(String name) {
            return false;
        }
    }

    private CommandResult run(ListCommandParser parser, String raw, CaptureModelStub model) throws Exception {
        ListCommand cmd = parser.parse(raw);
        return cmd.execute(model);
    }

    @Test
    void parseNoArgsReturnsListAllAndNoSidebarCalls() throws Exception {
        ListCommandParser parser = new ListCommandParser();
        CaptureModelStub model = new CaptureModelStub();

        CommandResult r = run(parser, "", model);

        assertEquals(ListCommand.MESSAGE_SUCCESS, r.getFeedbackToUser());
        assertTrue(model.lastPredicate != null);
        assertFalse(model.addActiveCalled);
        assertFalse(model.addCompositeCalled);
    }

    @Test
    void parseSingleTagNoSaveDoesNotTouchSidebar() throws Exception {
        ListCommandParser parser = new ListCommandParser();
        CaptureModelStub model = new CaptureModelStub();

        run(parser, " t/friends", model);

        assertFalse(model.addActiveCalled);
        assertFalse(model.addCompositeCalled);
        assertTrue(model.lastPredicate != null);
    }

    @Test
    void parseMultipleTagsNoSaveDoesNotTouchSidebar() throws Exception {
        ListCommandParser parser = new ListCommandParser();
        CaptureModelStub model = new CaptureModelStub();

        run(parser, " t/friends t/colleagues", model);

        assertFalse(model.addActiveCalled);
        assertFalse(model.addCompositeCalled);
    }

    @Test
    void parseOnlySNoTagsBehavesAsListAll() throws Exception {
        ListCommandParser parser = new ListCommandParser();
        CaptureModelStub model = new CaptureModelStub();

        CommandResult r = run(parser, "   s   ", model);

        assertEquals(ListCommand.MESSAGE_SUCCESS, r.getFeedbackToUser());
        assertFalse(model.addActiveCalled);
        assertFalse(model.addCompositeCalled);
    }

    @Test
    void parseWithTagsSaveOrDeleteOkay() throws Exception {
        ListCommandParser parser = new ListCommandParser();

        // save path
        ListCommand saveCmd = parser.parse(" t/friends s/");
        assertTrue(saveCmd != null); // parsing succeeded

        // delete path
        ListCommand delCmd = parser.parse(" t/friends d/");
        assertTrue(delCmd != null); // parsing succeeded
    }
}
