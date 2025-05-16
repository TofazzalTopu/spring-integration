package com.spring.integration.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency_rates")
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "currency_code", nullable = false, unique = true)
    private String currencyCode;

   @Column(name = "rate", nullable = false)
    private double rate;

    @Column(name = "previous_rate", nullable = false)
    private double previousRate;

    private LocalDateTime lastUpdated;

    public CurrencyRate() {
    }

    public double getChangePercent() {
        if (previousRate == 0) return 0;
        return ((rate - previousRate) / previousRate) * 100;
    }

    public CurrencyRate(Long id, String currencyCode, double rate, double previousRate, LocalDateTime lastUpdated) {
        this.id = id;
        this.currencyCode = currencyCode;
        this.rate = rate;
        this.previousRate = previousRate;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getPreviousRate() {
        return previousRate;
    }

    public void setPreviousRate(double previousRate) {
        this.previousRate = previousRate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}


