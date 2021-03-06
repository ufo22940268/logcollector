package me.biubiubiu.logcollector.app;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.sufficientlysecure.rootcommands.RootCommands;
import org.sufficientlysecure.rootcommands.Shell;
import org.sufficientlysecure.rootcommands.command.SimpleCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.biubiubiu.logcollector.app.ui.DbRowView;
import me.biubiubiu.logcollector.app.util.AppConstants;
import me.biubiubiu.logcollector.app.util.SystemManager;

/**
 * Created by ccheng on 5/16/14.
 */
public class DbTableFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    @InjectView(R.id.table_name)
    Spinner mTableName;
    @InjectView(R.id.list)
    ListView mList;
    @InjectView(R.id.header)
    LinearLayout mHeader;
    private ArrayAdapter mTableNameAdapter;
    private SQLiteDatabase mSqLiteDatabase;
    private CursorAdapter mDbDataAdapter;
    private String mPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.db_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SystemManager.root(getActivity());

        mPath = getArguments().getString("db_path");

        mSqLiteDatabase = getSQLiteDatabase();

        mTableNameAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        mTableNameAdapter.addAll(showAllTables(mSqLiteDatabase));
        mTableNameAdapter.notifyDataSetChanged();
        mTableName.setOnItemSelectedListener(this);
        mTableName.setAdapter(mTableNameAdapter);

        mDbDataAdapter = new MyCursorAdapter();
        mList.setAdapter(mDbDataAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = getActivity().getActionBar();
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.db_actionbar, null, false);
        actionBar.setCustomView(inflate);
    }

    public List<String> showAllTables(SQLiteDatabase ourDatabase) {
        String mySql = " SELECT name FROM sqlite_master " + " WHERE type='table'";
        Cursor cursor = ourDatabase.rawQuery(mySql, null);
        List<String> tables = new ArrayList<String>();
        while (cursor.moveToNext()) {
            tables.add(cursor.getString(0));
        }
        return tables;
    }

    private SQLiteDatabase getSQLiteDatabase() {
        // enable debug logging
        RootCommands.DEBUG = true;

        Shell shell = null;
        try {
            shell = Shell.startRootShell();
            SimpleCommand sc = new SimpleCommand();
            SimpleCommand command2 = new SimpleCommand("cp " + mPath + " " + AppConstants.SCARD_DB);
            shell.add(command2).waitForFinish();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


        return SQLiteDatabase.openDatabase(
                AppConstants.SCARD_DB, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    private Cursor queryTableCursor(String tableName) {
        return mSqLiteDatabase.rawQuery("select rowid _id, * from " + tableName, null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String tableName = (String) mTableNameAdapter.getItem(position);

        String[] headers = getDbColumnNames(tableName);
        mHeader.removeAllViews();
        for (int i = 0; i < headers.length && i < 3; i++) {
            String header = headers[i];
            mHeader.addView(getHeaderView(header));
        }


        mDbDataAdapter.changeCursor(queryTableCursor(tableName));
        mDbDataAdapter.notifyDataSetChanged();

    }

    private View getHeaderView(String header) {
        TextView tv = new TextView(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.weight = 1;
        tv.setTextAppearance(getActivity(), R.style.boldText);
        tv.setLayoutParams(lp);

        tv.setText(header);
        return tv;
    }

    private String[] getDbColumnNames(String tableName) {
        Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + tableName, null);
        return cursor.getColumnNames();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class MyCursorAdapter extends CursorAdapter {

        public static final int COLUMN_LIMIT = 4;
        private int mColumnCount;
        private boolean mOverflow;

        public MyCursorAdapter() {
            super(DbTableFragment.this.getActivity(), null, true);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            if (cursor.getColumnCount() < COLUMN_LIMIT) {
                mColumnCount = cursor.getColumnCount();
            } else {
                mColumnCount = COLUMN_LIMIT - 1;
                mOverflow = true;
            }
            return new DbRowView(context, mOverflow ? mColumnCount + 1 : mColumnCount);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            DbRowView row = (DbRowView) view;
            for (int i = 0; i < mColumnCount; i++) {
                row.setText(i, cursor.getString(i) == null ? "" : cursor.getString(i));
            }

            if (mOverflow) {
                row.setText(COLUMN_LIMIT - 1, "...");
            }
        }
    }
}
