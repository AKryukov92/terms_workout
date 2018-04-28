import org.junit.Assert;
import org.junit.Test;
import ru.ominit.RiddleLoader;
import ru.ominit.model.Fate;
import ru.ominit.model.Riddle;
import ru.ominit.model.Haystack;
import ru.ominit.model.Sphinx;
import ru.ominit.model.Verdict;

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

    @Test(expected = Error.class)
    public void failOnStartRiddleWithIrrelevantAnswer() throws IOException {
        RiddleLoader loader = new RiddleLoader("src/test/resources/haystacks");
        Sphinx sphinx = new Sphinx(loader, new Random());
        String riddleId = "8cd03f79-94c5-4471-ab74-bb0d1d6c1f8a";
        String haystackId = "irrelevant_answer";
        sphinx.decide(haystackId, riddleId);
    }

    @Test(expected = Error.class)
    public void failOnContinueRiddleWithIrrelevantAnswer() throws IOException {
        RiddleLoader loader = new RiddleLoader("src/test/resources/haystacks");
        Sphinx sphinx = new Sphinx(loader, new Random());
        String riddleId = "8da885a8-ba65-406b-8398-e314d7539491";
        sphinx.decide("irrelevant_answer", riddleId, "Program");
    }
}
