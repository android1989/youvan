package com.xwaydesigns.youvan.Model;

public class Reward
{
    public String donation_id;
    public String item_name;
    public String item_quantity;
    public String item_image;
    public String item_rewards;
    public String donation_date;

    public Reward( String donation_id, String item_name, String item_quantity, String item_image, String item_rewards, String donation_date)
    {
        this.donation_id = donation_id;
        this.item_name = item_name;
        this.item_quantity = item_quantity;
        this.item_image = item_image;
        this.item_rewards = item_rewards;
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

    public String getItem_rewards() {
        return item_rewards;
    }

    public void setItem_rewards(String item_rewards) {
        this.item_rewards = item_rewards;
    }

    public String getDonation_date() {
        return donation_date;
    }

    public void setDonation_date(String donation_date) {
        this.donation_date = donation_date;
    }
}
