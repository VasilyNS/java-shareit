package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * id — уникальный идентификатор комментария;
 * text — содержимое комментария;
 * item — вещь, к которой относится комментарий;
 * author — автор комментария;
 * created — дата создания комментария.
 * Хранение в CommentRepository, CommentMapper
 */
@Entity
@Table(name = "comments", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    private User author;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime created;

}
