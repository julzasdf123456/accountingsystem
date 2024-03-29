package com.boheco1.dev.integratedaccountingsystem.objects;


import java.time.LocalDate;
import java.time.LocalDateTime;

public class Stock {
    private String id;
    private String stockName;
    private String description;
    private String serialNumber;
    private String brand;
    private String model;
    private LocalDate manufacturingDate;
    private LocalDate validityDate;
    private String typeID;
    private String unit;
    private double quantity;
    private int critical;
    private double price;
    private String neaCode;
    private boolean isTrashed;
    private String comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime trashedAt;
    private String userIDCreated;
    private String userIDUpdated;
    private String userIDTrashed;
    private String localCode;
    private String acctgCode;
    private Double oldPrice;
    private StockEntryLog entryLog;
    private Releasing releasing;
    private ReceivingItem receivingItem;
    private String rrNo;
    private String localDescription;
    private boolean individualized;
    private boolean controlled;

    public Stock() {}

    public Stock(String id, String stockName, String model, String brand) {
        this.id = id;
        this.stockName = stockName;
        this.model = model;
        this.brand = brand;
    }

    public Stock(String id, String description, double price) {
        this.id=id;
        this.description=description;
        this.price=price;
    }

    public Stock(String id, String description, int price) {
        this.id=id;
        this.description=description;
        this.price=price;
    }

    /** Constructor with critical **/
    public Stock(String id, String stockName, String description, String serialNumber, String brand, String model, LocalDate manufacturingDate, LocalDate validityDate, String typeID, String unit, double quantity, int critical, double price, String neaCode, boolean isTrashed, String comments, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime trashedAt, String userIDCreated, String userIDUpdated, String userIDTrashed, String localCode, String acctgCode, boolean individualized) {
        this.id = id;
        this.stockName = stockName;
        this.description = description;
        this.serialNumber = serialNumber;
        this.brand = brand;
        this.model = model;
        this.manufacturingDate = manufacturingDate;
        this.validityDate = validityDate;
        this.typeID = typeID;
        this.unit = unit;
        this.quantity = quantity;
        this.critical = critical;
        this.price = price;
        this.neaCode = neaCode;
        this.isTrashed = isTrashed;
        this.comments = comments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.trashedAt = trashedAt;
        this.userIDCreated = userIDCreated;
        this.userIDUpdated = userIDUpdated;
        this.userIDTrashed = userIDTrashed;
        this.localCode = localCode;
        this.acctgCode = acctgCode;
        this.individualized=individualized;
    }

    public boolean isIndividualized() {
        return individualized;
    }

    public void setIndividualized(boolean individualized) {
        this.individualized = individualized;
    }

    public String getLocalCode() {
        return localCode;
    }

    public void setLocalCode(String localCode) {
        this.localCode = localCode;
    }

    public String getAcctgCode() {
        return acctgCode;
    }

    public void setAcctgCode(String acctgCode) {
        this.acctgCode = acctgCode;
    }

    public int getCritical() {
        return critical;
    }

    public void setCritical(int critical) {
        this.critical = critical;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(LocalDate manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public LocalDate getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(LocalDate validityDate) {
        this.validityDate = validityDate;
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNeaCode() {
        return neaCode;
    }

    public void setNeaCode(String neaCode) {
        this.neaCode = neaCode;
    }

    public boolean isTrashed() {
        return isTrashed;
    }

    public void setTrashed(boolean trashed) {
        isTrashed = trashed;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getTrashedAt() {
        return trashedAt;
    }

    public void setTrashedAt(LocalDateTime trashedAt) {
        this.trashedAt = trashedAt;
    }

    public String getUserIDCreated() {
        return userIDCreated;
    }

    public void setUserIDCreated(String userIDCreated) {
        this.userIDCreated = userIDCreated;
    }

    public String getUserIDUpdated() {
        return userIDUpdated;
    }

    public void setUserIDUpdated(String userIDUpdated) {
        this.userIDUpdated = userIDUpdated;
    }

    public String getUserIDTrashed() {
        return userIDTrashed;
    }

    public void setUserIDTrashed(String userIDTrashed) {
        this.userIDTrashed = userIDTrashed;
    }

    public StockEntryLog getEntryLog() {
        return entryLog;
    }

    public void setEntryLog(StockEntryLog entryLog) {
        this.entryLog = entryLog;
    }

    public Releasing getReleasing() {
        return releasing;
    }

    public void setReleasing(Releasing releasing) {
        this.releasing = releasing;
    }

    public ReceivingItem getReceivingItem() {
        return receivingItem;
    }

    public void setReceivingItem(ReceivingItem receivingItem) {
        this.receivingItem = receivingItem;
    }

    public Double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(Double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getRrNo() {
        return rrNo;
    }

    public void setRrNo(String rrNo) {
        this.rrNo = rrNo;
    }

    public String getLocalDescription() {
        return localDescription;
    }

    public void setLocalDescription(String localDescription) {
        this.localDescription = localDescription;
    }

    public boolean isControlled() {
        return controlled;
    }

    public void setControlled(boolean controlled) {
        this.controlled = controlled;
    }

    public String controlled(){
        return isControlled() ? "CON-" : "";
    }
}
