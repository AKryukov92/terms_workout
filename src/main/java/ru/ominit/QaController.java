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

        if (riddle.validate(answer)) {
            Riddle nextRiddle = sphinx.nextRiddle(riddle.getId());
            riddleToTemplate(nextRiddle, model, session);
            return QA_VIEW_NAME;
        } else {
            riddleToTemplate(riddle, model, session);
            return QA_VIEW_NAME;
        }
    }

    private void riddleToTemplate(Riddle riddle, Model model, HttpSession session){
        model.addAttribute(MODEL_ATTR_HAYSTACK, riddle.getHaystack());
        model.addAttribute(MODEL_ATTR_NEEDLE, riddle.getNeedle());
        session.setAttribute(LAST_RIDDLE_ATTR, riddle.getId());
    }
}
