package com.madvena.shorturl.repository;

import com.madvena.shorturl.model.Url;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Integer> {

    Optional<Url> findById(Integer id);

    Optional<Url> findByOriginal(String original);

    @Query(value = "SELECT * FROM url ORDER BY count DESC ", nativeQuery = true)
    Page<Url> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM url ORDER BY count DESC ", nativeQuery = true)
    List<Url> findAll();
}
