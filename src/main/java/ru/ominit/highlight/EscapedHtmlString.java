package ru.ominit.highlight;

import org.springframework.web.util.HtmlUtils;

public class EscapedHtmlString {
    public final String value;

    private EscapedHtmlString(String text) {
        value = text;
    }

    public int indexOf(EscapedHtmlString fragment) {
        return value.indexOf(fragment.value);
    }

    public int indexOf(EscapedHtmlString str, int fromIndex) {
        return value.indexOf(str.value, fromIndex);
    }

    public int lastIndexOf(EscapedHtmlString str, int fromIndex) {
        return value.lastIndexOf(str.value, fromIndex);
    }

    public int length() {
        return value.length();
    }

    public EscapedHtmlString substring(int beginIndex, int endIndex) {
        return new EscapedHtmlString(value.substring(beginIndex, endIndex));
    }

    public EscapedHtmlString substring(int beginIndex) {
        return new EscapedHtmlString(value.substring(beginIndex));
    }

    public boolean startsWith(EscapedHtmlString prefix) {
        return value.startsWith(prefix.value);
    }

    public boolean endsWith(EscapedHtmlString suffix) {
        return value.endsWith(suffix.value);
    }

    public int lastIndexOf(EscapedHtmlString str) {
        return value.lastIndexOf(str.value);
    }

    public static EscapedHtmlString make(String text) {
        return new EscapedHtmlString(HtmlUtils.htmlEscape(text));
    }

    public int indexOfNextNonWhitespace(int beginIndex) {
        int i = beginIndex + 1;
        while (i < value.length()) {
            char current = value.charAt(i);
            if (!Character.isWhitespace(current)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Тут пока не понятно, что правильно возвращать.
     * С одной стороны, в результате конкатенации получится неэкранированный текст.
     * С другой - эта функция для того и используется.
     * Возможно нужно ввести новый тип для этого, но пока оставляю костыль.
     *
     * @param otherString строка, которая будет приклеена в конец этой
     * @return экземпляр экранированной строки
     */
    public EscapedHtmlString concatWith(String otherString) {
        return new EscapedHtmlString(value + otherString);
    }

    public EscapedHtmlString concatWith(EscapedHtmlString otherEscapedString) {
        return new EscapedHtmlString(value + otherEscapedString.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EscapedHtmlString that = (EscapedHtmlString) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return value;
    }

    public EscapedHtmlString[] splitByWhitespace() {
        String[] strArr = value.split("\\s+");
        EscapedHtmlString[] result = new EscapedHtmlString[strArr.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new EscapedHtmlString(strArr[i]);
        }
        return result;
    }
}
