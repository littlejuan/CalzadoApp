package org.littlejuan.calzadoapp.database;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.compat.BuildConfig;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jquintero on 26/08/17.
 */

public class CalzadoOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = CalzadoOpenHelper.class.getSimpleName();
    private static final int SCHEMA_VERSION = 1;
    private static final String DB_NAME = "calzado";

    private final Context context;
    private static CalzadoOpenHelper instance;

    public synchronized static CalzadoOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CalzadoOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private CalzadoOpenHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA_VERSION);
        this.context = context;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            SQLiteDatabase db = getWritableDatabase();
            db.enableWriteAheadLogging();
            db.execSQL("PRAGMA foreign_keys = ON;");
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 1; i <= SCHEMA_VERSION; i++) {
            applySqlFile(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = (oldVersion + 1); i <= newVersion; i++) {
            applySqlFile(db);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        setWriteAheadLoggingEnabled(true);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void applySqlFile(SQLiteDatabase db) {
        BufferedReader reader = null;
        try {
            final InputStream inputStream =
                    context.getAssets().open("db.sql");
            reader =
                    new BufferedReader(new InputStreamReader(inputStream));
            final StringBuilder statement = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Reading line -> " + line);
                }

                if (!TextUtils.isEmpty(line) && !line.startsWith("--")) {
                    statement.append(line.trim());
                }
                if (line.endsWith(";")) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Running statement " + statement);
                    }
                    db.execSQL(statement.toString());
                    statement.setLength(0);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not apply SQL file", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.w(TAG, "Could not close reader", e);
                }
            }
        }
    }
}
