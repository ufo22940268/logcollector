package me.biubiubiu.logcollector.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.sufficientlysecure.rootcommands.Shell;
import org.sufficientlysecure.rootcommands.command.SimpleCommand;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ccheng on 5/20/14.
 */
public class DbListFragment extends MenuFragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.list)
    ListView mList;
    @InjectView(R.id.empty)
    ProgressBar mEmpty;
    private ArrayAdapter<String> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.db_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mList.setEmptyView(mEmpty);
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);
        new GetDbListTask().execute();
    }


    @Override
    public int getMenu() {
        return R.menu.db_actionbar;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), DbTableActivity.class);
        intent.putExtra("db_path", item);
        startActivity(intent);
    }

    private class GetDbListTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            Shell shell = null;
            try {
                shell = Shell.startRootShell();
                SimpleCommand simpleCommand = new SimpleCommand("find . -name *.db");
                shell.add(simpleCommand).waitForFinish();
                String[] split = simpleCommand.getOutput().split("\n");
                if (split != null) {
                    for (int i = 0; i < split.length; i++) {
                        String s = split[i];
                        if (s.length() >= 2 && s.charAt(0) == '.') {
                            split[i] = s.substring(1);
                        }
                    }
                }
                return split;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] items) {
            super.onPostExecute(items);
            if (items != null) {
                mAdapter.addAll(items);
                mAdapter.notifyDataSetChanged();
            }
        }

    }
}
