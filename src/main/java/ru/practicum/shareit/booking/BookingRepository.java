package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /*
     * Для более сложных запросов к БД оптимально использовать JPQL !!!
     * Это гибкость и универсальность (не зависит от конкретной СУБД)
     */

    // Блок для Booking / booker -----------------------------------------------------

    @Query("select b from Booking b " +
            "where b.booker = ?1 " +
            "order by b.start desc ")
    List<Booking> findBookingsForBookerStatusAll(User booker);

    @Query("select b from Booking b " +
            "where b.booker = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> findBookingsForBookerStatusFuture(User booker, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker = ?1 " +
            "and b.status = 'WAITING' ")
    List<Booking> findBookingsForBookerStatusWaiting(User booker, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker = ?1 " +
            "and b.status = 'REJECTED' ")
    List<Booking> findBookingsForBookerStatusRejected(User booker, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start desc ")
    List<Booking> findBookingsForBookerStatusCurrent(User booker, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc ")
    List<Booking> findBookingsForBookerStatusPast(User booker, LocalDateTime now);


    // Блок для Booking / owner -----------------------------------------------------

    @Query("select b from Booking b " +
            "join b.item i " +
            "where i.owner = ?1 " +
            "order by b.start desc ")
    List<Booking> findBookingsForOwnerStatusAll(User owner);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where i.owner = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> findBookingsForOwnerStatusFuture(User owner, LocalDateTime now);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where i.owner = ?1 " +
            "and b.status = 'WAITING' ")
    List<Booking> findBookingsForOwnerStatusWaiting(User owner, LocalDateTime now);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where i.owner = ?1 " +
            "and b.status = 'REJECTED' ")
    List<Booking> findBookingsForOwnerStatusRejected(User owner, LocalDateTime now);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where i.owner = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start desc ")
    List<Booking> findBookingsForOwnerStatusCurrent(User user, LocalDateTime now);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where i.owner = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc ")
    List<Booking> findBookingsForOwnerStatusPast(User user, LocalDateTime now);


    // Блок для last / next -----------------------------------------------------

    @Query("select b from Booking b " +
            "join b.item i " +
            "where b.item = ?1 " +
            "and b.start > ?2 " +
            "and i.owner = ?3 " +
            "and b.status <> 'REJECTED' " +
            "order by b.start asc ")
    List<Booking> getAllBookingsForNext(Item item, LocalDateTime now, User owner);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where b.item = ?1 " +
            "and b.start < ?2 " +
            "and i.owner = ?3 " +
            "and b.status <> 'REJECTED' " +
            "order by b.start desc ")
    List<Booking> getAllBookingsForLast(Item item, LocalDateTime now, User owner);

    // ------------------------------------------------------------------------------------

    /**
     * Число записей для проверки, что отзыв на Item может писать только тот, у кого есть
     * записи в bookings, причем дата окончания меньше чем now.
     * Если записей об этом больше нуля, то этот пользователь брал эту вещь напрокат
     */
    @Query("select count(b) from Booking b " +
            "where b.item = ?1 " +
            "and b.end < ?2 " +
            "and b.booker = ?3 " +
            "and b.status <> 'REJECTED' ")
    Long countListOfBookersForItem(Item item, LocalDateTime now, User user);

}
