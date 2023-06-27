package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "requester_id")
    private User requester;

    @CreationTimestamp
    private LocalDateTime created;

    public ItemRequest(String description) {
        this.description = description;
    }
}
