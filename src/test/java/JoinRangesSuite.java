import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.model.Answer;
import ru.ominit.model.Riddle;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ru.ominit.highlight.HighlightRangeType.MAXIMAL;

public class JoinRangesSuite {
    private EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
    private EscapedHtmlString joinedWheat = EscapedHtmlString.make("onetwothreefourfive");

    @Test
    public void ABCD() {
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(0, 6),
                new HighlightRange(11, 19)
        );
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("one two"));
            riddle.addAnswer(new Answer("four five"));

            List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("onetwo"));
            riddle.addAnswer(new Answer("fourfive"));

            List<HighlightRange> actual = riddle.extractRanges(joinedWheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void ACBD() {
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(0, 11)
        );
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("one two"));
            riddle.addAnswer(new Answer("two three"));
            List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("onetwo"));
            riddle.addAnswer(new Answer("twothree"));
            List<HighlightRange> actual = riddle.extractRanges(joinedWheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void CABD() {
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(0, 11)
        );
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("two"));
            riddle.addAnswer(new Answer("one two three"));
            List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("two"));
            riddle.addAnswer(new Answer("onetwothree"));
            List<HighlightRange> actual = riddle.extractRanges(joinedWheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void CADB() {
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(0, 11)
        );
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("two three"));
            riddle.addAnswer(new Answer("one two"));
            List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("twothree"));
            riddle.addAnswer(new Answer("onetwo"));
            List<HighlightRange> actual = riddle.extractRanges(joinedWheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void CDAB() {
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(0, 3),
                new HighlightRange(11, 15)
        );
        Riddle riddle = new Riddle("", "", "");
        riddle.addAnswer(new Answer("four"));
        riddle.addAnswer(new Answer("one"));
        {
            List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
        {
            List<HighlightRange> actual = riddle.extractRanges(joinedWheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void ACDB() {
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(0, 11)
        );
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("one two three"));
            riddle.addAnswer(new Answer("two"));
            List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("onetwothree"));
            riddle.addAnswer(new Answer("two"));
            List<HighlightRange> actual = riddle.extractRanges(joinedWheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void AABB(){
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(3, 6)
        );
        Riddle riddle = new Riddle("", "", "");
        riddle.addAnswer(new Answer("two"));
        riddle.addAnswer(new Answer("two"));
        List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
        HighlightRange.joinRanges(actual);
        Assert.assertEquals(expected, actual);
    }
}
