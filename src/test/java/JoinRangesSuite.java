import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.model.Answer;
import ru.ominit.model.Riddle;

import java.util.Arrays;
import java.util.List;

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
            riddle.addAnswer(new Answer("one two", "one two  th"));
            riddle.addAnswer(new Answer("four five", "e   four    five"));

            List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("onetwo", "onetwothre"));
            riddle.addAnswer(new Answer("fourfive", "hreefourfive"));

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
            riddle.addAnswer(new Answer("one two", "one two  th"));
            riddle.addAnswer(new Answer("two three", "one two  three   f"));
            List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("onetwo", "onetwothre"));
            riddle.addAnswer(new Answer("twothree", "onetwothreefour"));
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
            riddle.addAnswer(new Answer("two", "one two  th"));
            riddle.addAnswer(new Answer("one two three", "one two  three   f"));
            List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("two", "onetwothre"));
            riddle.addAnswer(new Answer("onetwothree", "onetwothreefour"));
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
            riddle.addAnswer(new Answer("two three", "one two  three   f"));
            riddle.addAnswer(new Answer("one two", "one two  th"));
            List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("twothree", "onetwothreefour"));
            riddle.addAnswer(new Answer("onetwo", "onetwothre"));
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
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("four", "e   four    "));
            riddle.addAnswer(new Answer("one", "one two"));
            List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("four", "hreefourfive"));
            riddle.addAnswer(new Answer("one", "onetwot"));
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
            riddle.addAnswer(new Answer("one two three", "one two  three   f"));
            riddle.addAnswer(new Answer("two", "one two  th"));
            List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
            HighlightRange.joinRanges(actual);
            Assert.assertEquals(expected, actual);
        }
        {
            Riddle riddle = new Riddle("", "", "");
            riddle.addAnswer(new Answer("onetwothree", "onetwothreefour"));
            riddle.addAnswer(new Answer("two", "onetwothre"));
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
        riddle.addAnswer(new Answer("two", "one two  th"));
        riddle.addAnswer(new Answer("two", "one two  th"));
        List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), MAXIMAL);
        HighlightRange.joinRanges(actual);
        Assert.assertEquals(expected, actual);
    }
}
