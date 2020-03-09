import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.model.Answer;

import java.util.ArrayList;
import java.util.List;


public class HighlightRangeMakeSuite {
    @Test
    public void test1() {
        EscapedHtmlString wheat = EscapedHtmlString.make("<html>" +
                "    <head>" +
                "    </head>" +
                "    <body>" +
                "    </body>" +
                "</html>");
        EscapedHtmlString[] grain = wheat.split("\\s+");
        Answer answer = new Answer("<html>");
        List<HighlightRange> actual = HighlightRange.highlightAll(grain, answer.getMinimalFragments());
        List<HighlightRange> expected = new ArrayList<>();
        expected.add(new HighlightRange(0, 5));
        Assert.assertEquals(expected, actual);
    }
}
