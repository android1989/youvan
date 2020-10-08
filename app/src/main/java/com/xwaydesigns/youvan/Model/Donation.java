package com.xwaydesigns.youvan.Model;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Donation
{

    public String donation_id;
    public String item_name;
    public String item_quantity;
    public String item_image;
    public String item_status;
    public String donation_date;

    public Donation(String donation_id, String item_name, String item_quantity, String item_image, String item_status, String donation_date) {
        this.donation_id = donation_id;
        this.item_name = item_name;
        this.item_quantity = item_quantity;
        this.item_image = item_image;
        this.item_status = item_status;
        this.donation_date = donation_date;
    }

    public String getDonation_id() {
        return donation_id;
    }

    public void setDonation_id(String donation_id) {
        this.donation_id = donation_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_quantity() {
        return item_quantity;
    }

    public void setItem_quantity(String item_quantity) {
        this.item_quantity = item_quantity;
    }

    public String getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }

    public String getItem_status() {
        return item_status;
    }

    public void setItem_status(String item_status) {
        this.item_status = item_status;
    }

    public String getDonation_date() {
        return donation_date;
    }

    public void setDonation_date(String donation_date) {
        this.donation_date = donation_date;
    }
}
