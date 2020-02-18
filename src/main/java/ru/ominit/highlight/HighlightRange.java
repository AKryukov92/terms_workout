package ru.ominit.highlight;

import java.util.Objects;
import java.util.Optional;

public class HighlightRange {
    private int startIndex;
    private int endIndex;
    private HighlightRangeType type;

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public HighlightRange(int startIndex, int endIndex, HighlightRangeType type) {
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("Start index should be less than end index");
        }
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.type = type;
    }

    public Optional<HighlightRange> connectWith(HighlightRange range) {
        if (this.type != range.type) {
            return Optional.empty();
        }
        if (this.endIndex < range.startIndex) {
            return Optional.empty();
        }
        if (range.endIndex < this.startIndex) {
            return Optional.empty();
        }

        if (this.startIndex <= range.startIndex) {
            if (this.endIndex <= range.endIndex) {
                return Optional.of(new HighlightRange(this.startIndex, range.endIndex, this.type));
            } else {
                return Optional.of(new HighlightRange(this.startIndex, this.endIndex, this.type));
            }
        } else {
            if (this.endIndex <= range.endIndex) {
                return Optional.of(new HighlightRange(range.startIndex, range.endIndex, this.type));
            } else {
                return Optional.of(new HighlightRange(range.startIndex, this.endIndex, this.type));
            }
        }
    }

    public String insert(String grain, String wheat) {
        String inside = grain.substring(startIndex, endIndex);
        String openTag;
        if (this.type == HighlightRangeType.MAXIMAL) {
            openTag = MAX_START;
        } else {
            openTag = MIN_START;
        }
        //Разбитие по пробелу нужно потому, что видимый пользователями текст и технический текст различаются именно количеством пробелов.
        //После разбития можно ориентироваться по цельным фрагментам, которые в обоих случаях будут одинаковы
        String[] grainParts = inside.split(" ");
        String result = wheat;
        int begin = result.indexOf(grainParts[0]);
        String lastPart = grainParts[grainParts.length - 1];
        int end = result.indexOf(lastPart, begin) + lastPart.length();
        while (begin >= 0) {
            result = result.substring(0, begin) + openTag + result.substring(begin, end) + END + result.substring(end);
            int shiftedEnd = end + openTag.length() + END.length();
            begin = result.indexOf(grainParts[0], shiftedEnd);
            end = result.indexOf(lastPart, shiftedEnd) + lastPart.length();
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HighlightRange that = (HighlightRange) o;
        return startIndex == that.startIndex &&
                endIndex == that.endIndex &&
                type == that.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(startIndex, endIndex, type);
    }

    @Override
    public String toString() {
        return "HighlightRange{" +
                "startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                ", type=" + type +
                '}';
    }

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
