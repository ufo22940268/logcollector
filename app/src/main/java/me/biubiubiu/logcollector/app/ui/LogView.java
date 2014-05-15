package me.biubiubiu.logcollector.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.biubiubiu.logcollector.app.R;

/**
 * Created by ccheng on 5/15/14.
 */
public class LogView extends ListView {

    private ArrayAdapter<String> mAdapter;

    private boolean mScrolled;

    public LogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDividerHeight(0);
        setDivider(null);
        setFastScrollEnabled(true);
        mScrolled = false;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item_logcat, android.R.id.text1);
        setAdapter(mAdapter);
    }

    public void appendLines(String lines) {
        mAdapter.add(lines);
        mAdapter.notifyDataSetChanged();

        if (!mScrolled) {
            post(new Runnable() {
                @Override
                public void run() {
                    setSelection(getCount() - 1);
                }
            });
        }
    }

    public boolean isScrolled() {
        return mScrolled;
    }

    public void setScrolled(boolean scrolled) {
        mScrolled = scrolled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mScrolled = true;
        return super.onTouchEvent(ev);
    }
}
