package ru.ominit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.ominit.highlight.HaystackFileMissingException;
import ru.ominit.diskops.RiddleLoaderService;
import ru.ominit.model.*;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
public class RiddleCreatorController {
    private Logger logger = LoggerFactory.getLogger(RiddleCreatorController.class);

    private static final String CREATOR_VIEW_NAME = "creator";
    private static final String UPLOAD_VIEW_NAME = "upload";

    private static final String HAYSTACK_ID_ATTR = "haystack_id";
    private static final String RIDDLE_WHEAT_ATTR = "wheat";
    private static final String INCORRECT_NEEDLE_ATTR = "incorrect_needle";
    private static final String INCORRECT_MIN_ASNWER_ATTR = "incorrect_min_answer";
    private static final String INCORRECT_MAX_ASNWER_ATTR = "incorrect_max_answer";

    @Autowired
    private Random random;

    @Autowired
    private MultipartConfigElement config;

    @Autowired
    private RiddleLoaderService loader;

    @GetMapping("/upload")
    public String initial(Model model) {
        logger.info("Receive GET /upload");
        logger.info("Max file size: " + config.getMaxFileSize());
        model.addAttribute(SphinxController.MODEL_ATTR_THEME_LIST, loader.loadMeta().getThemes());
        return UPLOAD_VIEW_NAME;
    }

    private Optional<String> ofNullableEmpty(String value) {
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(value);
        }
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("wheat") final MultipartFile wheatFile,
                         RedirectAttributes redirectAttributes) throws IOException {
        logger.info("Receive POST /upload");
        String haystackId = UUID.randomUUID().toString();
        String wheat = new String(wheatFile.getBytes());
        Haystack haystack = new Haystack(new String(wheat.getBytes()), new ArrayList<>());
        loader.write(haystackId, haystack);
        redirectAttributes.addFlashAttribute(RIDDLE_WHEAT_ATTR, wheat);
        redirectAttributes.addAttribute(HAYSTACK_ID_ATTR, haystackId);
        return "redirect:/" + CREATOR_VIEW_NAME;
    }

    @PostMapping("/submit")
    public String submit(@ModelAttribute("wheat") final String wheatText,
                         RedirectAttributes redirectAttributes) throws IOException {
        logger.info("Receive POST /submit");
        String haystackId = UUID.randomUUID().toString();
        Haystack haystack = new Haystack(new String(wheatText.getBytes()), new ArrayList<>());
        loader.write(haystackId, haystack);
        redirectAttributes.addFlashAttribute(RIDDLE_WHEAT_ATTR, wheatText);
        redirectAttributes.addAttribute(HAYSTACK_ID_ATTR, haystackId);
        return "redirect:/" + CREATOR_VIEW_NAME;
    }

    @GetMapping("/highlight")
    @ResponseBody
    public String highlight(@RequestParam("haystack_id") final String paramHaystackId,
                            @RequestParam("needle") final String needle) {
        logger.info("Receive GET /highlight");
        return ofNullableEmpty(paramHaystackId)
                .flatMap(loader::loadOptional)
                .map(haystack -> haystack.highlightNeedle(needle))
                .orElseThrow(HaystackFileMissingException::new);
    }

    @GetMapping("/creator")
    public String showFormToFill(@RequestParam(value = "haystack_id", required = false) final String paramHaystackId,
                                 Model model) {
        logger.info("Receive GET /creator");
        return ofNullableEmpty(paramHaystackId).flatMap(haystackId -> {
            logger.info("Loading haystack with id '{}'", haystackId);
            return loader.loadOptional(haystackId).map(Haystack::getWheat).map(wheat -> {
                model.addAttribute(RIDDLE_WHEAT_ATTR, wheat);
                return CREATOR_VIEW_NAME;
            });
        }).orElseGet(() -> {
            logger.info("Failed to get wheat. Redirecting to upload view");
            return "redirect:/" + UPLOAD_VIEW_NAME;
        });
    }

    @PostMapping("/creator")
    public String acceptForm(@RequestParam("haystack_id") final String paramHaystackId,
                             @RequestParam("needle") final String needle,
                             @RequestParam("min_attempt") final String minAttempt,
                             @RequestParam("max_attempt") final String maxAttempt,
                             @RequestParam(value = "is_last", required = false, defaultValue = "false") final Boolean isLast,
                             RedirectAttributes redirectAttributes
    ) {
        String effectiveMaxAttempt;
        if (maxAttempt == null || maxAttempt.isEmpty()) {
            logger.warn("Maximal answer was empty. Setting it equal to minimal.");
            effectiveMaxAttempt = minAttempt;
        } else {
            effectiveMaxAttempt = maxAttempt;
        }
        return "redirect:/" + ofNullableEmpty(paramHaystackId).flatMap(haystackId -> {
            redirectAttributes.addAttribute(HAYSTACK_ID_ATTR, haystackId);
            if (minAttempt == null || minAttempt.isEmpty()) {
                logger.warn("Minimal answer was empty.");
                return Optional.of(CREATOR_VIEW_NAME);
            }
            if (!Answer.areValid(minAttempt, effectiveMaxAttempt)) {
                logger.info("Answers min '{}' and max '{}' are incorrect. Telling this to user.", minAttempt, maxAttempt);
                redirectAttributes.addFlashAttribute(INCORRECT_NEEDLE_ATTR, needle);
                redirectAttributes.addFlashAttribute(INCORRECT_MIN_ASNWER_ATTR, minAttempt);
                redirectAttributes.addFlashAttribute(INCORRECT_MAX_ASNWER_ATTR, effectiveMaxAttempt);
                return Optional.of(CREATOR_VIEW_NAME);
            }
            Answer answer = new Answer(minAttempt, effectiveMaxAttempt);
            if (needle == null || needle.isEmpty()) {
                return Optional.of(CREATOR_VIEW_NAME);
            }
            logger.info("Haystack id is defined to {}", haystackId);
            return loader.loadOptional(haystackId).map(haystack -> {
                logger.info("Haystack loading was successful. Searching riddle by needle '{}'", needle);
                Optional<Riddle> riddleOpt = haystack.getRiddleByNeedle(needle);
                riddleOpt.ifPresent(riddle -> riddle.addAnswer(answer));
                if (!riddleOpt.isPresent()) {
                    logger.info("Riddle was not found. Adding new one");
                    String riddleId = UUID.randomUUID().toString();
                    Riddle riddle = new Riddle(riddleId, needle, null);
                    riddle.addAnswer(answer);
                    haystack.addRiddle(riddle);
                }
                try {
                    loader.write(haystackId, haystack);
                } catch (IOException e) {
                    logger.error("Failed to serialize haystack with id '{}'", haystackId, e);
                }
                if (isLast) {
                    return UPLOAD_VIEW_NAME;
                } else {
                    return CREATOR_VIEW_NAME;
                }
            });
        }).orElseGet(() -> {
            logger.info("Failed to retrieve target url. Redirecting to upload page");
            return UPLOAD_VIEW_NAME;
        });
    }
}
