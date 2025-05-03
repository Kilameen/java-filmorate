package ru.yandex.practicum.filmorate.enums;

public enum SearchParameter {
    DIRECTOR("director"),
    TITLE("title"),
    DIRECTOR_AND_TITLE("director,title"),
    TITLE_AND_DIRECTOR("title,director");

    private final String value;

    SearchParameter(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValid(String str) {
        for (SearchParameter param : SearchParameter.values()) {
            if (param.getValue().equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static SearchParameter fromString(String str) {
        for (SearchParameter param : SearchParameter.values()) {
            if (param.getValue().equals(str)) {
                return param;
            }
        }
        throw new IllegalArgumentException("Недопустимый параметр: " + str);
    }
}