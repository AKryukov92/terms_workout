import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;
import ru.ominit.model.HighlightRange;
import ru.ominit.model.Answer;
import ru.ominit.model.Riddle;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.ominit.model.HighlightRange.*;
import static ru.ominit.model.HighlightRangeType.MAXIMAL;
import static ru.ominit.model.HighlightRangeType.MINIMAL;

public class HighlightSuite {

    @Test
    public void test() {
        String wheat = "one two  three   four    five";
        String grain = "one two three four five";
        HighlightRange max = new HighlightRange(0, 13, MAXIMAL);
        HighlightRange min = new HighlightRange(0, 3, MINIMAL);
        String step1 = "one two three";
        String[] step2 = new String[]{"one", "two", "three"};
        int begin = wheat.indexOf(step2[0]);
        String lastPart = step2[step2.length - 1];
        int end = wheat.indexOf(lastPart) + lastPart.length();
        String result = wheat.substring(0, begin) + MAX_START + wheat.substring(begin, end) + END + wheat.substring(end);
        String expected = MAX_START + "one two  three" + END + "   four    five";
        Assert.assertEquals(expected, result);
        begin = result.indexOf("one");
        lastPart = "one";
        end = result.indexOf("one") + lastPart.length();
        result = result.substring(0, begin) + MIN_START + result.substring(begin, end) + END + result.substring(end);
        expected = MAX_START + MIN_START + "one" + END + " two  three" + END + "   four    five";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void test2() {
        String wheat = "one two  three   four    five";
        String grain = "one two three four five";
        Answer answer = new Answer("one", "one two three");
        Optional<HighlightRange> maxOpt = answer.highlight(grain, MAXIMAL);
        Optional<HighlightRange> minOpt = answer.highlight(grain, MINIMAL);
        Optional<String> resultOpt = maxOpt.map(m -> m.insert(grain, wheat));
        Optional<String> step2Opt = minOpt.flatMap(m -> resultOpt.map(r -> m.insert(grain, r)));
        String expected = MAX_START + "one two  three" + END + "   four    five";
        String expected2 = MAX_START + MIN_START + "one" + END + " two  three" + END + "   four    five";
        Assert.assertEquals(Optional.of(expected), resultOpt);
        Assert.assertEquals(Optional.of(expected2), step2Opt);
    }

    @Test
    public void testJoinAnswerRanges() {
        String grain = "one two three four five";
        Riddle riddle = new Riddle("", "", "");
        riddle.addAnswer(new Answer("", "one two"));
        riddle.addAnswer(new Answer("", "four five"));
        riddle.addAnswer(new Answer("", "two three four"));
        List<HighlightRange> result = riddle.joinAnswerRanges(grain, MAXIMAL);
        Assert.assertEquals(Collections.singletonList(new HighlightRange(0, 23, MAXIMAL)), result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void highlightAnswerOnGrainMinLikeMax() {
        String grain = "one two three four five";
        String result;

        result = new Answer("one", "one").highlightIn(grain).get();
        Assert.assertEquals(MAX_START + "one" + END + " two three four five", result);

        result = new Answer("two", "two").highlightIn(grain).get();
        Assert.assertEquals("one " + MAX_START + "two" + END + " three four five", result);

        result = new Answer("five", "five").highlightIn(grain).get();
        Assert.assertEquals("one two three four " + MAX_START + "five" + END, result);

        result = new Answer("one two", "one two").highlightIn(grain).get();
        Assert.assertEquals(MAX_START + "one two" + END + " three four five", result);

        result = new Answer("three four", "three four").highlightIn(grain).get();
        Assert.assertEquals("one two " + MAX_START + "three four" + END + " five", result);

        result = new Answer("four five", "four five").highlightIn(grain).get();
        Assert.assertEquals("one two three " + MAX_START + "four five" + END, result);
    }

    @Test
    public void highlightAnswerOnGrainMinUnlikeMax() {
        String grain = "one two three four five";
        String result;
        result = new Answer("one", "one two").highlightIn(grain).get();
        Assert.assertEquals(MAX_START + MIN_START + "one" + END + " two" + END + " three four five", result);

        result = new Answer("two", "one two").highlightIn(grain).get();
        Assert.assertEquals(MAX_START + "one " + MIN_START + "two" + END + END + " three four five", result);

        result = new Answer("four", "three four five").highlightIn(grain).get();
        Assert.assertEquals("one two " + MAX_START + "three " + MIN_START + "four" + END + " five" + END, result);
    }

    @Test
    public void highlightConsecutiveAnswers() {
        String grain = "one two three four five";
        String wheat = "one two  three   four    five";

        Riddle riddle = new Riddle("", "", "");
        riddle.addAnswer(new Answer("one", "one two"));
        riddle.addAnswer(new Answer("two", "two three"));
        String actual = riddle.insert(grain, wheat);
        String expected = wrapMax(wrapMin("one") + " " + wrapMin("two") + "  three") + "   four    five";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void highlightGrainWithHtml() {
        String grain = "<html> <head> </head> <body> </body></html>";
        String wheat = "<html>" +
                "    <head>" +
                "    </head>" +
                "    <body>" +
                "    </body>" +
                "</html>";
        Riddle riddle = new Riddle("", "любой тэг", "");
        riddle.addAnswer(new Answer("<html>", "<html>"));
        riddle.addAnswer(new Answer("<head>", "<head>"));
        riddle.addAnswer(new Answer("</head>", "</head>"));
        riddle.addAnswer(new Answer("<body>", "<body>"));
        riddle.addAnswer(new Answer("</body>", "</body>"));
        riddle.addAnswer(new Answer("</html>", "</html>"));

        String result = riddle.insert(HtmlUtils.htmlEscape(grain), HtmlUtils.htmlEscape(wheat));
        Assert.assertEquals(wrapMax(wrapMin("&lt;html&gt;")) + "    " +
                wrapMax(wrapMin("&lt;head&gt;")) + "    " +
                wrapMax(wrapMin("&lt;/head&gt;")) + "    " +
                wrapMax(wrapMin("&lt;body&gt;")) + "    " +
                wrapMax(wrapMin("&lt;/body&gt;&lt;/html&gt;")), result);
    }

    @Test
    public void highlightAllPossibleAnswers() {
        String grain = "one one one one";
        String wheat = "one  one  one  one";
        Riddle riddle = new Riddle("", "one", "");
        riddle.addAnswer(new Answer("one", "one"));

        String result = riddle.insert(HtmlUtils.htmlEscape(grain), HtmlUtils.htmlEscape(wheat));
        Assert.assertEquals(wrapMax(wrapMin("one")) + "  " +
                wrapMax(wrapMin("one")) + "  " +
                wrapMax(wrapMin("one")) + "  " +
                wrapMax(wrapMin("one")), result);
    }

    @Test
    public void endOfAnswerShouldBeAfterBegin() {
        String grain = "two one two three two";
        String wheat = "two one two  three   two";
        Riddle riddle = new Riddle("", "one", "");
        riddle.addAnswer(new Answer("one two", "one two"));
        String result = riddle.insert(HtmlUtils.htmlEscape(grain), HtmlUtils.htmlEscape(wheat));
        Assert.assertEquals("two " + wrapMax(wrapMin("one two")) + "  three   two", result);
    }
}
