package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.highlight.HighlightRangeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    public List<HighlightRange> extractWheatRanges(EscapedHtmlString wheat, HighlightRangeType type){
        if (type == HighlightRangeType.MINIMAL) {
            return answers.stream()
                    .flatMap(a -> HighlightRange.highlightAll(wheat, a.getMinimalFragments()).stream())
                    .collect(Collectors.toList());
        } else {
            return answers.stream()
                    .flatMap(a -> HighlightRange.highlightAll(wheat, a.getMaximalFragments()).stream())
                    .collect(Collectors.toList());
        }
    }

    public List<HighlightRange> extractGrainRanges(EscapedHtmlString[] grain, HighlightRangeType type) {
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
        EscapedHtmlString[] grain = wheat.splitByWhitespace();
        //Получить диапазоны текста для выделения
        List<HighlightRange> minimalRanges = extractGrainRanges(grain, HighlightRangeType.MINIMAL);
        List<HighlightRange> maximalRanges = extractGrainRanges(grain, HighlightRangeType.MAXIMAL);
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
        return String.join("", tokenize(minimalRanges, maximalRanges, grain, wheat));
    }
    //может я зря ищу диапазоны в grain, а лучше их искать во wheat?
    //тогда не придется извращаться при токенизации.

    /**
     * Формирует коллекцию фрагментов, состоящую из пробелов, фрагментов текста и пробельных фрагментов.
     *
     * @param minRanges коллекция объединенных минимальных диапазонов
     * @param maxRanges коллекция объединенных максимальных диапазонов
     * @param grain     коллекция фрагментов текста между пробелами
     * @param wheat     исходный текст
     * @return коллекция фрагментов, которые получились при разбиении
     */
    public static List<String> tokenize(List<HighlightRange> minRanges, List<HighlightRange> maxRanges, EscapedHtmlString[] grain, EscapedHtmlString wheat) {
        List<String> result = new ArrayList<>();
        int totalLength = 0;
        for (EscapedHtmlString str : grain) {
            totalLength += str.length();
        }
        if (minRanges.isEmpty() || maxRanges.isEmpty()) {
            appendTokens(result, grain, wheat, 0, totalLength);
            return result;
        }
        int currentIndex = maxRanges.get(0).getStartIndex();
        if (currentIndex > 0) {
            appendTokens(result, grain, wheat, 0, currentIndex);//пробелы справа
        }
        int minIndex = 0;
        for (HighlightRange currentMax : maxRanges) {
            getWhitespaces(grain, wheat, currentIndex).ifPresent(result::add);
            result.add(HighlightRange.MAX_START);
            HighlightRange currentMin = minRanges.get(minIndex);
            if (minIndex < minRanges.size() && currentMin.getEndIndex() <= currentMax.getEndIndex()) {
                appendTokens(result, grain, wheat, currentIndex, currentMin.getStartIndex());
                if (currentIndex < currentMin.getStartIndex()) {
                    getWhitespaces(grain, wheat, currentMin.getStartIndex()).ifPresent(result::add);//пробелы справа
                }
                result.add(HighlightRange.MIN_START);
                appendTokens(result, grain, wheat, currentMin.getStartIndex(), currentMin.getEndIndex());//без пробелов по краям
                result.add(HighlightRange.END);
                currentIndex = currentMin.getEndIndex();
                minIndex++;
            }
            if (minIndex < minRanges.size()) {
                currentMin = minRanges.get(minIndex);

                while (minIndex < minRanges.size() && currentMin.getEndIndex() <= currentMax.getEndIndex()) {
                    currentMin = minRanges.get(minIndex);
                    getWhitespaces(grain, wheat, currentIndex).ifPresent(result::add);
                    appendTokens(result, grain, wheat, currentIndex, currentMin.getStartIndex());//пробелы справа и слева
                    getWhitespaces(grain, wheat, currentMin.getStartIndex()).ifPresent(result::add);
                    result.add(HighlightRange.MIN_START);
                    appendTokens(result, grain, wheat, currentMin.getStartIndex(), currentMin.getEndIndex());//без пробелов по краям
                    result.add(HighlightRange.END);
                    currentIndex = currentMin.getEndIndex();
                    minIndex++;
                }
            }
            if (currentIndex < currentMax.getEndIndex()) {
                getWhitespaces(grain, wheat, currentIndex).ifPresent(result::add);
            }
            appendTokens(result, grain, wheat, currentIndex, currentMax.getEndIndex());//пробелы слева
            result.add(HighlightRange.END);
            currentIndex = currentMax.getEndIndex();
        }
        getWhitespaces(grain, wheat, currentIndex).ifPresent(result::add);
        if (currentIndex < totalLength) {
            appendTokens(result, grain, wheat, currentIndex, totalLength);
        }
        return result;
    }

    public static Optional<String> getWhitespaces(EscapedHtmlString[] grain, EscapedHtmlString wheat, int fragmentEndIndex) {
        //Если указанный индекс находится в конце какого-нибудь диапазона
        //то нужно найти закончившийся диапазон и следующий во wheat
        //вернуть фрагмент wheat между концом текущего и началом следующего
        int grainIndex = 0;
        int fragmentStart = 0;
        int endInWheat = 0;
        while (fragmentStart < fragmentEndIndex && grainIndex < grain.length) {
            endInWheat = wheat.indexOf(grain[grainIndex], endInWheat) + grain[grainIndex].length();
            fragmentStart += grain[grainIndex].length();
            grainIndex++;
        }
        if (fragmentStart == fragmentEndIndex && grainIndex < grain.length) {
            int positionInWheat = wheat.indexOf(grain[grainIndex], endInWheat);
            if (positionInWheat == endInWheat) {
                return Optional.empty();
            } else {
                return Optional.of(wheat.substring(endInWheat, positionInWheat).toString());
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * добавляет пробелы и текст из wheat ориентируясь на grain начиная с индекса rangeStart и заканчивая индексом rangeEnd
     *
     * @param tokenList  коллекция, в которую добавляются токены
     * @param grain      массив текстовых фрагментов без пробелов, по которому отсчитываются индексы
     * @param wheat      исходный текст, содержащий пробелы
     * @param rangeStart индекс начала извлекаемого фрагмента в grain. Отсчитывается с 0, включительно.
     * @param rangeEnd   индекс конца извлекаемого фрагмента в grain. Отсчитывается с 0, исключительно.
     */
    public static void appendTokens(List<String> tokenList,
                                    EscapedHtmlString[] grain,
                                    EscapedHtmlString wheat,
                                    int rangeStart,
                                    int rangeEnd) {
        if (grain.length == 0) {
            throw new IllegalArgumentException("Массив фрагментов текста должен быть непустым");
        }
        if (rangeStart > rangeEnd) {
            throw new IllegalArgumentException("Начало интервала должно быть меньше конца интервала");
        }
        if (rangeEnd > wheat.length()) {
            throw new IllegalArgumentException("Длина интервала должна быть меньше суммарной длины текста");
        }
        int totalLength = 0;
        for (EscapedHtmlString aGrain : grain) {
            totalLength += aGrain.length();
        }
        if (rangeEnd > totalLength) {
            throw new IllegalArgumentException("Длина интервала должна быть меньше суммарной длины текста");
        }
        if (rangeStart == totalLength) {
            return;
        }
        int grainIndex = 0;//просматриваемый фрагмент grain
        int startInWheat = 0;
        int startInGrain = 0;
        while (grainIndex < grain.length && rangeStart >= startInGrain + grain[grainIndex].length()) {
            //найти где начинается этот фрагмент во wheat
            startInWheat = wheat.indexOf(grain[grainIndex], startInWheat);
            startInGrain += grain[grainIndex].length();
            grainIndex++;
        }
        startInWheat = wheat.indexOf(grain[grainIndex], startInWheat);
        int rangeInFragmentStart = rangeStart - startInGrain;//вычитаю абсолютные индексы, чтобы найти индекс внутри фрагмента
        int rangeInFragmentEnd = rangeEnd - startInGrain;
        if (rangeInFragmentEnd <= grain[grainIndex].length()) {
            //Если интервал заканчивается внутри текущего фрагмента
            if (rangeInFragmentStart < rangeInFragmentEnd) {
                //Если интервал не пуст, то добавляю
                EscapedHtmlString extractedToken = grain[grainIndex].substring(rangeInFragmentStart, rangeInFragmentEnd);
                tokenList.add(extractedToken.toString());
            }
        } else {
            //Если интервал заканчивается за пределами текущего фрагмента
            //Добавляю часть фрагмента, который попадает в интервал
            EscapedHtmlString extractedToken = grain[grainIndex].substring(rangeInFragmentStart);
            tokenList.add(extractedToken.toString());

            startInGrain += grain[grainIndex].length();
            rangeInFragmentEnd = rangeEnd - startInGrain;
            startInWheat = startInWheat + grain[grainIndex].length();

            grainIndex++;
            if (grainIndex < grain.length) {
                int whitespaceEnd = wheat.indexOf(grain[grainIndex], startInWheat + 1);
                EscapedHtmlString wheatWhitespace = wheat.substring(startInWheat, whitespaceEnd);
                tokenList.add(wheatWhitespace.toString());
                startInWheat = whitespaceEnd + grain[grainIndex].length();

                //пока конец интервала за пределами текущего фрагмента
                while (rangeInFragmentEnd > grain[grainIndex].length()) {
                    //добавляем фрагменты целиком
                    extractedToken = grain[grainIndex];
                    tokenList.add(extractedToken.toString());

                    startInGrain += grain[grainIndex].length();
                    rangeInFragmentEnd = rangeEnd - startInGrain;
                    grainIndex++;

                    whitespaceEnd = wheat.indexOf(grain[grainIndex], startInWheat + 1);
                    wheatWhitespace = wheat.substring(startInWheat, whitespaceEnd);
                    tokenList.add(wheatWhitespace.toString());
                    startInWheat = whitespaceEnd + grain[grainIndex].length();
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
