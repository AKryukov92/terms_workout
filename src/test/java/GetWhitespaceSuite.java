import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.model.Riddle;

import java.util.Optional;

public class GetWhitespaceSuite {
    private EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
    private EscapedHtmlString[] grain = wheat.splitByWhitespace();

    @Test
    public void test1() {//Должен вернуть пробелы из wheat
        Assert.assertEquals(Optional.of("    "), Riddle.getWhitespaces(grain, wheat, 15));
        Assert.assertEquals(Optional.of(" "), Riddle.getWhitespaces(grain, wheat, 3));
        Assert.assertEquals(Optional.of("  "), Riddle.getWhitespaces(grain, wheat, 6));
        Assert.assertEquals(Optional.of("   "), Riddle.getWhitespaces(grain, wheat, 11));
    }

    @Test
    public void test2() {//Должен вернуть пустую строку
        Assert.assertEquals(Optional.empty(), Riddle.getWhitespaces(grain, wheat, 0));
        Assert.assertEquals(Optional.empty(), Riddle.getWhitespaces(grain, wheat, 5));
        Assert.assertEquals(Optional.empty(), Riddle.getWhitespaces(grain, wheat, 19));
        Assert.assertEquals(Optional.empty(), Riddle.getWhitespaces(grain, wheat, 1000));
    }
}
