package gauge.soberupp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class MainActivity extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener {

    private DBHandler db;
    private List<Alcohol> alcohols;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        getUnitsDrunkThisWeek();
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
     * Sets up the menu
     * @param menu : the menu to add
     * @return : if it is successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Performs an event if the titleBar event is selected
     * @param item : the item to be chosen
     * @return : a super call to the method about closing the titleBar menu
     */
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

    /**
     * Gets the menu item and sends it to the superior method to move page
     * @param item : The item of the menu to by selected
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return super.onNavigationItemSelected(item);
    }

    private void getUnitsDrunkThisWeek() {
        //Get the Monday for start of week
        Calendar dateFrom = Calendar.getInstance();
        dateFrom.set(dateFrom.get(Calendar.YEAR), dateFrom.get(Calendar.MONTH), dateFrom.get(Calendar.DAY_OF_MONTH)-1, 0, 0, 0);
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
        double units = 0;
        for (Alcohol alcohol : alcohols) {
            Calendar date = Calendar.getInstance();
            date.set(Integer.parseInt(alcohol.getYYYY()), Integer.parseInt(alcohol.getMM())-1, Integer.parseInt(alcohol.getDD()), 0, 0, 0);
            // dateFrom <= date < dateTo
            if ((date.compareTo(dateFrom) >= 0) && ((dateTo.compareTo(date) >= 0) || (dateTo.equals(date)))) {
                units += alcohol.getUnits();
            }

        }
        TextView setUnits = (TextView) findViewById(R.id.setUnits);
        setUnits.setText(units + " units");
    }
}
