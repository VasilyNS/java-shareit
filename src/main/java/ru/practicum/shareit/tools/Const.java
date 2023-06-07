package ru.practicum.shareit.tools;

public class Const {

    /**
     * Заголовок HTTP, где указан идентификатор пользователя, который добавляет вещь
     */
    public static final String X_OWNER = "x-sharer-user-id";

    /**
     * При незаданной пагинации используем это значение для ограничения выдачи и защиты от флуда
     */
    public static final int MAX_PAGE_SIZE = 20000;

}
