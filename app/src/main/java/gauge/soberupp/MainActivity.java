package gauge.soberupp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;

public class MainActivity extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener {

    private DBHandler db;
    private List<Alcohol> alcohols;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Gets the XML page for the layout
        setContentView(R.layout.activity_main);
        // Sets the title of the page
        setTitle("Home");
        Intent i = getIntent();

        // START Code for the Navigation Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // END Code for the Navigation Bar

        // START code for Database
        db = new DBHandler(this);
        alcohols = db.getAllAlcohols();

        // Code to set pie chart
        //https://github.com/zurche/plain-pie?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=3689
        PieView pieView = (PieView) findViewById(R.id.pieView);
        double percent = getUnitsDrunkThisWeek() * 100 / getCurrentGoal();
        if (percent == 0)
            pieView.setPercentage((float) 0.01);
        else
            pieView.setPercentage((float) percent);
        pieView.setInnerText("You have had " + Math.floor(percent) + "% of your weekly alowance of " + getCurrentGoal() + " units");
        pieView.setPercentageTextSize(30);
        //Sets the colour of the bar
        if (percent < 20) {
            pieView.setPercentageBackgroundColor(Color.GREEN);
        } else if (percent < 40) {
            pieView.setPercentageBackgroundColor(Color.YELLOW);
        } else if (percent < 60) {
            pieView.setPercentageBackgroundColor(Color.parseColor("#ffcc00"));
        } else if (percent < 80) {
            pieView.setPercentageBackgroundColor(Color.parseColor("#ff9900"));
        } else {
            pieView.setPercentageBackgroundColor(Color.RED);
        }
        PieAngleAnimation animation = new PieAngleAnimation(pieView);
        animation.setDuration(2500); //This is the duration of the animation in millis
        pieView.startAnimation(animation);

        scheduleNotification();
    }

    // Creates a notification
    //https://gist.github.com/BrandonSmith/6679223
    private void scheduleNotification() {

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("SoberUpp");
        builder.setContentText("Have you remembered to add data today?");
        builder.setSmallIcon(R.mipmap.ic_launcher);

        Intent resultIntent = new Intent(this, AddData.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AddData.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(1, builder.build());
    }

    /**
     * Runs when the Navigation Bar is closed
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Gets the menu item and sends it to the superior method to move page
     *
     * @param item : The item of the menu to by selected
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return super.onNavigationItemSelected(item);
    }

    /**
     * Calculates how many units have been drunk this week
     */
    private double getUnitsDrunkThisWeek() {
        //Get the Monday for start of week
        Calendar dateFrom = Calendar.getInstance();
        dateFrom.set(dateFrom.get(Calendar.YEAR), dateFrom.get(Calendar.MONTH), dateFrom.get(Calendar.DAY_OF_MONTH) - 1, 0, 0, 0);
        while (dateFrom.get(Calendar.DAY_OF_WEEK) > dateFrom.getFirstDayOfWeek()) {
            dateFrom.add(Calendar.DATE, -1); // Substract 1 day until first day of week.
        }
        dateFrom.set(dateFrom.get(Calendar.YEAR), dateFrom.get(Calendar.MONTH), dateFrom.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);

        // Gets the sunday of the end of the week
        Calendar dateTo = Calendar.getInstance();
        dateTo.set(dateTo.get(Calendar.YEAR), dateTo.get(Calendar.MONTH), dateTo.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        while (dateTo.get(Calendar.DAY_OF_WEEK) > dateTo.getFirstDayOfWeek()) {
            dateTo.add(Calendar.DATE, +1); // Adds 1 day until first day of week.
        }
        dateTo.set(dateTo.get(Calendar.YEAR), dateTo.get(Calendar.MONTH), dateTo.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);

        // Iterates though the alcohol list and works out which ones are in the range
        double units = 0;
        for (Alcohol alcohol : alcohols) {
            Calendar date = Calendar.getInstance();
            date.set(Integer.parseInt(alcohol.getYYYY()), Integer.parseInt(alcohol.getMM()) - 1, Integer.parseInt(alcohol.getDD()), 0, 0, 0);
            // dateFrom <= date < dateTo
            if ((date.compareTo(dateFrom) >= 0) && ((dateTo.compareTo(date) >= 0) || (dateTo.equals(date)))) {
                units += alcohol.getUnits();
            }

        }
        return units;
    }

    /**
     * Moves the page ot the Add Data page
     *
     * @param view : view of the button
     */
    public void goToAddData(View view) {
        startActivity(new Intent(this, AddData.class));
    }

    /**
     * Moves the page ot the Sober Diary page
     *
     * @param view : view of the button
     */
    public void goToSoberDiary(View view) {
        startActivity(new Intent(this, SoberDiary.class));
    }

    /**
     * Moves the page ot the Graphs page
     *
     * @param view : view of the button
     */
    public void goToGraphs(View view) {
        startActivity(new Intent(this, Graph.class));
    }

    /**
     * Moves the page ot the Settings page
     *
     * @param view : view of the button
     */
    public void goToSettings(View view) {
        startActivity(new Intent(this, Settings.class));
    }


    /**
     * Gets the current goal value
     *
     * @return : the current goal
     */
    private int getCurrentGoal() {
        //Get the Monday for start of week
        Calendar dateFrom = Calendar.getInstance();
        dateFrom.set(dateFrom.get(Calendar.YEAR), dateFrom.get(Calendar.MONTH), dateFrom.get(Calendar.DAY_OF_MONTH) - 1, 0, 0, 0);
        while (dateFrom.get(Calendar.DAY_OF_WEEK) > dateFrom.getFirstDayOfWeek()) {
            dateFrom.add(Calendar.DATE, -1); // Subtract 1 day until first day of week.
        }
        dateFrom.set(dateFrom.get(Calendar.YEAR), dateFrom.get(Calendar.MONTH), dateFrom.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
        String dateFromString = sdf.format(dateFrom.getTime());

        return getGoal(dateFromString);
    }

    /**
     * Gets the goal for the date provided
     *
     * @param date : the date string
     * @return : the goal
     */
    public int getGoal(String date) {
        HashMap<String, Integer> goals = db.getAllGoals();
        // Defaults to 14 if there are no goals set
        if (goals.size() == 0) {
            return 14;
        } else {
            // Checks if they have added a goal for this week
            if (goals.containsKey(date)) {
                return goals.get(date);
            } else {
                for(int i = 0; i< 5 ; i++) {
                    // Goes to previous weeks goals instead
                    String[] dateSplit = date.split("-");
                    Calendar goalDate = Calendar.getInstance();
                    goalDate.set(Integer.parseInt(dateSplit[2]), Integer.parseInt(dateSplit[1]) - 1, Integer.parseInt(dateSplit[0]));
                    // Gets Last weeks date
                    goalDate.add(Calendar.DATE, -7);
                    date = sdf.format(goalDate.getTime());
                    if (goals.containsKey(date)) {
                        return (goals.get(date));
                    }
                }
                return 14;
            }
        }
    }

}
