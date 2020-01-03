package ru.ominit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class RiddleCreator {
    private Logger logger = LoggerFactory.getLogger(RiddleCreator.class);

    private static String CREATOR_VIEW_NAME = "creator";
    private static String UPLOAD_VIEW_NAME = "upload";
    private static String RIDDLE_HAYSTACK_ATTR = "haystack";

    @Autowired
    private Random random;

    @Autowired
    private MultipartConfigElement config;

    @GetMapping("/upload")
    public String initial() {
        logger.info("Receive GET /upload");
        logger.info("Max file size: " + config.getMaxFileSize());
        return UPLOAD_VIEW_NAME;
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("haystack") MultipartFile haystack,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        logger.info("Receive POST /upload");
        session.setAttribute(RIDDLE_HAYSTACK_ATTR, haystack);
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded" + haystack.getOriginalFilename() + "!");
        return "redirect:/" + CREATOR_VIEW_NAME;
    }

    @GetMapping("/creator")
    public String creator() {
        logger.info("Receive GET /creator");
        return CREATOR_VIEW_NAME;
    }
}
