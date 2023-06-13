package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(User userSearch);

    @Query("select i from Item i " +
            "where i.request = ?1 ")
    List<Item> findByRequest(Request requestSearch);

    @Query("select i from Item i " +
            "where i.request in ?1 ")
    List<Item> findByRequestForArray(List<Request> requestsSearch);

    @Query("select i from Item i " +
            "where ( upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')) ) " +
            "and i.available = true")
    List<Item> getItemsByText(String text);

}
