package org.advmiguelturra;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;


public class TeamsActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

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
    }

    private void savePictureToGallery(final int id, final String filename) {
        FileOutputStream out = null;
        File file = new File(filename);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), id);

        //// TODO: 10/12/15 comprobar si ya existe y si es la misma o distinta

        try {
            out = new FileOutputStream(file);
            Log.i("ADV", "Saving picture: " + file.getAbsolutePath());
            bm.compress(Bitmap.CompressFormat.JPEG, 92, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e("SAVING", e.toString());
        }
    }

    public void openPicture(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        ImageView iv = (ImageView) v;
        String filename = Environment.getExternalStorageDirectory().toString() + "/Pictures/" + iv.getTag();
        Uri uri = Uri.parse("file://" + filename);
        intent.setDataAndType(uri, "image/jpeg");

        File f = new File(filename);
        if(!f.exists()) {
            HashMap<String, Integer> id = new HashMap<>();
            id.put("team_sf.jpg", R.drawable.team_sf);
            id.put("team_sma.jpg", R.drawable.team_sma);
            id.put("team_smb.jpg", R.drawable.default_team);
            id.put("team_jfa.jpg", R.drawable.team_jfa);
            id.put("team_jfb.jpg", R.drawable.team_jfb);
            id.put("team_jm.jpg", R.drawable.team_jm);

            savePictureToGallery(id.get(v.getTag()), filename);
        }

        try {
            Log.d("ADV PIC", "Trying to open " + uri.toString());
            startActivityForResult(intent, RESULT_OK);
        } catch (ActivityNotFoundException e) {
            Log.e("ADV PIC", e.toString());
            Toast t = Toast.makeText(this, "No se pudo abrir la imagen", Toast.LENGTH_SHORT);
            t.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teams, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
            return PlaceholderFragment.newInstance(getPageTitle(position).toString());
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
                    return "SF";
                case 1:
                    return "SMA";
                case 2:
                    return "SMB";
                case 3:
                    return "JFA";
                case 4:
                    return "JFB";
                case 5:
                    return "JM";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_TITLE = "section_title";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        /**
         * Returns a new instance of this fragment for the given section title.
         */
        public static PlaceholderFragment newInstance(String sectionTitle) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_TITLE, sectionTitle);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Bundle args = getArguments();
            String tabTitle = args.getString(ARG_SECTION_TITLE);
            View rootView;

            switch (tabTitle) {
                case "SF":
                    rootView = inflater.inflate(R.layout.senior_women, container, false);
                    break;
                case "SMA":
                    rootView = inflater.inflate(R.layout.senior_men, container, false);
                    break;
                case "SMB":
                    rootView = inflater.inflate(R.layout.senior_men_b, container, false);
                    break;
                case "JFA":
                    rootView = inflater.inflate(R.layout.junior_women, container, false);
                    break;
                case "JFB":
                    rootView = inflater.inflate(R.layout.junior_women_b, container, false);
                    break;
                case "JM":
                    rootView = inflater.inflate(R.layout.junior_men, container, false);
                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_teams, container, false);
                    break;
            }

            return rootView;
        }
    }
}

