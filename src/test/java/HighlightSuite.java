import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.highlight.HighlightRangeType;
import ru.ominit.model.Answer;
import ru.ominit.model.Riddle;

import java.util.*;

import static org.springframework.web.util.HtmlUtils.htmlEscape;
import static ru.ominit.highlight.EscapedHtmlString.make;
import static ru.ominit.highlight.HighlightRange.*;
import static ru.ominit.highlight.HighlightRangeType.MAXIMAL;
import static ru.ominit.highlight.HighlightRangeType.MINIMAL;

public class HighlightSuite {

    @Test
    public void test() {
        String wheat = "one two  three   four    five";
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
        expected = wrapMax(wrapMin("one") + " two  three") + "   four    five";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void test2() {
        EscapedHtmlString wheat = make("one two  three   four    five");
        EscapedHtmlString grain = make("one two three four five");
        Answer answer = new Answer("one", "one two three");
        Optional<HighlightRange> maxOpt = answer.highlight(grain, MAXIMAL);
        Optional<HighlightRange> minOpt = answer.highlight(grain, MINIMAL);

        Optional<EscapedHtmlString> resultOpt = maxOpt
                .map(max -> max.clarify(grain, wheat))
                .map(max -> max.insert(wheat, HighlightRange.MAX_START, HighlightRange.END));

        Optional<EscapedHtmlString> step2Opt = minOpt
                .flatMap(min -> resultOpt.map(result -> min.clarify(grain, result)))
                .flatMap(m -> resultOpt.map(r -> m.insert(r, HighlightRange.MIN_START, HighlightRange.END)));
        String expected = wrapMax("one two  three") + "   four    five";
        String expected2 = wrapMax(wrapMin("one") + " two  three") + "   four    five";
        Assert.assertEquals(Optional.of(expected), resultOpt.map(Object::toString));
        Assert.assertEquals(Optional.of(expected2), step2Opt.map(Object::toString));
    }

    @Test
    public void testJoinAnswerRanges() {
        EscapedHtmlString wheat = make("one two  three   four    five");
        Riddle riddle = new Riddle("", "", "");
        riddle.addAnswer(new Answer("one two"));
        riddle.addAnswer(new Answer("four five"));
        riddle.addAnswer(new Answer("two three four"));
        List<HighlightRange> actual = riddle.extractRanges(wheat.splitByWhitespace(), MAXIMAL);
        HighlightRange.joinRanges(actual);
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(0, 19)
        );
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void highlightConsecutiveMaxFragments() {
        EscapedHtmlString wheat = make("one two  three   four    five");

        Riddle riddle = new Riddle("", "", "");
        riddle.addAnswer(new Answer("one", "one two"));
        riddle.addAnswer(new Answer("two", "two three"));
        String newActual = riddle.insert(wheat);

        String expected = wrapMax(wrapMin("one") + " " + wrapMin("two") + "  three") + "   four    five";
        Assert.assertEquals(expected, newActual);
    }

    @Test
    public void highlightGrainWithHtml() {
        EscapedHtmlString wheat = make("<html>" +
                "\n    <head>" +
                "\n    </head>" +
                "\n    <body>" +
                "\n    </body>" +
                "\n</html>");
        Riddle riddle = new Riddle("", "любой тэг", "");
        riddle.addAnswer(new Answer("<html>", "<html>"));
        riddle.addAnswer(new Answer("<head>", "<head>"));
        riddle.addAnswer(new Answer("</head>", "</head>"));
        riddle.addAnswer(new Answer("<body>", "<body>"));
        riddle.addAnswer(new Answer("</body>", "</body>"));
        riddle.addAnswer(new Answer("</html>", "</html>"));

        String result = riddle.insert(wheat);
        String expected = wrapMax(wrapMin(htmlEscape("<html>"))) +
                "\n    " + wrapMax(wrapMin(htmlEscape("<head>"))) +
                "\n    " + wrapMax(wrapMin(htmlEscape("</head>"))) +
                "\n    " + wrapMax(wrapMin(htmlEscape("<body>"))) +
                "\n    " + wrapMax(wrapMin(htmlEscape("</body>"))) +
                "\n" + wrapMax(wrapMin(htmlEscape("</html>")));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void highlightAllPossibleAnswers() {
        EscapedHtmlString wheat = make("one  one  one  one");
        Riddle riddle = new Riddle("", "one", "");
        riddle.addAnswer(new Answer("one", "one"));

        String result = riddle.insert(wheat);
        Assert.assertEquals(wrapMax(wrapMin("one")) + "  " +
                wrapMax(wrapMin("one")) + "  " +
                wrapMax(wrapMin("one")) + "  " +
                wrapMax(wrapMin("one")), result);
    }

    @Test
    public void shouldHighlightOnlyFragmentWithWhitespaces() {
        EscapedHtmlString wheat = make("abcd a b c d");
        Riddle riddle = new Riddle("", "sequence of chars", "");
        riddle.addAnswer(new Answer("a b c d"));
        String result = riddle.insert(wheat);
        String expected = "abcd " + wrapMax(wrapMin("a b c d"));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void endOfAnswerShouldBeAfterBegin() {
        EscapedHtmlString wheat = make("two one two  three   two");
        Riddle riddle = new Riddle("", "one", "");
        riddle.addAnswer(new Answer("one two", "one two"));
        String result = riddle.insert(wheat);
        String expected = "two " + wrapMax(wrapMin("one two")) + "  three   two";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void highlightElementWithSimilarBeginning() {
        EscapedHtmlString wheat = make("<div id=\"result\"></div>" +
                "           <div id=\"list_container\"></div>");
        Riddle riddle = new Riddle("", "элемент с идентификатором list_container", "");
        riddle.addAnswer(new Answer("<div id=\"list_container\"></div>", "<div id=\"list_container\"></div>"));
        List<HighlightRange> actual = riddle.extractRanges(wheat.splitByWhitespace(), MAXIMAL);
        HighlightRange.joinRanges(actual);
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(44, 96)
        );
        Assert.assertEquals(expected, actual);

        String actualString = riddle.insert(wheat);
        String expectedString = make("<div id=\"result\"></div>").toString() +
                "           " + wrapMax(wrapMin(make("<div id=\"list_container\"></div>").toString()));
        Assert.assertEquals(expectedString, actualString);
    }

    @Test
    public void highlightElementWithAmbiguousEnd() {
        EscapedHtmlString wheat = make("function convert(element, index){\n" +
                "  return <li key={index}>Элемент {element}</li>;\n" +
                "}");
        Riddle riddle = new Riddle("", "функцию для формирования элемента списка", "");
        riddle.addAnswer(new Answer("function convert(element, index){ return <li key={index}>Элемент {element}</li>; }"));

        String actualResult = riddle.insert(wheat);
        String expectedResult = wrapMax(wrapMin("function convert(element, index){\n" +
                "  return &lt;li key={index}&gt;Элемент {element}&lt;/li&gt;;\n" +
                "}"));
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void clarifyRangeTargetAtEnd() {
        EscapedHtmlString wheat = make("a  a b  a b c  a b c d");
        Riddle riddle = new Riddle("", "sequence of chars", "");
        riddle.addAnswer(new Answer("a b c d"));
        List<HighlightRange> actual = riddle.extractRanges(wheat.splitByWhitespace(), MAXIMAL);
        HighlightRange.joinRanges(actual);
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(6, 10)
        );
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void clarifyRangeTargetUnchanged() {
        EscapedHtmlString wheat = make("a b c d  a b c  a b  a");
        Riddle riddle = new Riddle("", "sequence of chars", "");
        riddle.addAnswer(new Answer("a b c d"));
        List<HighlightRange> actual = riddle.extractRanges(wheat.splitByWhitespace(), MAXIMAL);
        HighlightRange.joinRanges(actual);
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(0, 4)
        );
        Assert.assertEquals(expected, actual);
    }
}
