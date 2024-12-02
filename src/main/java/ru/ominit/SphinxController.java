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
import ru.ominit.diskops.RiddleLoaderService;
import ru.ominit.journey.HaystackProgress;
import ru.ominit.journey.Journey;
import ru.ominit.journey.JourneyManager;
import ru.ominit.model.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;
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
    static final String MODEL_ATTR_THEME_LIST = "themes";
    private static final String MODEL_ATTR_VERDICT = "verdict";
    private static final String MODEL_ATTR_WHEAT = "wheat";
    private static final String MODEL_ATTR_NEXT_RIDDLE = "next_riddle";
    private static final String MODEL_ATTR_STEPS = "steps";
    private static final String MODEL_ATTR_MAX_PROGRESS = "max_progress";
    private static final String MODEL_ATTR_CURRENT_PROGRESS = "current_progress";
    private static final String MODEL_ATTR_MAX_ANS_PROGRESS = "max_answer_progress";
    private static final String MODEL_ATTR_CURRENT_ANS_PROGRESS = "current_answer_progress";

    private static final String MODEL_ATTR_HAYSTACK_ID = "haystack_id";
    private static final String MODEL_ATTR_RIDDLE_ID = "riddle_id";
    private static final String MODEL_ATTR_HINT_KEYWORD = "hint_keyword";

    @Autowired
    private Random random;

    @Autowired
    private RiddleLoaderService loader;

    @Autowired
    private Sphinx sphinx;

    @Autowired
    private JourneyManager journeyManager;

    @GetMapping("/sphinx")
    public String display(
            Model model,
            HttpSession session,
            @ModelAttribute(MODEL_ATTR_HAYSTACK_ID) String haystackId,
            @ModelAttribute(MODEL_ATTR_RIDDLE_ID) String riddleId
    ) {
        logger.info("Receive GET /sphinx with haystackId '{}' and riddleId '{}'", haystackId, riddleId);
        Journey journey = journeyManager.getJourney(session.getId());
        Optional<Haystack> haystackOpt;
        if (haystackId.isEmpty()) {
            logger.info("Random haystack");
            haystackOpt = loader.getAnyHaystackId(random).flatMap(newHaystackId -> loader.loadOptional(haystackId));
        } else {
            haystackOpt = loader.loadOptional(haystackId);
        }
        if (!haystackOpt.isPresent()) {
            logger.info("Failed to load haystack");
            return LIST_VIEW_NAME;
        }
        haystackOpt.ifPresent(haystack -> {
            HaystackProgress progress = journey.reportProgress(haystack, haystackId);
            if (journey.hasVerdicts()) {
                Verdict verdict = journey.getLast();
                Optional<Riddle> riddleOpt = haystack.getRiddle(riddleId);
                riddleOpt.ifPresent(riddle -> {
                    String modifiedWheat = journey.highlightSuccessfulAttempts(haystack, riddle.getId());
                    fillModel(model, riddle, progress, haystackId, verdict, modifiedWheat, haystack.getHint_keyword());
                });
                if (!riddleOpt.isPresent()) {
                    logger.info("Riddle was not found");
                    Riddle riddle = haystack.getFreshRiddle(random, haystackId, journey)
                            .orElse(haystack.getRiddle(random));
                    String modifiedWheat = journey.highlightSuccessfulAttempts(haystack, riddle.getId());
                    fillModel(model, riddle, progress, haystackId, verdict, modifiedWheat, haystack.getHint_keyword());
                }
            } else {
                logger.info("Journey just started");
                Verdict verdict = Fate.freshVerdict();
                Riddle riddle = haystack.getRiddle(random);
                String modifiedWheat = journey.highlightSuccessfulAttempts(haystack, riddle.getId());
                fillModel(model, riddle, progress, haystackId, verdict, modifiedWheat, haystack.getHint_keyword());
            }
        });
        return SPHINX_VIEW_NAME;
    }

    private void fillModel(Model model, Riddle riddle, HaystackProgress progress, String haystackId, Verdict verdict, String modifiedWheat, String hintKeyword) {
        model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, riddle);
        model.addAttribute(MODEL_ATTR_RIDDLE_ID, riddle.getId());
        model.addAttribute(MODEL_ATTR_CURRENT_PROGRESS, progress.currentProgress());
        model.addAttribute(MODEL_ATTR_MAX_PROGRESS, progress.maxProgress());
        model.addAttribute(MODEL_ATTR_CURRENT_ANS_PROGRESS, progress.currentAnswerProgress());
        model.addAttribute(MODEL_ATTR_MAX_ANS_PROGRESS, progress.maxAnswerProgress());
        model.addAttribute(MODEL_ATTR_HAYSTACK_ID, haystackId);
        model.addAttribute(MODEL_ATTR_VERDICT, verdict);
        model.addAttribute(MODEL_ATTR_WHEAT, modifiedWheat);
        model.addAttribute(MODEL_ATTR_HINT_KEYWORD, hintKeyword);
    }

    @PostMapping("/guess")
    public RedirectView guess(
            @ModelAttribute(MODEL_ATTR_RIDDLE_ID) String riddleId,
            @ModelAttribute(MODEL_ATTR_HAYSTACK_ID) String haystackId,
            @ModelAttribute("attempt") String attempt,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        logger.info("Receive POST /guess for session {} with haystackId {} and riddleId {}", session.getId(), haystackId, riddleId);
        Journey journey = journeyManager.getJourney(session.getId());
        Verdict verdict = sphinx.decide(haystackId, riddleId, attempt);
        if (verdict.past != null) {
            logger.info("User should select '{}' with attempt: \n{}\nit is {}.", verdict.past.getRiddle().getNeedle(), attempt, verdict.decision);
        }
        journey.addStep(verdict);
        redirectAttributes.addAttribute(MODEL_ATTR_HAYSTACK_ID, haystackId);
        if (verdict.incorrect) {
            redirectAttributes.addAttribute(MODEL_ATTR_RIDDLE_ID, riddleId);
        }
        return new RedirectView("sphinx");
    }

    @PostMapping("/skip")
    public RedirectView skip(
            @ModelAttribute(MODEL_ATTR_RIDDLE_ID) String riddleId,
            @ModelAttribute(MODEL_ATTR_HAYSTACK_ID) String haystackId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        logger.info("Receive POST /skip for session {} with haystackId {} and riddleId {}", session.getId(), haystackId, riddleId);
        if (haystackId.isEmpty()) {
            return new RedirectView("list");
        }
        Optional<Haystack> haystackOpt = loader.loadOptional(haystackId);
        return haystackOpt.map(haystack -> {
            Verdict verdict = haystack.getRiddle(riddleId)
                    .map(riddle -> Fate.skipKnown(haystackId, riddleId))
                    .orElse(Fate.skipUnknown(haystackId));
            Journey journey = journeyManager.getJourney(session.getId());
            journey.addStep(verdict);
            String nextRiddleId = haystack.getFreshRiddle(random, haystackId, journey)
                    .map(Riddle::getId)
                    .orElse("");
            redirectAttributes.addAttribute(MODEL_ATTR_RIDDLE_ID, nextRiddleId);
            redirectAttributes.addAttribute(MODEL_ATTR_HAYSTACK_ID, haystackId);
            return new RedirectView("sphinx");
        }).orElse(new RedirectView("list"));
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
