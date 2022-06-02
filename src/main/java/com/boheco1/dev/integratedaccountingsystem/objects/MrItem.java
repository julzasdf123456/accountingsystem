package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;

public class MrItem {
    private String id;
    private String stockID;
    private int qty;
    private String remarks;
    private Stock stock;
    private String mrNo;
    private String rrNo;
    public MrItem() { }

    public MrItem(String id, String mrNo, String stockID, int qty, String remarks) {
        this.id = id;
        this.mrNo = mrNo;
        this.stockID = stockID;
        this.qty = qty;
        this.remarks = remarks;
    }

    public String getMrNo() {
        return mrNo;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Stock getStock() throws Exception {
        if(stock==null) {
            stock = StockDAO.get(this.stockID);
        }
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public String getRrNo() {
        return rrNo;
    }
    public void setRrNo(String rrNo) {
        this.rrNo = rrNo;
    }
}
