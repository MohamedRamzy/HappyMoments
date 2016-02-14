package com.example.android.sunshine.app.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by mmahfouz on 2/2/2016.
 */
public class MomentsContentProvider extends ContentProvider {


    public static final String PROVIDER_NAME = "com.example.android.sunshine.app.moments";
    public static final String URL = "content://" + PROVIDER_NAME + "/moments";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String COLUMN_MOMENT = "moment";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_ICON = "icon";


    static final int MOMENTS = 1;
    static final int MOMENT_ID = 2;
    static final UriMatcher uriMatcher;
    private static HashMap<String,String> MOMENTS_PROJECTION_MAP;

    static{
        uriMatcher =  new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "moments", MOMENTS);
        uriMatcher.addURI(PROVIDER_NAME, "moments/#", MOMENT_ID);
    }

//    private SQLiteDatabase db;
    private DBHelper dbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MOMENTS_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case MOMENTS:
                qb.setProjectionMap(MOMENTS_PROJECTION_MAP);
                break;
            case MOMENT_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder == ""){
            // By default sort on student names
            sortOrder = COLUMN_DAY;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db,	projection,	selection, selectionArgs, null, null, sortOrder);

        // register to watch a content URI for changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowID = db.insert(MOMENTS_TABLE_NAME,null,contentValues);

        if(rowID > 0){

            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count = 0;
        switch (uriMatcher.match(uri)){
            case MOMENTS:
                count = db.delete(MOMENTS_TABLE_NAME, selection, selectionArgs);
                break;

            case MOMENT_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(MOMENTS_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count = 0;
        switch (uriMatcher.match(uri)){
            case MOMENTS:
                count = db.update(MOMENTS_TABLE_NAME, values, selection, selectionArgs);
                break;

            case MOMENT_ID:
                String id = uri.getPathSegments().get(1);
                count = db.update(MOMENTS_TABLE_NAME, values, _ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    // ====================================================================
    /**
     * Database specific constant declarations
     */
    public static final String DATABASE_NAME = "moments.sqlite";
    static final String MOMENTS_TABLE_NAME = "moments";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + MOMENTS_TABLE_NAME + "(" +
                    _ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MOMENT + " TEXT NOT NULL, " +
                    COLUMN_DAY + " TEXT NOT NULL, " +
                    COLUMN_ICON + " INTEGER);";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    public static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
            //db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = '0' WHERE NAME = '" + CREATE_DB_TABLE + "'");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  MOMENTS_TABLE_NAME);
            onCreate(db);
        }
    }
}
