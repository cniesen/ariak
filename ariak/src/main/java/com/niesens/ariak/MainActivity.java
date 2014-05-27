package com.niesens.ariak;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private Handler drawListHandler = new Handler();
    private NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(this);
    private TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

    private View tmpNotifyIconView = null;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper.getInstance(this);
        setContentView(R.layout.activity_main);

        drawListHandler.postDelayed(drawNotificationRunnable, 500);

    }

    @Override
    protected void onResume() {
        super.onResume();
        drawListHandler.postDelayed(drawListRunnable, 500);
    }

    @Override
    protected void onPause() {
        drawListHandler.removeCallbacks(drawListRunnable);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Runnable drawListRunnable = new Runnable() {
        @Override
        public void run() {
            List<String> monitoredPackageNames = MonitoredAppsDao.getPackageNames();
            final ArrayList<String> runningPackageNames = new ArrayList<String>();

            ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);

            for (ActivityManager.RunningAppProcessInfo procInfo: activityManager.getRunningAppProcesses()) {
                if (monitoredPackageNames.contains(procInfo.processName)) {
                    runningPackageNames.add(procInfo.processName);
                }
            }

            // list view
            ListView listView = (ListView) findViewById(R.id.main_list_monitoredProcesses);
            ArrayAdapter<String> arrayAdapter = new AppAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, runningPackageNames);
            listView.setAdapter(arrayAdapter);

            // register onClickListener to handle click events on each item
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // argument position gives the index of item which is clicked
                @Override
                public void onItemClick(AdapterView<?> arg0, View view,int position, long arg3) {
                    appInteraction(view, position, runningPackageNames);
                }
            });

            drawListHandler.postDelayed(this, 1000);

        }
    };

    // ToDo: Support older android version
    // ToDo: Handle notification better
    // ToDo: Configurable notification & turn it off
    private Runnable drawNotificationRunnable = new Runnable() {

        @Override
        public void run() {
            List<String> monitoredPackageNames = MonitoredAppsDao.getPackageNames();
            final ArrayList<String> runningPackageNames = new ArrayList<String>();

            ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();

            for (ActivityManager.RunningAppProcessInfo procInfo: activityManager.getRunningAppProcesses()) {
                if (monitoredPackageNames.contains(procInfo.processName)) {
                    if (tmpNotifyIconView == null) {
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        tmpNotifyIconView = inflater.inflate(R.layout.tmp_notification_icon, null);
                    }

                    Drawable x = AndroidProcessUtils.getIconFromPackageName(procInfo.processName, getApplicationContext());

                    ImageView tmpImageview = (ImageView) tmpNotifyIconView.findViewById(R.id.tmpNotificationDrawableId);
                    tmpImageview.setImageDrawable(x);

                    notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
//                    notificationBuilder.setSmallIcon(com.skype.raider.R.drawable.ic_launcher);
                    notificationBuilder.setContentTitle(AndroidProcessUtils.getNameFromPackageName(procInfo.processName, getApplicationContext()));
                    notificationBuilder.setContentText(procInfo.processName);

                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    notificationBuilder.setContentIntent(resultPendingIntent);
                    mNotificationManager.notify(procInfo.processName.hashCode(), notificationBuilder.build());
                }
            }

            // ToDo: Make this timer configurable
            drawListHandler.postDelayed(this, 10000);

        }
    };

    private void appInteraction(View view, int position, List<String> packageNames) {
        final String selectedPackageName = packageNames.get(position);

        Context context = view.getContext();
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(AndroidProcessUtils.getNameFromPackageName(selectedPackageName, context)
                        + " (" + selectedPackageName +")")
                .setMessage("What do you want to do?")
                .setPositiveButton("Kill app", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        activityManager.killBackgroundProcesses(selectedPackageName);
                    }

                })
                .setNeutralButton("Open app", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(selectedPackageName);
                        startActivity(LaunchIntent);
                    }
                })
                .setNegativeButton("Do nothing", null)
                .show();
    }

}

