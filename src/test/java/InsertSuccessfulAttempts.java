import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;

import java.util.Arrays;
import java.util.List;

public class InsertSuccessfulAttempts {
    EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");

    @Test
    public void test1() {
        List<HighlightRange> ranges = Arrays.asList(
                new HighlightRange(0, 3),
                new HighlightRange(3, 6),
                new HighlightRange(6, 11),
                new HighlightRange(11, 15),
                new HighlightRange(15, 19)
        );
        List<String> actual = HighlightRange.tokenize(ranges, wheat.splitByWhitespace(), wheat);
        List<String> expected = Arrays.asList(
                HighlightRange.ANSWER_START,
                "one",
                HighlightRange.END,
                " ",
                HighlightRange.ANSWER_START,
                "two",
                HighlightRange.END,
                "  ",
                HighlightRange.ANSWER_START,
                "three",
                HighlightRange.END,
                "   ",
                HighlightRange.ANSWER_START,
                "four",
                HighlightRange.END,
                "    ",
                HighlightRange.ANSWER_START,
                "five",
                HighlightRange.END
        );
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        List<HighlightRange> ranges = Arrays.asList(
                new HighlightRange(0, 6),
                new HighlightRange(11, 19)
        );
        List<String> actual = HighlightRange.tokenize(ranges, wheat.splitByWhitespace(), wheat);
        List<String> expected = Arrays.asList(
                HighlightRange.ANSWER_START,
                "one",
                " ",
                "two",
                HighlightRange.END,
                "  ",
                "three",
                "   ",
                HighlightRange.ANSWER_START,
                "four",
                "    ",
                "five",
                HighlightRange.END
        );
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test3() {
        List<HighlightRange> ranges = Arrays.asList(
                new HighlightRange(2, 5),
                new HighlightRange(5, 9)
        );
        List<String> actual = HighlightRange.tokenize(ranges, wheat.splitByWhitespace(), wheat);
        List<String> expected = Arrays.asList(
                "on",
                HighlightRange.ANSWER_START,
                "e",
                " ",
                "tw",
                HighlightRange.END,
                HighlightRange.ANSWER_START,
                "o",
                "  ",
                "thr",
                HighlightRange.END,
                "ee",
                "   ",
                "four",
                "    ",
                "five"
        );
        Assert.assertEquals(expected, actual);
    }
}
