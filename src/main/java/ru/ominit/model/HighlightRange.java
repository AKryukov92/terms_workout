package ru.ominit.model;

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
        String[] grainParts = inside.split(" ");
        int begin = wheat.indexOf(grainParts[0]);
        String lastPart = grainParts[grainParts.length - 1];
        int end = wheat.indexOf(lastPart) + lastPart.length();
        return wheat.substring(0, begin) + openTag + wheat.substring(begin, end) + END + wheat.substring(end);
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
}
