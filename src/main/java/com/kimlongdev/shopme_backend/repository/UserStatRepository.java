package com.kimlongdev.shopme_backend.repository;

import com.kimlongdev.shopme_backend.entity.user.UserStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatRepository extends JpaRepository<UserStat, Long> {
}
