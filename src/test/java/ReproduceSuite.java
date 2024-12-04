import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ominit.diskops.RiddleLoaderService;
import ru.ominit.model.*;

import java.io.IOException;

public class ReproduceSuite {
    private Logger logger = LoggerFactory.getLogger(ReproduceSuite.class);

    @Test
    public void validateSingleHaystack() throws IOException {
        RiddleLoaderService loaderService = new RiddleLoaderService("./resources/haystacks/");
        Haystack haystack = loaderService.load("a958ecc9-56a6-494f-a8f2-7633d8b5084f");
        for (Riddle r : haystack.listRiddles()) {
            r.assertRelevant(haystack.getGrain());
        }
    }

    @Test
    public void reproduce() throws IOException {
    }

    @Test
    public void testRelevancyOfAnswer(){//ответ в самом начала текста
        Answer answer = new Answer("C:\\Windows\\system32", "C:\\Windows\\system32>", context);
        String[] grain = "C:\\Windows\\system32>netstat -a -b".split("\\s+");
        Assert.assertTrue(answer.relevantTo(grain));
    }
}
