package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.entity.Moment;

public class DetailActivity extends ActionBarActivity {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    private final String HAPPYMOMENTS_APP_HASHTAG = "#HappyMoments";
    private Moment moment;

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.app_name));

        Intent intent = getIntent();

        if(intent != null && intent.getParcelableExtra("moment") != null){
            moment = intent.getParcelableExtra("moment");
            View detailView = findViewById(R.id.detail_view);

            ViewHolder holder = new ViewHolder(detailView);
            detailView.setTag(holder);

            holder.momentIcon.setImageResource(R.drawable.heart_logo);
            holder.momentText.setText(moment.getMoment());
            holder.momentDate.setText(moment.getDay());
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        String shareString  =  "";
        if(moment != null){
            shareString += moment.getMoment()+" - "+moment.getDay() + "\n " + HAPPYMOMENTS_APP_HASHTAG;
        }else{
            shareString = HAPPYMOMENTS_APP_HASHTAG;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        mShareActionProvider.setShareIntent(shareIntent);
//        Log.v(LOG_TAG, shareString);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }


    /* Private View Holder Class*/
    private class ViewHolder{
        ImageView momentIcon;
        TextView momentText;
        TextView momentDate;

        public ViewHolder(View convertView) {
            this.momentIcon = (ImageView) convertView.findViewById(R.id.imageView);
            this.momentText = (TextView) convertView.findViewById(R.id.main_moment_textview);
            this.momentDate = (TextView) convertView.findViewById(R.id.moment_date_textView);

        }
    }

}
