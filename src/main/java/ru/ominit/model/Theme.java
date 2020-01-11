package ru.ominit.model;

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
    private List<String> haystackNames;

    public Theme() {

    }

    public String getName() {
        return name;
    }

    public Optional<String> getRandomHaystack(Random rnd) {
        if (haystackNames.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(haystackNames.get(rnd.nextInt(haystackNames.size())));
    }

    public List<String> getHaystackNames() {
        return Collections.unmodifiableList(haystackNames);
    }
}
