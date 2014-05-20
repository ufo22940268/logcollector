package me.biubiubiu.logcollector.app.ui;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import me.biubiubiu.logcollector.app.R;

/**
 * Created by ccheng on 5/15/14.
 */
public class LogView extends ListView {

    private MyAdapter mAdapter;

    private boolean mScrolled;
    private List<String> mLogs = new ArrayList<String>();

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
        mAdapter = new MyAdapter((Activity) getContext(), R.layout.list_item_logcat);
        setAdapter(mAdapter);
    }

    public void appendLines(String lines) {
        mLogs.add(lines);
        mAdapter.setItems(mLogs);

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

    public void clean() {
    }

    private class MyAdapter extends SingleTypeAdapter<String> {

        public MyAdapter(Activity activity, int layoutResourceId) {
            super(activity, layoutResourceId);
        }

        @Override
        protected int[] getChildViewIds() {
            return new int[] {android.R.id.text1, android.R.id.text2};
        }

        @Override
        protected void update(int position, String item) {
            TextView view = getView(0, TextView.class);
            String tag = "";
            String content = "";
            if (item.contains(":")) {
                tag = item.substring(0, item.indexOf(":") + 1);
                content = item.substring(item.indexOf(":") + 1);
            }

            SpannableString ss = new SpannableString(item);
            ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.blue));
            ss.setSpan(span, 0, tag.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
            view.setText(ss);
        }
    }
}
