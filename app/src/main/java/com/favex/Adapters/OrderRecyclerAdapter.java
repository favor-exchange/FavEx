package com.favex.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.favex.POJOs.OrderItem;
import com.favex.R;

import java.util.ArrayList;

/**
 * Created by Tavish on 15-Jan-17.
 */

public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.OrderViewHolder> {
    private LayoutInflater layoutInflater;
    private ArrayList<OrderItem> orderItems;
    public OrderRecyclerAdapter(Context context, ArrayList<OrderItem> list)
    {
        layoutInflater= LayoutInflater.from(context);
        orderItems=list;
    }
    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= layoutInflater.inflate(R.layout.order_list_item, parent, false);
        return new OrderViewHolder(view);
    }
    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        OrderItem item= orderItems.get(position);
        holder.mItemName.setText(item.getItemName());
        holder.mQuantity.setText(String.valueOf(item.getQuantity()));
    }

    @Override
    public int getItemCount() {
        if(orderItems!= null)
            return orderItems.size();
        else
            return 0;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder{
        private TextView mItemName;
        private TextView mCost;
        private TextView mQuantity;
        public OrderViewHolder(View itemView) {
            super(itemView);
            mItemName= (TextView)itemView.findViewById(R.id.itemName);
            mCost= (TextView)itemView.findViewById(R.id.cost);
            mQuantity= (TextView)itemView.findViewById(R.id.quantity);
        }
    }
}
