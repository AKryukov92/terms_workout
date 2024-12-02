package ru.ominit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.web.util.HtmlUtils;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.journey.HaystackProgress;
import ru.ominit.journey.Journey;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Александр on 30.03.2018.
 */
public class Haystack {
    @JacksonXmlProperty(localName = "wheat")
    private String wheat;
    @JsonIgnore
    private String[] grain;
    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;

    @JacksonXmlElementWrapper(localName = "riddles")
    @JacksonXmlProperty(localName = "riddle")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<Riddle> riddles;

    @JacksonXmlProperty(localName = "hint_keyword")
    private String hint_keyword;

    public Haystack() {
        riddles = new ArrayList<>();
    }

    public Haystack(
            String wheat,
            List<Riddle> riddles
    ) {
        this.wheat = wheat;
        this.riddles = riddles;
    }

    /**
     * Получение случайной задачи из стога
     *
     * @param rnd ГПСЧ
     * @return одну из задач этого стога
     */
    public Riddle getRiddle(Random rnd) {
        int next = rnd.nextInt(riddles.size());
        return riddles.get(next);
    }

    public Optional<Riddle> getFreshRiddle(Random rnd, String haystackId, Journey journey) {
        HaystackProgress progress = journey.reportProgress(this, haystackId);
        if (progress.currentProgress() < progress.maxProgress()) {
            return progress.getRiddlesProgress().entrySet().stream()
                    .filter(t -> !t.getValue().isPartiallySolved())
                    .findFirst()
                    .map(Map.Entry::getKey)
                    .flatMap(this::getRiddle);
        } else {
            List<String> notFullySolvedRiddles = progress.getRiddlesProgress().entrySet().stream()
                    .filter(t -> !t.getValue().isFullySolved())
                    .map(Map.Entry::getKey).collect(Collectors.toList());
            if (!notFullySolvedRiddles.isEmpty()) {
                int next = rnd.nextInt(notFullySolvedRiddles.size());
                return getRiddle(notFullySolvedRiddles.get(next));
            } else {
                return Optional.empty();
            }
        }
    }

    public String getWheat() {
        return wheat;
    }

    public String[] getGrain() {
        if (grain == null) {
            grain = wheat.split("\\s+");
        }
        return grain;
    }

    public boolean isRelevant(String attempt) {
        String[] tokens = attempt.split("\\s+");
        String[] grain = getGrain();
        return indexOfInArr(grain, tokens) >= 0;
    }

    /**
     * Возвращает индекс первого символа из subarr внутри arr.
     * Подсчет ведется с нулевого символа внутри нулевого элемента arr. Подсчет сквозной.
     * Если удалось найти начало только во втором элементе,
     * то возвращается длина первого элемента + смещение во втором.
     *
     * @param arr    массив, в котором происходит поиск
     * @param subarr искомый массив
     * @return индекс начала subarr внутри arr. -1 если найти не удалось.
     */
    public static int indexOfInArr(String[] arr, String[] subarr) {
        return indexOfInArr(arr, subarr, 0);
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
    public static int indexOfInArr(String[] arr, String[] subarr, int fromIndex) {
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

    /**
     * Получение задачи из стога по идентификатору.
     *
     * @param riddleId идентификатор задачи
     * @return задачу из стога с совпадающим идентификатором или None
     */
    public Optional<Riddle> getRiddle(String riddleId) {
        for (Riddle riddle : riddles) {
            if (riddle.getId().equals(riddleId)) {
                return Optional.of(riddle);
            }
        }
        return Optional.empty();
    }

    /**
     * Получение задачи из стога по тексту загадки.
     *
     * @param needle текст загадки
     * @return задачу из стога с совпадающим текстом загадки или None
     */
    public Optional<Riddle> getRiddleByNeedle(String needle) {
        for (Riddle riddle : riddles) {
            if (riddle.getNeedle().equals(needle)) {
                return Optional.of(riddle);
            }
        }
        return Optional.empty();
    }

    public String highlightNeedle(String needle) {
        return getRiddleByNeedle(needle)
                .map(riddle -> riddle.insert(EscapedHtmlString.make(getWheat())))
                .orElseGet(() -> HtmlUtils.htmlEscape(getWheat()));
    }

    public void addRiddle(Riddle riddle) {
        riddles.add(riddle);
    }

    public List<String> listRiddleIds() {
        return riddles
                .stream()
                .map(Riddle::getId)
                .collect(Collectors.toList());
    }

    public List<Riddle> listRiddles() {
        return Collections.unmodifiableList(riddles);
    }

    public static Haystack DEFAULT() {
        ArrayList<Riddle> riddles = new ArrayList<>();
        riddles.add(Riddle.DEFAULT());
        return new Haystack("жизнь", riddles);
    }

    public String getHint_keyword() {
        return hint_keyword;
    }
}
