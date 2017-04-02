package gauge.soberupp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
        callPopup();
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
            dateFrom.add(Calendar.DATE, -1); // Subtract 1 day until first day of week.
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

    /**
     * Sets the popup to confirm deletion of database
     */
    private void callPopup() {
        // Creates the pop up window
        final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.popup, null);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        TextView popUp = (TextView) popupView.findViewById(R.id.popupText);
        popUp.setText("Are you sure you want to delete all entries");
        // Decides what happens on the button clicks
        ((Button) popupView.findViewById(R.id.cancel))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        popupWindow.dismiss();
                        Snackbar.make(getCurrentFocus(), "Database not cleared", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });
        ((Button) popupView.findViewById(R.id.confirm))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        popupWindow.dismiss();
                        DBHandler db = new DBHandler(getApplicationContext());
                        db.deleteAll();
                        Snackbar.make(getCurrentFocus(), "Database Cleared", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });
    }

}
