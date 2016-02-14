package com.example.android.sunshine.app;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.app.entity.Moment;
import com.example.android.sunshine.app.provider.MomentsContentProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by mmahfouz on 1/8/2016.
 */
public class MomentsFragment extends Fragment implements View.OnClickListener {

    private final String LOG_TAG = MomentsFragment.class.getSimpleName();

    private MomentsArrayAdapter mMemoryArrayAdapter;

    private ShareActionProvider mShareActionProvider;
    private Button mRandomMomentBtn;
    private Button mAddMomentBtn;
    private static int removePos = -1;

    public static Moment [] moments;
    public static final String MOMENTS_FILENAME = "moments.txt";

    public MomentsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to indicate that this fragment has a menu option (Refresh)
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMemoryArrayAdapter = new MomentsArrayAdapter(getActivity(),R.layout.list_item_forecast);
        final ListView mMomentsListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mMomentsListView.setAdapter(mMemoryArrayAdapter);
        mMomentsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                //Log.v(LOG_TAG,"onItemLongClick-> pos : "+pos +", l : "+l); // both pos & l are the array/listview index
                removePos = pos;
                String[] items = {"Edit","Delete"};
                AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
                build.setTitle("Are you sure you want to edit/delete this moment !?");
                build.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        if (index == 1) {
                            // can remove here from db
                            int _id = moments[removePos].get_id();
                            //Log.v(LOG_TAG,"_id = "+_id);
                            getActivity().getContentResolver().delete(MomentsContentProvider.CONTENT_URI, "_id=?", new String[]{String.valueOf(_id)});
                            Toast.makeText(getActivity(), "Moment successfully deleted", Toast.LENGTH_LONG).show();
                            /*
                                Moment[] newMoments = new Moment[moments.length - 1];
                                int j = 0;
                                for (int i = 0; i < moments.length; i++) {
                                    if (i == removePos) continue;
                                    newMoments[j] = moments[i];
                                    j++;
                                }
                                moments = newMoments;
                            */
                        }else { // edit
                            removePos = -1;
                            Log.v(LOG_TAG,"onItemLongClick-> EDIT"); // both pos & l are the array/listview index
                            Toast.makeText(getActivity(), "Editing functionality under development .. ", Toast.LENGTH_LONG).show();
                        }
//                        ((MainActivity) getActivity()).storeMomentsArray();
                        updateMomentsList();
                    }
                }).create().show();
                return true; // I handled it
            }

        });


        mAddMomentBtn = (Button) rootView.findViewById(R.id.add_btn);
        mRandomMomentBtn = (Button) rootView.findViewById(R.id.random_moment_btn);

        mAddMomentBtn.setOnClickListener(this);
        mRandomMomentBtn.setOnClickListener(this);

        mMomentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ((Callback) getActivity()).onItemSelected(mMemoryArrayAdapter.getItem(i));

            }
        });

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh){
            updateMomentsList();
            return true;
        }else if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }else if (item.getItemId() == R.id.action_store_to_db) {
            // store the values to the db
//            Toast.makeText(getActivity(), "Storing moments .. ", Toast.LENGTH_LONG).show();
//            storeMomentsToSQLiteDB();
            return true;
        }else if(item.getItemId() == R.id.action_backup_db) {
            // store the moments to a backup database file
            Toast.makeText(getActivity(), "Backup DB .. ", Toast.LENGTH_LONG).show();
            backupDatabaseToSD();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void backupDatabaseToSD() {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    File sd = Environment.getExternalStorageDirectory();
                    File data = Environment.getDataDirectory();

                    if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                        return null;
                    }
                    if (sd.canWrite()) {
                        String currentDBPath = "data/" + getActivity().getPackageName() + "/databases/" + MomentsContentProvider.DATABASE_NAME + "";
                        String backupDBPath = "backup_"+System.currentTimeMillis()+"_" + MomentsContentProvider.DATABASE_NAME;
                        //Log.v(LOG_TAG,currentDBPath);
                        File currentDB = new File(data, currentDBPath);
                        File backupDB = new File(sd, backupDBPath);
                        //Log.v(LOG_TAG,backupDB.getAbsolutePath());
                        if (currentDB.exists()) {
                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                        }
                    }
                } catch (Exception e) {
                    Log.v(LOG_TAG,e.getMessage());
                }
                return null;
            }
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Toast.makeText(getActivity(), "DB stored successfully!", Toast.LENGTH_LONG).show();
            }
        };
        asyncTask.execute();

    }



    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private void storeMomentsToSQLiteDB(){
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                // removing all records first
                int num = getActivity().getContentResolver().delete(MomentsContentProvider.CONTENT_URI, null, null);

                // adding them all again
                for (Moment moment: moments) {
                    ContentValues values = new ContentValues();
                    values.put(MomentsContentProvider.COLUMN_MOMENT,moment.getMoment());
                    values.put(MomentsContentProvider.COLUMN_DAY,moment.getDay());
                    values.put(MomentsContentProvider.COLUMN_ICON,moment.getIcon());
                    Uri uri = getActivity().getContentResolver().insert(MomentsContentProvider.CONTENT_URI, values);
                    //Toast.makeText(getActivity(), uri.toString(), Toast.LENGTH_SHORT).show();
                }
                Log.v(LOG_TAG, moments.length + " moments have been stored successfully!");
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Toast.makeText(getActivity(), moments.length + " moments have been stored successfully!", Toast.LENGTH_LONG).show();
            }
        };
        asyncTask.execute();
    }

    public void onResume() {
        super.onResume();
        updateMomentsList();
    }


    public void updateMomentsList(){
        FetchMomentTask fetchMomentTask = new FetchMomentTask(getActivity(),mMemoryArrayAdapter);
        fetchMomentTask.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_btn:
                Intent intent = new Intent(getActivity(), AddMomentActivity.class);
                startActivity(intent);
                break;
            case R.id.random_moment_btn:
                int index = getRandomIndex();
                Moment randomMom = moments[index];
                ((Callback) getActivity()).onItemSelected(randomMom);
                break;
            default:
                break;
        }
    }

    public int getRandomIndex(){
        return (int)(Math.random()*((double)moments.length));
    }

    /* Private View Holder Class*/
    private class ViewHolder{
        ImageView momentIcon;
        TextView momentText;
        TextView momentDay;
        TextView maxTemp;
        TextView minTemp;

        public ViewHolder(View convertView) {
            this.momentIcon = (ImageView) convertView.findViewById(R.id.weather_imageView);
            this.momentText = (TextView) convertView.findViewById(R.id.weather_day_textview);
            this.momentDay = (TextView) convertView.findViewById(R.id.weather_status_textView);
            this.maxTemp = (TextView) convertView.findViewById(R.id.weather_max_temp_textView);
            this.minTemp = (TextView) convertView.findViewById(R.id.weather_min_temp_textView);
        }
    }

    public class MomentsArrayAdapter extends ArrayAdapter<Moment>{
        private static final int VIEW_TYPE_RANDOM = 0;
        private static final int VIEW_TYPE_OTHER_MOMENT = 1;
        private static final int VIEW_TYPE_COUNT = 2;
        Context mContext;
        public MomentsArrayAdapter(Context context, int resource) {
            super(context, resource);
            this.mContext = context;
        }
        @Override
        public int getItemViewType(int position) {
            return (position == 0)? VIEW_TYPE_RANDOM: VIEW_TYPE_OTHER_MOMENT;
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            Moment moment = getItem(position);

            int viewType = getItemViewType(position);
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                if(viewType == VIEW_TYPE_RANDOM) {
                    convertView = mInflater.inflate(R.layout.list_item_forecast_today, null);
                }else {
                    convertView = mInflater.inflate(R.layout.list_item_forecast, null);
                }
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(viewType == VIEW_TYPE_RANDOM) {
                int MAXLEN = 100;
                holder.momentIcon.setImageResource(R.drawable.heart_logo);
                holder.momentDay.setText("محمد و مها");
                holder.momentText.setTextAppearance(getActivity(), android.R.style.TextAppearance_Large);
                if(moment.getMoment().length() < MAXLEN) {
                    holder.momentText.setText(moment.getMoment());
                }else {
                    holder.momentText.setText(moment.getMoment().substring(0, MAXLEN) + "  ...");
                }
            }else{
                int MAXLEN = 80;
                holder.momentIcon.setImageResource(R.drawable.heart_logo);
                holder.momentDay.setText(moment.getDay().length() != 0 ? moment.getDay() : "---");
                holder.maxTemp.setText("LOVE!");
                holder.minTemp.setText("♥(::)♥");
                holder.momentText.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
                if(moment.getMoment().length() < MAXLEN) {
                    holder.momentText.setText(moment.getMoment());
                }else {
                    holder.momentText.setText(moment.getMoment().substring(0, MAXLEN) + "  ...");
                }
            }

            return convertView;
        }
    }

   public class FetchMomentTask extends AsyncTask<Void, Void, Void> {
        private final String LOG_TAG = FetchMomentTask.class.getSimpleName();
        private MomentsArrayAdapter mMomentsArrayAdapter;
        private final Context mContext;
        public FetchMomentTask(Context context, MomentsArrayAdapter forecastAdapter) {
            mContext = context;
            mMomentsArrayAdapter = forecastAdapter;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mMomentsArrayAdapter != null) {
                mMomentsArrayAdapter.clear();

                // randomly select one and show it first.
                int index = getRandomIndex();
                if(index < moments.length) {
                    Moment randomMom = moments[index];
                    Moment tmp = moments[0];
                    moments[0] = randomMom;
                    moments[index] = tmp;
                }
                for(Moment s : moments) {
                    mMomentsArrayAdapter.add(s);
                }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
//            moments = ((MainActivity)getActivity()).loadMomentsFile(); // stored by day for now + some swaps
            moments = ((MainActivity)getActivity()).loadMomentsSQLiteDB(); // stored randomly
            return null;
        }
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Moment moment);
    }
    /*
    Left Alt + 2 : ☻
    Left Alt + 3 : ♥
    Left Alt + 4 : ♦
    Left Alt + 5 : ♣
    Left Alt + 6 : ♠
    Left Alt + 7 : •
    Left Alt + 8 : ◘
    Left Alt + 9 : ○
    Left Alt + 10 : ◙
    Left Alt + 11 : ♂
    Left Alt + 12 : ♀
    Left Alt + 13 : ♪
    Left Alt + 14 : ♫
    Left Alt + 15 : ☼
    Left Alt + 16 : ►
    * */

}

