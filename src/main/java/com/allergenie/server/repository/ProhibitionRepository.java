package com.allergenie.server.repository;

import com.allergenie.server.domain.Medicine;
import com.allergenie.server.domain.Prohibition;
import com.allergenie.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProhibitionRepository extends JpaRepository<Prohibition, Long> {
    List<Prohibition> findByUser(User user);
    Prohibition findByUserAndMedicine(User user, Medicine medicine);
}
