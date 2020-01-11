package ru.ominit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    public String initial() {
        logger.info("Receive GET /upload");
        logger.info("Max file size: " + config.getMaxFileSize());
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
    public String upload(@RequestParam("wheat") MultipartFile wheatFile,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) throws IOException {
        logger.info("Receive POST /upload");
        String haystackId = UUID.randomUUID().toString();
        String wheat = new String(wheatFile.getBytes());
        Haystack haystack = new Haystack(new String(wheat.getBytes()), Collections.EMPTY_LIST);
        loader.write(haystackId, haystack);
        session.setAttribute(HAYSTACK_ID_ATTR, haystackId);
        redirectAttributes.addFlashAttribute(HAYSTACK_ID_ATTR, haystackId);
        redirectAttributes.addFlashAttribute(RIDDLE_WHEAT_ATTR, wheat);
        return "redirect:/" + CREATOR_VIEW_NAME;
    }

    @GetMapping("/creator")
    public String showFormToFill(@RequestParam(value = "haystack_id", required = false) String paramHaystackId,
                                 HttpSession session,
                                 Model model) {
        logger.info("Receive GET /creator");
        Optional<String> paramHaystackIdOpt = ofNullableEmpty(paramHaystackId);
        paramHaystackIdOpt.ifPresent(haystackId -> {
            logger.info("Haystack id '{}' was defined in request. Override session defined one.", paramHaystackId);
            session.setAttribute(HAYSTACK_ID_ATTR, haystackId);
        });

        Optional<String> sessionHaystackIdOpt = ofNullableEmpty((String) session.getAttribute(HAYSTACK_ID_ATTR));
        Optional<Haystack> haystackOpt = sessionHaystackIdOpt.flatMap(haystackId -> {
            logger.info("Loading haystack with id '{}'", haystackId);
            model.addAttribute(HAYSTACK_ID_ATTR, haystackId);
            return loader.loadOptional(haystackId);
        });
        Optional<String> wheatOpt = haystackOpt.map(Haystack::getWheat);
        wheatOpt.ifPresent(wheat -> model.addAttribute(RIDDLE_WHEAT_ATTR, wheat));
        if (wheatOpt.isPresent()) {
            return CREATOR_VIEW_NAME;
        } else {
            logger.info("Failed to get wheat. Redirecting to upload view");
            return "redirect:/" + UPLOAD_VIEW_NAME;
        }
    }

    @PostMapping("/creator")
    public String acceptForm(@RequestParam("needle") String needle,
                             @RequestParam("min_attempt") String minAttempt,
                             @RequestParam("max_attempt") String maxAttempt,
                             @RequestParam(value = "is_last", required = false, defaultValue = "false") Boolean isLast,
                             HttpSession session,
                             RedirectAttributes redirectAttributes
    ) {
        if (minAttempt == null || minAttempt.isEmpty()) {
            logger.warn("Minimal answer was empty.");
            return "redirect:/" + CREATOR_VIEW_NAME;
        }
        if (maxAttempt == null || maxAttempt.isEmpty()) {
            logger.warn("Maximal answer was empty. Setting it equal to minimal.");
            maxAttempt = minAttempt;
        }
        if (!Answer.areValid(minAttempt, maxAttempt)) {
            logger.info("Answers min '{}' and max '{}' are incorrect. Telling this to user.", minAttempt, maxAttempt);
            redirectAttributes.addFlashAttribute(INCORRECT_NEEDLE_ATTR, needle);
            redirectAttributes.addFlashAttribute(INCORRECT_MIN_ASNWER_ATTR, minAttempt);
            redirectAttributes.addFlashAttribute(INCORRECT_MAX_ASNWER_ATTR, maxAttempt);
            return "redirect:/" + CREATOR_VIEW_NAME;
        }
        Answer answer = new Answer(minAttempt, maxAttempt);
        if (needle == null || needle.isEmpty()) {
            return "redirect:/" + CREATOR_VIEW_NAME;
        }
        Optional<String> haystackIdOpt = ofNullableEmpty((String) session.getAttribute(HAYSTACK_ID_ATTR));
        if (!haystackIdOpt.isPresent()) {
            logger.info("Haystack id is not set. Redirecting to upload page");
            return "redirect:/" + UPLOAD_VIEW_NAME;
        }
        haystackIdOpt.ifPresent(haystackId -> {
            logger.info("Haystack id is defined to {}", haystackId);
            loader.loadOptional(haystackId).ifPresent(haystack -> {
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
            });
        });
        if (isLast) {
            logger.info("Answer marked as last for this haystack. Cleaning up");
            session.setAttribute(HAYSTACK_ID_ATTR, null);
            return "redirect:/" + UPLOAD_VIEW_NAME;
        } else {
            return "redirect:/" + CREATOR_VIEW_NAME;
        }
    }
}
