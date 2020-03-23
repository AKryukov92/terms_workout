import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.model.Riddle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenizeSuite {
    private EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
    private EscapedHtmlString[] grain = wheat.splitByWhitespace();

    @Test
    public void test1() {
        List<String> tokens = Riddle.tokenize(new ArrayList<>(), new ArrayList<>(), grain, wheat);
        List<String> expected = Arrays.asList(
                "one", " ", "two", "  ", "three", "   ", "four", "    ", "five"
        );
        Assert.assertEquals(expected, tokens);
    }

    @Test
    public void test2() {
        List<String> tokens = Riddle.tokenize(
                Arrays.asList(new HighlightRange(2, 4)),
                Arrays.asList(new HighlightRange(0, 6)),
                grain, wheat
        );
        List<String> expected = Arrays.asList(
                HighlightRange.MAX_START,
                "on",
                HighlightRange.MIN_START, "e", " ", "t", HighlightRange.END,
                "wo",
                HighlightRange.END,
                "  ", "three", "   ", "four", "    ", "five"
        );

        Assert.assertEquals(expected, tokens);
    }

    @Test
    public void test3() {
        List<String> tokens = Riddle.tokenize(
                Arrays.asList(new HighlightRange(7, 14)),
                Arrays.asList(new HighlightRange(4, 17)),
                grain,
                wheat
        );
        List<String> expected = Arrays.asList(
                "one", " ", "t",
                HighlightRange.MAX_START,
                "wo", "  ", "t",
                HighlightRange.MIN_START,
                "hree", "   ", "fou",
                HighlightRange.END,
                "r",
                "    ",
                "fi",
                HighlightRange.END,
                "ve"
        );
        Assert.assertEquals(expected, tokens);
    }

    @Test
    public void test4() {
        List<String> tokens = Riddle.tokenize(
                Arrays.asList(new HighlightRange(6, 11)),
                Arrays.asList(new HighlightRange(3, 15)),
                grain,
                wheat
        );
        List<String> expected = Arrays.asList(
                "one",
                " ",
                HighlightRange.MAX_START,
                "two",
                "  ",
                HighlightRange.MIN_START,
                "three",
                HighlightRange.END,
                "   ",
                "four",
                HighlightRange.END,
                "    ",
                "five"
        );
        Assert.assertEquals(expected, tokens);
    }

    @Test
    public void test5() {
        List<String> tokens = Riddle.tokenize(
                Arrays.asList(new HighlightRange(3, 6), new HighlightRange(11, 15)),
                Arrays.asList(new HighlightRange(0, 19)),
                grain,
                wheat
        );
        List<String> expected = Arrays.asList(
                HighlightRange.MAX_START,
                "one",
                " ",
                HighlightRange.MIN_START,
                "two",
                HighlightRange.END,
                "  ",
                "three",
                "   ",
                HighlightRange.MIN_START,
                "four",
                HighlightRange.END,
                "    ",
                "five",
                HighlightRange.END
        );
        Assert.assertEquals(expected, tokens);
    }

    @Test
    public void test6() {
        EscapedHtmlString wheat = EscapedHtmlString.make("one one one one one");
        EscapedHtmlString[] grain = wheat.splitByWhitespace();
        List<String> tokens = Riddle.tokenize(
                Arrays.asList(new HighlightRange(3, 6), new HighlightRange(9, 12)),
                Arrays.asList(new HighlightRange(0, 15)),
                grain,
                wheat
        );
        List<String> expected = Arrays.asList(
                HighlightRange.MAX_START,
                "one",
                " ",
                HighlightRange.MIN_START,
                "one",
                HighlightRange.END,
                " ",
                "one",
                " ",
                HighlightRange.MIN_START,
                "one",
                HighlightRange.END,
                " ",
                "one",
                HighlightRange.END
        );
        Assert.assertEquals(expected, tokens);
    }

    @Test
    public void test7() {
        List<String> tokens = Riddle.tokenize(
                Arrays.asList(new HighlightRange(0, 19)),
                Arrays.asList(new HighlightRange(0, 19)),
                grain,
                wheat
        );
        List<String> expected = Arrays.asList(
                HighlightRange.MAX_START,
                HighlightRange.MIN_START,
                "one",
                " ",
                "two",
                "  ",
                "three",
                "   ",
                "four",
                "    ",
                "five",
                HighlightRange.END,
                HighlightRange.END
        );
        Assert.assertEquals(expected, tokens);
    }

    @Test
    public void test8() {
        List<String> tokens = Riddle.tokenize(
                Arrays.asList(new HighlightRange(3, 6), new HighlightRange(11, 15)),
                Arrays.asList(new HighlightRange(3, 6), new HighlightRange(11, 15)),
                grain,
                wheat
        );
        List<String> expected = Arrays.asList(
                "one",
                " ",
                HighlightRange.MAX_START,
                HighlightRange.MIN_START,
                "two",
                HighlightRange.END,
                HighlightRange.END,
                "  ",
                "three",
                "   ",
                HighlightRange.MAX_START,
                HighlightRange.MIN_START,
                "four",
                HighlightRange.END,
                HighlightRange.END,
                "    ",
                "five"
        );
        Assert.assertEquals(expected, tokens);
    }
}
