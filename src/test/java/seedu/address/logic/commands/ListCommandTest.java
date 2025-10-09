package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.TagMatchesAllPredicate;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code ListCommand}.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, "Listed all persons", expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ListCommand(), model, "Listed all persons", expectedModel);
    }

    @Test
    public void execute_listBySingleTag_filtersCorrectly() {
        // Create a minimal model with persons having tags
        AddressBook ab = new AddressBook();
        var bernice = new PersonBuilder().withName("Bernice").withTags("friends", "colleagues").build();
        var james = new PersonBuilder().withName("James").withTags("friends").build();
        var roy = new PersonBuilder().withName("Roy").withTags("colleagues").build();
        ab.addPerson(bernice);
        ab.addPerson(james);
        ab.addPerson(roy);
        model = new ModelManager(ab, new UserPrefs());

        Set<Tag> required = new LinkedHashSet<>();
        required.add(new Tag("friends"));
        ListCommand cmd = new ListCommand(new TagMatchesAllPredicate(required));
        cmd.execute(model);

        // Bernice and James have "friends"
        assertEquals(2, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_listByMultipleTags_filtersIntersection() {
        AddressBook ab = new AddressBook();
        var bernice = new PersonBuilder().withName("Bernice").withTags("friends", "colleagues").build();
        var james = new PersonBuilder().withName("James").withTags("friends").build();
        var roy = new PersonBuilder().withName("Roy").withTags("colleagues").build();
        ab.addPerson(bernice);
        ab.addPerson(james);
        ab.addPerson(roy);
        model = new ModelManager(ab, new UserPrefs());

        Set<Tag> required = new LinkedHashSet<>();
        required.add(new Tag("friends"));
        required.add(new Tag("colleagues"));
        ListCommand cmd = new ListCommand(new TagMatchesAllPredicate(required));
        cmd.execute(model);

        // Only Bernice has both tags
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals("Bernice", model.getFilteredPersonList().get(0).getName().fullName);
    }
}
