package ru.practicum.shareit.booking;

/**
 * Статус бронирования
 * Может принимать одно из следующих значений:<br>
 * WAITING=1 — новое бронирование, ожидает одобрения,<br>
 * APPROVED=2 — бронирование подтверждено владельцем,<br>
 * REJECTED=3 — бронирование отклонено владельцем,<br>
 * CANCELED=4 — бронирование отменено создателем.<br>
 */
public enum BookingStatus {
    WAITING(1),
    APPROVED(2),
    REJECTED(3),
    CANCELED(4);

    private int code;

    BookingStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
