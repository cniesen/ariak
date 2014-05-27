package com.niesens.ariak;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class SettingsActivity extends Activity {

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle(getResources().getString(R.string.app_name) + " - " + getResources().getString(R.string.action_settings));

        drawList();

        Button addButton = (Button) findViewById(R.id.settings_addline_button_monitoredProcess);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addApp();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
    }

    private void drawList() {
        final List<String> packageNames = MonitoredAppsDao.getPackageNames();
        ListView listView = (ListView) findViewById(R.id.maintainMonitoredApps_list_monitoredProcesses);

        listView.setAdapter( new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, packageNames));

        // register onClickListener to handle click events on each item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // argument position gives the index of item which is clicked
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,int position, long arg3) {
                removeApp(view, position, packageNames);
            }
        });
    }

    private void addApp() {
        TextView textView = (TextView) findViewById(R.id.maintainMonitoredApps_addline_edittext_monitoredProcess);
        String packageName = textView.getText().toString();
        if (StringUtils.isNotBlank(packageName)) {
            MonitoredAppsDao.addApp(packageName);
            drawList();
            textView.setText("");
        }
    }

    private void removeApp(View view, int position, List<String> packageNames) {
        final String selectedPackageName = packageNames.get(position);

        Context context = view.getContext();
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete " + selectedPackageName)
                .setMessage("Are you sure you want to remove the app from being monitored?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MonitoredAppsDao.removeApp(selectedPackageName);
                        drawList();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}

