import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ominit.diskops.RiddleLoaderService;
import ru.ominit.journey.Journey;
import ru.ominit.journey.JourneyManager;
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
        ControlledRandom rnd = new ControlledRandom(new int[]{
                2, 8, 4, 2
        });
        String sessionId = "ED573532983508E0FAB0BE4BAD2D6900";
        JourneyManager journeyManager = new JourneyManager();
        journeyManager.createJourney(sessionId);
        Journey journey = journeyManager.getJourney(sessionId);
        RiddleLoaderService loaderService = new RiddleLoaderService("./resources/haystacks/");
        Sphinx sphinx = new Sphinx(loaderService, rnd);
        String lastHaystackId = "f82aa433-2af3-48bd-a801-3a0f77f3f7db";
        String lastRiddleId = "0b0824a1-382f-4e46-a694-a5bd7fccfb42";
        String attempt = "ReactDOM.render(\n" +
                "  <Hello name=\"World\" />,\n" +
                "  document.getElementById('result')\n" +
                ");";
        logger.info("Load session {} with haystackId {} and riddleId {}", sessionId, lastHaystackId, lastRiddleId);
        Verdict verdict = sphinx.decide(lastHaystackId, lastRiddleId, attempt);
        logger.info("User should select '{}' with attempt: \n{}\nit is {}.", verdict.past.getRiddle().getNeedle(), attempt, verdict.decision);
        journey.addStep(verdict, sessionId);

        attempt = "<Hello name=\"World\" />";
        logger.info("Load session {} with haystackId {} and riddleId {}", sessionId, verdict.future.getHaystackId(), verdict.future.getRiddleId());
        verdict = sphinx.decide(lastHaystackId, verdict.future.getRiddleId(), attempt);
        logger.info("User should select '{}' with attempt: \n{}\nit is {}.", verdict.past.getRiddle().getNeedle(), attempt, verdict.decision);
        journey.addStep(verdict, sessionId);

        attempt = "class Hello extends React.Component {\n" +
                "  render() {\n" +
                "    return <div>Hello {this.props.name}</div>;\n" +
                "  }\n" +
                "}";
        logger.info("Load session {} with haystackId {} and riddleId {}", sessionId, verdict.future.getHaystackId(), verdict.future.getRiddleId());
        verdict = sphinx.decide(lastHaystackId, verdict.future.getRiddleId(), attempt);
        logger.info("User should select '{}' with attempt: \n{}\nit is {}.", verdict.past.getRiddle().getNeedle(), attempt, verdict.decision);
        journey.addStep(verdict, sessionId);

        attempt = "ReactDOM.render(\n" +
                "  <Hello name=\"World\" />,\n" +
                "  document.getElementById('result')\n" +
                ");";
        logger.info("Load session {} with haystackId {} and riddleId {}", sessionId, verdict.future.getHaystackId(), verdict.future.getRiddleId());
        verdict = sphinx.decide(lastHaystackId, verdict.future.getRiddleId(), attempt);
        logger.info("User should select '{}' with attempt: \n{}\nit is {}.", verdict.past.getRiddle().getNeedle(), attempt, verdict.decision);
        journey.addStep(verdict, sessionId);

        attempt = "<div id=\"result\"></div>";
        logger.info("Load session {} with haystackId {} and riddleId {}", sessionId, verdict.future.getHaystackId(), verdict.future.getRiddleId());
        verdict = sphinx.decide(lastHaystackId, verdict.future.getRiddleId(), attempt);
        logger.info("User should select '{}' with attempt: \n{}\nit is {}.", verdict.past.getRiddle().getNeedle(), attempt, verdict.decision);
        journey.addStep(verdict, sessionId);

        attempt = "<div id=\"result\"></div>";
        logger.info("Load session {} with haystackId {} and riddleId {}", sessionId, verdict.future.getHaystackId(), verdict.future.getRiddleId());
        verdict = sphinx.decide(lastHaystackId, verdict.future.getRiddleId(), attempt);
        logger.info("User should select '{}' with attempt: \n{}\nit is {}.", verdict.past.getRiddle().getNeedle(), attempt, verdict.decision);
        journey.addStep(verdict, sessionId);

        attempt = "<div>Hello {this.props.name}</div>;";
        logger.info("Load session {} with haystackId {} and riddleId {}", sessionId, verdict.future.getHaystackId(), verdict.future.getRiddleId());
        verdict = sphinx.decide(lastHaystackId, verdict.future.getRiddleId(), attempt);
        logger.info("User should select '{}' with attempt: \n{}\nit is {}.", verdict.past.getRiddle().getNeedle(), attempt, verdict.decision);
        journey.addStep(verdict, sessionId);

        attempt = "<div>Hello {this.props.name}</div>";
        logger.info("Load session {} with haystackId {} and riddleId {}", sessionId, verdict.future.getHaystackId(), verdict.future.getRiddleId());
        verdict = sphinx.decide(lastHaystackId, verdict.future.getRiddleId(), attempt);
        logger.info("User should select '{}' with attempt: \n{}\nit is {}.", verdict.past.getRiddle().getNeedle(), attempt, verdict.decision);
        journey.addStep(verdict, sessionId);
    }

    @Test
    public void testRelevancyOfAnswer(){//ответ в самом начала текста
        Answer answer = new Answer("C:\\Windows\\system32", "C:\\Windows\\system32>");
        String[] grain = "C:\\Windows\\system32>netstat -a -b".split("\\s+");
        Assert.assertTrue(answer.relevantTo(grain));
    }
}
