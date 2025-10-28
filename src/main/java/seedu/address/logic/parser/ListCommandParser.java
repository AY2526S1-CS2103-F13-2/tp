package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SAVE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.TagMatchesAllPredicate;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new {@code ListCommand} object.
 * <p>
 * This parser supports both:
 * <ul>
 *   <li>Listing all persons when no tag prefix is provided (e.g., {@code list})</li>
 *   <li>Listing persons who have <em>all</em> of the specified tags when one or more
 *       {@code t/} prefixes are provided (e.g., {@code list t/friends t/colleagues})</li>
 * </ul>
 *
 * <p>Example usages:
 * <pre>
 *     list
 *     list t/friends
 *     list t/friends t/colleagues
 * </pre>
 *
 * The parser tokenizes the input by the {@code PREFIX_TAG} and converts each tag
 * value into a {@link seedu.address.model.tag.Tag} object. These are collected
 * into a {@link java.util.Set}, and a {@link seedu.address.model.person.TagMatchesAllPredicate}
 * is created to filter the person list.
 */
public class ListCommandParser implements Parser<ListCommand> {
    @Override
    public ListCommand parse(String args) throws ParseException {
        final ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_TAG, PREFIX_SAVE, PREFIX_DELETE);

        final List<String> tagValues = map.getAllValues(PREFIX_TAG);
        final boolean wantsSave = !map.getAllValues(PREFIX_SAVE).isEmpty();
        final boolean wantsDelete = !map.getAllValues(PREFIX_DELETE).isEmpty();

        if (wantsSave && wantsDelete) {
            throw new ParseException("You cannot save and delete a folder in the same command.");
        }

        if (tagValues.isEmpty()) {
            if (wantsDelete) {
                throw new ParseException("Specify at least one tag with d/ to choose a folder to "
                        + "delete, e.g. `list t/friends d/`.");
            }
            if (wantsSave) {
                throw new ParseException("You cannot create a nameless folder."
                        + "Add at least one tag, e.g. `list t/friends s/`.");
            }
            return new ListCommand();
        }

        final java.util.Set<Tag> required = new java.util.LinkedHashSet<>();
        for (String v : tagValues) {
            required.add(ParserUtil.parseTag(v));
        }

        return new ListCommand(new TagMatchesAllPredicate(required), tagValues, wantsSave, wantsDelete);
    }
}
