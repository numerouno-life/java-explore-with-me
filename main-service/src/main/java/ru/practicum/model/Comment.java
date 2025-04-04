package ru.practicum.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.enums.StatusComment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User author;

    @Column(name = "text", nullable = false)
    @NotBlank
    String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    StatusComment status = StatusComment.CHECKING;

    @Column(name = "created", nullable = false)
    @CreationTimestamp
    LocalDateTime created;

    @Column(name = "updated")
    LocalDateTime updated;

    @Column(name = "is_deleted", nullable = false)
    Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    Comment parentComment; // // Родительский комментарий (null, если это корневой комментарий)

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> replies = new ArrayList<>(); // Список ответов на комментарий
}
