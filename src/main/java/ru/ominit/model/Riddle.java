package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.highlight.HighlightRangeType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Александр on 30.03.2018.
 */
public class Riddle {
    @JacksonXmlProperty(localName = "id")
    private String id;

    @JacksonXmlProperty(localName = "needle")
    private String needle;

    @JacksonXmlProperty(localName = "answer")
    @JacksonXmlElementWrapper(useWrapping = false, localName = "answer")
    private List<Answer> answers;

    @JacksonXmlProperty(localName = "next")
    private String nextId;

    public Riddle(
            @JacksonXmlProperty(localName = "id") String id,
            @JacksonXmlProperty(localName = "needle") String needle,
            @JacksonXmlProperty(localName = "next") String nextId
    ) {
        this.id = id;
        this.needle = needle;
        this.nextId = nextId;
        this.answers = new ArrayList<>();
    }

    @JacksonXmlProperty(localName = "answer")
    @JacksonXmlElementWrapper(useWrapping = false, localName = "answer")
    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public boolean intersectsAnything(Answer proposedAnswer) {
        for (Answer answer : answers) {
            if (answer.intersects(proposedAnswer)) {
                return true;
            }
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public String getNeedle() {
        return needle;
    }

    public String getNextId() {
        return nextId;
    }

    public boolean hasMultipleAnswers() {
        return answers.size() > 1;
    }

    public List<Answer> listAnswers() {
        return Collections.unmodifiableList(answers);
    }

    public boolean isCorrect(String attempt) {
        String[] tokens = attempt.split("\\s+");
        boolean found = false;
        for (Answer answer : answers) {
            if (answer.matches(tokens)) {
                found = true;
            }
        }
        return found;
    }

    public void assertRelevant(String[] grain) {
        for (Answer answer : answers) {
            if (!answer.relevantTo(grain)) {
                throw new InsaneTaskException(grain[0], needle, answer);
            }
        }
    }

    public List<HighlightRange> extractRanges(EscapedHtmlString[] grain, HighlightRangeType type) {
        if (type == HighlightRangeType.MINIMAL) {
            return answers.stream()
                    .flatMap(a -> HighlightRange.highlightAll(grain, a.getMinimalFragments()).stream())
                    .collect(Collectors.toList());
        } else {
            return answers.stream()
                    .flatMap(a -> HighlightRange.highlightAll(grain, a.getMaximalFragments()).stream())
                    .collect(Collectors.toList());
        }
    }

    /**
     * Внедряет в данный текст неэкранированные тэги, которые будут содержать в себе области с минимальным и максимальным ответом.
     *
     * @param wheat текст для преобразования
     * @return текст с внедренными тегами
     */
    public String insert(EscapedHtmlString wheat) {
        EscapedHtmlString[] grain = wheat.splitByWhitespace();
        //Получить диапазоны текста для выделения
        List<HighlightRange> minimalRanges = extractRanges(grain, HighlightRangeType.MINIMAL);
        List<HighlightRange> maximalRanges = extractRanges(grain, HighlightRangeType.MAXIMAL);
        //Соединить накладывающиеся диапазоны одинакового типа
        HighlightRange.joinRanges(minimalRanges);
        HighlightRange.joinRanges(maximalRanges);
        //разбивать текст на токены в начале не получится, т.к. не известно где начинаются и где заканчиваются диапазоны
        //нужен отдельный алгоритм, который получит на вход:
        //- коллекцию объединенных диапазонов
        //- массив фрагментов без пробелов
        //- исходный текст
        //этот алгоритм в результате сформирует коллекцию токенов следующих видов:
        //- пробельный токен
        //- значимый токен
        //- маркировочный токен (для тэгов диапазонов)
        //положить их в одну коллекцию и соединить
        return String.join("", HighlightRange.tokenize(minimalRanges, maximalRanges, grain, wheat));
    }

    public static Riddle DEFAULT() {
        return new Riddle("default", "смысл", "");
    }
}
