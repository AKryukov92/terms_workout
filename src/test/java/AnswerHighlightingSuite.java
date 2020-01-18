import org.junit.Assert;
import org.junit.Test;
import ru.ominit.model.HighlightRange;
import ru.ominit.model.Answer;

import java.util.Optional;

import static ru.ominit.model.HighlightRangeType.MAXIMAL;

public class AnswerHighlightingSuite {
    private String grain = "one two three four five";

    @Test
    public void ABCD() {
        Optional<HighlightRange> red = new Answer("one two", "one two").highlight(grain, MAXIMAL);
        Optional<HighlightRange> black = new Answer("four five", "four five").highlight(grain, MAXIMAL);
        Optional<HighlightRange> result = red.flatMap(r -> black.flatMap(r::connectWith));
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void ACBD() {
        Optional<HighlightRange> red = new Answer("one two", "one two").highlight(grain, MAXIMAL);
        Optional<HighlightRange> black = new Answer("two three", "two three").highlight(grain, MAXIMAL);
        Optional<HighlightRange> result = red.flatMap(r -> black.flatMap(r::connectWith));
        Assert.assertEquals(Optional.of("one two three"), result.map(r -> grain.substring(r.getStartIndex(), r.getEndIndex())));
        Assert.assertEquals(Optional.of(new HighlightRange(0, 13, MAXIMAL)), result);
    }

    @Test
    public void CABD() {
        Optional<HighlightRange> red = new Answer("two", "two").highlight(grain, MAXIMAL);
        Optional<HighlightRange> black = new Answer("one two three", "one two three").highlight(grain, MAXIMAL);
        Optional<HighlightRange> result = red.flatMap(r -> black.flatMap(r::connectWith));
        Assert.assertEquals(Optional.of("one two three"), result.map(r -> grain.substring(r.getStartIndex(), r.getEndIndex())));
        Assert.assertEquals(Optional.of(new HighlightRange(0, 13, MAXIMAL)), result);
    }

    @Test
    public void CADB() {
        Optional<HighlightRange> red = new Answer("two three", "two three").highlight(grain, MAXIMAL);
        Optional<HighlightRange> black = new Answer("one two", "one two").highlight(grain, MAXIMAL);
        Optional<HighlightRange> result = red.flatMap(r -> black.flatMap(r::connectWith));
        Assert.assertEquals(Optional.of("one two three"), result.map(r -> grain.substring(r.getStartIndex(), r.getEndIndex())));
        Assert.assertEquals(Optional.of(new HighlightRange(0, 13, MAXIMAL)), result);
    }

    @Test
    public void CDAB() {
        Optional<HighlightRange> red = new Answer("four", "four").highlight(grain, MAXIMAL);
        Optional<HighlightRange> black = new Answer("one", "one").highlight(grain, MAXIMAL);
        Optional<HighlightRange> result = red.flatMap(r -> black.flatMap(r::connectWith));
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void ACDB() {
        Optional<HighlightRange> red = new Answer("one two three", "one two three").highlight(grain, MAXIMAL);
        Optional<HighlightRange> black = new Answer("two", "two").highlight(grain, MAXIMAL);
        Optional<HighlightRange> result = red.flatMap(r -> black.flatMap(r::connectWith));
        Assert.assertEquals(Optional.of("one two three"), result.map(r -> grain.substring(r.getStartIndex(), r.getEndIndex())));
        Assert.assertEquals(Optional.of(new HighlightRange(0, 13, MAXIMAL)), result);
    }
}
