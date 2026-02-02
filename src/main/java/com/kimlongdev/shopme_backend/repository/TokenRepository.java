package com.kimlongdev.shopme_backend.repository;

import com.kimlongdev.shopme_backend.entity.user.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    // JOIN FETCH bảo Hibernate:
    // "Khi tìm Token, làm ơn lấy luôn cả thằng User đi kèm giúp tao trong cùng 1 lần query".
    // Nhờ đó, dù Session có đóng, data User đã nằm sẵn trong object rồi,
    // không cần query lại nữa.
    @Query("""
        SELECT t FROM Token t
        INNER JOIN FETCH t.user u
        WHERE t.refreshToken = :refreshToken
    """)
    Optional<Token> findByRefreshToken(String refreshToken);

    @Query("""
        SELECT t FROM Token t\s
        INNER JOIN t.user u\s
        WHERE u.userId = :userId\s
          AND t.expired = false\s
          AND t.revoked = false
   \s""")
    List<Token> findAllValidTokenByUser(Long userId);
}
