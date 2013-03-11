package com.hdweiss.codemap.view;

import com.hdweiss.codemap.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.widget.Toast;

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

        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
        if (mFragment != null && !mFragment.isDetached()) {
            FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
            ft.detach(mFragment);
            ft.commit();
        }
    }

    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (mFragment == null) {
            mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
            ft.add(R.id.codemap_content, mFragment, mTag);
        } else {
            ft.show(mFragment);
        }
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            ft.hide(mFragment);
        }
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
    }
}
