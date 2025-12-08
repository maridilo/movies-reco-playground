package com.acme.reco.api.config;

import com.acme.reco.domain.SimilarService;
import com.acme.reco.persistence.repo.MovieJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean SimilarService similarService(MovieJpaRepository movies) {
        return new SimilarService(movies);
    }
}
