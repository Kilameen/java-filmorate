package ru.yandex.practicum.filmorate.enums;

import org.springframework.data.domain.Sort;

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

    public static boolean isValid(String str) {
        for (SortType type : SortType.values()) {
            if (type.getValue().equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static SortType fromString(String str) {
        for (SortType param : SortType.values()) {
            if (param.getValue().equals(str)) {
                return param;
            }
        }
        throw new IllegalArgumentException("Недопустимый параметр: " + str);
    }
}