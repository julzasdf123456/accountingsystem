package com.boheco1.dev.integratedaccountingsystem.objects;

import java.util.List;

public class UnchargedMIRSReleases {
    private MIRS mirs;
    private List<UnchargedItemDetails> releases;

    public UnchargedMIRSReleases() {
        mirs = null;
        releases = null;
    }

    public UnchargedMIRSReleases(MIRS mirs, List<UnchargedItemDetails> releases) {
        this.mirs = mirs;
        this.releases = releases;
    }

    public MIRS getMirs() {
        return mirs;
    }

    public void setMirs(MIRS mirs) {
        this.mirs = mirs;
    }

    public List<UnchargedItemDetails> getReleases() {
        return releases;
    }

    public void setReleases(List<UnchargedItemDetails> releases) {
        this.releases = releases;
    }
}
