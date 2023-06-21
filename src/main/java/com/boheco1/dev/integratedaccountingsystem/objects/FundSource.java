package com.boheco1.dev.integratedaccountingsystem.objects;

public class FundSource {

    private String fsId;
    private String source;

    public FundSource() {}

    public String getFsId() {
        return fsId;
    }

    public void setFsId(String fsId) {
        this.fsId = fsId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    @Override
    public String toString() {
        return this.source;
    }
}
