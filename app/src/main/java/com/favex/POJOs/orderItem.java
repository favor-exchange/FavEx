package com.favex.POJOs;

/**
 * Created by Tavish on 15-Jan-17.
 */

public class OrderItem {
    private String itemName;
    private int quantity;
    public OrderItem(String n,int q)
    {
        itemName=n;
        quantity=q;
    }
    public String getItemName(){return itemName;}
    public int getQuantity(){return quantity;}
    public void setItemName(String value){itemName=value;}
    public void setQuantity(int value){quantity=value;}
}
