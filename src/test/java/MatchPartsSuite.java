import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.model.Haystack;

public class MatchPartsSuite {
    @Test
    public void test0() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("a  a b  a b c  a b c d").getGrain();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("a b c d").getGrain();
        Assert.assertEquals(6, HighlightRange.indexOfInArr(arr, subarr, 0));
    }

    @Test
    public void test1() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("a  ab  ab c  ab cd").getGrain();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("ab cd").getGrain();
        Assert.assertEquals(6, HighlightRange.indexOfInArr(arr, subarr, 0));
    }

    @Test
    public void test2() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("a a a b c d").getGrain();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("a b c d").getGrain();
        Assert.assertEquals(2, HighlightRange.indexOfInArr(arr, subarr, 0));
    }

    @Test
    public void test3() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("a b c d").getGrain();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("e").getGrain();
        Assert.assertEquals(-1, HighlightRange.indexOfInArr(arr, subarr, 0));
    }

    @Test
    public void test4() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("aaaaa bbbbb ccccc").getGrain();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("aa bbbbb cc").getGrain();
        Assert.assertEquals(3, HighlightRange.indexOfInArr(arr, subarr, 0));
        Assert.assertEquals(3, HighlightRange.indexOfInArr(arr, subarr, 1));
        Assert.assertEquals(3, HighlightRange.indexOfInArr(arr, subarr, 2));
        Assert.assertEquals(3, HighlightRange.indexOfInArr(arr, subarr, 3));
        Assert.assertEquals(-1, HighlightRange.indexOfInArr(arr, subarr, 4));
    }

    @Test
    public void test5() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("abc def ghi").getGrain();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("hi").getGrain();
        Assert.assertEquals(7, HighlightRange.indexOfInArr(arr, subarr, 0));
        Assert.assertEquals(7, HighlightRange.indexOfInArr(arr, subarr, 7));
        Assert.assertEquals(-1, HighlightRange.indexOfInArr(arr, subarr, 8));
    }

    @Test
    public void test6() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("aaaaa bbbbb ccccc").getGrain();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("cc dd").getGrain();
        Assert.assertEquals(-1, HighlightRange.indexOfInArr(arr, subarr, 0));
    }

    @Test
    public void test7() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("abcde").getGrain();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("bc").getGrain();
        Assert.assertEquals(1, HighlightRange.indexOfInArr(arr, subarr, 0));
        Assert.assertEquals(1, HighlightRange.indexOfInArr(arr, subarr, 1));
        Assert.assertEquals(-1, HighlightRange.indexOfInArr(arr, subarr, 2));
    }

    @Test
    public void test8() {
        String[] arr = "abc def ghi".split("\\s+");
        String[] subarr = "abc".split("\\s+");
        Assert.assertEquals(0, Haystack.indexOfInArr(arr, subarr, 0));
    }

    @Test
    public void test9() {
        String[] arr = "Main".split("\\s+");
        String[] subarr = "Main".split("\\s+");
        Assert.assertEquals(0, Haystack.indexOfInArr(arr, subarr, 0));
    }

    @Test
    public void test10() {
        String[] arr = "отало\\\"!\");".split("\\s+");
        //String[] subarr = "о\"!");";
    }

    @Test
    public void test01() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("<html>" +
                "    <head>" +
                "    </head>" +
                "    <body>" +
                "    </body>" +
                "</html>").getGrain();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("<html>").getGrain();
        Assert.assertEquals(0, HighlightRange.indexOfInArr(arr, subarr, 0));
    }
}
