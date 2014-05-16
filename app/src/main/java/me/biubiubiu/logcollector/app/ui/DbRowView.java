package me.biubiubiu.logcollector.app.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ccheng on 5/16/14.
 */
public class DbRowView extends LinearLayout {

    public DbRowView(Context context, int columnCount) {
        super(context);
        for (int i = 0; i < columnCount; i++) {
            addView(getTextView());
        }
    }

    private View getTextView() {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        tv.setLayoutParams(params);
        return tv;
    }

    public void setText(int i, String string) {
        TextView childAt = (TextView) getChildAt(i);
        childAt.setText(string);
    }
}
