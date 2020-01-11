import org.junit.Assert;
import org.junit.Test;
import ru.ominit.RiddleLoaderService;
import ru.ominit.model.*;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

/**
 * Created by Александр on 01.04.2018.
 */
public class LoaderTest {
    @Test
    public void canMatchAllFields() throws IOException {
        RiddleLoaderService loader = new RiddleLoaderService();
        Random rnd = new Random();
        Optional<String> nextSphinxId = loader.getAnyHaystackId(rnd);
        Assert.assertTrue(nextSphinxId.isPresent());
        String id = nextSphinxId.get();
        Haystack haystack = loader.load(id);
        Riddle riddle = haystack.getRiddle(rnd);
        Assert.assertNotNull(riddle);
    }

    @Test(expected = InsaneTaskException.class)
    public void failOnStartRiddleWithIrrelevantAnswer() {
        RiddleLoaderService loader = new RiddleLoaderService("src/test/resources/haystacks");
        Sphinx sphinx = new Sphinx(loader, new Random());
        String riddleId = "8cd03f79-94c5-4471-ab74-bb0d1d6c1f8a";
        String haystackId = "irrelevant_answer";
        sphinx.decide(haystackId, riddleId);
    }

    @Test(expected = InsaneTaskException.class)
    public void failOnContinueRiddleWithIrrelevantAnswer() {
        RiddleLoaderService loader = new RiddleLoaderService("src/test/resources/haystacks");
        Sphinx sphinx = new Sphinx(loader, new Random());
        String correctRiddleId = "8da885a8-ba65-406b-8398-e314d7539491";
        String haystackId = "irrelevant_answer";
        String correctAttempt = "class Program";
        sphinx.decide(haystackId, correctRiddleId, correctAttempt);
    }

    @Test(expected = MetaFileMissingException.class)
    public void failOnLoadAnythingInDirectoryWithoutMeta() {
        RiddleLoaderService loader = new RiddleLoaderService("src/test/resources/directory_without_meta");
        loader.getAnyHaystackId(new Random());
    }
}
