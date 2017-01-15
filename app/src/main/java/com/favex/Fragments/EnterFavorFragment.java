package com.favex.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.favex.Adapters.OrderRecyclerAdapter;
import com.favex.POJOs.OrderItem;
import com.favex.R;

import java.util.ArrayList;

/**
 * Created by Tavish on 15-Jan-17.
 */

public class EnterFavorFragment extends Fragment {
    private EditText mItemName;
    private EditText mPrice;
    private Button mAdd;
    private Button mSub;
    private Button mAddOrderBtn;
    private TextView mItemCount;
    private TextView mTotalCost;
    private RecyclerView mOrderRecycler;
    private OrderRecyclerAdapter orderRecyclerAdapter;
    private ArrayList<OrderItem> orderItems;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enter_favor_fragment, container, false);
        orderItems= new ArrayList<>();
        mItemName= (EditText)view.findViewById(R.id.itemName);
        mPrice= (EditText)view.findViewById(R.id.price);
        mItemCount= (TextView)view.findViewById(R.id.itemCount);
        mAdd= (Button)view.findViewById(R.id.add);
        mSub= (Button)view.findViewById(R.id.sub);
        mTotalCost= (TextView)view.findViewById(R.id.totalCost);
        mAddOrderBtn= (Button)view.findViewById(R.id.addOrderBtn);
        mOrderRecycler= (RecyclerView)view.findViewById(R.id.orderRecycler);
        orderRecyclerAdapter= new OrderRecyclerAdapter(getActivity(),orderItems);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        mOrderRecycler.setLayoutManager(linearLayoutManager);
        mOrderRecycler.setAdapter(orderRecyclerAdapter);
        ItemTouchHelper itemTouchHelper= initSwipeToDismissHelper();
        itemTouchHelper.attachToRecyclerView(mOrderRecycler);
        mItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                if(!mAdd.isEnabled()&& !mAddOrderBtn.isEnabled() &&
                        charSequence.length()>0 && mPrice.getText().length()>0)
                {
                    mAdd.setEnabled(true);
                    mAddOrderBtn.setEnabled(true);
                }
                else if(charSequence.length()<1 || mPrice.getText().length()<1)
                {
                    mAdd.setEnabled(false);
                    mAddOrderBtn.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        mPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!mAdd.isEnabled()&& !mAddOrderBtn.isEnabled() &&
                        charSequence.length()>0 && mItemName.getText().length()>0)
                {
                    mAdd.setEnabled(true);
                    mAddOrderBtn.setEnabled(true);
                }
                else if(charSequence.length()<1 && mItemName.getText().length()<1)
                {
                    mAdd.setEnabled(false);
                    mAddOrderBtn.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemCount= Integer.parseInt(mItemCount.getText().toString());
                mItemCount.setText(String.valueOf(++itemCount));
                mSub.setEnabled(true);
            }
        });
        mSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemCount= Integer.parseInt(mItemCount.getText().toString());
                mItemCount.setText(String.valueOf(--itemCount));
                if(itemCount==1)
                    mSub.setEnabled(false);
            }
        });
        mAddOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderItem item= new OrderItem(mItemName.getText().toString(),
                        Float.parseFloat(mPrice.getText().toString())*Integer.parseInt(mItemCount.getText().toString()),
                        Integer.parseInt(mItemCount.getText().toString()));
                orderItems.add(0,item);
                orderRecyclerAdapter.notifyItemInserted(0);
                mTotalCost.setText(String.valueOf(Integer.parseInt(mTotalCost.getText().toString())
                        +Integer.parseInt(mPrice.getText().toString())*Integer.parseInt(mItemCount.getText().toString())));
                Toast.makeText(getActivity(),mItemName.getText()+" added to list of size "+
                        String.valueOf(orderItems.size()),Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    public ItemTouchHelper initSwipeToDismissHelper()
    {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                orderItems.remove(viewHolder.getAdapterPosition());
                orderRecyclerAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                mTotalCost.setText(String.valueOf(Integer.parseInt(mTotalCost.getText().toString())
                        -orderItems.get(viewHolder.getAdapterPosition()).getCost()));
            }
        };
        return new ItemTouchHelper(simpleItemTouchCallback);
    }
}
