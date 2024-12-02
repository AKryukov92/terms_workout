import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.ominit.highlight.EscapedHtmlString.make;

public class AppendContentsOfMaxRangeSuite {
    private EscapedHtmlString wheat = EscapedHtmlString.make("aaa bbb ccc ddd eee fff ggg hhh iii jjj");

    @Test
    public void test1() {//Максимальный интервал содержит один минимальный без дополнительного текста
        List<String> actual = new ArrayList<>();
        HighlightRange.appendContentsOfMaxRange(
                actual,
                wheat,
                new HighlightRange(3, 6),
                Arrays.asList(new HighlightRange(3, 6), new HighlightRange(9, 12), new HighlightRange(15, 18))
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
        HighlightRange.appendContentsOfMaxRange(
                actual,
                wheat,
                new HighlightRange(3, 12),
                Arrays.asList(new HighlightRange(6, 9), new HighlightRange(12, 15), new HighlightRange(18, 21))
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

    @Test
    public void test3() {
        EscapedHtmlString wheat = make("for(int i = 0; i < arr.length");
        List<String> result = new ArrayList<>(Arrays.asList("for(", HighlightRange.MAX_START));
        HighlightRange.appendContentsOfMaxRange(
                result,
                wheat,
                new HighlightRange(4, 11),
                Arrays.asList(new HighlightRange(7, 10))
        );
        List<String> expected = Arrays.asList(
                "for(",
                HighlightRange.MAX_START,
                "int",
                " ",
                HighlightRange.MIN_START,
                "i",
                " ",
                "=",
                " ",
                "0",
                HighlightRange.END,
                ";"
        );
        Assert.assertEquals(expected, result);
    }

    @Test
    public void test4() {
        EscapedHtmlString wheat = make("select model, speed, hd");
        List<String> result = new ArrayList<>(Arrays.asList("select", " ", HighlightRange.MAX_START));
        HighlightRange.appendContentsOfMaxRange(result,
                wheat,
                new HighlightRange(6, 20),
                Arrays.asList(new HighlightRange(6, 11), new HighlightRange(12, 17), new HighlightRange(18, 20)));
        List<String> expected = Arrays.asList(
                "select", " ", HighlightRange.MAX_START, HighlightRange.MIN_START, "model", HighlightRange.END, ",", " ", HighlightRange.MIN_START, "speed", HighlightRange.END, ",", " ", HighlightRange.MIN_START, "hd", HighlightRange.END
        );
        Assert.assertEquals(expected, result);
    }
}
