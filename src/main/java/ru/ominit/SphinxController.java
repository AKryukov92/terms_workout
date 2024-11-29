package ru.ominit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
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

    private static final String MODEL_ATTR_HAYSTACK_ID = "haystack_id";
    private static final String MODEL_ATTR_RIDDLE_ID = "riddle_id";

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
            @ModelAttribute(MODEL_ATTR_RIDDLE_ID) String riddleId,
            @ModelAttribute(MODEL_ATTR_HAYSTACK_ID) String haystackId
    ) {
        logger.info("Receive GET /sphinx with haystackId '{}' and riddleId '{}'", haystackId, riddleId);
        Verdict verdict = sphinx.decide(haystackId, riddleId);
        Journey journey = journeyManager.getJourney(session.getId());
        logger.info("Assign haystackId {} and riddleId {}", verdict.future.getHaystackId(), verdict.future.getRiddleId());
        ShortProgress progress = journey.reportProgress(verdict.future.getHaystack(), verdict.future.getHaystackId());
        String modifiedWheat = journey.highlightSuccessfulAttempts(verdict);
        model.addAttribute(MODEL_ATTR_WHEAT, modifiedWheat);
        populateModel(model, verdict, progress);
        return SPHINX_VIEW_NAME;
    }

    @PostMapping("/guess")
    public RedirectView guess(
            @ModelAttribute(MODEL_ATTR_RIDDLE_ID) String riddleId,
            @ModelAttribute(MODEL_ATTR_HAYSTACK_ID) String haystackId,
            @ModelAttribute("attempt") String attempt,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        logger.info("Receive POST /guess for session {} with haystackId {} and riddleId {}", session.getId(), haystackId, riddleId);
        Journey journey = journeyManager.getJourney(session.getId());
        Verdict verdict = sphinx.decide(haystackId, riddleId, attempt, journey);
        logger.info("User should select '{}' with attempt: \n{}\nit is {}.", verdict.past.getRiddle().getNeedle(), attempt, verdict.decision);
        journey.addStep(verdict);
        String modifiedWheat = journey.highlightSuccessfulAttempts(verdict);
        model.addAttribute(MODEL_ATTR_WHEAT, modifiedWheat);
        ShortProgress progress = journey.reportProgress(verdict.future.getHaystack(), verdict.future.getHaystackId());
        populateModel(model, verdict, progress);
        redirectAttributes.addAttribute(MODEL_ATTR_HAYSTACK_ID, haystackId);
        redirectAttributes.addAttribute(MODEL_ATTR_RIDDLE_ID, riddleId);
        return new RedirectView("sphinx");
    }

    @PostMapping("/skip")
    public String skip(
            @ModelAttribute(LAST_RIDDLE_ATTR) String riddleId,
            @ModelAttribute(LAST_HAYSTACK_ATTR) String haystackId,
            HttpSession session,
            Model model
    ) {
        logger.info("Receive POST /skip for session {} with haystackId {} and riddleId {}", session.getId(), haystackId, riddleId);
        Verdict verdict = sphinx.skip(haystackId, riddleId);
        logger.info("User had to select '{}' and has SKIPPED task.", verdict.past.getRiddle().getNeedle());
        Journey journey = journeyManager.getJourney(session.getId());
        journey.addStep(verdict);
        model.addAttribute(MODEL_ATTR_WHEAT, HtmlUtils.htmlEscape(verdict.future.getWheat()));
        ShortProgress progress = journey.reportProgress(verdict.future.getHaystack(), verdict.future.getHaystackId());
        populateModel(model, verdict, progress);
        return SPHINX_VIEW_NAME;
    }

    private void populateModel(Model model, Verdict verdict, ShortProgress progress) {
        model.addAttribute(MODEL_ATTR_VERDICT, verdict);
        model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, verdict.future.getRiddle());
        model.addAttribute(MODEL_ATTR_CURRENT_PROGRESS, progress.getCurrentProgress());
        model.addAttribute(MODEL_ATTR_MAX_PROGRESS, progress.getMaxProgress());
        model.addAttribute(MODEL_ATTR_RIDDLE_ID, verdict.future.getRiddleId());
        model.addAttribute(MODEL_ATTR_HAYSTACK_ID, verdict.future.getHaystackId());
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute(MODEL_ATTR_THEME_LIST, loader.loadMeta().getThemes());
        return LIST_VIEW_NAME;
    }

    @GetMapping("/journey")
    public String journey(Model model, HttpSession session) {
        logger.info("Receive GET /journey for session {}", session.getId());
        journeyManager.getJourney(session.getId());
        Map<String, HaystackProgress> progress = journeyManager.reportProgress(session.getId());
        model.addAttribute(MODEL_ATTR_STEPS, progress.values());
        return JOURNEY_VIEW_NAME;
    }
}
