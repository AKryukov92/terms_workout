import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.model.Riddle;

import java.util.ArrayList;
import java.util.List;

public class AppendTokensSuite {
    @Test
    public void test1() {//интервал внутри одного фрагмента grain
        List<String> tokenList = new ArrayList<>();
        EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
        EscapedHtmlString[] grain = wheat.split("\\s+");

        Riddle.appendTokens(tokenList, grain, wheat, 0, 2);//С начала первого фрагмента до середины
        Assert.assertEquals("on", tokenList.get(0));
        Riddle.appendTokens(tokenList, grain, wheat, 1, 2);//В середине фрагмента
        Assert.assertEquals("n", tokenList.get(1));
        Riddle.appendTokens(tokenList, grain, wheat, 3, 5);//С начала не первого фрагмента до середины
        Assert.assertEquals("tw", tokenList.get(2));
        Riddle.appendTokens(tokenList, grain, wheat, 8, 11);//С середины до конца не первого фрагмента
        Assert.assertEquals("ree", tokenList.get(3));
        Riddle.appendTokens(tokenList, grain, wheat, 15, 19);//От начала до конца фрагмента
        Assert.assertEquals("five", tokenList.get(4));
    }

    @Test
    public void test2() {//интервал внутри двух фрагментов grain
        List<String> tokenList = new ArrayList<>();
        EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
        EscapedHtmlString[] grain = wheat.split("\\s+");
        Riddle.appendTokens(tokenList, grain, wheat, 0, 6);
        Assert.assertEquals("one", tokenList.get(0));
        Assert.assertEquals(" ", tokenList.get(1));
        Assert.assertEquals("two", tokenList.get(2));

    }

    @Test
    public void test3() {//интервал внутри всех фрагментов grain
        List<String> tokenList = new ArrayList<>();
        EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
        EscapedHtmlString[] grain = wheat.split("\\s+");
        Riddle.appendTokens(tokenList, grain, wheat, 2, 17);
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
        EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
        EscapedHtmlString[] grain = wheat.split("\\s+");
        Riddle.appendTokens(tokenList, grain, wheat, 4, 14);
        Assert.assertEquals("wo", tokenList.get(0));
        Assert.assertEquals("  ", tokenList.get(1));
        Assert.assertEquals("three", tokenList.get(2));
        Assert.assertEquals("   ", tokenList.get(3));
        Assert.assertEquals("fou", tokenList.get(4));
    }

    @Test
    public void test5() {//интервал из нескольких фрагментов grain и заканчивается в конце wheat
        List<String> tokenList = new ArrayList<>();
        EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
        EscapedHtmlString[] grain = wheat.split("\\s+");
        Riddle.appendTokens(tokenList, grain, wheat, 13, 19);

        Assert.assertEquals("ur", tokenList.get(0));
        Assert.assertEquals("    ", tokenList.get(1));
        Assert.assertEquals("five", tokenList.get(2));
    }

    @Test
    public void test6() {//интервал из нескольких фрагментов grain, начинается в начале, заканчивается в конце
        List<String> tokenList = new ArrayList<>();
        EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
        EscapedHtmlString[] grain = wheat.split("\\s+");
        Riddle.appendTokens(tokenList, grain, wheat, 3, 12);
        Assert.assertEquals("two", tokenList.get(0));
        Assert.assertEquals("  ", tokenList.get(1));
        Assert.assertEquals("three", tokenList.get(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test7() {//интервал меньше wheat, больше grain
        List<String> tokenList = new ArrayList<>();
        EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
        EscapedHtmlString[] grain = wheat.split("\\s+");
        Riddle.appendTokens(tokenList, grain, wheat, 3, 28);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test8() {//интервал больше wheat, больше grain
        List<String> tokenList = new ArrayList<>();
        EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
        EscapedHtmlString[] grain = wheat.split("\\s+");
        Riddle.appendTokens(tokenList, grain, wheat, 3, 1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test9() {//интервал нулевой длины
        List<String> tokenList = new ArrayList<>();
        EscapedHtmlString wheat = EscapedHtmlString.make("one two  three   four    five");
        EscapedHtmlString[] grain = wheat.split("\\s+");
        Riddle.appendTokens(tokenList, grain, wheat, 3, 3);
    }
}
