import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.model.Riddle;

public class GetWhitespaceSuite {
    private EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
    private EscapedHtmlString[] grain = wheat.splitByWhitespace();

    @Test
    public void test1() {//Должен вернуть пробелы из wheat
        Assert.assertEquals("    ", Riddle.getWhitespaces(grain, wheat, 15));
        Assert.assertEquals(" ", Riddle.getWhitespaces(grain, wheat, 3));
        Assert.assertEquals("  ", Riddle.getWhitespaces(grain, wheat, 6));
        Assert.assertEquals("   ", Riddle.getWhitespaces(grain, wheat, 11));
    }

    @Test
    public void test2() {//Должен вернуть пустую строку
        Assert.assertEquals("", Riddle.getWhitespaces(grain, wheat, 0));
        Assert.assertEquals("", Riddle.getWhitespaces(grain, wheat, 5));
        Assert.assertEquals("", Riddle.getWhitespaces(grain, wheat, 19));
        Assert.assertEquals("", Riddle.getWhitespaces(grain, wheat, 1000));
    }
}
