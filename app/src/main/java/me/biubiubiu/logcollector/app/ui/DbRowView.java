package me.biubiubiu.logcollector.app.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.Gravity;
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
        Resources resources = getResources();
        Resources.Theme theme = context.getTheme();
        TypedArray typedArray = theme.obtainStyledAttributes(new int[]{
                android.R.attr.listPreferredItemHeightSmall,
                android.R.attr.listPreferredItemPaddingLeft,
                android.R.attr.listPreferredItemPaddingRight});
        int height = typedArray.getDimensionPixelSize(0, 1);
        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height));

        int left = typedArray.getDimensionPixelSize(1, 1);
        int right = typedArray.getDimensionPixelSize(2, 1);
        setPadding(left, 0, right, 0);
        setGravity(Gravity.CENTER_VERTICAL);
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
