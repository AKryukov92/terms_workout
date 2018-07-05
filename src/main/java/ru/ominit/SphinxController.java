package ru.ominit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.ominit.model.HaystackProgress;
import ru.ominit.model.JourneyManager;
import ru.ominit.model.Sphinx;
import ru.ominit.model.Verdict;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;
import java.util.Random;

/**
 * Created by Александр on 30.03.2018.
 */
@Controller
public class SphinxController {
    private Logger logger = LoggerFactory.getLogger(SphinxController.class);

    private static final String SPHINX_VIEW_NAME = "sphinx";
    private static final String JOURNEY_VIEW_NAME = "journey";
    private static final String HOME_VIEW_NAME = "home";
    private static final String LAST_RIDDLE_ATTR = "last_riddle";
    private static final String LAST_HAYSTACK_ATTR = "last_haystack";
    private static final String MODEL_ATTR_VERDICT = "verdict";
    private static final String MODEL_ATTR_WHEAT = "wheat";
    private static final String MODEL_ATTR_NEXT_RIDDLE = "next_riddle";
    private static final String MODEL_ATTR_STEPS = "steps";

    @Autowired
    private Random random;

    @Autowired
    private RiddleLoader loader;

    @Autowired
    private Sphinx sphinx;

    @Autowired
    private JourneyManager journeyManager;

    @GetMapping(WebSecurityConfig.SPHINX_URL)
    public String initial(
            Model model,
            HttpSession session,
            Principal principal,
            @ModelAttribute("haystack") String haystackId,
            @ModelAttribute("riddle") String riddleId
    ) {
        logger.info("Receive GET /sphinx with haystackId {} and riddleId {}", haystackId, riddleId);
        Verdict verdict = sphinx.decide(haystackId, riddleId);
        logger.info("Assign haystackId {} and riddleId {}", verdict.future.getHaystackId(), verdict.future.getRiddleId());
        model.addAttribute(MODEL_ATTR_VERDICT, verdict);
        model.addAttribute(MODEL_ATTR_WHEAT, verdict.future.getWheat());
        model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, verdict.future.getRiddle());
        session.setAttribute(LAST_RIDDLE_ATTR, verdict.future.getRiddleId());
        session.setAttribute(LAST_HAYSTACK_ATTR, verdict.future.getHaystackId());
        logger.info("Create user session {}", session.getId());
        journeyManager.createJourney(session.getId());
        return SPHINX_VIEW_NAME;
    }

    @PostMapping(WebSecurityConfig.SPHINX_URL)
    public String answer(
            @ModelAttribute("attempt") String attempt,
            Model model,
            HttpSession session,
            Principal principal
    ) {
        logger.info("Receive POST /sphinx for session {} with attempt: \n{}", session.getId(), attempt);
        String lastRiddleId = (String) session.getAttribute(LAST_RIDDLE_ATTR);
        String lastHaystackId = (String) session.getAttribute(LAST_HAYSTACK_ATTR);
        logger.info("Load session {} with haystackId {} and riddleId {}", session.getId(), lastHaystackId, lastRiddleId);
        Verdict verdict = sphinx.decide(lastHaystackId, lastRiddleId, attempt);
        journeyManager.addStep(session.getId(), verdict);
        logger.info("Attempt is {}. assign haystackId {} and riddleId {}", verdict.decision, verdict.future.getHaystackId(), verdict.future.getRiddleId());
        model.addAttribute(MODEL_ATTR_VERDICT, verdict);
        model.addAttribute(MODEL_ATTR_WHEAT, verdict.future.getWheat());
        model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, verdict.future.getRiddle());
        session.setAttribute(LAST_RIDDLE_ATTR, verdict.future.getRiddleId());
        session.setAttribute(LAST_HAYSTACK_ATTR, verdict.future.getHaystackId());
        return SPHINX_VIEW_NAME;
    }

    @GetMapping(WebSecurityConfig.JOURNEY_URL)
    public String journey(Model model, HttpSession session, Principal principal) {
        logger.info("Receive GET /journey for session {}", session.getId());
        Map<String, HaystackProgress> progress = journeyManager.reportProgress(session.getId());
        model.addAttribute(MODEL_ATTR_STEPS, progress.values());
        return JOURNEY_VIEW_NAME;
    }
}
