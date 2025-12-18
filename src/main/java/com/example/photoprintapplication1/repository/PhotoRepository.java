package com.example.photoprintapplication1.repository;

import com.example.photoprintapplication1.models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    @Query("SELECT p FROM Photo p WHERE p.order.user.id = :userId")
    List<Photo> findByOrderUserId(Long userId);

    @Query("SELECT f.id, f.name, COUNT(p) FROM Photo p JOIN p.format f GROUP BY f.id, f.name ORDER BY COUNT(p) DESC")
    List<Object[]> findPopularFormats();
}