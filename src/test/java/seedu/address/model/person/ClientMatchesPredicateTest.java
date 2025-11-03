package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

/**
 * Combined test coverage for ClientMatchesPredicate.
 *
 * Includes adapted checks from the original NameContainsKeywordsPredicateTest
 * plus new attribute-based matching (phone, email) and partial/case-insensitive logic.
 */
public class ClientMatchesPredicateTest {

    // ===============================================================
    // equals()
    // ===============================================================
    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        ClientMatchesPredicate firstPredicate = new ClientMatchesPredicate(firstPredicateKeywordList);
        ClientMatchesPredicate secondPredicate = new ClientMatchesPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        ClientMatchesPredicate firstPredicateCopy = new ClientMatchesPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different keyword list -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    // ===============================================================
    // test() positive cases
    // ===============================================================

    @Test
    @DisplayName("Name partial match returns true")
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword (partial)
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Collections.singletonList("Ali"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new ClientMatchesPredicate(Arrays.asList("Alice", "Bob"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        predicate = new ClientMatchesPredicate(Arrays.asList("Bob", "Carol"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        predicate = new ClientMatchesPredicate(Arrays.asList("aLIce", "bOB"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    @DisplayName("Phone number substring match returns true")
    public void test_phoneContainsKeywords_returnsTrue() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Collections.singletonList("1234"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withPhone("99123456").withEmail("alice@example.com").build()));
    }

    @Test
    @DisplayName("Email substring match returns true")
    public void test_emailContainsKeywords_returnsTrue() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Collections.singletonList("gmail"));
        assertTrue(predicate.test(new PersonBuilder().withName("Bob")
                .withPhone("81234567").withEmail("bob@gmail.com").build()));
    }

    @Test
    @DisplayName("Case-insensitive match returns true")
    public void test_caseInsensitive_returnsTrue() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Arrays.asList("JAMIE", "GMAIL.COM"));
        assertTrue(predicate.test(new PersonBuilder().withName("Jamie Tan")
                .withPhone("91234567").withEmail("jamie@gmail.com").build()));
    }

    @Test
    @DisplayName("Multiple keywords use OR semantics")
    public void test_multipleKeywords_orSemantics() {
        // one keyword unmatched, one keyword matched by phone
        ClientMatchesPredicate pred = new ClientMatchesPredicate(Arrays.asList("zzz", "9123"));
        Person person = new PersonBuilder().withName("X Y")
                .withPhone("99123123") // valid 8 digits containing 9123
                .withEmail("x@y.z.com") // valid domain for Email
                .build();
        assertTrue(pred.test(person));
    }

    @Test
    @DisplayName("Multiple hits in same field still true")
    public void test_multipleHitsSameField_returnsTrue() {
        ClientMatchesPredicate pred = new ClientMatchesPredicate(List.of("li"));
        Person person = new PersonBuilder()
                .withName("David Li")
                .withPhone("91031282")
                .withEmail("lidavid@example.com")
                .build();
        assertTrue(pred.test(person));
    }

    // ===============================================================
    // test() negative cases
    // ===============================================================

    @Test
    @DisplayName("Empty keywords list returns false")
    public void test_emptyKeywords_returnsFalse() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Collections.emptyList());
        Person person = new PersonBuilder().withName("Alice").build();
        assertFalse(predicate.test(person));
    }

    @Test
    @DisplayName("Only blank keywords are ignored, returns false")
    public void test_onlyBlankKeywords_returnsFalse() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(List.of("   ", "\t"));
        Person person = new PersonBuilder().withName("Alice").build();
        assertFalse(predicate.test(person));
    }

    @Test
    @DisplayName("No matching fields returns false")
    public void test_noMatchingFields_returnsFalse() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(List.of("nomatch"));
        Person person = new PersonBuilder()
                .withName("David Li")
                .withPhone("91031282")
                .withEmail("lidavid@example.com")
                .build();
        assertFalse(predicate.test(person));
    }

    // ===============================================================
    // toString(), hashCode()
    // ===============================================================

    @Test
    @DisplayName("toString() reflects keywords")
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(keywords);
        String expected = ClientMatchesPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }

    @Test
    @DisplayName("hashCode contract: equal objects have equal hash")
    public void hashCode_contract() {
        ClientMatchesPredicate a = new ClientMatchesPredicate(List.of("x", "y"));
        ClientMatchesPredicate b = new ClientMatchesPredicate(List.of("x", "y"));
        ClientMatchesPredicate c = new ClientMatchesPredicate(List.of("x", "z"));

        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a.hashCode(), c.hashCode());
    }

    // ===============================================================
    // Additional coverage tests for logging and edge cases
    // ===============================================================

    @Test
    @DisplayName("Test with single character keyword matches")
    public void test_singleCharacterKeyword_returnsTrue() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Collections.singletonList("a"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));
    }

    @Test
    @DisplayName("Test with keyword matching only email domain")
    public void test_emailDomainMatch_returnsTrue() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Collections.singletonList("example.com"));
        assertTrue(predicate.test(new PersonBuilder().withName("Bob")
                .withPhone("91234567").withEmail("bob@example.com").build()));
    }

    @Test
    @DisplayName("Test with keyword matching phone area code")
    public void test_phoneAreaCodeMatch_returnsTrue() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Collections.singletonList("91"));
        assertTrue(predicate.test(new PersonBuilder().withName("Charlie")
                .withPhone("91234567").withEmail("charlie@example.com").build()));
    }

    @Test
    @DisplayName("Test with mixed blank and valid keywords")
    public void test_mixedBlankAndValidKeywords_returnsTrue() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Arrays.asList("   ", "Alice", "\t"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));
    }

    @Test
    @DisplayName("Test no match across all fields")
    public void test_noMatchAcrossAllFields_returnsFalse() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Collections.singletonList("xyz123"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob")
                .withPhone("91234567").withEmail("alice@example.com").build()));
    }

    @Test
    @DisplayName("Test with special characters in keyword")
    public void test_specialCharactersInKeyword_worksCorrectly() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Collections.singletonList("@example"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withPhone("91234567").withEmail("alice@example.com").build()));
    }

    @Test
    @DisplayName("Test with numeric keyword matching phone")
    public void test_numericKeywordMatchingPhone_returnsTrue() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Collections.singletonList("123"));
        assertTrue(predicate.test(new PersonBuilder().withName("Agent Smith")
                .withPhone("91234567").withEmail("agent@example.com").build()));
    }

    @Test
    @DisplayName("Test with very long keyword")
    public void test_veryLongKeyword_worksCorrectly() {
        String longKeyword = "verylongkeywordthatdoesnotmatchanything";
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Collections.singletonList(longKeyword));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));
    }

    @Test
    @DisplayName("Test multiple keywords with first matching")
    public void test_multipleKeywordsFirstMatches_returnsTrue() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Arrays.asList("Alice", "xyz", "abc"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));
    }

    @Test
    @DisplayName("Test multiple keywords with last matching")
    public void test_multipleKeywordsLastMatches_returnsTrue() {
        ClientMatchesPredicate predicate = new ClientMatchesPredicate(Arrays.asList("xyz", "abc", "Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));
    }

    @Test
    @DisplayName("Test case variations")
    public void test_caseVariations_allMatch() {
        Person person = new PersonBuilder().withName("Alice").build();
        assertTrue(new ClientMatchesPredicate(Collections.singletonList("alice")).test(person));
        assertTrue(new ClientMatchesPredicate(Collections.singletonList("ALICE")).test(person));
        assertTrue(new ClientMatchesPredicate(Collections.singletonList("AlIcE")).test(person));
    }
}
