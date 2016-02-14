package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.entity.Moment;

/**
 * Created by mmahfouz on 1/16/2016.
 */
public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    private final String SUNSHINE_APP_HASHTAG = "#SunshineApp";
    private String units;
    private Moment moment;

    private ShareActionProvider mShareActionProvider;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }


    /* Private View Holder Class*/
    private class ViewHolder{
        ImageView weatherIcon;
        TextView day;
        TextView weatherStatsText;

        public ViewHolder(View convertView) {
            this.weatherIcon = (ImageView) convertView.findViewById(R.id.weather_imageView);
            this.day = (TextView) convertView.findViewById(R.id.weather_day_textview);
            this.weatherStatsText = (TextView) convertView.findViewById(R.id.weather_status_textView);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewHolder holder;

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

//            TextView weatherText = (TextView) rootView.findViewById(R.id.weather_text);
//        Intent intent = getActivity().getIntent();
        Bundle args = getArguments();
        if(args != null){
            moment = args.getParcelable("moment");
            holder = new ViewHolder(rootView);
            rootView.setTag(holder);
//                weatherText.setText(weather.getWeatherStatsText());
            String units = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default));
            holder.weatherIcon.setImageResource(R.drawable.heart_logo);
            holder.day.setText(moment.getMoment());
            holder.weatherStatsText.setText(moment.getDay());
//            holder.maxTemp.setText(Utility.formatTemperature(getActivity(), weather.getMaxTemp(),units.equals(getString(R.string.pref_units_metric))));
//            holder.minTemp.setText(Utility.formatTemperature(getActivity(), weather.getMinTemp(), units.equals(getString(R.string.pref_units_metric))));

        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        String shareString  =  SUNSHINE_APP_HASHTAG;
        if(moment != null){
            shareString += moment.getMoment()+" - "+moment.getDay();
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        mShareActionProvider.setShareIntent(shareIntent);
//        Log.v(LOG_TAG, shareString);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
