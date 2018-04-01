import org.junit.Assert;
import org.junit.Test;
import ru.ominit.RiddleLoader;
import ru.ominit.model.Riddle;
import ru.ominit.model.Sphinx;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Александр on 01.04.2018.
 */
public class LoaderTest {
    @Test
    public void canMatchAllFields() throws IOException {
        RiddleLoader loader = new RiddleLoader();
        Random rnd = new Random();
        String nextSphinxId = loader.getAnySphinxId(rnd);
        Sphinx sphinx = loader.load(nextSphinxId);
        Riddle riddle = sphinx.getRiddle(rnd);
        Assert.assertNotNull(riddle);
    }
}
