package ru.ominit.diskops;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Theme {
    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;
    @JacksonXmlProperty(localName = "haystack")
    @JacksonXmlElementWrapper(useWrapping = false, localName = "haystack")
    private List<HaystackMention> haystackMenthions;

    public Theme() {
    }

    public String getName() {
        return name;
    }

    public Optional<String> getRandomHaystackId(Random rnd) {
        if (haystackMenthions.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(haystackMenthions.get(rnd.nextInt(haystackMenthions.size())).getId());
    }

    public List<HaystackMention> getHaystackMentions() {
        return Collections.unmodifiableList(haystackMenthions);
    }
}
