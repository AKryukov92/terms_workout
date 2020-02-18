package ru.ominit.highlight;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HighlightRange {
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

    public EscapedHtmlString insert(EscapedHtmlString grain, EscapedHtmlString wheat, String openTag, String closeTag) {
        EscapedHtmlString inside = grain.substring(startIndex, endIndex);
        //Разбитие по пробелу нужно потому, что видимый пользователями текст и технический текст различаются именно количеством пробелов.
        //После разбития можно ориентироваться по цельным фрагментам, которые в обоих случаях будут одинаковы
        EscapedHtmlString[] grainParts = inside.split(" ");
        EscapedHtmlString result = wheat;
        int begin = result.indexOf(grainParts[0]);
        EscapedHtmlString lastPart = grainParts[grainParts.length - 1];
        int end = result.indexOf(lastPart, begin) + lastPart.length();
        while (begin >= 0) {
            result = result.substring(0, begin)
                    .concatWith(openTag)
                    .concatWith(result.substring(begin, end))
                    .concatWith(closeTag)
                    .concatWith(result.substring(end));
            int shiftedEnd = end + openTag.length() + closeTag.length();
            begin = result.indexOf(grainParts[0], shiftedEnd);
            end = result.indexOf(lastPart, shiftedEnd) + lastPart.length();
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

    public static Optional<HighlightRange> highlight(String answer, EscapedHtmlString escapedGrain) {
        EscapedHtmlString escapedText = EscapedHtmlString.make(answer);
        int start = escapedGrain.indexOf(escapedText);
        if (start < 0) {
            return Optional.empty();
        } else {
            return Optional.of(new HighlightRange(
                    start,
                    start + escapedText.length()
            ));
        }
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
