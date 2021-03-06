import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;

import java.util.Optional;

public class GetWhitespaceSuite {
    private EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");

    @Test
    public void test1() {//Должен вернуть пробелы из wheat
        Assert.assertEquals(Optional.of("    "), HighlightRange.getWhitespaces(wheat, 15));
        Assert.assertEquals(Optional.of(" "), HighlightRange.getWhitespaces(wheat, 3));
        Assert.assertEquals(Optional.of("  "), HighlightRange.getWhitespaces(wheat, 6));
        Assert.assertEquals(Optional.of("   "), HighlightRange.getWhitespaces(wheat, 11));
    }

    @Test
    public void test2() {//Должен вернуть пустую строку
        Assert.assertEquals(Optional.empty(), HighlightRange.getWhitespaces(wheat, 0));
        Assert.assertEquals(Optional.empty(), HighlightRange.getWhitespaces(wheat, 5));
        Assert.assertEquals(Optional.empty(), HighlightRange.getWhitespaces(wheat, 19));
        Assert.assertEquals(Optional.empty(), HighlightRange.getWhitespaces(wheat, 1000));
    }
}
