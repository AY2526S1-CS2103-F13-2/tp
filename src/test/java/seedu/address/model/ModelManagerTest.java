package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_FIELD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_CATEGORY;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.ClientMatchesPredicate;
import seedu.address.model.tag.FeatureTag;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setAddressBookFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookFilePath(path);
        assertEquals(path, modelManager.getAddressBookFilePath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void manipulate_bizTags_successful() throws CommandException {
        modelManager.addPerson(ALICE);
        FeatureTag feature = new FeatureTag(VALID_FIELD);
        Tag category = new Tag(VALID_TAG_CATEGORY);
        Set<Tag> tags = new HashSet<>(List.of(category));
        modelManager.addBizTags(feature, tags);

        assertTrue(modelManager.isBizFeature(feature));
        assertEquals(tags, modelManager.getBizTags().get(feature));

        modelManager.removeBizFeature(feature);
        assertFalse(modelManager.isBizFeature(feature));
        assertNull(modelManager.getBizTags().get(feature));
    }

    @Test
    public void checkSize_emptyList_returnsTrue() {
        assertEquals(0, modelManager.getSize());
    }

    @Test
    public void checkSize_onePerson_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertEquals(1, modelManager.getSize());
    }

    @Test
    public void notMutableCommand_notPushedToStack() {
        // an not mutable command
        Command notMutableCommand = new Command() {
            @Override
            public CommandResult execute(Model model) {
                return null;
            }

            @Override
            public String man() {
                return "";
            }
        };

        // push it to the history stack
        modelManager.pushMutableCommandHistory(notMutableCommand);
        assertTrue(modelManager.popLastMutableCommand().isEmpty());

        // push it to the undo stack
        modelManager.pushUndoCommandHistory(notMutableCommand);
        assertTrue(modelManager.popLastUndoCommand().isEmpty());
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertEquals(modelManagerCopy, modelManager);

        // same object -> returns true
        assertEquals(modelManager, modelManager);

        // null -> returns false
        assertNotEquals(null, modelManager);

        // different addressBook -> returns false
        assertNotEquals(new ModelManager(differentAddressBook, userPrefs), modelManager);

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new ClientMatchesPredicate(Arrays.asList(keywords)));
        assertNotEquals(new ModelManager(addressBook, userPrefs), modelManager);

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertNotEquals(new ModelManager(addressBook, differentUserPrefs), modelManager);
    }

    @Test
    void folderKey_sortsAndLowersCorrectly() throws Exception {
        var list = List.of("B", "a", "C");
        Method m = ModelManager.class.getDeclaredMethod("folderKey", List.class);
        m.setAccessible(true);
        String key = (String) m.invoke(null, list);
        assertEquals("a|b|c", key);
    }

    @Test
    void addActiveTagFolders_handlesNullEmptyAndAdds() {

        modelManager.addActiveTagFolders(null);
        modelManager.addActiveTagFolders(Collections.emptyList());
        assertTrue(modelManager.getActiveTagFolders().isEmpty());

        // need to have those tags first
        modelManager.addPerson(new PersonBuilder().withTags("friends", "colleagues").build());

        modelManager.addActiveTagFolders(List.of("friends", "colleagues"));
        assertEquals(2, modelManager.getActiveTagFolders().size());
    }

    @Test
    void addCompositeTagFolder_coversAllBranches() {

        // null → return
        modelManager.addCompositeTagFolder(null);
        // empty → return
        modelManager.addCompositeTagFolder(Collections.emptyList());

        modelManager.addPerson(new PersonBuilder().withTags("friend").build());

        // valid add
        // first add friends
        modelManager.addCompositeTagFolder(List.of("Friend", " Colleagues ", "friend"));
        assertEquals(1, modelManager.getActiveTagFolders().size());

        // duplicate key path triggers refresh (no add)
        modelManager.addCompositeTagFolder(List.of("colleagues", "friends"));
        assertEquals(1, modelManager.getActiveTagFolders().size());
    }

    @Test
    void ensureFoldersExistForTags_handlesNullAndAddsNewFolders() throws Exception {
        Method method = ModelManager.class.getDeclaredMethod(
                "ensureFoldersExistForTags", Collection.class);
        method.setAccessible(true);

        // null → return
        method.invoke(modelManager, (Object) null);
        assertTrue(modelManager.getActiveTagFolders().isEmpty());

        // add new tags
        var tags = List.of(new Tag("alpha"), new Tag("beta"));
        method.invoke(modelManager, tags);
        assertEquals(2, modelManager.getActiveTagFolders().size());
    }

    @Test
    void bootstrapAllTags_populatesFoldersFromPeople() throws Exception {
        Method bootstrap = ModelManager.class.getDeclaredMethod("bootstrapAllTags");
        bootstrap.setAccessible(true);
        bootstrap.invoke(modelManager);
        assertNotNull(modelManager.getActiveTagFolders());
    }

    @Test
    void equals_returnsFalse_whenOtherIsNotModelManager() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());
        assertFalse(mm.equals("not-a-model-manager"));
    }


}
