package com.niesens.ariak;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "Database";

    private static DatabaseHelper sInstance;

    private static final String DATABASE_NAME = "ariak.db";
    private static final int DATABASE_VERSION = 1;

    public static DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public static List<Map<String, String>> performSelect(String tableName, List<String> requestedFields, ContentValues criteria) {
        List results = new ArrayList<Map<String, String>>();

        String[] columns = null;
        if (requestedFields.size() > 0) {
            columns = requestedFields.toArray(new String[0]);
        }

        String whereClause = null;
        String[] whereArgs = null;
        if (criteria.size() > 0) {
            whereClause = new String();
            Set<String> whereArgsSet = new HashSet<String>();
            for (Map.Entry whereEntry : criteria.valueSet()) {
                if (StringUtils.isNotBlank(whereClause)) {
                    whereClause = whereClause + ",";
                }
                whereClause = whereClause + whereEntry.getKey() + "=?";
                whereArgsSet.add((String) whereEntry.getValue());
            }
            whereArgs = whereArgsSet.toArray(new String[0]);
        }

        SQLiteDatabase db = sInstance.getReadableDatabase();
        Cursor cursor = db.query(tableName, columns, whereClause, whereArgs, null, null, null, null);
        if (cursor == null) {
            return results;
        }

        if (!cursor.moveToFirst()) {
            return results;
        }

        do {
            Map rowData = new HashMap<String, String>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                rowData.put(cursor.getColumnName(i), cursor.getString(i));
            }
            results.add(rowData);
        } while (cursor.moveToNext());

        cursor.close();
        db.close();

        return results;
    }

    public static boolean performInsert(String tableName, ContentValues values) {
        if (values.size() == 0) {
            return false;
        }

        SQLiteDatabase db = sInstance.getWritableDatabase();
        long rowId = db.insert(tableName, null, values);
        db.close();
        if (rowId > -1) {
            return true;
        } else {
            return false;
        }
    }

    public static int performDelete(String tableName, ContentValues criteria) {
        if (criteria.size() == 0) {
            return 0;
        }

        String whereClause = new String();
        Set<String> whereArgs = new HashSet<String>();
        for (Map.Entry whereEntry : criteria.valueSet()) {
           if (StringUtils.isNotBlank(whereClause)) {
                whereClause = whereClause + ",";
            }
            whereClause = whereClause + whereEntry.getKey() + "=?";
           whereArgs.add((String) whereEntry.getValue());
        }
        SQLiteDatabase db = sInstance.getWritableDatabase();
        int count = db.delete(tableName, whereClause, whereArgs.toArray(new String[0]));
        db.close();
        return count;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MonitoredAppsDao.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        MonitoredAppsDao.onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}