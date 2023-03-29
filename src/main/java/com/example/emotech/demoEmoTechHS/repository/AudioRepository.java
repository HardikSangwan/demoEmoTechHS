package com.example.emotech.demoEmoTechHS.repository;

import com.example.emotech.demoEmoTechHS.data.Audio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AudioRepository extends JpaRepository<Audio, Long> {
    Audio findByName(String name);

    boolean existsByName(String name);

    @Query(nativeQuery = true, value="SELECT name FROM audio")
    List<String> getAllEntryNames();
}
