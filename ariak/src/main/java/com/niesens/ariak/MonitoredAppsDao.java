package com.niesens.ariak;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MonitoredAppsDao {
    private static final String MONITORED_APPS_TABLE_NAME = "monitored_apps";

    public static List<String> getPackageNames() {
        List<String> packageNames = new ArrayList<String>();
        List<Map<String, String>> results = DatabaseHelper.performSelect(MONITORED_APPS_TABLE_NAME,
                new ArrayList<String>(Arrays.asList(MonitoredAppsColumns.PACKAGE_NAME)), new ContentValues());
        for (Map<String, String> row : results) {
            packageNames.add(row.get(MonitoredAppsColumns.PACKAGE_NAME));
        }
        return packageNames;
    }

    public static boolean addApp(String packageName) {
        ContentValues values = new ContentValues();
        values.put(MonitoredAppsColumns.PACKAGE_NAME, packageName);

        return DatabaseHelper.performInsert(MONITORED_APPS_TABLE_NAME, values);
    }

    public static boolean removeApp(String packageName) {
        ContentValues values = new ContentValues();
        values.put(MonitoredAppsColumns.PACKAGE_NAME, packageName);

        int deleteCount = DatabaseHelper.performDelete(MONITORED_APPS_TABLE_NAME, values);
        if (deleteCount > 0){
            return true;
        } else {
            return false;
        }
    }

    private static final class MonitoredAppsColumns implements BaseColumns {
        private static final String PACKAGE_NAME = "package_name";
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MONITORED_APPS_TABLE_NAME + " ("
                + MonitoredAppsColumns._ID + " INTEGER PRIMARY KEY, "
                + MonitoredAppsColumns.PACKAGE_NAME + " TEXT UNIQUE NOT NULL "
                + ");");
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.TAG, "Upgrading " + MONITORED_APPS_TABLE_NAME + " table");
        db.execSQL("DROP TABLE IF EXISTS " + MONITORED_APPS_TABLE_NAME);
        onCreate(db);
    }

}