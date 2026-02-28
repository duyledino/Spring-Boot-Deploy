package com.kimlongdev.shopme_backend.entity.interaction;

import com.kimlongdev.shopme_backend.entity.common.BaseEntity;
import com.kimlongdev.shopme_backend.entity.seller.Seller;
import com.kimlongdev.shopme_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seller_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatMessage> messages;

    @UpdateTimestamp
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;
}