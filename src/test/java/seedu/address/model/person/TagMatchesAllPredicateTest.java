package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;


import seedu.address.logic.commands.ListCommand;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class TagMatchesAllPredicateTest {

    @Test
    public void test_personHasAnyRequiredTag_returnsTrue() {
        // Person with friends + colleagues
        Person person = new PersonBuilder()
                .withName("Bernice Yu")
                .withTags("friends", "colleagues")
                .build();

        TagMatchesAllPredicate predicate =
                new TagMatchesAllPredicate(Set.of(new Tag("friends"), new Tag("colleagues")));

        assertTrue(predicate.test(person));
    }

    @Test
    public void test_personHasOneOfRequiredTags_returnsTrue() {
        // Person with only friends still matches (ANY semantics)
        Person person = new PersonBuilder()
                .withName("James Ho")
                .withTags("friends")
                .build();

        TagMatchesAllPredicate predicate =
                new TagMatchesAllPredicate(Set.of(new Tag("friends"), new Tag("colleagues")));

        assertTrue(predicate.test(person));
    }

    @Test
    public void equals() {
        TagMatchesAllPredicate a = new TagMatchesAllPredicate(Set.of(new Tag("friends")));
        TagMatchesAllPredicate b = new TagMatchesAllPredicate(Set.of(new Tag("friends")));
        TagMatchesAllPredicate c = new TagMatchesAllPredicate(Set.of(new Tag("colleagues")));

        assertEquals(a, b); // same values
        assertEquals(a, a); // same object
        assertNotEquals(null, a); // null
        assertNotEquals("x", a); // different type
        assertNotEquals(a, c); // different values
    }
}
