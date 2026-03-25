package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRuleConstants;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceRuleService {

    /**
     * Resolves the warning threshold to use for a maintenance rule.
     * <p>
     * If {@code warningThresholdKm} is {@code null} or less than 1, the system
     * default ({@link MaintenanceRuleConstants#DEFAULT_WARNING_THRESHOLD_KM}) is
     * returned. Otherwise, the supplied value is returned as-is.
     *
     * @param warningThresholdKm value provided by the client (nullable)
     * @return the resolved threshold in kilometres
     */
    public int resolveWarningThreshold(Integer warningThresholdKm) {
        if (warningThresholdKm == null || warningThresholdKm < 1) {
            return MaintenanceRuleConstants.DEFAULT_WARNING_THRESHOLD_KM;
        }
        return warningThresholdKm;
    }
}
