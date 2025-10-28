package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.person.Person;
import seedu.address.model.tag.FeatureTag;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

class ModelManagerSidebarTest {
    private Person p(String name, String... tagNames) {
        Set<Tag> tags = new LinkedHashSet<>();
        for (String t : tagNames) {
            tags.add(new Tag(t));
        }
        // note: if your PersonBuilder expects Set<Tag>, not String, use .withTags(tags)
        return new PersonBuilder().withName(name).withTags(String.valueOf(tags)).build();
    }

    private AddressBook ab(Person... people) {
        AddressBook ab = new AddressBook();
        for (Person p : people) {
            ab.addPerson(p);
        }
        return ab;
    }


    @Test
    void bizFeature_map_addRemoveAndCopy() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());
        FeatureTag feature = new FeatureTag("biz");
        Set<Tag> tags = Set.of(new Tag("t1"), new Tag("t2"));

        assertFalse(mm.isBizFeature(feature));
        mm.addBizTags(feature, tags);
        assertTrue(mm.isBizFeature(feature));

        // deep copy semantics
        Map<FeatureTag, Set<Tag>> copy = mm.getBizTags();
        assertNotSame(copy, mm.getBizTags());
        assertEquals(copy.get(feature), mm.getBizTags().get(feature));

        mm.removeBizFeature(feature);
        assertFalse(mm.isBizFeature(feature));
    }

    @Test
    void userPrefs_pathsAndGuiSettings_roundTrip() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());
        Path p = Paths.get("ab.json");
        mm.setAddressBookFilePath(p);
        assertEquals(p, mm.getAddressBookFilePath());

        GuiSettings gs = new GuiSettings(800, 600, 10, 20);
        mm.setGuiSettings(gs);
        assertEquals(gs, mm.getGuiSettings());
    }

    @Test
    void undoRedoStacks_onlyMutablePushedAndPopped() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());
        Command immutable = new Command() {
            @Override public CommandResult execute(Model model) {
                return null;
            }

            @Override
            public String man() {
                return "";
            }

            @Override public boolean isMutable() {
                return false;
            }
        };
        Command mutable = new Command() {
            @Override public CommandResult execute(Model model) {
                return null;
            }

            @Override
            public String man() {
                return "";
            }

            @Override public boolean isMutable() {
                return true;
            }
        };

        mm.pushMutableCommandHistory(immutable); // should be ignored
        mm.pushMutableCommandHistory(mutable); // pushed to undo
        assertTrue(mm.popLastMutableCommand().isPresent());
        assertTrue(mm.popLastMutableCommand().isEmpty()); // stack empty

        mm.pushUndoCommandHistory(mutable); // goes to redo
        assertTrue(mm.popLastUndoCommand().isPresent());
        assertTrue(mm.popLastUndoCommand().isEmpty());
    }
    @Test
    void hasTagFolder_null_returnsFalse() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());
        assertFalse(mm.hasTagFolder(null)); // null guard
    }

    @Test
    void hasTagFolder_foundAndNotFound() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());
        mm.addActiveTagFoldersFromUser(List.of("friends"));
        assertTrue(mm.hasTagFolder("friends"));
        assertTrue(mm.hasTagFolder("FRIENDS"));  // case-insensitive
        assertFalse(mm.hasTagFolder("colleagues"));
    }

    @Test
    void addActiveTagFolders_nullOrEmpty_noChange() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());
        mm.addActiveTagFolders(null);           // early return
        mm.addActiveTagFolders(List.of());      // early return
        assertEquals(0, mm.getActiveTagFolders().size());
    }

    @Test
    void addCompositeTagFolder_nullOrEmpty_earlyReturn() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());
        mm.addCompositeTagFolder(null);
        mm.addCompositeTagFolder(List.of());
        assertEquals(0, mm.getActiveTagFolders().size());
    }

    @Test
    void addCompositeTagFolderearlyReturn() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());
        mm.addCompositeTagFolder(List.of("   ", "\t", "")); // all blank → norm.isEmpty()
        assertEquals(0, mm.getActiveTagFolders().size());
    }

    @Test
    void addActiveTagFoldersFromUser_nullEmptyBlank() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());

        mm.addActiveTagFoldersFromUser(null);      // early return
        mm.addActiveTagFoldersFromUser(List.of()); // early return
        mm.addActiveTagFoldersFromUser(List.of("   ")); // blank skipped
        assertEquals(0, mm.getActiveTagFolders().size());

        mm.addActiveTagFoldersFromUser(List.of("Friends"));
        assertTrue(mm.hasTagFolder("friends"));
        int before = mm.getActiveTagFolders().size();

        mm.addActiveTagFoldersFromUser(List.of("friends")); // duplicate ignored
        assertEquals(before, mm.getActiveTagFolders().size());
    }

    @Test
    void removeTagFolderByNamenullAndNotFoundandFound() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());

        assertFalse(mm.removeTagFolderByName(null));     // null → false
        assertFalse(mm.removeTagFolderByName("missing"));// not found → false

        mm.addActiveTagFoldersFromUser(List.of("toRemove"));
        assertTrue(mm.hasTagFolder("toRemove"));

        assertTrue(mm.removeTagFolderByName("toRemove"));// found → true
        assertFalse(mm.hasTagFolder("toRemove"));
    }

    @Test
    void addCompositeTagFolder_nullOrEmpty_returnsEarly() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());

        mm.addCompositeTagFolder(null);      // covers: tagNames == null -> return
        mm.addCompositeTagFolder(List.of()); // covers: tagNames.isEmpty() -> return

        assertEquals(0, mm.getActiveTagFolders().size());
    }

    @Test
    void addCompositeTagFolder_normEmpty_returnsEarly() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());

        // all whitespace -> norm.isEmpty() -> return
        mm.addCompositeTagFolder(List.of("   ", "\t", ""));

        assertEquals(0, mm.getActiveTagFolders().size());
    }

    @Test
    void addCompositeTagFolderFromUser_nullOrEmpty_returnsEarly() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());

        mm.addCompositeTagFolderFromUser(null);      // covers: tagNames == null -> return
        mm.addCompositeTagFolderFromUser(List.of()); // covers: tagNames.isEmpty() -> return

        assertEquals(0, mm.getActiveTagFolders().size());
    }

    @Test
    void addCompositeTagFolderFromUser_normEmpty_returnsEarly() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());

        // all whitespace -> norm.isEmpty() -> return
        mm.addCompositeTagFolderFromUser(List.of("   ", "\n", "\t"));

        assertEquals(0, mm.getActiveTagFolders().size());
    }

    @Test
    void addCompositeTagFolderFromUser_duplicateDisplay_hitsHasAndReturns() {
        ModelManager mm = new ModelManager(new AddressBook(), new UserPrefs());

        // First add
        mm.addCompositeTagFolderFromUser(List.of("friends", "colleagues"));
        int before = mm.getActiveTagFolders().size();
        assertTrue(mm.hasTagFolder("colleagues & friends")); // <-- sorted display

        // Duplicate (different order) -> hits hasTagFolder(display) -> refresh + return
        mm.addCompositeTagFolderFromUser(List.of("colleagues", "friends"));

        assertEquals(before, mm.getActiveTagFolders().size());
    }

}
