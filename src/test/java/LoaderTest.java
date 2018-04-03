import org.junit.Assert;
import org.junit.Test;
import ru.ominit.RiddleLoader;
import ru.ominit.model.Riddle;
import ru.ominit.model.Haystack;

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
        String nextSphinxId = loader.getAnyHaystackId(rnd);
        Haystack haystack = loader.load(nextSphinxId);
        Riddle riddle = haystack.getRiddle(rnd);
        Assert.assertNotNull(riddle);
    }
}
