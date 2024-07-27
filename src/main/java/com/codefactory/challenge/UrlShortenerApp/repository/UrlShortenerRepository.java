package com.codefactory.challenge.UrlShortenerApp.repository;

import com.codefactory.challenge.UrlShortenerApp.entity.UrlDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlShortenerRepository extends JpaRepository<UrlDataEntity, Integer> {
}
