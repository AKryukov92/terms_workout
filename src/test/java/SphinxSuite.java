import org.junit.Assert;
import org.junit.Test;
import ru.ominit.diskops.RiddleLoaderService;
import ru.ominit.model.Sphinx;
import ru.ominit.model.Verdict;
import ru.ominit.model.VerdictDecision;

import java.util.Random;

/**
 * @author akryukov
 * 03.04.2018
 */
public class SphinxSuite {
    private RiddleLoaderService loader;
    private Random random;
    private Sphinx sphinx;

    private String wheat1662 = "using System;\n" +
            "using System.Collections.Generic;\n" +
            "using System.Linq;\n" +
            "using System.Text;\n" +
            "using System.Threading.Tasks;\n" +
            "\n" +
            "namespace Example\n" +
            "{\n" +
            "    class Program\n" +
            "    {\n" +
            "        static void Main(string[] args)\n" +
            "        {\n" +
            "            Console.Write(\"Ура!\\n\\\"Заработало\\\"!\");\n" +
            "        }\n" +
            "    }\n" +
            "}";
    private String grain1662 = wheat1662.replaceAll("\\s+", " ");

    public SphinxSuite() {
        loader = new RiddleLoaderService();
        random = new Random();
        sphinx = new Sphinx(loader, random);
    }

    @Test
    public void tryToLoadRiddleNotPresentInHaystack(){
        String riddleId = "a90236d6-c08e-4da7-89d1-234cde20abef";
        String haystackId = "1860";
        Verdict verdict = sphinx.decide(haystackId, riddleId, "whatever");
        Assert.assertEquals(verdict.decision, VerdictDecision.IRRELEVANT);
    }

    @Test
    public void correctAttemptFullAnswer() {
        String lastRiddleId = "c9e9f0f9-336c-4a90-bb74-a9e9d38ae995";
        String lastHaystackId = "1662";
        String attempt = "static void Main(string[] args)\n        {";
        Verdict verdict = sphinx.decide(lastHaystackId, lastRiddleId, attempt);
        Assert.assertEquals(verdict.decision, VerdictDecision.CORRECT);
    }

    @Test
    public void correctAttemptShortAnswer() {
        String lastRiddleId = "c9e9f0f9-336c-4a90-bb74-a9e9d38ae995";
        String lastHaystackId = "1662";
        String attempt = "Main";
        Verdict verdict = sphinx.decide(lastHaystackId, lastRiddleId, attempt);
        Assert.assertEquals(verdict.decision, VerdictDecision.CORRECT);
    }

    @Test
    public void incorrectAttemptRelevantAnswer() {
        String lastRiddleId = "c9e9f0f9-336c-4a90-bb74-a9e9d38ae995";
        String lastHaystackId = "1662";
        String attempt = "using System;";
        Verdict verdict = sphinx.decide(lastHaystackId, lastRiddleId, attempt);
        Assert.assertEquals(verdict.decision, VerdictDecision.INCORRECT);
    }

    @Test
    public void incorrectAttemptIrrelevantAnswer() {
        String lastRiddleId = "c9e9f0f9-336c-4a90-bb74-a9e9d38ae995";
        String lastHaystackId = "1662";
        String attempt = "irrelevantAnswer";
        Verdict verdict = sphinx.decide(lastHaystackId, lastRiddleId, attempt);
        Assert.assertEquals(verdict.decision, VerdictDecision.IRRELEVANT);
    }

    @Test
    public void recognizeCorrectMultilineAnswerWithCRLF() {
        String riddleId = "a90236d6-c08e-4da7-89d1-234cde20abef";
        String haystackId = "9231";
        String attempt = "t = Console.ReadLine();\r\n            x = double.Parse(t);";
        Verdict verdict = sphinx.decide(haystackId, riddleId, attempt);
        Assert.assertEquals(verdict.decision, VerdictDecision.CORRECT);
    }

    @Test
    public void acceptAnyOfAnswers(){
        String riddleId = "027eb6c1-d242-48a1-9fa8-6b3671df71c9";
        String haystackId = "1860";
        String firstAttempt = "String first";
        Verdict firstVerdict = sphinx.decide(haystackId, riddleId, firstAttempt);
        Assert.assertEquals(firstVerdict.decision, VerdictDecision.CORRECT);

        String secondAttempt = "String second";
        Verdict secondVerdict = sphinx.decide(haystackId, riddleId, secondAttempt);
        Assert.assertEquals(secondVerdict.decision, VerdictDecision.CORRECT);
    }
}
