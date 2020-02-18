import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.HighlightRange;

import java.util.Optional;

public class HighlightRangeSuite {
    //There are two ranges: AB and CD
    //Tests are named to reflect order of their endings
    @Test
    public void ABCD() {
        HighlightRange red = new HighlightRange(0, 4);
        HighlightRange black = new HighlightRange(6, 10);
        Optional<HighlightRange> result = red.connectWith(black);
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void ACBD() {
        HighlightRange red = new HighlightRange(0, 6);
        HighlightRange black = new HighlightRange(4, 10);
        Optional<HighlightRange> result = red.connectWith(black);
        Assert.assertEquals(Optional.of(new HighlightRange(0, 10)), result);
    }

    @Test
    public void CABD() {
        HighlightRange red = new HighlightRange(4, 6);
        HighlightRange black = new HighlightRange(0, 10);
        Optional<HighlightRange> result = red.connectWith(black);
        Assert.assertEquals(Optional.of(new HighlightRange(0, 10)), result);
    }

    @Test
    public void CADB() {
        HighlightRange red = new HighlightRange(4, 10);
        HighlightRange black = new HighlightRange(0, 6);
        Optional<HighlightRange> result = red.connectWith(black);
        Assert.assertEquals(Optional.of(new HighlightRange(0, 10)), result);
    }

    @Test
    public void CDAB() {
        HighlightRange red = new HighlightRange(6, 10);
        HighlightRange black = new HighlightRange(0, 4);
        Optional<HighlightRange> result = red.connectWith(black);
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void ACDB() {
        HighlightRange red = new HighlightRange(0, 10);
        HighlightRange black = new HighlightRange(4, 6);
        Optional<HighlightRange> result = red.connectWith(black);
        Assert.assertEquals(Optional.of(new HighlightRange(0, 10)), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void BA() {
        new HighlightRange(2, 1);
    }
}
