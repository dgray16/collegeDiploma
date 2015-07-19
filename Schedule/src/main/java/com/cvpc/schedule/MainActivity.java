package com.cvpc.schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements ActionBar.TabListener {



    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    static int weekPointer = 0;                 //For previous and next week
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    Calendar calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_WEEK);

    //<JSON>
    //http://serv.polytech.cv.ua/adminka/rozklad.php?accadem_year=1&semestr=0&option=32

    public static final String TEMP_URL1 = "http://polytech.cv.ua/schedule.php?day=";
    public static final String URL2 = "http://192.168.1.1/standalone/getsh.php?date=";
    public static final String URL = "http://serv.polytech.cv.ua/standalone/getsh.php?date=";
    private static RequestQueue myQueue;
    //</JSON>



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //<JSON>
        myQueue = Volley.newRequestQueue(getApplicationContext());
        //</JSON>

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        // Create the adapter that will return a fragment for each
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mViewPager.setAdapter(mSectionsPagerAdapter);



        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.

            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        switch(day){
            case 2:
                mViewPager.setCurrentItem(0);
                break;

            case 3:
                mViewPager.setCurrentItem(1);
                break;

            case 4:
                mViewPager.setCurrentItem(2);
                break;

            case 5:
                mViewPager.setCurrentItem(3);
                break;

            case 6:
                mViewPager.setCurrentItem(4);
                break;

            case 7:
                mViewPager.setCurrentItem(5);
                break;
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null) {
            //Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "З'єднання з мережею інтернет - відсутнє", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;

        int id = item.getItemId();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        int temp = mViewPager.getCurrentItem();

        if (id == R.id.action_about) {
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_refresh){
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setCurrentItem(temp);

            if (netInfo == null) {
                //Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "З'єднання з мережею інтернет - відсутнє", Toast.LENGTH_LONG).show();
            }
        }

        if (id == R.id.action_previous_week) {
            weekPointer = weekPointer - 7;

            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setCurrentItem(temp);
        }

        if (id == R.id.action_next_week) {
            weekPointer = weekPointer + 7;

            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setCurrentItem(temp);
        }

        if (id == R.id.action_time) {
            intent = new Intent(this, TimeActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_change_account) {
            intent = new Intent(this, AuthorizationActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 6 total pages.
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1_ua).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2_ua).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3_ua).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4_ua).toUpperCase(l);
                case 4:
                    return getString(R.string.title_section5_ua).toUpperCase(l);
                case 5:
                    return getString(R.string.title_section6_ua).toUpperCase(l);

            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
            int pageNumber = getArguments().getInt(ARG_SECTION_NUMBER);




            final String ATTRIBUTE_LESSON_ORDER = "order";
            final String ATTRIBUTE_LESSON_NAME = "name";
            final String ATTRIBUTE_LESSON_PLACE = "place";
            final ListView lvSimple;
            lvSimple = (ListView) rootView.findViewById(R.id.lessonList);


            Calendar calendar = Calendar.getInstance();
            int pageNumberTemp = pageNumber + 1;
            calendar.set(Calendar.DAY_OF_WEEK, pageNumberTemp);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + weekPointer);

            SimpleDateFormat SDF = new SimpleDateFormat("d");
            final String day = SDF.format(calendar.getTime());

            SDF.applyPattern("MM");
            final String month = SDF.format(calendar.getTime());

            SDF.applyPattern("yyyy");
            final String year = SDF.format(calendar.getTime());

            // Get data from AuthorizationActivity
            savedInstanceState = getActivity().getIntent().getExtras();

            String AccessToken = "";

            if (savedInstanceState != null){
                AccessToken = savedInstanceState.getString("AccessToken");
            }


            //<JSON>

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL + day + "." + month + "." + year + "&atoken=" + AccessToken, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject json) {
                    try {
                        JSONArray JsonData = json.getJSONArray("response");
                        String[] Order = new String[JsonData.length()];
                        String[] Name = new String[JsonData.length()];
                        String[] Place = new String[JsonData.length()];

                        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(JsonData.length());
                        Map<String, Object> m;
                        JSONObject fields;
                        int i1 = 1;

                        for (int i = 0; i < JsonData.length(); i++) {
                            m = new HashMap<String, Object>();
                            fields = JsonData.getJSONObject(i);

                            Order[i] = "" + i1;
                            i1++;

                            if ((fields.getString("less_id").equals("0")) && (fields.getString("aud_id").equals("0"))) {
                                Name[i] = "-----";
                                Place[i] = "--";
                            } else {
                                Name[i] = fields.getString("less_id");
                                Place[i] = fields.getString("aud_id");
                            }

                            if (fields.getString("iszamina").equals("1")) {
                                Name[i] = Name[i] + " (Заміна)";
                            }

                            m.put(ATTRIBUTE_LESSON_ORDER, Order[i]);
                            m.put(ATTRIBUTE_LESSON_NAME, Name[i]);
                            m.put(ATTRIBUTE_LESSON_PLACE, Place[i]);
                            data.add(m);

                        }
                        // массив имен атрибутов, из которых будут читаться данные
                        String[] from = {ATTRIBUTE_LESSON_ORDER, ATTRIBUTE_LESSON_NAME, ATTRIBUTE_LESSON_PLACE};
                        // массив ID View-компонентов, в которые будут вставлять данные
                        int[] to = {R.id.lessonOrder, R.id.lessonName, R.id.lessonPlace};

                        // создаем адаптер
                        SimpleAdapter sAdapter = new SimpleAdapter(getActivity(), data, R.layout.fragment_schedule_item, from, to);
                        lvSimple.setAdapter(sAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Розклад відсутній на: " + day + "." + month + "." + year, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError VolleyError) {

                }
            });
            // </JSON>

            //Date
            final TextView DateLabel;
            DateLabel = (TextView) rootView.findViewById(R.id.date);

            //DateLabel.setText(" Current day: " + day + "." + month + "." + year);
            DateLabel.setText(" Поточний день: " + day + "." + month + "." + year);

            //<JSON>
            myQueue.add(jsonObjectRequest);
            //</JSON>

            return rootView;
        }
    }
}
