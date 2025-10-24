package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
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
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.FeatureTag;
import seedu.address.model.tag.Tag;


/**
 * Focused tests for the "save folder" behaviour of ListCommand.
 */
public class ListCommandSaveFolderTest {

    /**
     * Minimal Model stub capturing sidebar interactions.
     */
    private static class CaptureModelStub implements Model {
        private Predicate<Person> lastPredicate;
        private boolean addActiveCalled = false;
        private boolean addCompositeCalled = false;
        private List<String> capturedTags = new ArrayList<>();

        // --- Sidebar API we care about ---
        @Override
        public void addActiveTagFolders(List<String> tagNames) {
            addActiveCalled = true;
            capturedTags = new ArrayList<>(tagNames);
        }

        @Override
        public void addCompositeTagFolder(List<String> tagNames) {
            addCompositeCalled = true;
            capturedTags = new ArrayList<>(tagNames);
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
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

        // --- The rest are harmless stubs / no-ops ---
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
    }

    @Test
    void execute_singleTagWithSave_callsAddActive() {
        CaptureModelStub model = new CaptureModelStub();
        Predicate<Person> any = p -> true;

        List<String> tags = List.of("friends"); // single
        new ListCommand(any, tags, /*saveFolder=*/true).execute(model);

        assertTrue(model.addActiveCalled);
        assertFalse(model.addCompositeCalled);
        assertEquals(List.of("friends"), model.capturedTags);
        assertEquals(any, model.lastPredicate); // filtering still applied
    }

    @Test
    void execute_multipleTagsWithSave_callsAddComposite() {
        CaptureModelStub model = new CaptureModelStub();
        Predicate<Person> any = p -> true;

        List<String> tags = List.of("friends", "colleagues");
        new ListCommand(any, tags, /*saveFolder=*/true).execute(model);

        assertTrue(model.addCompositeCalled);
        assertFalse(model.addActiveCalled);
        assertEquals(List.of("friends", "colleagues"), model.capturedTags);
        assertEquals(any, model.lastPredicate);
    }

    @Test
    void execute_withSaveButNoTags_doesNotTouchSidebar() {
        CaptureModelStub model = new CaptureModelStub();
        Predicate<Person> any = p -> true;

        // tag list is empty -> no sidebar calls
        new ListCommand(any, List.of(), /*saveFolder=*/true).execute(model);

        assertFalse(model.addActiveCalled);
        assertFalse(model.addCompositeCalled);
        assertEquals(List.of(), model.capturedTags);
        assertEquals(any, model.lastPredicate);
    }

    @Test
    void execute_withoutSave_doesNotTouchSidebar() {
        CaptureModelStub model = new CaptureModelStub();
        Predicate<Person> any = p -> true;

        // saveFolder=false -> filter only
        new ListCommand(any, List.of("friends"), /*saveFolder=*/false).execute(model);

        assertFalse(model.addActiveCalled);
        assertFalse(model.addCompositeCalled);
        assertEquals(List.of(), model.capturedTags);
        assertEquals(any, model.lastPredicate);
    }

    @Test
    void execute_listAllPath_setsShowAllPredicateAndNoSidebarCalls() {
        CaptureModelStub model = new CaptureModelStub();

        CommandResult result = new ListCommand().execute(model);

        assertEquals(ListCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        // filtering set to "show all" (predicate is not null)
        assertTrue(model.lastPredicate != null);
        // no sidebar call on "list" with no tags
        assertFalse(model.addActiveCalled);
        assertFalse(model.addCompositeCalled);
        assertTrue(model.capturedTags.isEmpty());
    }

    @Test
    void equals_checksPredicateOnly() {
        Predicate<Person> any = p -> true;
        Predicate<Person> none = p -> false;

        ListCommand a = new ListCommand(any, List.of("x"), true);
        ListCommand b = new ListCommand(any, List.of("y"), false); // different flags/tags but same predicate
        ListCommand c = new ListCommand(none, List.of("x"), true);
        ListCommand d = new ListCommand(); // null predicate

        assertTrue(a.equals(b)); // equal because predicates equal
        assertFalse(a.equals(c)); // different predicates
        assertFalse(a.equals(d)); // one has null predicate, the other doesn't
        assertTrue(d.equals(new ListCommand())); // both null predicate
        assertFalse(a.equals(null));
        assertFalse(a.equals("not-a-listcommand"));
    }
}
