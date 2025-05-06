package ru.yandex.practicum.filmorate.enums;

public enum SortType {
    YEAR("year"),
    LIKES("likes");

    private final String value;

    SortType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SortType fromString(String str) {
        for (SortType param : SortType.values()) {
            if (param.getValue().equals(str)) {
                return param;
            }
        }
        throw new IllegalArgumentException("Недопустимый параметр сортировки: " + str);
    }
}