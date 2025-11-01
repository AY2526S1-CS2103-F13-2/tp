package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDTAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETETAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values except when using the ADDTAG prefix,"
            + " instead of TAG prefix, where tags input after that prefix will be added to "
            + "existing tags of the Person.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]..." + " OR "
            + "[" + PREFIX_ADDTAG + "ADDTAG]..." + " OR "
            + "[" + PREFIX_DELETETAG + "DELETETAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MANUAL = String.join("\n",
            "NAME",
            "  edit — Edits details of an existing person.",
            "",
            "USAGE",
            "  edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] {[t/TAG]… OR [at/TAG]… OR [dt/TAG]} ",
            "",
            "PARAMETERS",
            "  • INDEX: positive integer referring to displayed list index",

            "  • Any of NAME, PHONE, EMAIL, ADDRESS, TAG fields may be provided",
            "  • TAG fields replace the existing tag set if provided",
            "  • ADDTAG fields add on to the existing tag set of Person if provided",
            "  • DELETETAG fields are removed from the existing tag set of Person if provided",
            "\n  • NAME (optional): " + Name.MESSAGE_CONSTRAINTS.replace("\n", "\n\t"),
            "\n  • PHONE (optional): " + Phone.MESSAGE_CONSTRAINTS.replace("\n", "\n\t"),
            "\n  • EMAIL (optional): " + Email.MESSAGE_CONSTRAINTS.replace("\n", "\n\t"),
            "\n  • ADDRESS (optional): " + Address.MESSAGE_CONSTRAINTS.replace("\n", "\n\t"),
            "\n  • TAG (optional): " + Tag.MESSAGE_CONSTRAINTS.replace("\n", "\n\t"),
            "",
            "EXAMPLES",
            "  edit 2 p/91234567 e/new@example.com",
            "  edit 1 n/Alice Bob a/55, New Road t/friends",
            "",
            "SEE MORE",
            "  https://ay2526s1-cs2103-f13-2.github.io/tp/UserGuide.html#editing-a-person--edit"
    );

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person:\n%1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_TOO_MANY_TAG_PREFIXES =
            "Only one of t/, at/, or dt/ can be used in a single edit command.";
    public static final String MESSAGE_TAGS_TO_DELETE_NOT_FOUND =
            "One or more tags to delete do not exist for this person.";
    public static final String MESSAGE_EXCEEDING_MAX_TAGS =
            "Too many tags after edit: at most %d allowed, but %d provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MESSAGE_UNDO_SUCCESS = "Reverted last edit:\n%1$s";
    public static final String MESSAGE_UNDO_FAILED = "No edit to revert.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    // to keep the current status of the person to be edited
    private Person personToEdit = null;
    private ReadOnlyAddressBook currentAddressBook = null;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        personToEdit = personToEdit == null ? lastShownList.get(index.getZeroBased()) : personToEdit;
        currentAddressBook = new AddressBook(model.getAddressBook().getPersonList());
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, editedPerson));
    }

    @Override
    public String man() {
        return MANUAL;
    }

    public boolean isMutable() {
        return true;
    }

    @Override
    public String undo(Model model) {
        requireNonNull(model);
        if (personToEdit != null) {
            model.setAddressBook(currentAddressBook);
            return String.format(MESSAGE_UNDO_SUCCESS, personToEdit);
        } else {
            throw new IllegalStateException(MESSAGE_UNDO_FAILED);
        }
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit,
                                             EditPersonDescriptor editPersonDescriptor) throws CommandException {

        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Set<Tag> updatedTags;

        if (editPersonDescriptor.getTags().isPresent()) {
            updatedTags = editPersonDescriptor.getTags().get();
        } else if (editPersonDescriptor.getAddTags().isPresent()) {
            updatedTags = new HashSet<>(personToEdit.getTags());
            updatedTags.addAll(editPersonDescriptor.getAddTags().get());
        } else if (editPersonDescriptor.getDeleteTags().isPresent()) {
            updatedTags = new HashSet<>(personToEdit.getTags());
            for (Tag tag : editPersonDescriptor.getDeleteTags().get()) {
                if (!updatedTags.contains(tag)) {
                    throw new CommandException(MESSAGE_TAGS_TO_DELETE_NOT_FOUND);
                }
            }
            updatedTags.removeAll(editPersonDescriptor.getDeleteTags().get());
        } else {
            updatedTags = personToEdit.getTags();
        }

        // Edit-time guard for max tags per person
        if (updatedTags.size() > Person.MAX_TAGS_PER_PERSON) {
            throw new CommandException(String.format(
                    MESSAGE_EXCEEDING_MAX_TAGS,
                    Person.MAX_TAGS_PER_PERSON, updatedTags.size()));
        }

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;
        private Set<Tag> addTags;
        private Set<Tag> deleteTags;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
            addTags(toCopy.addTags);
            deleteTags(toCopy.deleteTags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags, addTags, deleteTags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Adds {@code tags} to this object's {@code tags"}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void addTags(Set<Tag> tags) {
            this.addTags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Adds {@code tags} to this object's {@code tags"}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void deleteTags(Set<Tag> tags) {
            this.deleteTags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getAddTags() {
            return (addTags != null) ? Optional.of(Collections.unmodifiableSet(addTags)) : Optional.empty();
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getDeleteTags() {
            return (deleteTags != null) ? Optional.of(Collections.unmodifiableSet(deleteTags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags)
                    && Objects.equals(addTags, otherEditPersonDescriptor.addTags)
                    && Objects.equals(deleteTags, otherEditPersonDescriptor.deleteTags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("tags", tags)
                    .add("addTags", addTags)
                    .add("deleteTags", deleteTags)
                    .toString();
        }
    }
}
