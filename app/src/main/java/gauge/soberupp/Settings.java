package gauge.soberupp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Settings extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Gets the XML file for the layout
        setContentView(R.layout.activity_settings);
        // Sets the title of the page
        setTitle("Settings");
        setNewGoalDate();
        setCurrentGoal();
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
     * Clears the text from the file
     *
     * @param view : the view of the button
     */
    public void clearDB(View view) {
        DBHandler db = new DBHandler(this);
        db.deleteAll();
    }

    /**
     * Goes to the edit data page
     *
     * @param view : the view of the button
     */
    public void goToEditData(View view) {
        startActivity(new Intent(this, EditData.class));
    }


    public void setCurrentGoal() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        //Get the Monday for start of week
        Calendar dateFrom = Calendar.getInstance();
        dateFrom.set(dateFrom.get(Calendar.YEAR), dateFrom.get(Calendar.MONTH), dateFrom.get(Calendar.DAY_OF_MONTH) - 1, 0, 0, 0);
        while (dateFrom.get(Calendar.DAY_OF_WEEK) > dateFrom.getFirstDayOfWeek()) {
            dateFrom.add(Calendar.DATE, -1); // Substract 1 day until first day of week.
        }
        dateFrom.set(dateFrom.get(Calendar.YEAR), dateFrom.get(Calendar.MONTH), dateFrom.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
        String dateFromString = sdf.format(dateFrom.getTime());


        // Gets the Sunday of the end of the week
        Calendar dateTo = Calendar.getInstance();
        dateTo.set(dateTo.get(Calendar.YEAR), dateTo.get(Calendar.MONTH), dateTo.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        while (dateTo.get(Calendar.DAY_OF_WEEK) > dateTo.getFirstDayOfWeek()) {
            dateTo.add(Calendar.DATE, +1); // Adds 1 day until first day of week.
        }
        dateTo.set(dateTo.get(Calendar.YEAR), dateTo.get(Calendar.MONTH), dateTo.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        String dateToString = sdf.format(dateTo.getTime());

        TextView currentGoal = (TextView) findViewById(R.id.CurrentGoal);
        currentGoal.setText("Your current goal is \n From " + dateFromString + " to " + dateToString + "\n with ?? units this week");
    }

    public void setNewGoalDate() {
        // Gets the Monday of the beginning of next week
        Calendar dateTo = Calendar.getInstance();
        dateTo.set(dateTo.get(Calendar.YEAR), dateTo.get(Calendar.MONTH), dateTo.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        while (dateTo.get(Calendar.DAY_OF_WEEK) > dateTo.getFirstDayOfWeek()) {
            dateTo.add(Calendar.DATE, +1); // Adds 1 day until first day of week.
        }
        dateTo.set(dateTo.get(Calendar.YEAR), dateTo.get(Calendar.MONTH), dateTo.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(dateTo.getTime());
        TextView dateForNewGoal = (TextView) findViewById(R.id.goalSetDate);
        dateForNewGoal.setText("The next goal is for the week beginning " + date);

    }
}
