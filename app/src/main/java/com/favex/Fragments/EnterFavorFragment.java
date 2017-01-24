package com.favex.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.favex.Activities.FavorFormActivity;
import com.favex.Adapters.OrderRecyclerAdapter;
import com.favex.POJOs.OrderItem;
import com.favex.PriceRangeSeekBar;
import com.favex.R;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;

/**
 * Created by Tavish on 15-Jan-17.
 */

public class EnterFavorFragment extends Fragment {
    private EditText mItemName;
    private Button mAdd;
    private Button mSub;
    private Button mAddOrderBtn;
    private Button mNextBtn;
    private TextView mItemCount;
    private RecyclerView mOrderRecycler;
    private OrderRecyclerAdapter orderRecyclerAdapter;
    private ArrayList<OrderItem> orderItems;
    private PriceRangeSeekBar mPriceRange;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.enter_favor_fragment, container, false);

        orderItems= new ArrayList<>();
        mItemName= (EditText)view.findViewById(R.id.itemName);
        mItemCount= (TextView)view.findViewById(R.id.itemCount);
        mAdd= (Button)view.findViewById(R.id.add);
        mSub= (Button)view.findViewById(R.id.sub);
        mNextBtn= (Button)view.findViewById(R.id.nextBtn);
        mPriceRange= (PriceRangeSeekBar)view.findViewById(R.id.priceRange);
        mAddOrderBtn= (Button)view.findViewById(R.id.addOrderBtn);
        mOrderRecycler= (RecyclerView)view.findViewById(R.id.orderRecycler);
        orderRecyclerAdapter= new OrderRecyclerAdapter(getActivity(),orderItems);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        mOrderRecycler.setLayoutManager(linearLayoutManager);
        mOrderRecycler.setAdapter(orderRecyclerAdapter);
        ItemTouchHelper itemTouchHelper= initSwipeToDismissHelper();
        itemTouchHelper.attachToRecyclerView(mOrderRecycler);
        mPriceRange.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Number minValue, Number maxValue) {
                ((FavorFormActivity)getActivity()).getPriceRange()[0]=minValue.intValue();
                ((FavorFormActivity)getActivity()).getPriceRange()[1]=maxValue.intValue();
            }
        });
        mItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                if(!mAdd.isEnabled()&& !mAddOrderBtn.isEnabled() &&
                        charSequence.length()>0)
                {
                    mAdd.setEnabled(true);
                    mAddOrderBtn.setEnabled(true);
                }
                else if(charSequence.length()<1)
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
                        Integer.parseInt(mItemCount.getText().toString()));
                orderItems.add(0, item);
                ((FavorFormActivity)getActivity()).copyArrayList(orderItems);
                orderRecyclerAdapter.notifyItemInserted(0);
                Toast.makeText(getActivity(),mItemName.getText()+" added to list of size "+
                        String.valueOf(orderItems.size()),Toast.LENGTH_SHORT).show();
                mItemName.setText("");
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FavorFormActivity)getActivity()).getVerticalViewPager().setCurrentItem(3);
            }
        });
        return view;
    }
    public ItemTouchHelper initSwipeToDismissHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                orderItems.remove(viewHolder.getAdapterPosition());
                orderRecyclerAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };
        return new ItemTouchHelper(simpleItemTouchCallback);
    }
}
