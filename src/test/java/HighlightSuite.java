import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.model.Answer;
import ru.ominit.model.Riddle;

import java.util.Arrays;
import java.util.List;

import static org.springframework.web.util.HtmlUtils.htmlEscape;
import static ru.ominit.highlight.EscapedHtmlString.make;
import static ru.ominit.highlight.HighlightRange.*;

public class HighlightSuite {
    @Test
    public void test2() {
        EscapedHtmlString wheat = make("one two  three   four    five");
        Riddle riddle = new Riddle("", "", "");
        riddle.addAnswer(new Answer("one", "one two three", "one two  three   f"));
        String actual = riddle.insert(wheat);
        String expected = wrapMax(wrapMin("one") + " two  three") + "   four    five";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testJoinAnswerRanges() {
        EscapedHtmlString wheat = make("one two  three   four    five");
        Riddle riddle = new Riddle("", "", "");
        riddle.addAnswer(new Answer("one two", "one two  th"));
        riddle.addAnswer(new Answer("four five", "e   four    five"));
        riddle.addAnswer(new Answer("two three four", "one two  three   four    "));
        String actual = riddle.insert(wheat);
        String expected = wrapMax(wrapMin("one two  three   four    five"));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void highlightConsecutiveMaxFragments() {
        EscapedHtmlString wheat = make("one two  three   four    five");

        Riddle riddle = new Riddle("", "", "");
        riddle.addAnswer(new Answer("one", "one two", "one two  th"));
        riddle.addAnswer(new Answer("two", "two three", "two  three   f"));
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
        riddle.addAnswer(new Answer("<html>", "<html>", "<html>\n   "));
        riddle.addAnswer(new Answer("<head>", "<head>", "    <head>\n   "));
        riddle.addAnswer(new Answer("</head>", "</head>", "    </head>\n   "));
        riddle.addAnswer(new Answer("<body>", "<body>", "    <body>\n   "));
        riddle.addAnswer(new Answer("</body>", "</body>", "    </body>\n   "));
        riddle.addAnswer(new Answer("</html>", "</html>", "dy>\n</html>"));

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
        riddle.addAnswer(new Answer("one", "one", "ne  one  on"));

        String result = riddle.insert(wheat);
        Assert.assertEquals("one" + "  " +
                wrapMax(wrapMin("one")) + "  " +
                wrapMax(wrapMin("one")) + "  " +
                "one", result);
    }

    @Test
    public void shouldHighlightOnlyFragmentWithWhitespaces() {
        EscapedHtmlString wheat = make("abcd a b c d");
        Riddle riddle = new Riddle("", "sequence of chars", "");
        riddle.addAnswer(new Answer("a b c d", "bcd a b c d"));
        String result = riddle.insert(wheat);
        String expected = "abcd " + wrapMax(wrapMin("a b c d"));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void endOfAnswerShouldBeAfterBegin() {
        EscapedHtmlString wheat = make("two one two  three   two");
        Riddle riddle = new Riddle("", "one", "");
        riddle.addAnswer(new Answer("one two", "one two", "two one two  th"));
        String result = riddle.insert(wheat);
        String expected = "two " + wrapMax(wrapMin("one two")) + "  three   two";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void highlightElementWithSimilarBeginning() {
        EscapedHtmlString wheat = make("<div id=\"result\"></div>" +
                "           <div id=\"list_container\"></div>");
        Riddle riddle = new Riddle("", "элемент с идентификатором list_container", "");
        riddle.addAnswer(new Answer(
                "<div id=\"list_container\"></div>",
                "<div id=\"list_container\"></div>",
                "    <div id=\"list_container\"></div>"
                ));
        List<HighlightRange> actual = riddle.extractRanges(wheat.getGrain(), Answer::getMaximalFragments);
        HighlightRange.joinRanges(actual);
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(44, 96)
        );
        Assert.assertEquals(expected, actual);

        String actualString = riddle.insert(wheat);
        String expectedString = make("<div id=\"result\"></div>") +
                "           " + wrapMax(wrapMin(make("<div id=\"list_container\"></div>").toString()));
        Assert.assertEquals(expectedString, actualString);
    }

    @Test
    public void highlightElementWithAmbiguousEnd() {
        EscapedHtmlString wheat = make("function convert(element, index){\n" +
                "  return <li key={index}>Элемент {element}</li>;\n" +
                "}");
        Riddle riddle = new Riddle("", "функцию для формирования элемента списка", "");
        riddle.addAnswer(new Answer("function convert(element, index){ return <li key={index}>Элемент {element}</li>; }",
                "function convert(element, index){ return <li key={index}>Элемент {element}</li>; }"
        ));

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
        riddle.addAnswer(new Answer("a b c d", " c  a b c d"));
        String actual = riddle.insert(wheat);
        String expected = "a  a b  a b c  " + wrapMax(wrapMin("a b c d"));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void clarifyRangeTargetUnchanged() {
        EscapedHtmlString wheat = make("a b c d  a b c  a b  a");
        Riddle riddle = new Riddle("", "sequence of chars", "");
        riddle.addAnswer(new Answer("a b c d", "a b c d  a "));
        String actual = riddle.insert(wheat);
        String expected = wrapMax(wrapMin("a b c d")) + "  a b c  a b  a";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void highlightCaseX() {
        EscapedHtmlString wheat = make("for(int i = 0; i < arr.length");
        Riddle riddle = new Riddle("", "sequence of chars", "");
        riddle.addAnswer(new Answer("i = 0", "int i = 0;", "for(int i = 0; i <"));
        String actual = riddle.insert(wheat);
        String expected = "for(" + wrapMax("int " + wrapMin("i = 0") + ";") + EscapedHtmlString.make(" i < arr.length");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void highlighCaseX1() {
        //6b01cdc9-4ae4-4ce9-a91e-3f0ef5240e91
        //Если к заданию "запись значения в переменную" добавить ответ "i = i + 1",
        // то в режиме преподавателя не удается выделить текст
        //Воспроизвести не получилось
        EscapedHtmlString wheat = make("int i = 0; some text i = i + 1;");
        Riddle riddle = new Riddle("", "sequence of chars", "");
        riddle.addAnswer(new Answer("i = 0", "int i = 0;", "int i = 0; som"));
        riddle.addAnswer(new Answer("i = i + 1", "i = i + 1;", "ext i = i + 1;"));
        String actual = riddle.insert(wheat);
        String expected = wrapMax("int " + wrapMin("i = 0") + ";") + " some text " + wrapMax(wrapMin("i = i + 1") + ";");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void highlightCaseX2() {
        //7564d271-5f3e-433b-849c-6b4b829262ef
        //Если к заданию "уточнение пространства имен для идентификатора" добавить ответ "std::",
        // то в режиме преподавателя не удается выделить текст
        //Воспроизвести не пытался
        //При этом в режиме студента ответ считается правильным
        //При повторном запросе в режиме студента ответ подсвечивается правильно
    }

    @Test
    public void highlightCaseWithBrackets() {
        EscapedHtmlString wheat = make("int[] ints = new int[]{5, 5, 5, 5, 5};\n" +
                "        actual = task3946(ints);");
        Riddle riddle = new Riddle("", "выражение, которое передается в аргумент arr метода task3946", "");
        riddle.addAnswer(new Answer("ints", "(ints)", "946(ints);"));

        String actual = riddle.insert(wheat);
        Assert.assertEquals("int[] ints = new int[]{5, 5, 5, 5, 5};\n" +
                "        actual = task3946<span class=\"max\">(<span class=\"min\">ints</span>)</span>;", actual);
        //не должен выбрасывать исключение
    }

    @Test
    public void highlightMinAtTheEndOfMax() {
        EscapedHtmlString wheat = make("select model, speed, hd\n" +
                "from pc");
        Riddle riddle = new Riddle("", "поле таблицы pc", "");
        riddle.addAnswer(new Answer("model", "model,", "ect model, sp"));
        riddle.addAnswer(new Answer("speed", ", speed,", "odel, speed, hd\n"));
        riddle.addAnswer(new Answer("hd", ", hd", "peed, hd\nfro"));
        String actual = riddle.insert(wheat);
        Assert.assertEquals("select <span class=\"max\"><span class=\"min\">model</span>, <span class=\"min\">speed</span>, <span class=\"min\">hd</span></span>\n" +
                "from pc", actual);
    }

    @Test
    public void highlightCorrectMinIfMaxDiffers() {
        EscapedHtmlString wheat = make("select t.model from product t where product.model='whatever'");
        Riddle riddle = new Riddle("", "поле таблицы product", "");
        riddle.addAnswer(new Answer("model", "t.model", "ect t.model fro"));
        riddle.addAnswer(new Answer("model", "product.model", "ere product.model='wh"));
        String actual = riddle.insert(wheat);
        Assert.assertEquals("select <span class=\"max\">t.<span class=\"min\">model</span></span> from product t where <span class=\"max\">product.<span class=\"min\">model</span></span>=&#39;whatever&#39;", actual);
    }

    @Test
    public void reproduce() {
        EscapedHtmlString wheat = make("select p.maker, p.model, pc.price\n" +
                "from product p\n" +
                "inner join pc on p.model=pc.model");
        Riddle riddle = new Riddle("", "поле таблицы pc", "");
        riddle.addAnswer(new Answer("price", ", pc.price", "odel, pc.price\nfro"));
        riddle.addAnswer(new Answer("model", "=pc.model", "odel=pc.model"));
        riddle.insert(wheat);
    }

    @Test
    public void highlightStartOfLiteral() {
        EscapedHtmlString wheat = make("Console.Write(\"Ура!\\n\\\"Заработало\\\"!\");");
        Riddle riddle = new Riddle("", "Начало строкового литерала", "");
        riddle.addAnswer(new Answer("\"", "ite(\"Ура!"));
        List<HighlightRange> actualHlg = HighlightRange.highlightAll(
                wheat.getGrain(),
                riddle.listAnswers().get(0).getMinimalFragments(),
                riddle.listAnswers().get(0).getContextFragments()
        );
        List<HighlightRange> actualJoinedRange = riddle.extractRanges(wheat.getGrain(), Answer::getMaximalFragments);
        HighlightRange.joinRanges(actualJoinedRange);
        List<HighlightRange> expected = Arrays.asList(
                new HighlightRange(14, 20)
        );
        Assert.assertEquals(expected, actualHlg);
        Assert.assertEquals(expected, actualJoinedRange);

        String actualInserted = riddle.insert(wheat);
        Assert.assertEquals("Console.Write(" + wrapMax(wrapMin("&quot;")) + "Ура!\\n\\&quot;Заработало\\&quot;!&quot;);", actualInserted);
    }
}
