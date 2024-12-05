import org.junit.Assert;
import org.junit.Test;
import ru.ominit.journey.HaystackProgress;
import ru.ominit.model.Answer;
import ru.ominit.model.Haystack;
import ru.ominit.model.Riddle;
import ru.ominit.model.Verdict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProgressSuite {
    Haystack haystack = new Haystack(
            "Console.Write(\"Ура!\\n\\\"Заработало\\\"!\");",
            Arrays.asList(
                    new Riddle("1", "команда вывода текста на экран", ""){{
                        addAnswer(new Answer("Console.Write(\"Ура!\\n\\\"Заработало\\\"!\")",
                                "Console.Write(\"Ура!\\n\\\"Заработало\\\"!\");",
                                "Console.Write(\"Ура!\\n\\\"Заработало\\\"!\");"));
                    }},
                    new Riddle("2", "спецсимвол для вывода кавычки", "") {{
                        addAnswer(new Answer("\\\"", "\\\"", "а!\\n\\\"Зара"));
                        addAnswer(new Answer("\\\"", "\\\"", "тало\\\"!\");"));
                    }},
                    new Riddle("3", "конец строкового литерала", ""){{
                        addAnswer(new Answer("\\\"", "\\\"", "о\\\"!\");"));
                    }}
            )
    );
    @Test
    public void test1() {
        List<Verdict> verdictList = Arrays.asList(

        );
        HaystackProgress progress = new HaystackProgress(haystack, "", verdictList);
        Assert.assertEquals(0, progress.currentProgress());
        Assert.assertEquals(0, progress.maxProgress());
        Assert.assertEquals(0, progress.currentAnswerProgress());
        Assert.assertEquals(0, progress.maxAnswerProgress());
    }
}
