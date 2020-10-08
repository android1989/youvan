package com.xwaydesigns.youvan.Model;

import android.graphics.Bitmap;

public class DonateNow
{
    Bitmap bitmap;
    String name;
    String quantity;
    String date;

    public DonateNow(Bitmap bitmap, String name, String quantity, String date) {
        this.bitmap = bitmap;
        this.name = name;
        this.quantity = quantity;
        this.date = date;
    }

    public Bitmap getBitmap() { return bitmap; }

    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
