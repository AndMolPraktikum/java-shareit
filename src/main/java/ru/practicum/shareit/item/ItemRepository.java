package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderByIdAsc(Long owner);

    @Query("SELECT i FROM Item i WHERE (UPPER(i.name) like %?1% " +
            "OR UPPER(i.description) like %?1%) AND i.available = true")
    List<Item> findByNameContainingOrDescriptionContaining(String text);
}
