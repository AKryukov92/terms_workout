package ru.ominit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.ominit.model.Riddle;
import ru.ominit.model.Sphinx;
import ru.ominit.model.Verdict;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Александр on 30.03.2018.
 */
@Controller
public class SphinxController {
    private static String SPHINX_VIEW_NAME = "sphinx";
    private static String LAST_RIDDLE_ATTR = "last_riddle";
    private static String LAST_SHPINX_ATTR = "last_sphinx";
    private static String MODEL_ATTR_VERDICT = "verdict";
    private static String MODEL_ATTR_HAYSTACK = "haystack";
    private static String MODEL_ATTR_NEXT_RIDDLE = "next_riddle";

    @Autowired
    private RiddleLoader loader;
    private Random random = new Random();

    @GetMapping("/sphinx")
    public String initial(
            Model model,
            HttpSession session,
            @ModelAttribute("sphinx") String sphinxId,
            @ModelAttribute("riddle") String riddleId
    ) throws IOException {
        Riddle nextRiddle;
        Sphinx nextSphinx;
        String nextSphinxId = sphinxId;
        if (sphinxId == null || sphinxId.isEmpty()) {
            nextSphinxId = loader.getAnySphinxId(random);
        }
        nextSphinx = loader.load(nextSphinxId);
        if (riddleId == null || riddleId.isEmpty()){
            nextRiddle = nextSphinx.getRiddle(random);
        } else {
            nextRiddle = nextSphinx.getRiddle(riddleId, random);
        }
        model.addAttribute(MODEL_ATTR_VERDICT, Verdict.makeFresh());
        model.addAttribute(MODEL_ATTR_HAYSTACK, nextSphinx.getHaystack());
        model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, nextRiddle);
        session.setAttribute(LAST_RIDDLE_ATTR, nextRiddle.getId());
        session.setAttribute(LAST_SHPINX_ATTR, nextSphinxId);
        return SPHINX_VIEW_NAME;
    }

    @PostMapping("/sphinx")
    public String answer(
            @ModelAttribute("attempt") String attempt,
            Model model,
            HttpSession session
    ) throws IOException {
        String lastRiddleId = (String) session.getAttribute(LAST_RIDDLE_ATTR);
        String lastSphinxId = (String) session.getAttribute(LAST_SHPINX_ATTR);
        Riddle lastRiddle;
        Sphinx lastSphinx;
        if (lastSphinxId == null || lastSphinxId.isEmpty()) {
            lastSphinxId = loader.getAnySphinxId(random);
        }
        lastSphinx = loader.load(lastSphinxId);
        if (lastRiddleId == null || lastRiddleId.isEmpty()){
            lastRiddle = lastSphinx.getRiddle(random);
        } else {
            lastRiddle = lastSphinx.getRiddle(lastRiddleId, random);
        }
        Verdict verdict = lastSphinx.decide(lastRiddle, attempt);
        model.addAttribute(MODEL_ATTR_VERDICT, verdict);
        if (verdict.correct) {
            String nextSphinxId = loader.getAnySphinxId(random);
            Sphinx nextSphinx = loader.load(nextSphinxId);
            Riddle nextRiddle = nextSphinx.getRiddle(lastRiddleId, random);
            model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, nextRiddle);
            model.addAttribute(MODEL_ATTR_HAYSTACK, nextSphinx.getHaystack());
            session.setAttribute(LAST_RIDDLE_ATTR, nextRiddle.getId());
            session.setAttribute(LAST_SHPINX_ATTR, nextSphinxId);
        } else {
            model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, lastRiddle);
            model.addAttribute(MODEL_ATTR_HAYSTACK, lastSphinx.getHaystack());
            session.setAttribute(LAST_RIDDLE_ATTR, lastRiddleId);
            session.setAttribute(LAST_SHPINX_ATTR, lastSphinxId);
        }
        return SPHINX_VIEW_NAME;
    }
}
