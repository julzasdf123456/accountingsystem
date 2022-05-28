package com.boheco1.dev.integratedaccountingsystem.objects;

import java.util.List;

public class MCTReleasings {
    private MCT mct;
    private List<Releasing> releasings;

    public MCTReleasings(MCT mct, List<Releasing> releasings) {
        this.mct = mct;
        this.releasings = releasings;
    }

    public MCTReleasings() {
        mct=null;
        releasings=null;
    }

    public MCT getMct() {
        return mct;
    }

    public void setMct(MCT mct) {
        this.mct = mct;
    }

    public List<Releasing> getReleasings() {
        return releasings;
    }

    public void setReleasings(List<Releasing> releasings) {
        this.releasings = releasings;
    }
}
