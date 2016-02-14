package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.example.android.sunshine.app.entity.Moment;
import com.example.android.sunshine.app.provider.MomentsContentProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class MainActivity extends ActionBarActivity implements MomentsFragment.Callback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public Moment[] loadMomentsSQLiteDB(){

        Cursor cursor = getContentResolver().query(MomentsContentProvider.CONTENT_URI, null, null, null, MomentsContentProvider._ID);
        Moment []pup = new Moment[cursor.getCount()];
        int i = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Moment moment = new Moment();
            moment.set_id(cursor.getInt(cursor.getColumnIndex(MomentsContentProvider._ID)));
            moment.setMoment(cursor.getString(cursor.getColumnIndex(MomentsContentProvider.COLUMN_MOMENT)));
            moment.setDay(cursor.getString(cursor.getColumnIndex(MomentsContentProvider.COLUMN_DAY)));
            moment.setIcon(cursor.getInt(cursor.getColumnIndex(MomentsContentProvider.COLUMN_ICON)));
            pup[i++] = moment;
//            Log.v(LOG_TAG,moment.get_id()+"");
        }
        return pup;
    }

    @Override
    public void onItemSelected(Moment moment) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("moment", (Parcelable)moment);
        startActivity(intent);
    }

    /**
     * ===============================================================================================================
     *  Reading/Writing the moments array to/from files
     *  not used for now, instead a database is used
     * */

    public Moment[] loadMomentsFile(){
        //Log.v("loadMain", getApplicationContext().getFilesDir().getAbsolutePath());
        FileInputStream fin = null;
        Moment []pup = null;
        try {
            fin = openFileInput(MomentsFragment.MOMENTS_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fin);
            pup = (Moment [])ois.readObject();
            ois.close();
            fin.close();
        }catch (FileNotFoundException e) {
            //e.printStackTrace();
            Log.w(LOG_TAG, "File not found, will be created once data is inserted");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (pup == null)
                return new Moment[0];
            return pup;
        }
    }

    public void storeMomentsArray(){
        FileOutputStream fout = null;
        try {
            fout = openFileOutput(MomentsFragment.MOMENTS_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(MomentsFragment.moments);
            oos.close();
            fout.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}