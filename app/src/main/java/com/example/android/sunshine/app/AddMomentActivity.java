package com.example.android.sunshine.app;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.sunshine.app.entity.Moment;
import com.example.android.sunshine.app.provider.MomentsContentProvider;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AddMomentActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText mMomentText;
    private EditText mMomentDate;
    private Button mAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_moment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMomentText = (EditText)findViewById(R.id.moment_text);
        mMomentDate = (EditText)findViewById(R.id.moment_date);
        // default date of the date of the recent moment
        if(MomentsFragment.moments != null && MomentsFragment.moments.length > 0) {
            mMomentDate.setText(MomentsFragment.moments[MomentsFragment.moments.length - 1].getDay());
        }else{
            mMomentDate.setText("30/1/2019");
        }
        mAddBtn = (Button)findViewById(R.id.add_moment_btn);

        mAddBtn.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_moment_btn:
//                addMomentToArray(mMomentText.getText().toString(), mMomentDate.getText().toString(), 500);
                if(validMoment(mMomentText.getText().toString(), mMomentDate.getText().toString())) {
                    addMomentToDB(mMomentText.getText().toString(), mMomentDate.getText().toString(), 500);
                    this.finish();
                }else{
                    Utility.showErrorMessage(this,"Alert","Please enter a valid moment/date");
                }
                break;
            default:
                break;
        }
    }

    private boolean validMoment(String moment,String date){
        if(moment == null || moment.equals("") || date == null || date.equals(""))
            return false;
        return true;
    }

    public void addMomentToArray(String momentText, String momentDate, int momentIcon){
        //Log.v("AddMoment", getApplicationContext().getFilesDir().getAbsolutePath());
        ArrayList<Moment> list = new ArrayList<Moment>();
        for (Moment s: MomentsFragment.moments){
            list.add(s);
        }

        Moment moment = new Moment();
        moment.setMoment(momentText);
        moment.setDay(momentDate);
        moment.setIcon(momentIcon);

        list.add(moment);

        Moment []res = new Moment [list.size()];
        for(int i = 0; i < res.length;i++){
            res[i] = list.get(i);
        }

        FileOutputStream fout = null;
        try {
            fout = openFileOutput(MomentsFragment.MOMENTS_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(res);
            oos.close();
            fout.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MomentsFragment.moments = new Moment[list.size()];
        for (int i = 0;i < list.size(); i++){
            MomentsFragment.moments[i] = list.get(i);
        }
        //Log.v("addMoment","addMoment");
    }


    public void addMomentToDB(String momentText, String momentDate, int momentIcon){
        ContentValues value = new ContentValues();
        value.put(MomentsContentProvider.COLUMN_MOMENT,momentText);
        value.put(MomentsContentProvider.COLUMN_DAY,momentDate);
        value.put(MomentsContentProvider.COLUMN_ICON,momentIcon);
        getContentResolver().insert(MomentsContentProvider.CONTENT_URI,value);
    }
}
