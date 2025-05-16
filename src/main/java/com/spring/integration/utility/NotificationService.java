package com.spring.integration.utility;


import com.spring.integration.integration.CurrencyIntegrationFlow;
import com.spring.integration.model.CurrencyRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NotificationService {
    public static void notifyIfNeeded(List<CurrencyRate> rates) {
        System.out.println("Sending notification for rates: "+ rates);
        rates.stream()
                .filter(r -> Math.abs(r.getChangePercent()) > 5)
                .forEach(r -> System.out.println("Alert: " + r.getCurrencyCode() + " changed by " + r.getChangePercent() + "%"));
    }

}
