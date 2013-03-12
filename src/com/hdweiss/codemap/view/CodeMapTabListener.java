package com.hdweiss.codemap.view;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.hdweiss.codemap.R;

public class CodeMapTabListener<T extends Fragment> implements ActionBar.TabListener {
    private final Activity mActivity;
    private final String mTag;
    private final Class<T> mClass;
    private final Bundle mArgs;
    private Fragment mFragment;

    public CodeMapTabListener(Activity activity, String tag, Class<T> clz) {
        this(activity, tag, clz, null);
    }

    public CodeMapTabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
        mArgs = args;
    }

    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        Fragment currentFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
        
        if (currentFragment == null) {
            mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
            ft.add(R.id.codemap_content, mFragment, mTag);
        } else {
            ft.show(currentFragment);
        }
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        Fragment currentFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);

        if (currentFragment != null) {
        	ft.hide(currentFragment);
        } else if (mFragment != null) {
        	ft.hide(mFragment);
        }
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
}
