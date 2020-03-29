import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.model.Riddle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppendContentsOfMaxRangeSuite {
    private EscapedHtmlString wheat = EscapedHtmlString.make("aaa bbb ccc ddd eee fff ggg hhh iii jjj");

    @Test
    public void test1() {//Максимальный интервал содержит один минимальный без дополнительного текста
        List<String> actual = new ArrayList<>();
        Riddle.appendContentsOfMaxRange(
                actual,
                wheat.splitByWhitespace(),
                wheat,
                new HighlightRange(3, 6),
                Arrays.asList(new HighlightRange(3, 6), new HighlightRange(9, 12), new HighlightRange(15, 18)),
                0
        );
        List<String> expected = Arrays.asList(
                HighlightRange.MIN_START,
                "bbb",
                HighlightRange.END
        );
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {//Максимальный интервал содержит один минимальный с дополнительным текстом по сторонам
        List<String> actual = new ArrayList<>();
        Riddle.appendContentsOfMaxRange(
                actual,
                wheat.splitByWhitespace(),
                wheat,
                new HighlightRange(3, 12),
                Arrays.asList(new HighlightRange(6, 9), new HighlightRange(12, 15), new HighlightRange(18, 21)),
                0
        );
        List<String> expected = Arrays.asList(
                "bbb",
                " ",
                HighlightRange.MIN_START,
                "ccc",
                HighlightRange.END,
                " ",
                "ddd"
        );
        Assert.assertEquals(expected, actual);
    }
}
