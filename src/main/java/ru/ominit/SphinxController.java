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
    private static String MODEL_ATTR_NEXT_RIDDLE = "next_riddle";

    @Autowired
    private Sphinx sphinx;

    @Autowired
    private RiddleLoader loader;

    @Bean
    public Sphinx sphinx(){
        return new Sphinx();
    }

    @GetMapping("/sphinx")
    public String initial(Model model, HttpSession session) {
        Riddle riddle = loader.loadAny();
        model.addAttribute(MODEL_ATTR_VERDICT, Verdict.makeFresh());
        model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, riddle);
        session.setAttribute(LAST_RIDDLE_ATTR, riddle.getId());
        return SPHINX_VIEW_NAME;
    }

    @PostMapping("/sphinx")
    public String answer(
            @ModelAttribute("attempt") String attempt,
            Model model,
            HttpSession session
    ) {
        String lastRiddleId = (String) session.getAttribute(LAST_RIDDLE_ATTR);
        Riddle lastRiddle = loader.load(lastRiddleId);
        Verdict verdict = sphinx.decide(lastRiddle, attempt);
        if (verdict.correct){
            Riddle nextRiddle = loader.load(lastRiddle.getNextId() + ".xml");
            model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, nextRiddle);
            session.setAttribute(LAST_RIDDLE_ATTR, nextRiddle.getId());
        } else {
            model.addAttribute(MODEL_ATTR_NEXT_RIDDLE, lastRiddle);
            session.setAttribute(LAST_RIDDLE_ATTR, lastRiddle.getId());
        }
        model.addAttribute(MODEL_ATTR_VERDICT, verdict);
        return SPHINX_VIEW_NAME;
    }
}
