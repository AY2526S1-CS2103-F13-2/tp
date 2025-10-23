package seedu.address.model;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.Command;
import seedu.address.model.person.Person;
import seedu.address.model.tag.FeatureTag;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.TagFolder;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Insert the given person at the given index.
     * {@code person} must not already exist in the address book.
     */

    void insertPerson(Index index, Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Returns number of people in {@code Model}
     */
    int getSize();

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /** Returns a copy of the filtered person list */
    ObservableList<Person> getPersonListCopy();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Returns True if Field is present inside the {@code bizTags} in Model.
     */
    boolean isBizFeature(FeatureTag feature);

    /**
     * Declares the {@code bizTags} inside Model.
     */
    void addBizTags(FeatureTag feature, Set<Tag> tags);

    /**
     * Undeclares the {@code Field} from the {@code bizTags} inside Model.
     * @param feature Field to undeclare
     */
    void removeBizFeature(FeatureTag feature);

    /**
     * Returns a copy of all the {@code bizTags} inside Model.
     */
    HashMap<FeatureTag, Set<Tag>> getBizTags();

    /**
     * Saves the mutable command history to support undo.
     */
    void pushMutableCommandHistory(Command command);

    /**
     * Saves the undo history to support redo.
     */
    void pushUndoCommandHistory(Command command);

    /**
     * Returns the last mutable command in the history.
     */
    Optional<Command> popLastMutableCommand();

    /**
     * Returns the last undo command in the history.
     */
    Optional<Command> popLastUndoCommand();

    ObservableList<TagFolder> getActiveTagFolders();

    void setActiveTagFolders(java.util.List<String> tagNames);

    /** Back-compat alias used by some tests. */
    default void addActiveTagFolders(List<String> tagNames) {
        setActiveTagFolders(tagNames);
    }

    void addCompositeTagFolder(List<String> tagNames);

    /** Recompute the badge counts for all active folders. */
    void refreshActiveTagFolderCounts();

}
