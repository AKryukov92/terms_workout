import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;

import java.util.Arrays;
import java.util.List;

public class HighlightInWheatSuite {
    private EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");

    @Test
    public void test1() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                wheat,
                EscapedHtmlString.make("e t").splitByWhitespace()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(2, 4)
        );
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void test2() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                wheat,
                EscapedHtmlString.make("one two").splitByWhitespace()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(0, 6)
        );
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void test3() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                wheat,
                EscapedHtmlString.make("wo three four fi").splitByWhitespace()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(5, 26)
        );
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void test4() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                EscapedHtmlString.make("one  one  one  one"),
                EscapedHtmlString.make("one").splitByWhitespace()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(0, 2),
                new HighlightRange(5, 7),
                new HighlightRange(10, 12),
                new HighlightRange(15, 17)
        );
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void test5() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                EscapedHtmlString.make("one  one  one  one"),
                EscapedHtmlString.make("one one").splitByWhitespace()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(0, 7),
                new HighlightRange(10, 17)
        );
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void test6() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                EscapedHtmlString.make("a a b a b c a b c d"),
                EscapedHtmlString.make("a b c d").splitByWhitespace()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(12, 19)
        );
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void test7() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                EscapedHtmlString.make("abcd a b c d"),
                EscapedHtmlString.make("a b c d").splitByWhitespace()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(6, 12)
        );
        Assert.assertEquals(actual, expected);
    }
}
