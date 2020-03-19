package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.highlight.HighlightRangeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public List<HighlightRange> joinAnswerRanges(EscapedHtmlString escapedGrain, HighlightRangeType type) {
        List<HighlightRange> ranges = answers.stream()
                .flatMap(a -> a.highlightAll(escapedGrain, type).stream())
                .collect(Collectors.toList());
        HighlightRange.joinRanges(ranges);
        return ranges;
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

    public List<HighlightRange> shiftForSizeOfTags(List<HighlightRange> ranges, String openTag, String closeTag) {
        List<HighlightRange> result = new ArrayList<>();
        int shiftDistance = openTag.length() + closeTag.length();
        int cumulativeShift = 0;
        for (HighlightRange range : ranges) {
            HighlightRange shifted = new HighlightRange(range.getStartIndex() + cumulativeShift, range.getEndIndex() + cumulativeShift);
            cumulativeShift += shiftDistance;
            result.add(shifted);
        }
        return result;
    }

    public String insert(EscapedHtmlString grain, EscapedHtmlString wheat) {
        List<HighlightRange> highlightRangesMaximal = shiftForSizeOfTags(
                joinAnswerRanges(grain, HighlightRangeType.MAXIMAL),
                HighlightRange.MAX_START,
                HighlightRange.END
        );
        EscapedHtmlString modifiedWheat = wheat;
        EscapedHtmlString modifiedGrain = grain;
        for (HighlightRange range : highlightRangesMaximal) {
            HighlightRange rangeForWheat = range.clarify(grain, modifiedWheat);
            modifiedWheat = rangeForWheat.insert(modifiedWheat, HighlightRange.MAX_START, HighlightRange.END);
            modifiedGrain = range.insert(modifiedGrain, HighlightRange.MAX_START, HighlightRange.END);
        }
        List<HighlightRange> highlightRangesMinimal = shiftForSizeOfTags(
                joinAnswerRanges(grain, HighlightRangeType.MINIMAL),
                HighlightRange.MIN_START,
                HighlightRange.END
        );
        for (HighlightRange range : highlightRangesMinimal) {
            HighlightRange rangeForWheat = range.clarify(grain, modifiedWheat);
            modifiedWheat = rangeForWheat.insert(modifiedWheat, HighlightRange.MIN_START, HighlightRange.END);
        }
        return modifiedWheat.toString();
    }

    /**
     * Внедряет в данный текст неэкранированные тэги, которые будут содержать в себе области с минимальным и максимальным ответом.
     *
     * @param wheat текст для преобразования
     * @return текст с внедренными тегами
     */
    public String insert(EscapedHtmlString wheat) {
        EscapedHtmlString[] grain = wheat.split("\\s+");
        //Получить диапазоны текста для выделения
        List<HighlightRange> minimalRanges = extractRanges(grain, HighlightRangeType.MINIMAL);
        List<HighlightRange> maximalRanges = extractRanges(grain, HighlightRangeType.MAXIMAL);
        //Соединить накладывающиеся диапазоны одинакового типа
        HighlightRange.joinRanges(minimalRanges);
        HighlightRange.joinRanges(maximalRanges);

        //Нужно добавить алгоритм поиска конца диапазона
        //Нужно поменять алгоритм поиска диапазона на использование массива фрагментов

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
        return "";
    }

    public List<String> tokenize(List<HighlightRange> minRanges, List<HighlightRange> maxRanges, EscapedHtmlString[] grain, EscapedHtmlString wheat) {
        if (minRanges.isEmpty() || maxRanges.isEmpty()) {
            //нужно извлечь пробельные и текстовые токены из wheat с помощью grain
            return new ArrayList<>();
        }
        int initialIndex = 0;
        HighlightRange currentMax = maxRanges.get(0);
        currentMax.getStartIndex();
        //повторять для всех максимальных интервалов
        //  собрать пробельные и текстовые токены из wheat с помощью grain до индекса currentMax.getStartIndex
        //  добавить токен начала максимального интервала

        //  взять начало следующего минимального токена
        //  повторять пока начало следующего минимального интервала меньше конца текущего максимального интервала
        //    собрать пробельные и текстовые токены из wheat с помощью grain до индекса currentMin.getStartIndex
        //    добавить токен начала минимального интервала
        //    собрать пробельные и текстовые токены из wheat с помощью grain до индекса currentMin.getEndIndex
        //    добавить токен конца минимального интервала

        //  собрать пробельные и текстовые токены из wheat с помощью grain до индекса currentMax.getEndIndex
        //  добавить токен конца максимального интервала

        //собрать пробельные и текстовые токены из wheat с помощью grain до конца строки
        return new ArrayList<>();
    }

    public static void appendTokens(List<String> tokenList,
                                    EscapedHtmlString[] grain,
                                    EscapedHtmlString wheat,
                                    int rangeStart,
                                    int rangeEnd) {
        if (rangeStart >= rangeEnd) {
            throw new IllegalArgumentException("Начало интервала должно быть меньше конца интервала");
        }
        if (rangeEnd > wheat.length()){
            throw new IllegalArgumentException("Длина интервала должна быть меньше суммарной длины текста");
        }
        //найти фрагмент grain, в котором находится rangeStart
        //найти соответствующий фрагмент wheat
        //Когда поиск дошел до нужного фрагмента, то добавляем кусок текста из grain, а затем пробелы из wheat и текст из grain
        int grainIndex = 0;//индекс текущего фрагмента
        int fragmentStart = 0;//индекс начала текущего фрагмента в сплошном тексте без пробелов
        int whitespaceStart = 0;//позиция текущего фрагмента во wheat
        while (grainIndex < grain.length) {
            int fragmentEnd = fragmentStart + grain[grainIndex].length();
            if (fragmentStart <= rangeStart && rangeStart < fragmentEnd) {
                break;
            }
            fragmentStart += grain[grainIndex].length();
            grainIndex++;
            whitespaceStart = wheat.indexOf(grain[grainIndex], whitespaceStart + 1);
        }
        int rangeInFragmentStart = rangeStart - fragmentStart;//вычитаю абсолютные индексы, чтобы найти индекс внутри фрагмента
        int rangeInFragmentEnd = rangeEnd - fragmentStart;
        if (rangeInFragmentEnd <= grain[grainIndex].length()) {
            //Если интервал заканчивается внутри текущего фрагмента
            EscapedHtmlString extractedToken = grain[grainIndex].substring(rangeInFragmentStart, rangeInFragmentEnd);
            tokenList.add(extractedToken.toString());
        } else {
            //Если интервал заканчивается за пределами текущего фрагмента
            //Добавляю часть фрагмента, который попадает в интервал
            EscapedHtmlString extractedToken = grain[grainIndex].substring(rangeInFragmentStart);
            tokenList.add(extractedToken.toString());

            fragmentStart += grain[grainIndex].length();
            rangeInFragmentEnd = rangeEnd - fragmentStart;
            whitespaceStart = whitespaceStart + grain[grainIndex].length();

            grainIndex++;
            if (grainIndex < grain.length) {
                int whitespaceEnd = wheat.indexOf(grain[grainIndex], whitespaceStart + 1);
                EscapedHtmlString wheatWhitespace = wheat.substring(whitespaceStart, whitespaceEnd);
                tokenList.add(wheatWhitespace.toString());
                whitespaceStart = whitespaceEnd + grain[grainIndex].length();

                //пока конец интервала за пределами текущего фрагмента
                while (rangeInFragmentEnd > grain[grainIndex].length()) {
                    //добавляем фрагменты целиком
                    extractedToken = grain[grainIndex];
                    tokenList.add(extractedToken.toString());

                    fragmentStart += grain[grainIndex].length();
                    rangeInFragmentEnd = rangeEnd - fragmentStart;
                    grainIndex++;
                    if (grainIndex >= grain.length) {
                        throw new IllegalArgumentException("Длина интервала должна быть меньше суммарной длины текста");
                    }

                    whitespaceEnd = wheat.indexOf(grain[grainIndex], whitespaceStart + 1);
                    wheatWhitespace = wheat.substring(whitespaceStart, whitespaceEnd);
                    tokenList.add(wheatWhitespace.toString());
                    whitespaceStart = whitespaceEnd + grain[grainIndex].length();
                }
                //когда дошли до фрагмента, в середине которого конец интервала, то нужно отрезать от начала до конца интервала
                extractedToken = grain[grainIndex].substring(0, rangeInFragmentEnd);
                tokenList.add(extractedToken.toString());
            }
        }
    }

    public static Riddle DEFAULT() {
        return new Riddle("default", "смысл", "");
    }
}
