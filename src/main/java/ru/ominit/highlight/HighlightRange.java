package ru.ominit.highlight;

import ru.ominit.model.Haystack;

import java.util.*;

public class HighlightRange {
    private int startIndex;
    private int endIndex;

    public HighlightRange(int startIndex, int endIndex) {
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("Start index should be less than end index");
        }
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public boolean contains(HighlightRange range) {
        return this.startIndex <= range.startIndex && range.endIndex <= this.endIndex;
    }

    public boolean intersects(HighlightRange range) {
        return this.endIndex > range.startIndex && range.endIndex > this.startIndex;
    }

    public Optional<HighlightRange> connectWith(HighlightRange range) {
        if (!intersects(range)) {
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

    /**
     * Модифицирует данную ему коллекцию диапазонов. Соединяет диапазоны, которые пересекаются.
     * Если один диапазон заканчивается на индексе начала следующего диапазона, то диапазоны не склеиваются.
     * Сортирует диапазоны по индексу начала. Если они равны, то по индексу конца.
     *
     * @param ranges коллекция диапазонов
     */
    public static void joinRanges(List<HighlightRange> ranges) {
        ranges.sort((a, b) -> {
            if (a.startIndex == b.startIndex) {
                return a.endIndex - b.endIndex;
            } else {
                return a.startIndex - b.startIndex;
            }
        });
        int toCon = 0;
        while (toCon < ranges.size() - 1) {
            HighlightRange current = ranges.get(toCon);
            HighlightRange next = ranges.get(toCon + 1);
            if (current.intersects(next)) {
                int finalToCon = toCon;
                current.connectWith(next).ifPresent(connected -> {
                    ranges.remove(finalToCon);
                    ranges.remove(finalToCon);
                    ranges.add(finalToCon, connected);
                });
            } else {
                toCon++;
            }
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

    /**
     * Формирует коллекцию фрагментов, состоящую из пробелов, фрагментов текста и пробельных фрагментов.
     *
     * @param minRanges коллекция объединенных минимальных диапазонов
     * @param maxRanges коллекция объединенных максимальных диапазонов
     * @param wheat     исходный текст
     * @return коллекция фрагментов, которые получились при разбиении
     */
    public static List<String> tokenize(List<HighlightRange> minRanges, List<HighlightRange> maxRanges, EscapedHtmlString wheat) {
        EscapedHtmlString[] grain = wheat.getGrain();
        List<String> result = new ArrayList<>();
        int totalLength = 0;
        for (EscapedHtmlString str : grain) {
            totalLength += str.length();
        }
        if (minRanges.isEmpty() || maxRanges.isEmpty()) {
            appendTokens(result, wheat, 0, totalLength);
            return result;
        }
        Iterator<HighlightRange> maxRangesItr = maxRanges.iterator();
        if (maxRangesItr.hasNext()) {
            HighlightRange currentMax = maxRangesItr.next();
            int currentIndex = currentMax.getStartIndex();
            appendTokens(result, wheat, 0, currentIndex);//пробелы справа
            getWhitespaces(wheat, currentMax.getStartIndex()).ifPresent(result::add);
            result.add(MAX_START);
            int minIndex = 0;
            minIndex = appendContentsOfMaxRange(result, wheat, currentMax, minRanges, minIndex);
            result.add(END);
            int prevEndIndex = currentMax.getEndIndex();

            while (maxRangesItr.hasNext()) {
                currentMax = maxRangesItr.next();
                getWhitespaces(wheat, prevEndIndex).ifPresent(result::add);
                if (prevEndIndex < currentMax.getStartIndex()) {
                    appendTokens(result, wheat, prevEndIndex, currentMax.getStartIndex());
                    getWhitespaces(wheat, currentMax.getStartIndex()).ifPresent(result::add);
                }
                result.add(MAX_START);
                minIndex = appendContentsOfMaxRange(result, wheat, currentMax, minRanges, minIndex);
                result.add(END);
                prevEndIndex = currentMax.getEndIndex();
            }
            getWhitespaces(wheat, prevEndIndex).ifPresent(result::add);
            if (prevEndIndex < totalLength) {
                appendTokens(result, wheat, prevEndIndex, totalLength);
            }
        }
        return result;
    }

    public static List<String> tokenize(List<HighlightRange> successfulAttempts,
                                        EscapedHtmlString wheat) {
        EscapedHtmlString[] grain = wheat.getGrain();
        List<String> result = new ArrayList<>();
        int totalLength = 0;
        for (EscapedHtmlString str : grain) {
            totalLength += str.length();
        }
        if (successfulAttempts.isEmpty()) {
            appendTokens(result, wheat, 0, totalLength);
        }
        Iterator<HighlightRange> rangesItr = successfulAttempts.iterator();
        if (rangesItr.hasNext()) {
            HighlightRange current = rangesItr.next();
            int currentIndex = current.getStartIndex();
            appendTokens(result, wheat, 0, currentIndex);
            getWhitespaces(wheat, current.getStartIndex()).ifPresent(result::add);
            result.add(ANSWER_START);
            appendTokens(result, wheat, current.getStartIndex(), current.getEndIndex());
            result.add(END);
            int prevEndIndex = current.getEndIndex();
            while (rangesItr.hasNext()) {
                current = rangesItr.next();
                getWhitespaces(wheat, prevEndIndex).ifPresent(result::add);
                if (prevEndIndex < current.getStartIndex()) {
                    appendTokens(result, wheat, prevEndIndex, current.getStartIndex());
                    getWhitespaces(wheat, current.getStartIndex()).ifPresent(result::add);
                }
                result.add(ANSWER_START);
                appendTokens(result, wheat, current.getStartIndex(), current.getEndIndex());
                result.add(END);
                prevEndIndex = current.getEndIndex();
            }
            getWhitespaces(wheat, prevEndIndex).ifPresent(result::add);
            if (prevEndIndex < totalLength) {
                appendTokens(result, wheat, prevEndIndex, totalLength);
            }
        }
        return result;
    }

    public static int appendContentsOfMaxRange(List<String> result,
                                               EscapedHtmlString wheat,
                                               HighlightRange currentMax,
                                               List<HighlightRange> minRanges,
                                               int minIndex) {
        if (minIndex >= minRanges.size()) {
            throw new IllegalArgumentException("Индекс минимального диапазона должен быть меньше длины коллекции с диапазонами");
        }
        int currentIndex = currentMax.getStartIndex();
        HighlightRange currentMin = minRanges.get(minIndex);
        if (currentMin.getStartIndex() < currentMax.getStartIndex()) {
            throw new IllegalArgumentException("Индекс начала первого минимального диапазона должен быть больше или равен индексу максимального диапазона");
        }
        if (!currentMax.contains(currentMin)) {//Хотя бы один диапазон должен находиться внутри максимального
            throw new IllegalArgumentException("Максимальный диапазон не содержит минимальный диапазон на указанном индексе");
        }
        appendTokens(result, wheat, currentIndex, currentMin.getStartIndex());
        if (currentIndex < currentMin.getStartIndex()) {
            getWhitespaces(wheat, currentMin.getStartIndex()).ifPresent(result::add);//пробелы справа
        }
        result.add(MIN_START);
        appendTokens(result, wheat, currentMin.getStartIndex(), currentMin.getEndIndex());//без пробелов по краям
        result.add(END);
        currentIndex = currentMin.getEndIndex();
        minIndex++;
        while (minIndex < minRanges.size() && minRanges.get(minIndex).getEndIndex() < currentMax.getEndIndex()) {
            currentMin = minRanges.get(minIndex);
            getWhitespaces(wheat, currentIndex).ifPresent(result::add);
            if (currentIndex < currentMin.getStartIndex()) {
                appendTokens(result, wheat, currentIndex, currentMin.getStartIndex());//пробелы справа и слева
                getWhitespaces(wheat, currentMin.getStartIndex()).ifPresent(result::add);
            }
            result.add(MIN_START);
            appendTokens(result, wheat, currentMin.getStartIndex(), currentMin.getEndIndex());//без пробелов по краям
            result.add(END);
            currentIndex = currentMin.getEndIndex();
            minIndex++;
        }
        if (currentIndex < currentMax.getEndIndex()) {
            getWhitespaces(wheat, currentIndex).ifPresent(result::add);
        }
        appendTokens(result, wheat, currentIndex, currentMax.getEndIndex());//пробелы слева
        return minIndex;
    }

    public static Optional<String> getWhitespaces(EscapedHtmlString wheat, int fragmentEndIndex) {
        EscapedHtmlString[] grain = wheat.getGrain();
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
     * @param wheat      исходный текст, содержащий пробелы
     * @param rangeStart индекс начала извлекаемого фрагмента в grain. Отсчитывается с 0, включительно.
     * @param rangeEnd   индекс конца извлекаемого фрагмента в grain. Отсчитывается с 0, исключительно.
     */
    public static void appendTokens(List<String> tokenList,
                                    EscapedHtmlString wheat,
                                    int rangeStart,
                                    int rangeEnd) {
        EscapedHtmlString[] grain = wheat.getGrain();
        if (rangeStart > rangeEnd) {
            throw new IllegalArgumentException("Начало интервала должно быть меньше конца интервала");
        }
        if (rangeEnd > wheat.length()) {
            throw new IllegalArgumentException("Длина интервала должна быть меньше суммарной длины текста");
        }
        int totalLength = wheat.getTotalLength();
        if (rangeEnd > totalLength) {
            throw new IllegalArgumentException("Длина интервала должна быть меньше суммарной длины текста");
        }
        if (rangeStart == totalLength) {
            return;
        }
        PositionOfFragment position = wheat.find(rangeStart);
        int grainIndex = position.grainIndex;//просматриваемый фрагмент grain
        int startInWheat = position.startInWheat;
        int startInGrain = position.startInGrain;
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
}
