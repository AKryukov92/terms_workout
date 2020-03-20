import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.model.Haystack;

public class MatchPartsSuite {
    @Test
    public void test0() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("a  a b  a b c  a b c d").splitByWhitespace();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("a b c d").splitByWhitespace();
        Assert.assertEquals(6, Haystack.indexOfInArr(arr, subarr, 0));
    }

    @Test
    public void test1() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("a  ab  ab c  ab cd").splitByWhitespace();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("ab cd").splitByWhitespace();
        Assert.assertEquals(6, Haystack.indexOfInArr(arr, subarr, 0));
    }

    @Test
    public void test2() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("a a a b c d").splitByWhitespace();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("a b c d").splitByWhitespace();
        Assert.assertEquals(2, Haystack.indexOfInArr(arr, subarr, 0));
    }

    @Test
    public void test3() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("a b c d").splitByWhitespace();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("e").splitByWhitespace();
        Assert.assertEquals(-1, Haystack.indexOfInArr(arr, subarr, 0));
    }

    @Test
    public void test4() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("aaaaa bbbbb ccccc").splitByWhitespace();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("aa bbbbb cc").splitByWhitespace();
        Assert.assertEquals(3, Haystack.indexOfInArr(arr, subarr, 0));
        Assert.assertEquals(3, Haystack.indexOfInArr(arr, subarr, 1));
        Assert.assertEquals(3, Haystack.indexOfInArr(arr, subarr, 2));
        Assert.assertEquals(3, Haystack.indexOfInArr(arr, subarr, 3));
        Assert.assertEquals(-1, Haystack.indexOfInArr(arr, subarr, 4));
    }

    @Test
    public void test5() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("abc def ghi").splitByWhitespace();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("hi").splitByWhitespace();
        Assert.assertEquals(7, Haystack.indexOfInArr(arr, subarr, 0));
        Assert.assertEquals(7, Haystack.indexOfInArr(arr, subarr, 7));
        Assert.assertEquals(-1, Haystack.indexOfInArr(arr, subarr, 8));
    }

    @Test
    public void test6() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("aaaaa bbbbb ccccc").splitByWhitespace();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("cc dd").splitByWhitespace();
        Assert.assertEquals(-1, Haystack.indexOfInArr(arr, subarr, 0));
    }

    @Test
    public void test7() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("abcde").splitByWhitespace();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("bc").splitByWhitespace();
        Assert.assertEquals(1, Haystack.indexOfInArr(arr, subarr, 0));
        Assert.assertEquals(1, Haystack.indexOfInArr(arr, subarr, 1));
        Assert.assertEquals(-1, Haystack.indexOfInArr(arr, subarr, 2));
    }

    @Test
    public void test8(){
        String[] arr = "abc def ghi".split("\\s+");
        String[] subarr = "abc".split("\\s+");
        Assert.assertEquals(0, Haystack.indexOfInArr(arr, subarr, 0));
    }

    @Test
    public void test01() {
        EscapedHtmlString[] arr = EscapedHtmlString.make("<html>" +
                "    <head>" +
                "    </head>" +
                "    <body>" +
                "    </body>" +
                "</html>").splitByWhitespace();
        EscapedHtmlString[] subarr = EscapedHtmlString.make("<html>").splitByWhitespace();
        Assert.assertEquals(0, Haystack.indexOfInArr(arr, subarr, 0));
    }
}
