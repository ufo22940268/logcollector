package me.biubiubiu.logcollector.app;

import android.app.Fragment;
import android.view.MenuItem;

/**
 * Created by ccheng on 5/16/14.
 */
public abstract class MenuFragment extends Fragment {

    public abstract int getMenu();
    public abstract boolean onOptionsItemSelected(MenuItem item);

    public void onMenuItemSelected(int featureId, MenuItem item) {

    }
}
