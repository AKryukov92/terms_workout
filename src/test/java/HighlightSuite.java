import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.highlight.HighlightRangeType;
import ru.ominit.model.Answer;
import ru.ominit.model.Riddle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.ominit.highlight.HighlightRange.*;
import static ru.ominit.highlight.HighlightRangeType.MAXIMAL;
import static ru.ominit.highlight.HighlightRangeType.MINIMAL;

public class HighlightSuite {

    @Test
    public void test() {
        String wheat = "one two  three   four    five";
        String grain = "one two three four five";
        HighlightRange max = new HighlightRange(0, 13);
        HighlightRange min = new HighlightRange(0, 3);
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
        EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
        EscapedHtmlString grain = EscapedHtmlString.make("one two three four five");
        Answer answer = new Answer("one", "one two three");
        Optional<HighlightRange> maxOpt = answer.highlight(grain, MAXIMAL);
        Optional<HighlightRange> minOpt = answer.highlight(grain, MINIMAL);

        Optional<EscapedHtmlString> resultOpt = maxOpt
                .map(max -> max.clarify(grain, wheat))
                .map(max -> max.insert(wheat, HighlightRange.MAX_START, HighlightRange.END));

        Optional<EscapedHtmlString> step2Opt = minOpt
                .flatMap(min -> resultOpt.map(result -> min.clarify(grain, result)))
                .flatMap(m -> resultOpt.map(r -> m.insert(r, HighlightRange.MIN_START, HighlightRange.END)));
        String expected = MAX_START + "one two  three" + END + "   four    five";
        String expected2 = MAX_START + MIN_START + "one" + END + " two  three" + END + "   four    five";
        Assert.assertEquals(Optional.of(expected), resultOpt.map(Object::toString));
        Assert.assertEquals(Optional.of(expected2), step2Opt.map(Object::toString));
    }

    @Test
    public void testJoinAnswerRanges() {
        EscapedHtmlString grain = EscapedHtmlString.make("one two three four five");
        Riddle riddle = new Riddle("", "", "");
        riddle.addAnswer(new Answer("", "one two"));
        riddle.addAnswer(new Answer("", "four five"));
        riddle.addAnswer(new Answer("", "two three four"));
        List<HighlightRange> result = riddle.joinAnswerRanges(grain, MAXIMAL);
        Assert.assertEquals(Collections.singletonList(new HighlightRange(0, 23)), result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void highlightConsecutiveMaxFragments() {
        EscapedHtmlString grain = EscapedHtmlString.make("one two three four five");
        EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");

        Riddle riddle = new Riddle("", "", "");
        riddle.addAnswer(new Answer("one", "one two"));
        riddle.addAnswer(new Answer("two", "two three"));
        String actual = riddle.insert(grain, wheat);
        String expected = wrapMax(wrapMin("one") + " " + wrapMin("two") + "  three") + "   four    five";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void highlightGrainWithHtml() {
        EscapedHtmlString grain = EscapedHtmlString.make("<html> <head> </head> <body> </body></html>");
        EscapedHtmlString wheat = EscapedHtmlString.make("<html>" +
                "    <head>" +
                "    </head>" +
                "    <body>" +
                "    </body>" +
                "</html>");
        Riddle riddle = new Riddle("", "любой тэг", "");
        riddle.addAnswer(new Answer("<html>", "<html>"));
        riddle.addAnswer(new Answer("<head>", "<head>"));
        riddle.addAnswer(new Answer("</head>", "</head>"));
        riddle.addAnswer(new Answer("<body>", "<body>"));
        riddle.addAnswer(new Answer("</body>", "</body>"));
        riddle.addAnswer(new Answer("</html>", "</html>"));

        String result = riddle.insert(grain, wheat);
        Assert.assertEquals(wrapMax(wrapMin("&lt;html&gt;")) + "    " +
                wrapMax(wrapMin("&lt;head&gt;")) + "    " +
                wrapMax(wrapMin("&lt;/head&gt;")) + "    " +
                wrapMax(wrapMin("&lt;body&gt;")) + "    " +
                wrapMax(wrapMin("&lt;/body&gt;&lt;/html&gt;")), result);
    }

    @Test
    public void highlightAllPossibleAnswers() {
        EscapedHtmlString grain = EscapedHtmlString.make("one one one one");
        EscapedHtmlString wheat = EscapedHtmlString.make("one  one  one  one");
        Riddle riddle = new Riddle("", "one", "");
        riddle.addAnswer(new Answer("one", "one"));

        String result = riddle.insert(grain, wheat);
        Assert.assertEquals(wrapMax(wrapMin("one")) + "  " +
                wrapMax(wrapMin("one")) + "  " +
                wrapMax(wrapMin("one")) + "  " +
                wrapMax(wrapMin("one")), result);
    }

    @Test
    public void endOfAnswerShouldBeAfterBegin() {
        EscapedHtmlString grain = EscapedHtmlString.make("two one two three two");
        EscapedHtmlString wheat = EscapedHtmlString.make("two one two  three   two");
        Riddle riddle = new Riddle("", "one", "");
        riddle.addAnswer(new Answer("one two", "one two"));
        String result = riddle.insert(grain, wheat);
        Assert.assertEquals("two " + wrapMax(wrapMin("one two")) + "  three   two", result);
    }

    @Test
    public void highlightElementWithSimilarBeginning() {
        EscapedHtmlString grain = EscapedHtmlString.make("<div id=\"result\"></div>" +
                "<div id=\"list_container\"></div>");
        EscapedHtmlString wheat = EscapedHtmlString.make("<div id=\"result\"></div>" +
                "           <div id=\"list_container\"></div>");
        Riddle riddle = new Riddle("", "элемент с идентификатором list_container", "");
        riddle.addAnswer(new Answer("<div id=\"list_container\"></div>", "<div id=\"list_container\"></div>"));

        List<HighlightRange> actualRangesMax = riddle.joinAnswerRanges(grain, HighlightRangeType.MAXIMAL);
        List<HighlightRange> expectedRanges = new ArrayList<>();
        expectedRanges.add(new HighlightRange(45, 98));
        Assert.assertEquals(expectedRanges, actualRangesMax);

        HighlightRange maxRange = actualRangesMax.get(0);
        EscapedHtmlString modifiedWheat = maxRange
                .clarify(grain, wheat)
                .insert(wheat, HighlightRange.MAX_START, HighlightRange.END);
        String expectedAfterMaxInsertion = "&lt;div id=&quot;result&quot;&gt;&lt;/div&gt;           " +
                wrapMax("&lt;div id=&quot;list_container&quot;&gt;&lt;/div&gt;");
        Assert.assertEquals(expectedAfterMaxInsertion, modifiedWheat.toString());

        String result = riddle.insert(grain, wheat);
        String expectedResult = "&lt;div id=&quot;result&quot;&gt;&lt;/div&gt;           " +
                wrapMax(wrapMin("&lt;div id=&quot;list_container&quot;&gt;&lt;/div&gt;"));
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void highlightElementWithAmbiguousEnd() {
        EscapedHtmlString grain = EscapedHtmlString.make("function convert(element, index){ " +
                "return <li key={index}>Элемент {element}</li>; " +
                "}");
        EscapedHtmlString wheat = EscapedHtmlString.make("function convert(element, index){\n" +
                "  return <li key={index}>Элемент {element}</li>;\n" +
                "}");
        Riddle riddle = new Riddle("", "функцию для формирования элемента списка", "");
        riddle.addAnswer(new Answer("function convert(element, index){ return <li key={index}>Элемент {element}</li>; }"));


        String actualResult = riddle.insert(grain, wheat);
        String expectedResult = wrapMax(wrapMin("function convert(element, index){ return &lt;li key={index}>&gt;лемент {element}&lt;/li&gt;; }"));
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void clarifyRangeTargetAtEnd() {
        EscapedHtmlString grain = EscapedHtmlString.make("a a b a b c a b c d");
        EscapedHtmlString wheat = EscapedHtmlString.make("a  a b  a b c  a b c d");
        Riddle riddle = new Riddle("", "sequence of chars", "");
        riddle.addAnswer(new Answer("a b c d"));
        HighlightRange range = riddle.joinAnswerRanges(grain, HighlightRangeType.MAXIMAL).get(0);
        HighlightRange clarifiedRange = range.clarify(grain, wheat);
        HighlightRange expectedRange = new HighlightRange(15, 22);
        Assert.assertEquals(expectedRange, clarifiedRange);
    }

    @Test
    public void clarifyRangeTargetUnchanged() {
        EscapedHtmlString grain = EscapedHtmlString.make("a b c d a b c a b a");
        EscapedHtmlString wheat = EscapedHtmlString.make("a b c d  a b c  a b  a");
        Riddle riddle = new Riddle("", "sequence of chars", "");
        riddle.addAnswer(new Answer("a b c d"));
        HighlightRange range = riddle.joinAnswerRanges(grain, HighlightRangeType.MAXIMAL).get(0);
        HighlightRange clarifiedRange = range.clarify(grain, wheat);
        HighlightRange expectedrange = new HighlightRange(0, 7);
        Assert.assertEquals(expectedrange, clarifiedRange);
    }
}
