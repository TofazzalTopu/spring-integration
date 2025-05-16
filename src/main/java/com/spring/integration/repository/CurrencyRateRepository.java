package com.spring.integration.repository;

import com.spring.integration.model.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, String> {
    Optional<CurrencyRate> findByCurrencyCode(String code);
}
