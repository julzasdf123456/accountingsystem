package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Period {
    private LocalDateTime period;
    private String status;
    private String lockedBy;
    private LocalDateTime dateLocked;
    private String unlockedBy;
    private LocalDateTime dateUnlocked;

    public Period(LocalDateTime period, String status, String lockedBy, LocalDateTime dateLocked, String unlockedBy, LocalDateTime dateUnlocked) {
        this.period = period;
        this.status = status;
        this.lockedBy = lockedBy;
        this.dateLocked = dateLocked;
        this.unlockedBy = unlockedBy;
        this.dateUnlocked = dateUnlocked;
    }

    public LocalDateTime getPeriod() {
        return period;
    }

    public void setPeriod(LocalDateTime period) {
        this.period = period;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public LocalDateTime getDateLocked() {
        return dateLocked;
    }

    public void setDateLocked(LocalDateTime dateLocked) {
        this.dateLocked = dateLocked;
    }

    public String getUnlockedBy() {
        return unlockedBy;
    }

    public void setUnlockedBy(String unlockedBy) {
        this.unlockedBy = unlockedBy;
    }

    public LocalDateTime getDateUnlocked() {
        return dateUnlocked;
    }

    public void setDateUnlocked(LocalDateTime dateUnlocked) {
        this.dateUnlocked = dateUnlocked;
    }

    public String getFormattedPeriod() {
        if(period==null) return "---";
        return this.period.format(DateTimeFormatter.ofPattern("MMMM - YYYY"));
    }

    public String getFormattedDateLocked() {
        if(dateLocked==null) return "---";
        return this.dateLocked.format(DateTimeFormatter.ofPattern("MMMM dd, YYYY hh:mm"));
    }

    public String getFormattedDateUnlocked() {
        if(dateUnlocked==null) return "---";
        return this.dateUnlocked.format(DateTimeFormatter.ofPattern("MMMM dd, YYYY hh:mm"));
    }

    public boolean isLocked() {
        return this.status.equals("closed");
    }
}
