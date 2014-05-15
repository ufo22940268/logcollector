package me.biubiubiu.logcollector.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.biubiubiu.logcollector.app.R;

/**
 * Created by ccheng on 5/15/14.
 */
public class LogView extends ScrollView {

    @InjectView(R.id.content)
    TextView mContent;

    public LogView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.log_view, this);
        ButterKnife.inject(this);
        mContent.setText("asdfaaf");
    }

    public void appendLine(String line) {
        String s = mContent.getText().toString();
        mContent.setText(s + "\n" + line);
        fullScroll(View.FOCUS_DOWN);
    }
}
