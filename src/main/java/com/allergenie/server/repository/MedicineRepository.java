package com.allergenie.server.repository;

import com.allergenie.server.domain.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    @Query("SELECT m FROM Medicine m " +
            "WHERE m.name LIKE CONCAT('%', :search, '%') " +
            "ORDER BY CASE WHEN m.name = :search THEN 0 ELSE 1 END, m.name ASC")
    Page<Medicine> findByNameContaining(@Param("search") String search, Pageable pageable);

    Medicine findByMedicineId(Long medicineId);

}
