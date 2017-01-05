package favex.favex;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Tavish on 06-Jan-17.
 */

public class TabFragmentPagerAdapter extends FragmentPagerAdapter
{
    private String tabTitles[]= new String[]{"Near Me", "Chats", "Recent"};

    public TabFragmentPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return new NearMeFragment();
            case 1:
                return new ChatFragment();
            case 2:
                return new RecentFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabTitles[position];
    }

    @Override
    public int getCount()
    {
        return 3;
    }
}
