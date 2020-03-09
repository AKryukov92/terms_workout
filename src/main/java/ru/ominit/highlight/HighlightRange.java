package ru.ominit.highlight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ominit.model.Haystack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HighlightRange {
    private Logger logger = LoggerFactory.getLogger(HighlightRange.class);
    private int startIndex;
    private int endIndex;

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public HighlightRange(int startIndex, int endIndex) {
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("Start index should be less than end index");
        }
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public Optional<HighlightRange> connectWith(HighlightRange range) {
        if (this.endIndex < range.startIndex) {
            return Optional.empty();
        }
        if (range.endIndex < this.startIndex) {
            return Optional.empty();
        }

        if (this.startIndex <= range.startIndex) {
            if (this.endIndex <= range.endIndex) {
                return Optional.of(new HighlightRange(this.startIndex, range.endIndex));
            } else {
                return Optional.of(new HighlightRange(this.startIndex, this.endIndex));
            }
        } else {
            if (this.endIndex <= range.endIndex) {
                return Optional.of(new HighlightRange(range.startIndex, range.endIndex));
            } else {
                return Optional.of(new HighlightRange(range.startIndex, this.endIndex));
            }
        }
    }

    /**
     * Функция для уточнения границ диапазона, который был построен на grain, так чтобы он соответствовал wheat.
     * Уточненный диапазон должен содержать все фрагменты в той последовательности, в какой они находятся в массиве.
     * Пробелы между фрагментами игнорируются.
     *
     * @param grain текст, из которого берутся фрагменты
     * @param wheat текст в котором ожидаются фрагменты
     * @return индекс начала диапазона и индекс конца диапазона
     */
    public HighlightRange clarify(EscapedHtmlString grain, EscapedHtmlString wheat) {
        //Разбитие по пробелу нужно потому, что видимый пользователями текст и технический текст различаются именно количеством пробелов.
        //После разбития можно ориентироваться по цельным фрагментам, которые в обоих случаях будут одинаковы
        EscapedHtmlString[] parts = grain
                .substring(startIndex, endIndex)
                .split(" ");
        //нашли первый фрагмент
        //пропустили пробелы
        //достали фрагмент после пробелов длиной в следующий кусок текста
        //проверили что извлеченный фрагмент совпадает со следующим куском текста
        //если совпадает, то проверяем следующий кусок текста
        //если не совпадает, то ищем первый кусок текста, начиная с текущей позиции
        int partIndex = 0;
        int currentPartEndIndex;
        int currentPartStartIndex;
        int initialPartStartIndex;
        currentPartStartIndex = wheat.indexOf(parts[partIndex], this.startIndex);
        initialPartStartIndex = currentPartStartIndex;

        do {
            currentPartEndIndex = currentPartStartIndex + parts[partIndex].length();
            EscapedHtmlString partFromText = wheat.substring(currentPartStartIndex, currentPartEndIndex);
            if (partFromText.equals(parts[partIndex])) {
                partIndex++;
                if (partIndex == parts.length) {
                    return new HighlightRange(initialPartStartIndex, currentPartEndIndex);
                }
                currentPartStartIndex = wheat.indexOfNextNonWhitespace(currentPartEndIndex);
            } else {
                partIndex = 0;
                currentPartStartIndex = wheat.indexOf(parts[partIndex], currentPartStartIndex);
                initialPartStartIndex = currentPartStartIndex;
            }
        }
        while (currentPartStartIndex > 0);
        return new HighlightRange(initialPartStartIndex, currentPartEndIndex);
    }

    public EscapedHtmlString insert(EscapedHtmlString wheat, String openTag, String closeTag) {
        EscapedHtmlString result;
        try {
            result = wheat.substring(0, startIndex)
                    .concatWith(openTag)
                    .concatWith(wheat.substring(startIndex, endIndex))
                    .concatWith(closeTag)
                    .concatWith(wheat.substring(endIndex));
        } catch (StringIndexOutOfBoundsException ex) {
            logger.error("Failed to insert highlighted range '{}' to wheat '{}'", this, wheat);
            throw ex;
        }
        return result;
    }

    /**
     * Модифицирует данную ему коллекцию диапазонов. Соединяет диапазоны, которые пересекаются
     *
     * @param ranges коллекция диапазонов
     */
    public static void joinRanges(List<HighlightRange> ranges) {
        boolean somethingChanged = true;
        while (somethingChanged) {
            somethingChanged = false;
            for (HighlightRange rangeToConnect : ranges) {
                boolean innerFound = false;
                for (HighlightRange rangeToBeConnected : ranges) {
                    if (rangeToConnect.equals(rangeToBeConnected)) {
                        continue;
                    }
                    Optional<HighlightRange> connectedOpt = rangeToConnect.connectWith(rangeToBeConnected);
                    connectedOpt.ifPresent(connected -> {
                        ranges.remove(rangeToConnect);
                        ranges.remove(rangeToBeConnected);
                        ranges.add(connected);
                    });
                    if (connectedOpt.isPresent()) {
                        innerFound = true;
                        break;
                    }
                }
                if (innerFound) {
                    somethingChanged = true;
                    break;
                }
            }
        }
    }

    public static Optional<HighlightRange> highlight(String answer, EscapedHtmlString grain) {
        EscapedHtmlString escapedText = EscapedHtmlString.make(answer);
        int start = grain.indexOf(escapedText);
        if (start < 0) {
            return Optional.empty();
        } else {
            return Optional.of(new HighlightRange(
                    start,
                    start + escapedText.length()
            ));
        }
    }

    public static List<HighlightRange> highlightAll(EscapedHtmlString[] grain, EscapedHtmlString[] fragments) {
        List<HighlightRange> result = new ArrayList<>();
        int start = Haystack.indexOfInArr(grain, fragments, 0);
        while (start >= 0) {
            int end = start;
            for (EscapedHtmlString fragment : fragments) {
                end += fragment.length();
            }
            result.add(new HighlightRange(start, end));
            start = Haystack.indexOfInArr(grain, fragments, end);
        }
        return result;
    }

    public static List<HighlightRange> highlightAll(String answer, EscapedHtmlString grain) {
        EscapedHtmlString escapedText = EscapedHtmlString.make(answer);
        List<HighlightRange> result = new ArrayList<>();
        int start = grain.indexOf(escapedText);
        while (start >= 0) {
            int end = start + escapedText.length();
            result.add(new HighlightRange(start, end));
            start = grain.indexOf(escapedText, end);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HighlightRange that = (HighlightRange) o;
        return startIndex == that.startIndex &&
                endIndex == that.endIndex;
    }

    @Override
    public int hashCode() {

        return Objects.hash(startIndex, endIndex);
    }

    @Override
    public String toString() {
        return "HighlightRange{" +
                "startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                '}';
    }

    public static final String ANSWER_START = "<span class=\"answer\">";
    public static final String MIN_START = "<span class=\"min\">";
    public static final String MAX_START = "<span class=\"max\">";
    public static final String END = "</span>";

    public static String wrapMin(String text) {
        return MIN_START + text + END;
    }

    public static String wrapMax(String text) {
        return MAX_START + text + END;
    }
}
