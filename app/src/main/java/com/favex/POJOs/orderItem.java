package com.favex.POJOs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    public static JSONArray orderItemsListToJsonArray(ArrayList<OrderItem> list) {
        JSONArray jArray = new JSONArray();

        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();// /sub Object

            try
            {
                jsonObject.put("itemName", list.get(i).getItemName());
                jsonObject.put("quantity", list.get(i).getQuantity());
                jArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jArray;
    }
    public static ArrayList<OrderItem> jsonArraytoOrderItemsList(JSONArray jArray) {
        ArrayList<OrderItem> list = new ArrayList<>();
        for (int i = 0; i < jArray.length(); i++) {
            try {
                JSONObject jsonObject = jArray.getJSONObject(i);
                OrderItem orderItem = new OrderItem(jsonObject.getString("itemName"),
                        jsonObject.getInt("quantity"));
                list.add(orderItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
