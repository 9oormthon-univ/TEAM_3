package com.allergenie.server.repository;

import com.allergenie.server.domain.Image;
import com.allergenie.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository <Image, Long> {
    Image findByImageId(Long imageId);
}
