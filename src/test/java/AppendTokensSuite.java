import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.highlight.PositionOfFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppendTokensSuite {
    private EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");

    @Test
    public void test1() {//интервал внутри одного фрагмента grain
        PositionOfFragment actualPos = wheat.find(15);
        PositionOfFragment expectedPos = new PositionOfFragment(4, 25, 15);
        Assert.assertEquals(expectedPos, actualPos);

        List<String> tokenList = new ArrayList<>();

        HighlightRange.appendTokens(tokenList, wheat, 0, 2);//С начала первого фрагмента до середины
        Assert.assertEquals("on", tokenList.get(0));
        HighlightRange.appendTokens(tokenList, wheat, 1, 2);//В середине фрагмента
        Assert.assertEquals("n", tokenList.get(1));
        HighlightRange.appendTokens(tokenList, wheat, 3, 5);//С начала не первого фрагмента до середины
        Assert.assertEquals("tw", tokenList.get(2));
        HighlightRange.appendTokens(tokenList, wheat, 8, 11);//С середины до конца не первого фрагмента
        Assert.assertEquals("ree", tokenList.get(3));
        HighlightRange.appendTokens(tokenList, wheat, 15, 19);//От начала до конца фрагмента
        Assert.assertEquals("five", tokenList.get(4));
    }

    @Test
    public void test2() {//интервал внутри двух фрагментов grain
        PositionOfFragment actualPos = wheat.find(0);
        PositionOfFragment expectedPos = new PositionOfFragment(0, 0, 0);
        Assert.assertEquals(actualPos, expectedPos);

        List<String> tokenList = new ArrayList<>();
        HighlightRange.appendTokens(tokenList, wheat, 0, 6);
        Assert.assertEquals("one", tokenList.get(0));
        Assert.assertEquals(" ", tokenList.get(1));
        Assert.assertEquals("two", tokenList.get(2));
    }

    @Test
    public void test3() {//интервал внутри всех фрагментов grain
        PositionOfFragment actualPos = wheat.find(2);
        PositionOfFragment expectedPos = new PositionOfFragment(0, 0, 0);
        Assert.assertEquals(actualPos, expectedPos);

        List<String> tokenList = new ArrayList<>();
        HighlightRange.appendTokens(tokenList, wheat, 2, 17);
        Assert.assertEquals("e", tokenList.get(0));
        Assert.assertEquals(" ", tokenList.get(1));
        Assert.assertEquals("two", tokenList.get(2));
        Assert.assertEquals("  ", tokenList.get(3));
        Assert.assertEquals("three", tokenList.get(4));
        Assert.assertEquals("   ", tokenList.get(5));
        Assert.assertEquals("four", tokenList.get(6));
        Assert.assertEquals("    ", tokenList.get(7));
        Assert.assertEquals("fi", tokenList.get(8));
    }

    @Test
    public void test4() {//интервал внутри фрагментов grain, кроме крайних
        List<String> tokenList = new ArrayList<>();
        HighlightRange.appendTokens(tokenList, wheat, 4, 14);
        Assert.assertEquals("wo", tokenList.get(0));
        Assert.assertEquals("  ", tokenList.get(1));
        Assert.assertEquals("three", tokenList.get(2));
        Assert.assertEquals("   ", tokenList.get(3));
        Assert.assertEquals("fou", tokenList.get(4));
    }

    @Test
    public void test5() {//интервал из нескольких фрагментов grain и заканчивается в конце wheat
        List<String> tokenList = new ArrayList<>();
        HighlightRange.appendTokens(tokenList, wheat, 13, 19);

        Assert.assertEquals("ur", tokenList.get(0));
        Assert.assertEquals("    ", tokenList.get(1));
        Assert.assertEquals("five", tokenList.get(2));
    }

    @Test
    public void test6() {//интервал из нескольких фрагментов grain, начинается в начале, заканчивается в конце
        PositionOfFragment actualPos = wheat.find(3);
        PositionOfFragment expectedPos = new PositionOfFragment(1, 4, 3);
        Assert.assertEquals(actualPos, expectedPos);

        List<String> tokenList = new ArrayList<>();
        HighlightRange.appendTokens(tokenList, wheat, 3, 12);
        Assert.assertEquals("two", tokenList.get(0));
        Assert.assertEquals("  ", tokenList.get(1));
        Assert.assertEquals("three", tokenList.get(2));
    }

    @Test
    public void test10() {//интервал нулевой длины в конце grain
        List<String> tokenList = new ArrayList<>();
        HighlightRange.appendTokens(tokenList, wheat, 19, 19);
        Assert.assertTrue(tokenList.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test7() {//интервал меньше wheat, больше grain
        List<String> tokenList = new ArrayList<>();
        HighlightRange.appendTokens(tokenList, wheat, 3, 28);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test8() {//интервал больше wheat, больше grain
        List<String> tokenList = new ArrayList<>();
        HighlightRange.appendTokens(tokenList, wheat, 3, 1000);
    }

    @Test
    public void test9() {//интервал нулевой длины
        List<String> tokenList = new ArrayList<>();
        HighlightRange.appendTokens(tokenList, wheat, 3, 3);
        Assert.assertTrue(tokenList.isEmpty());
    }

    @Test
    public void test12() {//в список токенов попадает текст без пробелов
        EscapedHtmlString wheat = EscapedHtmlString.make("abcd a b c d");

        PositionOfFragment actualPos = wheat.find(4);
        PositionOfFragment expectedPos = new PositionOfFragment(1, 5, 4);
        Assert.assertEquals(expectedPos, actualPos);

        List<String> tokenList = new ArrayList<>();
        HighlightRange.appendTokens(tokenList, wheat, 4, 8);
        List<String> expected = Arrays.asList(
                "a",
                " ",
                "b",
                " ",
                "c",
                " ",
                "d"
        );
        Assert.assertEquals(expected, tokenList);
    }

    @Test
    public void test13() {
        EscapedHtmlString wheat = EscapedHtmlString.make("for(int i = 0; i < arr.length");

        PositionOfFragment actualPos = wheat.find(7);
        PositionOfFragment expectedPos = new PositionOfFragment(1, 8, 7);
        Assert.assertEquals(expectedPos, actualPos);
    }

    @Test
    public void test14() {
        EscapedHtmlString wheat = EscapedHtmlString.make("aaiaai i");
        PositionOfFragment actualPos = wheat.find(6);
        PositionOfFragment expectedPos = new PositionOfFragment(1, 7, 6);
        Assert.assertEquals(expectedPos, actualPos);
    }
}
