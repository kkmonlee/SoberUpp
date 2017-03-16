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

import java.util.List;

public class MainActivity extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener {

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
        DBHandler db = new DBHandler(this);

        // Inserting Alcohol/Rows
        /*
        Log.d("Insert: ", "Inserting...");
        db.addAlcohol(new Alcohol(1, "13-03-2017", AlcoholType.BEER, 568, 1));
        db.addAlcohol(new Alcohol(2, "14-03-2017", AlcoholType.WINE, 175, 2));
        db.addAlcohol(new Alcohol(3, "15-03-2017", AlcoholType.BEER, 568, 2));
        db.addAlcohol(new Alcohol(4, "16-03-2017", AlcoholType.CIDER, 568, 1));
        */
        // Reading all Alcohols
        Log.d("Reading: ", "Reading all Alcohol...");
        List<Alcohol> alcohols = db.getAllAlcohols();

        for (Alcohol alcohol : alcohols) {
            String log = "id: " + alcohol.getId() + ", Date: " + alcohol.getDate() +
                    ", Type: " + alcohol.getAlcoholType().getName() + ", Volume: " +
                    alcohol.getVolume() + ", Quantity: " + alcohol.getQuantity() + ", Units: " + alcohol.getUnits();
            // Writing alcohol to log
            Log.d("Alcohol: ", log);
        }
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
    }}
