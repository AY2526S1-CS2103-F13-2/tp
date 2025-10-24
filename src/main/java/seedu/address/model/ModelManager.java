package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.Command;
import seedu.address.model.person.Person;
import seedu.address.model.tag.FeatureTag;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.TagFolder;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final HashMap<FeatureTag, Set<Tag>> bizTags;

    // Stacks for undo and redo functionality
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    // Sidebar state
    private final ObservableList<TagFolder> activeFolders =
            FXCollections.observableArrayList();
    private final LinkedHashMap<String, Integer> folderIndex =
            new LinkedHashMap<>();

    // --- constructors must come before any methods ---
    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        this.bizTags = new HashMap<>();

        requireAllNonNull(addressBook, userPrefs);
        // call this at the end of ModelManager constructor after addressBook is set:
        bootstrapAllTags();
    }

    // --- methods below constructors ---
    private static String folderKey(java.util.List<String> tags) {
        return tags.stream()
                .map(String::toLowerCase)
                .sorted()
                .collect(java.util.stream.Collectors.joining("|"));
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
        activeFolders.clear();
        folderIndex.clear();
        bootstrapAllTags();
        refreshActiveTagFolderCounts();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
        refreshActiveTagFolderCounts();
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        ensureFoldersExistForTags(person.getTags());
        refreshActiveTagFolderCounts();
    }

    @Override
    public void insertPerson(Index index, Person person) {
        requireAllNonNull(index, person);
        addressBook.insertPerson(index.getZeroBased(), person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        ensureFoldersExistForTags(person.getTags());
        refreshActiveTagFolderCounts();
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);
        addressBook.setPerson(target, editedPerson);
        ensureFoldersExistForTags(editedPerson.getTags());
        refreshActiveTagFolderCounts();
    }

    @Override
    public int getSize() {
        return addressBook.getSize();
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public ObservableList<Person> getPersonListCopy() {
        return addressBook.getPersonList();
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public boolean isBizFeature(FeatureTag field) {
        return bizTags.containsKey(field);
    }

    @Override
    public void addBizTags(FeatureTag field, Set<Tag> tags) {
        this.bizTags.put(field, tags);
    }

    @Override
    public void removeBizFeature(FeatureTag field) {
        this.bizTags.remove(field);
    }

    @Override
    public HashMap<FeatureTag, Set<Tag>> getBizTags() {
        HashMap<FeatureTag, Set<Tag>> deepCopy = new HashMap<>();
        for (Map.Entry<FeatureTag, Set<Tag>> entry : bizTags.entrySet()) {
            deepCopy.put(entry.getKey(), new LinkedHashSet<>(entry.getValue()));
        }
        return deepCopy;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

    //=========== Undo/Redo ==================================================================================
    @Override
    public void pushMutableCommandHistory(Command command) {
        if (command.isMutable()) {
            undoStack.push(command);
            redoStack.clear(); // Clear redo stack on new command
        }
    }

    @Override
    public Optional<Command> popLastMutableCommand() {
        if (undoStack.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(undoStack.pop());
    }

    @Override
    public void pushUndoCommandHistory(Command command) {
        if (command.isMutable()) {
            redoStack.push(command);
        }
    }

    @Override
    public Optional<Command> popLastUndoCommand() {
        if (redoStack.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(redoStack.pop());
    }

    // =========== Tag Folders (sidebar) ==================================================

    @Override
    public javafx.collections.ObservableList<TagFolder> getActiveTagFolders() {
        return javafx.collections.FXCollections.unmodifiableObservableList(activeFolders);
    }

    @Override
    public void addActiveTagFolders(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        for (String raw : tagNames) {
            String key = raw.toLowerCase();
            if (!folderIndex.containsKey(key)) {
                activeFolders.add(new TagFolder(raw, 1)); // single-tag folder
            }
        }
        refreshActiveTagFolderCounts();

        // Keep folders sorted
        sortFolders();
    }

    @Override
    public void refreshActiveTagFolderCounts() {
        var people = getAddressBook().getPersonList();

        for (TagFolder tagFolder : activeFolders) {
            int count = (int) people.stream()
                    .filter(p -> tagFolder.getQueryTags().stream()
                            .allMatch(qt -> p.getTags().stream()
                                    .anyMatch(t -> t.tagName.equalsIgnoreCase(qt))))
                    .count();
            tagFolder.setCount(count);
        }

        // Remove folders with zero count
        activeFolders.removeIf(folder -> folder.getCount() == 0);
    }

    @Override
    public void addCompositeTagFolder(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        // Normalise: trim, lower-case, distinct, sorted (so "b a" == "a b")
        List<String> norm = tagNames.stream()
                .map(s -> s.trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted()
                .toList();

        if (norm.isEmpty()) {
            return;
        }

        String key = String.join("&", norm);
        if (folderIndex.containsKey(key)) {
            // already saved; just make sure counts are fresh
            refreshActiveTagFolderCounts();
            return;
        }
        // Display name like "friends & colleagues"
        String display = String.join(" & ", norm);

        // TagFolder must carry the query tags of a composite
        TagFolder folder = TagFolder.composite(display, norm); // see helper below
        activeFolders.add(folder);
        folderIndex.put(key, activeFolders.size() - 1);

        refreshActiveTagFolderCounts();
    }

    private void bootstrapAllTags() {
        java.util.Set<String> all = new java.util.HashSet<>();
        getAddressBook().getPersonList().forEach(p ->
                p.getTags().forEach(t -> all.add(t.tagName)));
        addActiveTagFolders(new java.util.ArrayList<>(all));
    }

    // Ensures every tag has a corresponding TagFolder.
    private void ensureFoldersExistForTags(java.util.Collection<? extends seedu.address.model.tag.Tag> tags) {
        if (tags == null) {
            return;
        }
        for (seedu.address.model.tag.Tag t : tags) {
            String key = t.tagName.toLowerCase();
            if (!folderIndex.containsKey(key)) {
                activeFolders.add(new TagFolder(t.tagName, 0));
                folderIndex.put(key, activeFolders.size() - 1);
            }
        }
        sortFolders();
    }

    private void sortFolders() {
        FXCollections.sort(activeFolders);
        folderIndex.clear();
        for (int i = 0; i < activeFolders.size(); i++) {
            folderIndex.put(activeFolders.get(i).getName().toLowerCase(), i);
        }
    }
}
