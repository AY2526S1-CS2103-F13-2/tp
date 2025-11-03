package seedu.address.model.person;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a {@code Person}'s fields (name, phone, email)
 * contain ANY of the given keywords. Matching is case-insensitive and allows partial matches.
 *
 * Defensive behaviors:
 * 1) Ignores blank keywords.
 * 2) Null-safe field access.
 */
public final class ClientMatchesPredicate implements Predicate<Person> {
    private static final Logger logger = LogsCenter.getLogger(ClientMatchesPredicate.class);
    private final List<String> keywords;

    /**
     * @param keywords list of search keywords (non-null). Caller ensures tokens are trimmed/lowercased.
     * @throws NullPointerException if keywords is null
     */
    public ClientMatchesPredicate(List<String> keywords) {
        Objects.requireNonNull(keywords, "Keywords list cannot be null");
        this.keywords = keywords;
        logger.fine("Created ClientMatchesPredicate with " + keywords.size() + " keywords: " + keywords);
    }

    @Override
    public boolean test(Person person) {
        Objects.requireNonNull(person, "Person to test cannot be null");
        // Assertion: Person should have at least a name (business logic requirement)
        assert person.getName() != null : "Person should always have a name";

        if (keywords.isEmpty()) {
            logger.finer("No keywords provided, returning false for person: " + person.getName());
            return false;
        }

        final String name = safeLower(person.getName() == null ? null : person.getName().toString());
        final String phone = safeLower(person.getPhone() == null ? null : person.getPhone().toString());
        final String email = safeLower(person.getEmail() == null ? null : person.getEmail().toString());

        logger.finer("Testing person: " + person.getName() + " against keywords: " + keywords);

        for (String kw : keywords) {
            if (kw.isBlank()) {
                logger.finest("Skipping blank keyword during search");
                continue;
            }
            final String k = kw.toLowerCase();
            // Check each field and log which field matched
            if (contains(name, k)) {
                logger.fine("Keyword '" + k + "' matched name: " + name);
                return true;
            }
            if (contains(phone, k)) {
                logger.fine("Keyword '" + k + "' matched phone: " + phone);
                return true;
            }
            if (contains(email, k)) {
                logger.fine("Keyword '" + k + "' matched email: " + email);
                return true;
            }
        }
        logger.finer("No match found for person: " + person.getName());
        return false;
    }

    /**
     * Checks if a field contains the keyword.
     * Defensive: handles null fields and empty keywords.
     *
     * @param field the field to search in (can be null)
     * @param kw the keyword to search for (should not be empty)
     * @return true if field contains keyword, false otherwise
     */
    private static boolean contains(String field, String kw) {
        // Assertion: keyword should not be empty when this method is called
        assert kw != null && !kw.isEmpty() : "Keyword should not be null or empty";
        return field != null && field.contains(kw);
    }

    /**
     * Safely converts a string to lowercase.
     * Defensive: handles null strings gracefully.
     *
     * @param s the string to convert (can be null)
     * @return lowercase version of s, or null if s is null
     */
    private static String safeLower(String s) {
        return s == null ? null : s.toLowerCase();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ClientMatchesPredicate otherClientMatchesPredicate)) {
            return false;
        }

        return keywords.equals(otherClientMatchesPredicate.keywords);
    }

    @Override
    public int hashCode() {
        return keywords.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
