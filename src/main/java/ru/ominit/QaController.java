package ru.ominit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.ominit.model.Riddle;
import ru.ominit.model.Sphinx;

import javax.servlet.http.HttpSession;

/**
 * Created by Александр on 30.03.2018.
 */
@Controller
public class QaController {
    private static String QA_VIEW_NAME = "qa";
    private static String LAST_RIDDLE_ATTR = "last_q";
    private static String MODEL_ATTR_HAYSTACK = "haystack";
    private static String MODEL_ATTR_NEEDLE = "needle";
    private static String MODEL_ATTR_INCORRECT = "incorrect";
    private static String MODEL_ATTR_CORRECT = "correct";
    private static String MODEL_ATTR_IRRELEVANT = "irrelevant";
    private static String MODEL_ATTR_LAST_ATTEMPT = "last_attempt";

    @Autowired
    private Sphinx sphinx;

    @Bean
    public Sphinx sphinx(){
        return new Sphinx();
    }

    @GetMapping("/qa")
    public String question(Model model, HttpSession session) {
        Riddle riddle = sphinx.firstRiddle();
        riddleToTemplate(riddle, model, session);
        return QA_VIEW_NAME;
    }

    @PostMapping("/qa")
    public String answer(
            @ModelAttribute("a") String answer,
            Model model,
            HttpSession session
    ) {
        Integer q = (Integer) session.getAttribute(LAST_RIDDLE_ATTR);
        Riddle riddle = sphinx.riddleById(q);
        Riddle nextRiddle;
        boolean isRelevant = riddle.isRelevant(answer);
        if (isRelevant) {
            boolean isCorrect = riddle.isCorrect(answer);
            resolutionToTemplate(answer, isCorrect, model);
            if (isCorrect) {
                nextRiddle = sphinx.nextRiddle(riddle.getId());
            } else {
                nextRiddle = riddle;
            }
        } else {
            nextRiddle = riddle;
            model.addAttribute(MODEL_ATTR_IRRELEVANT, Boolean.TRUE);
            model.addAttribute(MODEL_ATTR_LAST_ATTEMPT, answer);
        }
        riddleToTemplate(nextRiddle, model, session);
        return QA_VIEW_NAME;
    }

    private void resolutionToTemplate(String answer, boolean correct, Model model){
        model.addAttribute(MODEL_ATTR_CORRECT, correct);
        model.addAttribute(MODEL_ATTR_INCORRECT, !correct);
        model.addAttribute(MODEL_ATTR_LAST_ATTEMPT, answer);
    }

    private void riddleToTemplate(Riddle riddle, Model model, HttpSession session){
        model.addAttribute(MODEL_ATTR_HAYSTACK, riddle.getHaystack());
        model.addAttribute(MODEL_ATTR_NEEDLE, riddle.getNeedle());
        session.setAttribute(LAST_RIDDLE_ATTR, riddle.getId());
    }
}
