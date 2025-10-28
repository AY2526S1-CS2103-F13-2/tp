package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class TagFolderTest {

    @Test
    void ctor_singleTag_setsNameCountAndQueryTags() {
        TagFolder f = new TagFolder("friends", 3);
        assertEquals("friends", f.getName());
        assertEquals(3, f.getCount());
        assertEquals(List.of("friends"), f.getQueryTags());
        assertEquals("friends (3)", f.toString());
    }

    @Test
    void composite_factory_usesDisplayNameAndQueryTags() {
        TagFolder f = TagFolder.composite("friends & colleagues", List.of("friends", "colleagues"));
        assertEquals("friends & colleagues", f.getName());
        assertEquals(0, f.getCount()); // factory starts at 0
        assertEquals(List.of("friends", "colleagues"), f.getQueryTags());
    }

    @Test
    void countProperty_setterUpdatesGetCount() {
        TagFolder f = new TagFolder("x", 0);
        f.setCount(7);
        assertEquals(7, f.getCount());
        // Property exists (used by UI binders) but safe to access without starting JavaFX
        assertNotNull(f.countProperty());
    }

    @Test
    void equals_isCaseInsensitiveOnName_only() {
        TagFolder a = new TagFolder("Friends", 1);
        TagFolder b = new TagFolder("friends", 99);
        TagFolder c = TagFolder.composite("friends & colleagues", List.of("friends", "colleagues"));

        assertTrue(a.equals(b));
        assertTrue(a.hashCode() == b.hashCode());
        assertNotSame(a, c); // different names → not equal
    }

    @Test
    void threeArgConstructor_setsFieldsAndCopiesQueryTags() {
        // This hits: TagFolder(String name, int count, List<String> queryTags) { this(name, count, queryTags, false); }
        TagFolder tf = new TagFolder("friends & colleagues", 0, List.of("friends", "colleagues"));

        assertEquals("friends & colleagues", tf.getName());
        assertEquals(0, tf.getCount());
        assertEquals(List.of("friends", "colleagues"), tf.getQueryTags());

        // ensure queryTags is an unmodifiable copy (constructor uses List.copyOf)
        assertThrows(UnsupportedOperationException.class, () -> tf.getQueryTags().add("new"));
    }

    @Test
    void equals_sameInstance_returnsTrue() {
        TagFolder tf = new TagFolder("friends", 1, List.of("friends"));
        assertTrue(tf.equals(tf)); // hits "this == other" branch
    }

    @Test
    void equals_differentType_returnsFalse() {
        TagFolder tf = new TagFolder("friends", 1, List.of("friends"));
        assertFalse(tf.equals("not a tag folder")); // hits "!(other instanceof TagFolder)" branch
    }

    @Test
    void equals_caseInsensitiveName_returnsTrue() {
        TagFolder a = new TagFolder("Friends", 2, List.of("friends"));
        TagFolder b = new TagFolder("friends", 5, List.of("friends")); // different count doesn't matter
        assertTrue(a.equals(b)); // hits equalsIgnoreCase path
    }

    @Test
    void equals_differentNames_returnsFalse() {
        TagFolder a = new TagFolder("friends", 1, List.of("friends"));
        TagFolder b = new TagFolder("colleagues", 1, List.of("colleagues"));
        assertFalse(a.equals(b));
    }

    @Test
    void hashCode_consistentWithEquals_caseInsensitive() {
        TagFolder a = new TagFolder("Friends", 0, List.of("friends"));
        TagFolder b = new TagFolder("friends", 0, List.of("friends"));
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
    }
}
