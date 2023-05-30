package com.boheco1.dev.integratedaccountingsystem.objects;

public class APP {
    private String appId;
    private String year;
    private boolean isOpen;
    private String boardRes;

    public APP(){}

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getBoardRes() {
        return boardRes;
    }

    public void setBoardRes(String boardRes) {
        this.boardRes = boardRes;
    }
}
