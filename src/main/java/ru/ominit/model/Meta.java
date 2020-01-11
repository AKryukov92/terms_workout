package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.*;

public class Meta {
    @JacksonXmlProperty(localName = "theme")
    @JacksonXmlElementWrapper(useWrapping = false, localName = "theme")
    private List<Theme> themes = new ArrayList<>();

    public Meta() {
    }

    public Optional<Theme> getRandomTheme(Random rnd) {
        if (themes.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(themes.get(rnd.nextInt(themes.size())));
    }

    public List<Theme> getThemes() {
        return Collections.unmodifiableList(themes);
    }
}
