package gauge.soberupp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearSmoothScroller;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class TrophyPage extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private DBHandler db;
    private int noOfGoalsAcheived = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy_page);

        db = new DBHandler(this);
        goalsAchieved();
        showTrophies();
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
     * Calculates how many goals have been achieved
     */
    private void goalsAchieved(){
        HashMap<String, Integer> goals = db.getAllGoals();
        List<Alcohol> alcohols = db.getAllAlcohols();
        // Adds each weeks entry to a treemap
        TreeMap<Calendar, Double> alcoholList = new TreeMap<>();

        for (Alcohol alcohol : alcohols) {
            Calendar date = Calendar.getInstance();
            date.set(Integer.parseInt(alcohol.getYYYY()), Integer.parseInt(alcohol.getMM()) - 1, Integer.parseInt(alcohol.getDD()) -1, 0, 0, 0);

            //Get the Monday for start of week from date
            Calendar dateFrom = date;
            while (dateFrom.get(Calendar.DAY_OF_WEEK) > dateFrom.getFirstDayOfWeek()) {
                dateFrom.add(Calendar.DATE, -1); // Subtract 1 day until first day of week.
            }
            dateFrom.set(dateFrom.get(Calendar.YEAR), dateFrom.get(Calendar.MONTH), dateFrom.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
            String dateFromString = sdf.format(dateFrom.getTime());
            // Adds the entry to the week associated with it
            if (alcoholList.containsKey(dateFrom)) {
                alcoholList.put(dateFrom, alcoholList.get(dateFrom) + alcohol.getUnits());
            } else {
                alcoholList.put(dateFrom, alcohol.getUnits());
            }
        }

        // Checks if the goal has been met
        for(Calendar date: alcoholList.keySet()){
            String dateString = sdf.format(date.getTime());
            System.out.println(dateString);
            int goal = getGoal(dateString);
            System.out.println(goal);
            System.out.println(alcoholList.get(date));

            if(goal >= alcoholList.get(date)){
                noOfGoalsAcheived++;
            }
        }
    }

    /**
     * Gets the goal for the date provided
     * @param date : the date string
     * @return : the goal
     */
    public int getGoal(String date){
        HashMap<String, Integer> goals = db.getAllGoals();
        // Defaults to 14 if there are no goals set
        int numberOfGoals = 0;
        if(goals.size() == 0) {
            return 14;
        } else {
            // Checks if they have added a goal for this week
            if(goals.containsKey(date)){
                return goals.get(date);
            } else {
                while(numberOfGoals < goals.size()) {
                    // Goes to previous weeks goals instead
                    String[] dateSplit = date.split("-");
                    Calendar goalDate = Calendar.getInstance();
                    goalDate.set(Integer.parseInt(dateSplit[2]), Integer.parseInt(dateSplit[1])-1, Integer.parseInt(dateSplit[0]));
                    // Gets Last weeks date
                    goalDate.add(Calendar.DATE, -7);
                    date = sdf.format(goalDate.getTime());
                    if(goals.containsKey(date)){
                        return(goals.get(date));
                    }
                    numberOfGoals ++;
                }
                return 14;
            }
        }
    }

    private void showTrophies(){
        TextView goalsAchieved = (TextView) findViewById(R.id.noOfTrophies);
        goalsAchieved.setText("You have achieved " + noOfGoalsAcheived + " goals");
        if(noOfGoalsAcheived >= 1)
            findViewById(R.id.OneGoal).setVisibility(View.VISIBLE);
        if(noOfGoalsAcheived >= 2)
            findViewById(R.id.TwoGoal).setVisibility(View.VISIBLE);
        if(noOfGoalsAcheived >= 3)
            findViewById(R.id.ThreeGoal).setVisibility(View.VISIBLE);
        if(noOfGoalsAcheived >= 5)
            findViewById(R.id.FiveGoal).setVisibility(View.VISIBLE);
        if(noOfGoalsAcheived >= 8)
            findViewById(R.id.EightGoal).setVisibility(View.VISIBLE);
        if(noOfGoalsAcheived == 0)
            findViewById(R.id.ZeroGoal).setVisibility(View.VISIBLE);
    }


}
