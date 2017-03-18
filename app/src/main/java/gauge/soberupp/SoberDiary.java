package gauge.soberupp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SoberDiary extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener, OnDateSelectedListener, OnMonthChangedListener {

    MaterialCalendarView widget;
    DBHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the XML file for the layout
        setContentView(R.layout.activity_sober_diary);
        db = new DBHandler(this);
        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);
        //Sets the title of the page
        setTitle("Sober Diary");
        Intent intent = getIntent();
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

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        TextView day = (TextView) findViewById(R.id.DataForTheDay);
        day.setText(getSelectedDatesString());
    }
    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        TextView day = (TextView) findViewById(R.id.DataForTheDay);
        day.setText(date.getDate().toString());
    }


    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return setRecordsForDay(date);
    }

    private String setRecordsForDay(CalendarDay selectedDate){
        String log = "";
        Calendar chosenDay = Calendar.getInstance();
        chosenDay.set(selectedDate.getYear(), selectedDate.getMonth()+1, selectedDate.getDay(), 0,0,0);
        List<Alcohol> alcohols = db.getAllAlcohols();
        for(Alcohol alcohol:alcohols){
            Calendar currentDay = Calendar.getInstance();
            currentDay.set(Integer.parseInt(alcohol.getYYYY()),
                    Integer.parseInt(alcohol.getMM()), Integer.parseInt(alcohol.getDD()), 0, 0, 0);
            if(chosenDay.equals(currentDay)){
                log += "id: " + alcohol.getId() + ", Date: " + alcohol.getDate() +
                        ", Type: " + alcohol.getAlcoholType().getName() + ", Volume: " +
                        alcohol.getVolume() + ", Quantity: " + alcohol.getQuantity() +
                        "Units: " + alcohol.getUnits() +  "\nComment: " + alcohol.getComment() +
                        "\n";
            }
        }
        if(log.isEmpty()){
            return "No entries for date";}
        return log;
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
}
