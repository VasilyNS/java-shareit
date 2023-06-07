package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    /**
     * Возвращаются только Request которые делал сам пользователь
     */
    @Query("select r from Request r " +
            "where r.requestor = ?1 ")
    List<Request> findByOwn(User user);

    /**
     * Возвращаются только Request для вещи, которой владеет пользователь
     * Пример нативного SQL-запроса с пагинацией, настоящий SQL более гибок!
     */
    @Query(value = "SELECT * FROM REQUESTS r, ITEMS i WHERE (i.REQUEST_ID IS NOT NULL) AND (i.OWNER_ID = ?1)",
            countQuery = "SELECT count(*) FROM REQUESTS r, ITEMS i WHERE (i.REQUEST_ID IS NOT NULL) AND (i.OWNER_ID = ?1)",
            nativeQuery = true)
    Page<Request> findAllByItemsOwner(Long userId, Pageable page);

}
