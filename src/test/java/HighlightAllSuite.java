import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;

import java.util.Arrays;
import java.util.List;

public class HighlightAllSuite {
    private EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");

    @Test
    public void test1() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                wheat.getGrain(),
                EscapedHtmlString.make("e t").getGrain(),
                EscapedHtmlString.make("one two ").getGrain()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(2, 4)
        );
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                wheat.getGrain(),
                EscapedHtmlString.make("one two").getGrain(),
                EscapedHtmlString.make("one two  th").getGrain()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(0, 6)
        );
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test3() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                wheat.getGrain(),
                EscapedHtmlString.make("wo three four fi").getGrain(),
                EscapedHtmlString.make("ne two  three   four    five").getGrain()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(4, 17)
        );
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test4() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                EscapedHtmlString.make("one  one  one  one").getGrain(),
                EscapedHtmlString.make("one").getGrain(),
                EscapedHtmlString.make("ne  one  on").getGrain()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(3, 6),
                new HighlightRange(6, 9)
        );
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test5() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                EscapedHtmlString.make("one  one  one  one").getGrain(),
                EscapedHtmlString.make("one one").getGrain(),
                EscapedHtmlString.make("ne  one  one  on").getGrain()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(6, 12)
        );
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test6() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                EscapedHtmlString.make("a a b a b c a b c d").getGrain(),
                EscapedHtmlString.make("a b c d").getGrain(),
                EscapedHtmlString.make("b c a b c d").getGrain()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(6, 10)
        );
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test7() {
        List<HighlightRange> actual = HighlightRange.highlightAll(
                EscapedHtmlString.make("abcd a b c d").getGrain(),
                EscapedHtmlString.make("a b c d").getGrain(),
                EscapedHtmlString.make("bcd a b c d").getGrain()
        );
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(4, 8)
        );
        Assert.assertEquals(expected, actual);
    }
}
