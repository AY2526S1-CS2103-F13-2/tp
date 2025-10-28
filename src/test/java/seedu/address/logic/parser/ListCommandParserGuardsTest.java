package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ListCommandParserGuardsTest {

    @Test
    void saveAndDeleteTogether_throws() {
        ListCommandParser parser = new ListCommandParser();
        assertThrows(ParseException.class, () -> parser.parse("t/friends s/ d/"));
        assertThrows(ParseException.class, () -> parser.parse("t/friends d/ s/"));
    }

    @Test
    void deleteWithoutTags_throws() {
        ListCommandParser parser = new ListCommandParser();
        assertThrows(ParseException.class, () -> parser.parse("d/"));
        assertThrows(ParseException.class, () -> parser.parse("   d/   "));
    }

    @Test
    void saveWithoutTags_throws() {
        ListCommandParser parser = new ListCommandParser();
        assertThrows(ParseException.class, () -> parser.parse("s/"));
        assertThrows(ParseException.class, () -> parser.parse("   s/   "));
    }

    @Test
    void withTags_saveOrDelete_ok() throws Exception {
        ListCommandParser parser = new ListCommandParser();
        ListCommand saveCmd = parser.parse("t/friends s/");
        ListCommand delCmd = parser.parse("t/friends d/");
    }
}
