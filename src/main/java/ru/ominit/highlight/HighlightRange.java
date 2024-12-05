package ru.ominit.highlight;

import java.util.*;
import java.util.stream.Collectors;

public class HighlightRange {
    private final int startIndex;
    private final int endIndex;

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
    /**
     * Возвращает индекс первого символа из subarr внутри arr, начиная с $fromIndex
     * Подсчет ведется с нулевого символа внутри нулевого элемента arr. Подсчет сквозной.
     * Если удалось найти начало только во втором элементе,
     * то возвращается длина первого элемента + смещение во втором.
     *
     * @param arr       массив, в котором происходит поиск
     * @param subarr    искомый массив
     * @param fromIndex позиция с которой нужно начинать поиск
     * @return индекс начала subarr внутри arr. -1 если найти не удалось.
     */
    public static int indexOfInArr(EscapedHtmlString[] arr, EscapedHtmlString[] subarr, int fromIndex) {
        int remaining = fromIndex;
        int substracted = 0;
        int currentArrIndex = 0;
        while (remaining > arr[currentArrIndex].length()) {
            remaining -= arr[currentArrIndex].length();
            substracted += arr[currentArrIndex].length();
            currentArrIndex++;
        }

        int position = substracted;

        if (subarr.length == 1) {
            for (int i = currentArrIndex; i < arr.length; i++) {
                int initial = arr[i].indexOf(subarr[0], remaining);
                remaining = 0;
                if (initial >= 0) {
                    position += initial;
                    return position;
                }
                position += arr[i].length();
            }
            return -1;
        }

        for (int i = currentArrIndex; i < arr.length - subarr.length + 1; i++) {
            if (arr[i].substring(remaining).endsWith(subarr[0])) {
                int initial = arr[i].substring(remaining).lastIndexOf(subarr[0]);
                position += remaining;
                int checkIndex = i + 1;
                boolean found = true;
                for (int j = 1; j < subarr.length - 1; j++) {
                    if (!subarr[j].equals(arr[checkIndex])) {
                        found = false;
                        break;
                    }
                    checkIndex++;
                }
                if (found && arr[checkIndex].startsWith(subarr[subarr.length - 1])) {
                    return position + initial;
                }
            }
            position += arr[i].length();
            remaining = 0;
        }
        return -1;
    }

    public static List<HighlightRange> highlightAll(EscapedHtmlString[] grain, EscapedHtmlString[] fragments, EscapedHtmlString[] context) {
        List<HighlightRange> result = new ArrayList<>();
        int minusChars = indexOfInArr(context, fragments, 0);
        int start = indexOfInArr(grain, fragments, 0);
        int contextStart = indexOfInArr(grain, context, start >= minusChars ? start - minusChars : 0);
        int contextEnd = contextStart + length(context);
        while (start >= 0) {
            int end = start + length(fragments);
            if (contextStart <= start && end <= contextEnd) {
                result.add(new HighlightRange(start, end));
            }
            start = indexOfInArr(grain, fragments, end);
            contextStart = indexOfInArr(grain, context, start >= minusChars ? start - minusChars : 0);
            contextEnd = contextStart + length(context);
        }
        return result;
    }

    public static int length(EscapedHtmlString[] grain) {
        return Arrays.stream(grain).mapToInt(EscapedHtmlString::length).sum();
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
            int currentIndex = currentMax.startIndex;
            appendTokens(result, wheat, 0, currentIndex);//пробелы справа
            getWhitespaces(wheat, currentMax.startIndex).ifPresent(result::add);
            result.add(MAX_START);
            appendContentsOfMaxRange(result, wheat, currentMax, minRanges);
            result.add(END);
            int prevEndIndex = currentMax.endIndex;

            while (maxRangesItr.hasNext()) {
                currentMax = maxRangesItr.next();
                getWhitespaces(wheat, prevEndIndex).ifPresent(result::add);
                if (prevEndIndex < currentMax.startIndex) {
                    appendTokens(result, wheat, prevEndIndex, currentMax.startIndex);
                    getWhitespaces(wheat, currentMax.startIndex).ifPresent(result::add);
                }
                result.add(MAX_START);
                appendContentsOfMaxRange(result, wheat, currentMax, minRanges);
                result.add(END);
                prevEndIndex = currentMax.endIndex;
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
            int currentIndex = current.startIndex;
            appendTokens(result, wheat, 0, currentIndex);
            getWhitespaces(wheat, current.startIndex).ifPresent(result::add);
            result.add(ANSWER_START);
            appendTokens(result, wheat, current.startIndex, current.endIndex);
            result.add(END);
            int prevEndIndex = current.endIndex;
            while (rangesItr.hasNext()) {
                current = rangesItr.next();
                getWhitespaces(wheat, prevEndIndex).ifPresent(result::add);
                if (prevEndIndex < current.startIndex) {
                    appendTokens(result, wheat, prevEndIndex, current.startIndex);
                    getWhitespaces(wheat, current.startIndex).ifPresent(result::add);
                }
                result.add(ANSWER_START);
                appendTokens(result, wheat, current.startIndex, current.endIndex);
                result.add(END);
                prevEndIndex = current.endIndex;
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
                                               List<HighlightRange> allMinRanges) {
        List<HighlightRange> minRanges = allMinRanges.stream().filter(currentMax::contains).collect(Collectors.toList());
        int minIndex = 0;
        int currentIndex = currentMax.startIndex;
        HighlightRange currentMin = minRanges.get(minIndex);
        if (currentMin.startIndex < currentMax.startIndex) {
            throw new IllegalArgumentException("Индекс начала первого минимального диапазона должен быть больше или равен индексу максимального диапазона");
        }
        if (!currentMax.contains(currentMin)) {//Хотя бы один диапазон должен находиться внутри максимального
            throw new IllegalArgumentException("Максимальный диапазон не содержит минимальный диапазон на указанном индексе");
        }
        appendTokens(result, wheat, currentIndex, currentMin.startIndex);
        if (currentIndex < currentMin.startIndex) {
            getWhitespaces(wheat, currentMin.startIndex).ifPresent(result::add);//пробелы справа
        }
        result.add(MIN_START);
        appendTokens(result, wheat, currentMin.startIndex, currentMin.endIndex);//без пробелов по краям
        result.add(END);
        currentIndex = currentMin.endIndex;
        minIndex++;
        while (minIndex < minRanges.size() && minRanges.get(minIndex).endIndex <= currentMax.endIndex) {
            currentMin = minRanges.get(minIndex);
            getWhitespaces(wheat, currentIndex).ifPresent(result::add);
            if (currentIndex < currentMin.startIndex) {
                appendTokens(result, wheat, currentIndex, currentMin.startIndex);//пробелы справа и слева
                getWhitespaces(wheat, currentMin.startIndex).ifPresent(result::add);
            }
            result.add(MIN_START);
            appendTokens(result, wheat, currentMin.startIndex, currentMin.endIndex);//без пробелов по краям
            result.add(END);
            currentIndex = currentMin.endIndex;
            minIndex++;
        }
        if (currentIndex < currentMax.endIndex) {
            getWhitespaces(wheat, currentIndex).ifPresent(result::add);
        }
        appendTokens(result, wheat, currentIndex, currentMax.endIndex);//пробелы слева
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
