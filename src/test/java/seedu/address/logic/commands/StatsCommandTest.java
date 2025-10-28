package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.FeatureTag;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.TagFolder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains unit tests for StatsCommand.
 */
public class StatsCommandTest {
    @Test
    public void execute_stats_success() {
        StatsCommand statsCommand = new StatsCommand();
        String barOutput = "==================================";
        String barTable = "------------------------------------------------";
        String actualResult = "Total Number of Customers in AddressBook: 10"
                + "\n\n" + barOutput + "\n\n"
                + "Gender   |  Number of people\n"
                + "Other    |  0\n"
                + "Female   |  0\n"
                + "Male     |  3\n\n"
                + "Total for Feature: 3\n"
                + "Average: 1.00\n"
                + "Max Tag: Male (3 people)\n"
                + "Min Tag: Other & Female (0 people)\n"
                + barTable + "\n\n\n"
                + "Plan   |  Number of people\n"
                + "A      |  3\n"
                + "B      |  0\n"
                + "C      |  0\n\n"
                + "Total for Feature: 3\n"
                + "Average: 1.00\n"
                + "Max Tag: A (3 people)\n"
                + "Min Tag: B & C (0 people)\n"
                + barTable + "\n\n";
        assertEquals(new CommandResult(actualResult),
                statsCommand.execute(new ModelStub()));

    }

    @Test
    public void execute_stats_mixedCounts() {
        StatsCommand cmd = new StatsCommand();
        Model model = new ModelStub() {
            @Override
            public ObservableList<Person> getPersonListCopy() {
                return FXCollections.observableArrayList(
                        new PersonBuilder().withTags("Male", "A").build(),
                        new PersonBuilder().withTags("Female", "A").build(),
                        new PersonBuilder().withTags("Male", "B").build()
                );
            }

            @Override
            public int getSize() {
                return 3;
            }
        };

        String result = cmd.execute(model).toString();
        assertTrue(result.contains("Male")); // max tag
        assertTrue(result.contains("Other")); // min tag
        assertTrue(result.contains("Average: 1.00"));
    }

    @Test
    public void execute_stats_allEqualCounts() {
        StatsCommand cmd = new StatsCommand();
        Model model = new ModelStub() {
            @Override
            public ObservableList<Person> getPersonListCopy() {
                return FXCollections.observableArrayList(
                        new PersonBuilder().withTags("Male").build(),
                        new PersonBuilder().withTags("Female").build(),
                        new PersonBuilder().withTags("Other").build()
                );
            }

            @Override
            public int getSize() {
                return 3;
            }
        };

        String result = cmd.execute(model).toString();
        // all tags count = 1, so maxTag = all, minTag = all
        assertTrue(result.contains("Max Tag: Other & Female & Male"));
        assertTrue(result.contains("Min Tag: Other & Female & Male"));
        assertTrue(result.contains("Average: 1.00"));
    }


    @Test
    public void execute_stats_emptyList() {
        StatsCommand statsCommand = new StatsCommand();
        Model emptyModel = new ModelStub() {
            @Override
            public ObservableList<Person> getPersonListCopy() {
                return FXCollections.observableArrayList();
            }

            @Override
            public int getSize() {
                return 0;
            }
        };
        CommandResult result = statsCommand.execute(emptyModel);
        System.out.print(result.toString());
        assertTrue(result.toString().contains("Total Number of Customers in AddressBook: 0"));
        assertTrue(result.toString().contains("Average: 0.00")); // mean < 0 branch
    }

    @Test
    public void man_returnsManualString() {
        StatsCommand cmd = new StatsCommand();
        String manual = cmd.man();
        assertTrue(manual.contains("stats"));
        assertTrue(manual.contains("SEE MORE"));
    }


    /**
     * A Stub for Model class for unit testing of StatsCommand.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {

        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            return null;
        }

        @Override
        public GuiSettings getGuiSettings() {
            return null;
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {

        }

        @Override
        public Path getAddressBookFilePath() {
            return null;
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {

        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook addressBook) {

        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return null;
        }

        @Override
        public boolean hasPerson(Person person) {
            return false;
        }

        @Override
        public void deletePerson(Person target) {

        }

        @Override
        public void addPerson(Person person) {

        }

        @Override
        public void insertPerson(Index index, Person person) {
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {

        }

        @Override
        public int getSize() {
            return 10;
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return new PersonListStub();
        }

        @Override
        public ObservableList<Person> getPersonListCopy() {
            Person dummy = new PersonBuilder().withTags("A", "Male").build();
            return FXCollections.observableArrayList(dummy, dummy, dummy);
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {

        }

        @Override
        public boolean isBizFeature(FeatureTag field) {
            return false;
        }

        @Override
        public void addBizTags(FeatureTag field, Set<Tag> tags) {

        }

        @Override
        public void removeBizFeature(FeatureTag field) {

        }

        @Override
        public HashMap<FeatureTag, Set<Tag>> getBizTags() {
            // Hardcode dummy bizTags
            HashMap<FeatureTag, Set<Tag>> bizTags = new HashMap<>();
            Set<Tag> plans = new HashSet<>();
            plans.add(new Tag("A"));
            plans.add(new Tag("B"));
            plans.add(new Tag("C"));
            Set<Tag> genders = new HashSet<>();
            genders.add(new Tag("Male"));
            genders.add(new Tag("Female"));
            genders.add(new Tag("Other"));
            bizTags.put(new FeatureTag("Plan"), plans);
            bizTags.put(new FeatureTag("Gender"), genders);
            return bizTags;
        }

        @Override
        public void pushMutableCommandHistory(Command command) {
        }

        @Override
        public Optional<Command> popLastMutableCommand() {
            return Optional.empty();
        }

        @Override
        public void pushUndoCommandHistory(Command command) {
        }

        @Override
        public Optional<Command> popLastUndoCommand() {
            return Optional.empty();
        }

        @Override
        public ObservableList<TagFolder> getActiveTagFolders() {
            return null;
        }

        @Override
        public void addActiveTagFolders(List<String> tagNames) {
        }

        @Override
        public void addCompositeTagFolder(List<String> tagNames) {

        }

        @Override
        public void refreshActiveTagFolderCounts() {
        }

        @Override
        public boolean hasTagFolder(String name) {
            return false;
        }

        @Override
        public void addActiveTagFoldersFromUser(List<String> tagNames) {

        }

        @Override
        public void addCompositeTagFolderFromUser(List<String> tagNames) {

        }

        @Override
        public boolean removeTagFolderByName(String name) {
            return false;
        }
    }

    /**
     * Stub for {@code ObservableList Person} Object in ModelStub for unit testing.
     */
    private class PersonListStub implements ObservableList<Person> {
        private final List<Person> people;

        public PersonListStub() {
            people = List.of(
                    new PersonBuilder().withTags("Male", "A").build(),
                    new PersonBuilder().withTags("Female", "B").build(),
                    new PersonBuilder().withTags("Other", "C").build(),
                    new PersonBuilder().withTags("Male", "A").build()
            );
        }

        @Override
        public void addListener(ListChangeListener<? super Person> listener) {

        }

        @Override
        public void addListener(InvalidationListener listener) {

        }

        @Override
        public void removeListener(InvalidationListener listener) {

        }

        @Override
        public void removeListener(ListChangeListener<? super Person> listener) {

        }

        @Override
        public boolean addAll(Person... elements) {
            return false;
        }

        @Override
        public boolean addAll(int index, Collection<? extends Person> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Person> c) {
            return false;
        }

        @Override
        public boolean setAll(Person... elements) {
            return false;
        }

        @Override
        public boolean setAll(Collection<? extends Person> col) {
            return false;
        }

        @Override
        public boolean removeAll(Person... elements) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Person... elements) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void remove(int from, int to) {

        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public Person remove(int index) {
            return null;
        }

        @Override
        public int size() {
            return people.size();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<Person> iterator() {
            return List.<Person>of().iterator();
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(Person person) {
            return false;
        }

        @Override
        public void add(int index, Person element) {

        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public Person get(int index) {
            return null;
        }

        @Override
        public Person set(int index, Person element) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @Override
        public ListIterator<Person> listIterator() {
            return List.<Person>of().listIterator();
        }

        @Override
        public ListIterator<Person> listIterator(int index) {
            return List.<Person>of().listIterator(index);
        }

        @Override
        public List<Person> subList(int fromIndex, int toIndex) {
            return List.of();
        }
    }
}

