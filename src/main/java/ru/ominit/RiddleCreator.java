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
import ru.ominit.model.Answer;
import ru.ominit.model.Haystack;
import ru.ominit.model.NotEnoughDataException;
import ru.ominit.model.Riddle;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
public class RiddleCreator {
    private Logger logger = LoggerFactory.getLogger(RiddleCreator.class);

    private static String CREATOR_VIEW_NAME = "creator";
    private static String UPLOAD_VIEW_NAME = "upload";

    private static String HAYSTACK_ID_ATTR = "haystack_id";
    private static String RIDDLE_WHEAT_ATTR = "wheat";

    @Autowired
    private Random random;

    @Autowired
    private MultipartConfigElement config;

    @Autowired
    private RiddleLoader loader;

    @GetMapping("/upload")
    public String initial() {
        logger.info("Receive GET /upload");
        logger.info("Max file size: " + config.getMaxFileSize());
        return UPLOAD_VIEW_NAME;
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("wheat") MultipartFile wheat,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) throws IOException {
        logger.info("Receive POST /upload");
        String haystackId = UUID.randomUUID().toString();
        session.setAttribute(RIDDLE_WHEAT_ATTR, new String(wheat.getBytes()));
        session.setAttribute(HAYSTACK_ID_ATTR, haystackId);
        redirectAttributes.addFlashAttribute(HAYSTACK_ID_ATTR, haystackId);
        redirectAttributes.addFlashAttribute(RIDDLE_WHEAT_ATTR, wheat);
        return "redirect:/" + CREATOR_VIEW_NAME;
    }

    @GetMapping("/creator")
    public String showFormToFill(HttpSession session,
                                 Model model) {
        logger.info("Receive GET /creator");
        String wheat = (String) session.getAttribute(RIDDLE_WHEAT_ATTR);
        if (wheat != null && !wheat.isEmpty()) {
            model.addAttribute(RIDDLE_WHEAT_ATTR, wheat);
            return CREATOR_VIEW_NAME;
        } else {
            return "redirect:/" + UPLOAD_VIEW_NAME;
        }
    }

    @PostMapping("/creator")
    public String acceptForm(@RequestParam("needle") String needle,
                             @RequestParam("min_attempt") String minAttempt,
                             @RequestParam("max_attempt") String maxAttempt,
                             @RequestParam(value = "is_last", required = false, defaultValue = "false") Boolean isLast,
                             HttpSession session
    ) throws IOException {
        if (minAttempt == null || minAttempt.isEmpty()) {
            throw new NotEnoughDataException();
        }
        if (maxAttempt == null || maxAttempt.isEmpty()) {
            throw new NotEnoughDataException();
        }
        Answer answer = new Answer(minAttempt, maxAttempt);
        String riddleId = UUID.randomUUID().toString();
        if (needle == null || needle.isEmpty()) {
            throw new NotEnoughDataException();
        }
        String haystackId = (String) session.getAttribute(HAYSTACK_ID_ATTR);
        if (haystackId == null || haystackId.isEmpty()) {
            return "redirect:/" + UPLOAD_VIEW_NAME;
        }
        String wheat = (String) session.getAttribute(RIDDLE_WHEAT_ATTR);
        if (wheat == null || wheat.isEmpty()) {
            return "redirect:/" + UPLOAD_VIEW_NAME;
        }
        Optional<Haystack> haystackOpt = loader.loadOpt(haystackId);
        Haystack haystack = haystackOpt.orElseGet(() -> new Haystack(wheat, new ArrayList<>()));
        Optional<Riddle> riddleOpt = haystack.getRiddleByNeedle(needle);
        if (riddleOpt.isPresent()) {
            riddleOpt.ifPresent(riddle -> {
                if (riddle.intersectsAnything(answer)) {
                    riddle.addAnswer(answer);
                }
            });
        } else {
            Riddle riddle = new Riddle(riddleId, needle, null);
            riddle.addAnswer(answer);
            haystack.addRiddle(riddle);
        }
        loader.write(haystackId, haystack);

        if (isLast) {
            session.setAttribute(RIDDLE_WHEAT_ATTR, null);
            session.setAttribute(HAYSTACK_ID_ATTR, null);
        }
        return "redirect:/" + CREATOR_VIEW_NAME;
    }
}
