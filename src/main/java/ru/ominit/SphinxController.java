package ru.ominit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
    private Logger logger = LoggerFactory.getLogger(SphinxController.class);

    private static String SPHINX_VIEW_NAME = "sphinx";
    private static String LAST_RIDDLE_ATTR = "last_riddle";
    private static String LAST_HAYSTACK_ATTR = "last_haystack";
    private static String MODEL_ATTR_VERDICT = "verdict";
    private static String MODEL_ATTR_WHEAT = "wheat";
    private static String MODEL_ATTR_NEXT_RIDDLE = "next_riddle";

    @Autowired
    private Random random;

    @Autowired
    private RiddleLoader loader;

    @Autowired
    private Sphinx sphinx;

    @GetMapping("/sphinx")
    public String initial(
            Model model,
            HttpSession session,
            @ModelAttribute("haystack") String haystackId,
            @ModelAttribute("riddle") String riddleId
    ) throws IOException {
        logger.info("Receive GET /sphinx with haystackId {} and riddleId {}", haystackId, riddleId);
        Verdict verdict = sphinx.decide(haystackId, riddleId);
        logger.info("Assign haystackId {} and riddleId {}", verdict.future.getHaystackId(), verdict.future.getRiddleId());
        model.addAttribute(MODEL_ATTR_VERDICT, verdict);
        model.addAttribute(MODEL_ATTR_WHEAT, verdict.future.getWheat());
        model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, verdict.future.getRiddle());
        session.setAttribute(LAST_RIDDLE_ATTR, verdict.future.getRiddleId());
        session.setAttribute(LAST_HAYSTACK_ATTR, verdict.future.getHaystackId());
        logger.info("Create user session {}", session.getId());
        return SPHINX_VIEW_NAME;
    }

    @PostMapping("/sphinx")
    public String answer(
            @ModelAttribute("attempt") String attempt,
            Model model,
            HttpSession session
    ) throws IOException {
        logger.info("Receive POST /sphinx for session {} with attempt: \n{}", session.getId(), attempt);
        String lastRiddleId = (String) session.getAttribute(LAST_RIDDLE_ATTR);
        String lastHaystackId = (String) session.getAttribute(LAST_HAYSTACK_ATTR);
        logger.info("Load session {} with haystackId {} and riddleId {}", session.getId(), lastHaystackId, lastRiddleId);
        Verdict verdict = sphinx.decide(lastHaystackId, lastRiddleId, attempt);
        logger.info("Attempt is {}. assign haystackId {} and riddleId {}", verdict.decision(), verdict.future.getHaystackId(), verdict.future.getRiddleId());
        model.addAttribute(MODEL_ATTR_VERDICT, verdict);
        model.addAttribute(MODEL_ATTR_WHEAT, verdict.future.getWheat());
        model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, verdict.future.getRiddle());
        session.setAttribute(LAST_RIDDLE_ATTR, verdict.future.getRiddleId());
        session.setAttribute(LAST_HAYSTACK_ATTR, verdict.future.getHaystackId());
        return SPHINX_VIEW_NAME;
    }
}
