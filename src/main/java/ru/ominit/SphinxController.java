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
import ru.ominit.model.Verdict;

import javax.servlet.http.HttpSession;

/**
 * Created by Александр on 30.03.2018.
 */
@Controller
public class SphinxController {
    private static String SPHINX_VIEW_NAME = "sphinx";
    private static String LAST_RIDDLE_ATTR = "last_q";
    private static String MODEL_ATTR_VERDICT = "verdict";

    @Autowired
    private Sphinx sphinx;

    @Bean
    public Sphinx sphinx(){
        return new Sphinx();
    }

    @GetMapping("/sphinx")
    public String question(Model model, HttpSession session) {
        Riddle riddle = sphinx.firstRiddle();
        model.addAttribute(MODEL_ATTR_VERDICT, Verdict.makeFresh(riddle));
        session.setAttribute(LAST_RIDDLE_ATTR, riddle.getId());
        return SPHINX_VIEW_NAME;
    }

    @PostMapping("/sphinx")
    public String answer(
            @ModelAttribute("attempt") String attempt,
            Model model,
            HttpSession session
    ) {
        Integer lastRiddleId = (Integer) session.getAttribute(LAST_RIDDLE_ATTR);
        Verdict verdict = sphinx.attempt(lastRiddleId, attempt);
        model.addAttribute(MODEL_ATTR_VERDICT, verdict);
        session.setAttribute(LAST_RIDDLE_ATTR, verdict.next_riddle.getId());
        return SPHINX_VIEW_NAME;
    }
}
