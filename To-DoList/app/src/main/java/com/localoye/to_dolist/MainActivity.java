package com.localoye.to_dolist;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {


    SectionsPagerAdapter mSectionsPagerAdapter;
    String list[]=new String[3];
    String tasks[][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Fetch Current Tasks from DB
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        tasks =dbHandler.fetchList("inprogress");

        // Notification for Current Tasks
        notifyUser(tasks[0],"Current",10);

        // Fetch Pending Tasks from DB
        tasks =dbHandler.fetchList("pending");

        // Notification for Pending Tasks
        notifyUser(tasks[0], "Pending", 12);

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

    // If you exit the application or if the app goes in background
    // update the notifications by cancelling the previous ones
    // and sending an updated one
    @Override
    protected void onPause() {
        super.onPause();

        // Cancel previous current task notification
        cancelNotification(getApplicationContext(),10);

        // Cancel previous current task notification
        cancelNotification(getApplicationContext(),12);

        // Fetch Current Tasks from DB
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        tasks =dbHandler.fetchList("inprogress");

        // Notification for Current Tasks
        notifyUser(tasks[0],"Current",10);

        // Fetch Pending Tasks from DB
        tasks =dbHandler.fetchList("pending");

        // Notification for Pending Tasks
        notifyUser(tasks[0], "Pending", 12);
    }


    // Function for creating Notification
    public void notifyUser(String[] title, String type,int id) {

        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
        .setContentTitle("To-Do List")
        .setContentText(type+" Tasks")
        .setSubText("Pull down to view tasks")
        .setSmallIcon(R.drawable.abc_btn_radio_material)
        .setAutoCancel(true)
        .setOngoing(true)
        .setTicker("Task Alert!")
        .setSound(soundUri);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(type+" Tasks");
        for(int i=0;i<title.length;i++) {
            inboxStyle.addLine(title[i]);
        }
        if(title.length==0){
            builder.setSubText("No tasks found!");
        }
        builder.setStyle(inboxStyle);

        Notification notification = builder.build();
        NotificationManager notificationManger =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

    public static void cancelNotification(Context context, int notifyId) {
        String notificationService = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(notificationService);
        notificationManager.cancel(notifyId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // Adding a new note
        if(id == R.id.action_add_task){
            // AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add a task");
            final EditText inputTitle = new EditText(this);
            inputTitle.setHint("Title");

            final EditText inputText = new EditText(this);
            inputText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            inputText.setSingleLine(false);
            inputText.setHint("Description");
            inputText.setLines(3);

            final EditText inputDate = new EditText(this);
            inputDate.setHint("Date (DD/MM/YYYY)");
            inputDate.setInputType(InputType.TYPE_NULL);
            inputDate.setFocusable(false);

            final Calendar myCalendar = Calendar.getInstance();

            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel();
                }
                private void updateLabel() {

                    String myFormat = "yyyy-MM-dd";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    inputDate.setText(sdf.format(myCalendar.getTime()));
                }
            };
            inputDate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // Show datepicker
                    new DatePickerDialog(MainActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            final EditText inputTime = new EditText(this);
            inputTime.setHint("Time (HH:MM)");
            inputTime.setInputType(InputType.TYPE_NULL);
            inputTime.setFocusable(false);

            final TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String hour=String.format("%02d",hourOfDay);
                    String min=String.format("%02d",minute);
                    inputTime.setText(hour + ":" + min);
                }
            };

            inputTime.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // Show timepicker
                    new TimePickerDialog(MainActivity.this, timePickerListener, myCalendar
                            .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                            true).show();
                }
            });

            LinearLayout linearLayout=new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(inputTitle);
            linearLayout.addView(inputText);
            linearLayout.addView(inputDate);
            linearLayout.addView(inputTime);

            builder.setView(linearLayout);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    list[0]=inputTitle.getText().toString();
                    list[1]=inputText.getText().toString();
                    list[2]=inputDate.getText().toString()+" "+inputTime.getText().toString()+":00";
                    if(list[0].matches("")||list[1].matches("")||inputTime.getText().toString().matches("")||inputDate.getText().toString().matches("")){
                        Toast.makeText(getApplicationContext(),"Enter all details and try again",Toast.LENGTH_LONG).show();
                    }else {
                        // Add to DB
                        MyDBHandler dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
                        dbHandler.addList(list);
                        NewTaskList newTaskListFragment = new NewTaskList();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, newTaskListFragment).commit();
                    }
                }
            });

            builder.setNegativeButton("Cancel",null);
            builder.create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        if (tab.getPosition() == 0) {
            NewTaskList newTaskListFragment = new NewTaskList();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, newTaskListFragment).commit();
        }
        else if (tab.getPosition() == 1) {
            InProgressTaskList inProgressTaskListFragment = new InProgressTaskList();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, inProgressTaskListFragment).commit();
        }
        else if (tab.getPosition() == 2) {
            CompletedTaskList completedTaskListFragment = new CompletedTaskList();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, completedTaskListFragment).commit();
        }
        else {
            PendingTaskListFragment pendingTaskListFragment = new PendingTaskListFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, pendingTaskListFragment).commit();
        }
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
            // Show 4 pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
