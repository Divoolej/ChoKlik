package com.braincode.okap.choklik;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

/**
 * Created by hubert on 14.03.15.
 */
public class OfferClickListener implements View.OnClickListener {

    Offer myOffer;

    public Offer getMyOffer() {
        return myOffer;
    }

    public OfferClickListener(Offer offer) {
        myOffer = offer;
    }

    @Override
    public void onClick(View v) {
    }
}
