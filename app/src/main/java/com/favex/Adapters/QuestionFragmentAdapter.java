package com.favex.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.favex.Fragments.EnterDestinationFragment;
import com.favex.Fragments.EnterFavorFragment;
import com.favex.Fragments.EnterLocationFragment;
import com.favex.Fragments.EnterTipFragment;

/**
 * Created by Tavish on 08-Jan-17.
 */

public class QuestionFragmentAdapter extends FragmentPagerAdapter {
    public QuestionFragmentAdapter(FragmentManager fm)
    {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new EnterLocationFragment();
            case 1:
                return new EnterDestinationFragment();
            case 2:
                return new EnterFavorFragment();
            case 3:
                return new EnterTipFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
