package me.biubiubiu.logcollector.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * Created by ccheng on 5/20/14.
 */
public class DbTableActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_table_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        DbTableFragment dbTableFragment = new DbTableFragment();
        dbTableFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().add(R.id.root, dbTableFragment).commit();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        super.onMenuItemSelected(featureId, item);
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;

        }

        return true;
    }
}
