import org.junit.Assert;
import org.junit.Test;
import ru.ominit.model.HighlightRange;
import ru.ominit.model.Answer;
import ru.ominit.model.HighlightRangeType;

import java.util.Optional;

import static ru.ominit.model.HighlightRangeType.MAXIMAL;

public class AnswerHighlightingSuite {
    private String grainWithWhitespace = "one two three four five";
    private String grainSingleWord = "onetwothreefourfive";

    @Test
    public void ABCD() {
        Optional<HighlightRange> red = new Answer("one two", "one two").highlight(grainWithWhitespace, MAXIMAL);
        Optional<HighlightRange> black = new Answer("four five", "four five").highlight(grainWithWhitespace, MAXIMAL);
        Optional<HighlightRange> result = red.flatMap(r -> black.flatMap(r::connectWith));
        Assert.assertFalse(result.isPresent());

        red = new Answer("onetwo", "onetwo").highlight(grainSingleWord, MAXIMAL);
        Optional<HighlightRange> green = new Answer("fourfive", "fourfive").highlight(grainSingleWord, MAXIMAL);
        result = red.flatMap(r -> green.flatMap(r::connectWith));
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void ACBD() {
        {
            Optional<HighlightRange> red = new Answer("one two", "one two").highlight(grainWithWhitespace, MAXIMAL);
            Optional<HighlightRange> black = new Answer("two three", "two three").highlight(grainWithWhitespace, MAXIMAL);
            Optional<HighlightRange> result = red.flatMap(r -> black.flatMap(r::connectWith));
            Assert.assertEquals(Optional.of("one two three"), result.map(r -> grainWithWhitespace.substring(r.getStartIndex(), r.getEndIndex())));
            Assert.assertEquals(Optional.of(new HighlightRange(0, 13, MAXIMAL)), result);
        }
        {
            Optional<HighlightRange> red = new Answer("onetwo", "onetwo").highlight(grainSingleWord, MAXIMAL);
            Optional<HighlightRange> black = new Answer("twothree", "twothree").highlight(grainSingleWord, MAXIMAL);
            Optional<HighlightRange> result = red.flatMap(r -> black.flatMap(r::connectWith));
            Optional<String> hightlightedText = result.map(r -> grainSingleWord.substring(r.getStartIndex(), r.getEndIndex()));
            Assert.assertEquals(Optional.of("onetwothree"), hightlightedText);
        }
    }

    @Test
    public void CABD() {
        Optional<HighlightRange> red = new Answer("two", "two").highlight(grainWithWhitespace, MAXIMAL);
        Optional<HighlightRange> black = new Answer("one two three", "one two three").highlight(grainWithWhitespace, MAXIMAL);
        Optional<HighlightRange> result = red.flatMap(r -> black.flatMap(r::connectWith));
        Assert.assertEquals(Optional.of("one two three"), result.map(r -> grainWithWhitespace.substring(r.getStartIndex(), r.getEndIndex())));
        Assert.assertEquals(Optional.of(new HighlightRange(0, 13, MAXIMAL)), result);
    }

    @Test
    public void CADB() {
        Optional<HighlightRange> red = new Answer("two three", "two three").highlight(grainWithWhitespace, MAXIMAL);
        Optional<HighlightRange> black = new Answer("one two", "one two").highlight(grainWithWhitespace, MAXIMAL);
        Optional<HighlightRange> result = red.flatMap(r -> black.flatMap(r::connectWith));
        Assert.assertEquals(Optional.of("one two three"), result.map(r -> grainWithWhitespace.substring(r.getStartIndex(), r.getEndIndex())));
        Assert.assertEquals(Optional.of(new HighlightRange(0, 13, MAXIMAL)), result);
    }

    @Test
    public void CDAB() {
        Optional<HighlightRange> red = new Answer("four", "four").highlight(grainWithWhitespace, MAXIMAL);
        Optional<HighlightRange> black = new Answer("one", "one").highlight(grainWithWhitespace, MAXIMAL);
        Optional<HighlightRange> result = red.flatMap(r -> black.flatMap(r::connectWith));
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void ACDB() {
        Optional<HighlightRange> red = new Answer("one two three", "one two three").highlight(grainWithWhitespace, MAXIMAL);
        Optional<HighlightRange> black = new Answer("two", "two").highlight(grainWithWhitespace, MAXIMAL);
        Optional<HighlightRange> result = red.flatMap(r -> black.flatMap(r::connectWith));
        Assert.assertEquals(Optional.of("one two three"), result.map(r -> grainWithWhitespace.substring(r.getStartIndex(), r.getEndIndex())));
        Assert.assertEquals(Optional.of(new HighlightRange(0, 13, MAXIMAL)), result);
    }
}
