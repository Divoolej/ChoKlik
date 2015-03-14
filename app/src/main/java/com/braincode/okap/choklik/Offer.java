package com.braincode.okap.choklik;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by hubert on 13.03.15.
 */

public class Offer {
    private String description;
    private String photoUrl;
    private String sellerName;
    private String offerUrl;
    private boolean buyNow;
    private boolean auction;
    private double price;
    private Long endingTime;

    public String getDescription() {
        return description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getOfferUrl() {
        return offerUrl;
    }

    public boolean isBuyNow() {
        return buyNow;
    }

    public boolean isAuction() {
        return auction;
    }

    public double getPrice() {
        return price;
    }

    public String getEndingTimeString() {
        int days = (int)(endingTime / 86400);
        int hours = (int)((endingTime % 86400) / 3600);
        int minutes = (int)(((endingTime % 86400) % 3600) / 60);
        return Integer.toString(days) + "d " + Integer.toString(hours) + "h " + Integer.toString(minutes) + "min";
    }

    public Offer (JSONObject object) throws JSONException {
        description = object.getString("name");
        if (!object.isNull("mainImage"))
            photoUrl = object.getJSONObject("mainImage").getString("medium");
        else photoUrl = "shit";
        sellerName = object.getJSONObject("seller").getString("login");
        offerUrl = object.getJSONObject("source").getString("url");
        buyNow = object.getBoolean("buyNow");
        auction = object.getBoolean("auction");
        endingTime = object.getLong("secondsLeft");
        if (isBuyNow())
            price = object.getJSONObject("prices").getDouble("buyNow");
    }

    @Override
    public String toString() {
        return ("description: " + description +
                ", photoUrl: " + photoUrl +
                ", sellerName: " + sellerName +
                ", offerUrl: " + offerUrl +
                ", buyNow: " + Boolean.toString(buyNow) +
                ", price: " + Double.toString(price) +
                ", auction: " + Boolean.toString(auction) +
                ", time left: " + getEndingTimeString());
    }
}
