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

    public int length() {
        return value.length();
    }

    public EscapedHtmlString substring(int beginIndex, int endIndex) {
        return new EscapedHtmlString(value.substring(beginIndex, endIndex));
    }

    public EscapedHtmlString substring(int beginIndex) {
        return new EscapedHtmlString(value.substring(beginIndex));
    }

    public static EscapedHtmlString make(String text){
        return new EscapedHtmlString(HtmlUtils.htmlEscape(text));
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
    public String toString() {
        return value;
    }

    public EscapedHtmlString[] split(String regex) {
        String[] strArr = value.split(regex);
        EscapedHtmlString[] result = new EscapedHtmlString[strArr.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new EscapedHtmlString(strArr[i]);
        }
        return result;
    }
}
