import org.junit.Assert;
import org.junit.Test;
import ru.ominit.highlight.EscapedHtmlString;

public class EscapedHtmlStringSuite {
    @Test
    public void testCreation() {
        EscapedHtmlString actual = EscapedHtmlString.make("<html>");
        String expected = "&lt;html&gt;";
        Assert.assertEquals(expected, actual.toString());
    }

    @Test
    public void testSubstring() {
        EscapedHtmlString initial = EscapedHtmlString.make("<html><head></head></html>");
        EscapedHtmlString actual = initial.substring(0, 15);
        String expected = "&lt;html&gt;&lt";
        Assert.assertEquals(expected, actual.toString());
    }

    @Test
    public void testSubstringSingleParam() {
        EscapedHtmlString initial = EscapedHtmlString.make("<html>");
        EscapedHtmlString actual = initial.substring(3);
        String expected = ";html&gt;";
        Assert.assertEquals(expected, actual.toString());
    }

    @Test
    public void testSearchNonWhitespace(){
        EscapedHtmlString initial = EscapedHtmlString.make("<html>   <head>");
        //&lt;html&gt;   &lt;head&gt;
        Assert.assertEquals(1, initial.indexOfNextNonWhitespace(0));
        Assert.assertEquals(15, initial.indexOfNextNonWhitespace(12));
        Assert.assertEquals(26, initial.indexOfNextNonWhitespace(25));
        Assert.assertEquals(-1, initial.indexOfNextNonWhitespace(26));
    }
}
