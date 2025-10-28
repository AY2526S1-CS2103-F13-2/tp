package seedu.address.model.tag;

import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/** Making a tag folder feature */
public final class TagFolder implements Comparable<TagFolder> {
    private final String name;
    private final List<String> queryTags; // lower-cased tags this folder represents (1 or many)
    private final IntegerProperty count = new SimpleIntegerProperty();
    private final boolean userCreated;

    /** Single-tag folder. */
    public TagFolder(String name, int count) {
        this(name, count, List.of(name), false);
    }

    /** Composite or single-tag folder. queryTags should be lower-cased. */
    public TagFolder(String name, int count, List<String> queryTags) {
        this(name, count, queryTags, false);
    }

    /** Full ctor (used for user-created folders). */
    public TagFolder(String name, int count, List<String> queryTags, boolean userCreated) {
        this.name = name;
        this.count.set(count);
        this.queryTags = List.copyOf(queryTags);
        this.userCreated = userCreated;
    }

    // factory for composites
    public static TagFolder composite(String displayName, List<String> queryTags) {
        return new TagFolder(displayName, 0, queryTags, false);
    }

    /** factory for single-tag user folder */
    public static TagFolder userSingle(String displayName) {
        return new TagFolder(displayName, 0, List.of(displayName), true);
    }

    /** factory for composites created by the user */
    public static TagFolder userComposite(String displayName, List<String> queryTags) {
        return new TagFolder(displayName, 0, queryTags, /*userCreated*/ true);
    }

    public boolean isUserCreated() {
        return userCreated;
    }

    public String getName() {
        return name;
    }
    public List<String> getQueryTags() {
        return queryTags;
    }
    public int getCount() {
        return count.get();
    }
    public void setCount(int value) {
        count.set(value);
    }
    public IntegerProperty countProperty() {
        return count;
    }

    @Override public String toString() {
        return name + " (" + getCount() + ")";
    }

    @Override public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TagFolder)) {
            return false;
        }
        TagFolder o = (TagFolder) other;
        return this.name.equalsIgnoreCase(o.name);
    }

    @Override public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    /**
     * Compares this TagFolder with another TagFolder for sorting purposes.
     * The comparison is based on a case-insensitive alphabetical order of folder names.
     * This method is primarily used to determine the order of TagFolders in the UI.
     *
     * @param other The other TagFolder to compare against.
     * @return A negative integer, zero, or a positive integer as this TagFolder's name
     *         is less than, equal to, or greater than the other TagFolder's name, ignoring case.
     */
    @Override
    public int compareTo(TagFolder other) {
        // Case-insensitive alphabetical order
        return this.name.compareToIgnoreCase(other.name);
    }
}
