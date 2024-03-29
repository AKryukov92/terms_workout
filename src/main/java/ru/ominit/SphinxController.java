package ru.ominit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.HtmlUtils;
import ru.ominit.diskops.RiddleLoaderService;
import ru.ominit.journey.HaystackProgress;
import ru.ominit.journey.Journey;
import ru.ominit.journey.JourneyManager;
import ru.ominit.journey.ShortProgress;
import ru.ominit.model.Sphinx;
import ru.ominit.model.Verdict;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;

/**
 * Created by Александр on 30.03.2018.
 */
@Controller
public class SphinxController {
    private Logger logger = LoggerFactory.getLogger(SphinxController.class);

    private static final String LIST_VIEW_NAME = "list";
    private static final String SPHINX_VIEW_NAME = "sphinx";
    private static final String JOURNEY_VIEW_NAME = "journey";
    private static final String LAST_RIDDLE_ATTR = "last_riddle";
    private static final String LAST_HAYSTACK_ATTR = "last_haystack";
    static final String MODEL_ATTR_THEME_LIST = "themes";
    private static final String MODEL_ATTR_VERDICT = "verdict";
    private static final String MODEL_ATTR_WHEAT = "wheat";
    private static final String MODEL_ATTR_NEXT_RIDDLE = "next_riddle";
    private static final String MODEL_ATTR_STEPS = "steps";
    private static final String MODEL_ATTR_MAX_PROGRESS = "max_progress";
    private static final String MODEL_ATTR_CURRENT_PROGRESS = "current_progress";

    @Autowired
    private Random random;

    @Autowired
    private RiddleLoaderService loader;

    @Autowired
    private Sphinx sphinx;

    @Autowired
    private JourneyManager journeyManager;

    @GetMapping("/sphinx")
    public String initial(
            Model model,
            HttpSession session,
            @ModelAttribute("haystack") String haystackId,
            @ModelAttribute("riddle") String riddleId
    ) {
        logger.info("Receive GET /sphinx with haystackId '{}' and riddleId '{}'", haystackId, riddleId);
        Verdict verdict = sphinx.decide(haystackId, riddleId);
        logger.info("Assign haystackId {} and riddleId {}", verdict.future.getHaystackId(), verdict.future.getRiddleId());
        model.addAttribute(MODEL_ATTR_VERDICT, verdict);
        model.addAttribute(MODEL_ATTR_WHEAT, HtmlUtils.htmlEscape(verdict.future.getWheat()));
        model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, verdict.future.getRiddle());
        model.addAttribute(MODEL_ATTR_CURRENT_PROGRESS, 0);
        model.addAttribute(MODEL_ATTR_MAX_PROGRESS, 0);
        session.setAttribute(LAST_RIDDLE_ATTR, verdict.future.getRiddleId());
        session.setAttribute(LAST_HAYSTACK_ATTR, verdict.future.getHaystackId());
        logger.info("Create user session {}", session.getId());
        journeyManager.createJourney(session.getId());
        return SPHINX_VIEW_NAME;
    }

    @PostMapping("/sphinx")
    public String answer(
            @ModelAttribute("attempt") String attempt,
            Model model,
            HttpSession session
    ) {
        logger.info("Receive POST /sphinx for session {}", session.getId());
        String lastRiddleId = (String) session.getAttribute(LAST_RIDDLE_ATTR);
        String lastHaystackId = (String) session.getAttribute(LAST_HAYSTACK_ATTR);
        logger.info("Load session {} with haystackId {} and riddleId {}", session.getId(), lastHaystackId, lastRiddleId);
        Verdict verdict = sphinx.decide(lastHaystackId, lastRiddleId, attempt);
        logger.info("User should select '{}' with attempt: \n{}\nit is {}.", verdict.past.getRiddle().getNeedle(), attempt, verdict.decision);
        Journey journey = journeyManager.getJourney(session.getId());
        journey.addStep(verdict, session.getId());
        String modifiedWheat = journey.highlightSuccessfulAttempts(verdict);
        logger.info("Assign haystackId {} and riddleId {}", verdict.future.getHaystackId(), verdict.future.getRiddleId());
        model.addAttribute(MODEL_ATTR_VERDICT, verdict);
        model.addAttribute(MODEL_ATTR_WHEAT, modifiedWheat);
        model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, verdict.future.getRiddle());
        ShortProgress progress = journeyManager.reportProgress(session.getId(), lastHaystackId);
        model.addAttribute(MODEL_ATTR_CURRENT_PROGRESS, progress.getCurrentProgress());
        model.addAttribute(MODEL_ATTR_MAX_PROGRESS, progress.getMaxProgress());
        session.setAttribute(LAST_RIDDLE_ATTR, verdict.future.getRiddleId());
        session.setAttribute(LAST_HAYSTACK_ATTR, verdict.future.getHaystackId());
        return SPHINX_VIEW_NAME;
    }

    @PostMapping("/skip")
    public String skip(HttpSession session, Model model) {
        logger.info("Receive POST /sphinx for session {}", session.getId());
        String lastRiddleId = (String) session.getAttribute(LAST_RIDDLE_ATTR);
        String lastHaystackId = (String) session.getAttribute(LAST_HAYSTACK_ATTR);
        logger.info("Load session {} with haystackId {} and riddleId {}", session.getId(), lastHaystackId, lastRiddleId);
        Verdict verdict = sphinx.skip(lastHaystackId, lastRiddleId);
        logger.info("User had to select '{}' and has SKIPPED task.", verdict.past.getRiddle().getNeedle());
        Journey journey = journeyManager.getJourney(session.getId());
        journey.addStep(verdict, session.getId());
        logger.info("Assign decision {} for haystackId {} and riddleId {}", verdict.decision, verdict.future.getHaystackId(), verdict.future.getRiddleId());
        model.addAttribute(MODEL_ATTR_VERDICT, verdict);
        model.addAttribute(MODEL_ATTR_WHEAT, verdict.future.getWheat());
        model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, verdict.future.getRiddle());
        ShortProgress progress = journeyManager.reportProgress(session.getId(), lastHaystackId);
        model.addAttribute(MODEL_ATTR_CURRENT_PROGRESS, progress.getCurrentProgress());
        model.addAttribute(MODEL_ATTR_MAX_PROGRESS, progress.getMaxProgress());
        session.setAttribute(LAST_RIDDLE_ATTR, verdict.future.getRiddleId());
        session.setAttribute(LAST_HAYSTACK_ATTR, verdict.future.getHaystackId());
        return SPHINX_VIEW_NAME;
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute(MODEL_ATTR_THEME_LIST, loader.loadMeta().getThemes());
        return LIST_VIEW_NAME;
    }

    @GetMapping("/journey")
    public String journey(Model model, HttpSession session) {
        logger.info("Receive GET /journey for session {}", session.getId());
        Map<String, HaystackProgress> progress = journeyManager.reportProgress(session.getId());
        model.addAttribute(MODEL_ATTR_STEPS, progress.values());
        return JOURNEY_VIEW_NAME;
    }
}
