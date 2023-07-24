package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.owner.id = ?1 ORDER BY id ASC")
    List<Item> findAllByOwnerIdOrderByIdAsc(Long owner, Pageable page);

    @Query("SELECT i FROM Item i WHERE (UPPER(i.name) like %?1% " +
            "OR UPPER(i.description) like %?1%) AND i.available = true")
    List<Item> findByNameContainingOrDescriptionContaining(String text, Pageable page);

    List<Item> findByRequestIdOrderByRequestCreatedDesc(long requestId);
}
