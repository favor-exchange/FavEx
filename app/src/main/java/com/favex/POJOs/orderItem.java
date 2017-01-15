package com.favex.POJOs;

/**
 * Created by Tavish on 15-Jan-17.
 */

public class OrderItem {
    private String itemName;
    private float cost;
    private int quantity;
    public OrderItem(String n, float c, int q)
    {
        itemName=n;
        cost=c;
        quantity=q;
    }
    public String getItemName(){return itemName;}
    public float getCost(){return cost;}
    public int getQuantity(){return quantity;}
    public void setItemName(String value){itemName=value;}
    public void setCost(float value){cost=value;}
    public void setQuantity(int value){quantity=value;}
}
