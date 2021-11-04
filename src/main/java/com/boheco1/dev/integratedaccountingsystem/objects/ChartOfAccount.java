package com.boheco1.dev.integratedaccountingsystem.objects;

public class ChartOfAccount {
    private String accountCode;
    private String description;
    private char accountType;
    private int levelNumber;
    private String summaryAccount;
    private char glsl;
    private String oldAccountCode;
    private String newDescription;
    private String oldSummaryAccount;
    private String status;

    public ChartOfAccount(String accountCode, String description, char accountType, int levelNumber, String summaryAccount, char glsl, String oldAccountCode, String newDescription, String oldSummaryAccount, String status) {
        this.accountCode = accountCode;
        this.description = description;
        this.accountType = accountType;
        this.levelNumber = levelNumber;
        this.summaryAccount = summaryAccount;
        this.glsl = glsl;
        this.oldAccountCode = oldAccountCode;
        this.newDescription = newDescription;
        this.oldSummaryAccount = oldSummaryAccount;
        this.status = status;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public char getAccountType() {
        return accountType;
    }

    public void setAccountType(char accountType) {
        this.accountType = accountType;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public String getSummaryAccount() {
        return summaryAccount;
    }

    public void setSummaryAccount(String summaryAccount) {
        this.summaryAccount = summaryAccount;
    }

    public char getGlsl() {
        return glsl;
    }

    public void setGlsl(char glsl) {
        this.glsl = glsl;
    }

    public String getOldAccountCode() {
        return oldAccountCode;
    }

    public void setOldAccountCode(String oldAccountCode) {
        this.oldAccountCode = oldAccountCode;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
    }

    public String getOldSummaryAccount() {
        return oldSummaryAccount;
    }

    public void setOldSummaryAccount(String oldSummaryAccount) {
        this.oldSummaryAccount = oldSummaryAccount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
