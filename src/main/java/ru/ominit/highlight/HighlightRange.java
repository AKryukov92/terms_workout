package ru.ominit.highlight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ominit.model.Haystack;

import java.util.*;

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

    public boolean contains(HighlightRange range) {
        return this.startIndex <= range.startIndex && range.endIndex <= this.endIndex;
    }

    public Optional<HighlightRange> connectWith(HighlightRange range) {
        if (this.endIndex <= range.startIndex) {
            return Optional.empty();
        }
        if (range.endIndex <= this.startIndex) {
            return Optional.empty();
        }

        if (this.startIndex <= range.startIndex) {
            if (this.endIndex < range.endIndex) {
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
     * Модифицирует данную ему коллекцию диапазонов. Соединяет диапазоны, которые пересекаются.
     * Если один диапазон заканчивается на индексе начала следующего диапазона, то диапазоны не склеиваются.
     * Сортирует диапазоны по индексу начала. Если они равны, то по индексу конца.
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
        ranges.sort((a, b) -> {
            if (a.startIndex == b.startIndex) {
                return a.endIndex - b.endIndex;
            } else {
                return a.startIndex - b.startIndex;
            }
        });
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
