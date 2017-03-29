package gauge.soberupp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;


public class SoberDiary extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener, OnDateSelectedListener {

    MaterialCalendarView widget;
    DBHandler db;
    List<Alcohol> alcohols;
    TreeMap<Date, Double> alcoholList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the XML file for the layout
        setContentView(R.layout.activity_sober_diary);
        // Gets all the alcohol objects from the database
        db = new DBHandler(this);
        alcohols = db.getAllAlcohols();

        // Sets the calendar to have background colours if you have drunk
        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        widget.setOnDateChangedListener(this);
        addDatesToHashMap();
        widget.addDecorator(new EventDecorator(Color.RED, getDays(10)));
        widget.addDecorator(new EventDecorator(Color.parseColor("#ff9900"), getDays(5, 10)));
        widget.addDecorator(new EventDecorator(Color.YELLOW, getDays(3, 5)));
        widget.addDecorator(new EventDecorator(Color.GREEN, getDays(0, 3)));

        //Sets the title of the page
        setTitle("Sober Diary");
        Intent intent = getIntent();
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

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        TextView day = (TextView) findViewById(R.id.DataForTheDay);
        day.setText("");
        day.setText(getSelectedDatesString());
    }


    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return setRecordsForDay(date);
    }

    /**
     * Gets the record log for the day selected
     *
     * @param selectedDate : the date selected
     * @return : a string of the data for the day
     */
    private String setRecordsForDay(CalendarDay selectedDate) {
        String log = "";
        Calendar chosenDay = Calendar.getInstance();
        chosenDay.set(selectedDate.getYear(), selectedDate.getMonth(), selectedDate.getDay(), 0, 0, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateChosen = sdf.format(chosenDay.getTime());

        for (Alcohol alcohol : alcohols) {
            Calendar currentDay = Calendar.getInstance();
            currentDay.set(Integer.parseInt(alcohol.getYYYY()),
                    Integer.parseInt(alcohol.getMM()) - 1, Integer.parseInt(alcohol.getDD()), 0, 0, 0);
            String alcoholDate = sdf.format(currentDay.getTime());

            if (alcoholDate.equals(dateChosen)) {
                log += "id: " + alcohol.getId() + ", Date: " + alcohol.getDate() +
                        ", Type: " + alcohol.getAlcoholType().getName() + ", Volume: " +
                        alcohol.getVolume() + ", Quantity: " + alcohol.getQuantity() +
                        ", Units: " + alcohol.getUnits() + "\nComment: " + alcohol.getComment() +
                        "\n";
            }
        }
        if (log.isEmpty()) {
            log = "No entries for date";
        }
        return log;
    }

    /**
     * Adds all the dates in the DB to a hashmap
     */
    private void addDatesToHashMap() {
        alcoholList = new TreeMap<Date, Double>();
        for (Alcohol alcohol : alcohols) {
            Calendar date = Calendar.getInstance();
            date.set(Integer.parseInt(alcohol.getYYYY()), Integer.parseInt(alcohol.getMM()) - 1, Integer.parseInt(alcohol.getDD()), 0, 0, 0);
            Date d = date.getTime();
            if (alcoholList.containsKey(d)) {
                alcoholList.put(d, alcoholList.get(d) + alcohol.getUnits());
            } else {
                alcoholList.put(d, alcohol.getUnits());
            }

        }
    }

    /**
     * Gets the days where the units drunk is within the min/max
     *
     * @param min : the min units drunk
     * @param max : the max units drunk
     * @return : a list of all the days within the range
     */
    private List<CalendarDay> getDays(int min, int max) {
        ArrayList<CalendarDay> dates = new ArrayList<CalendarDay>();
        for (Date d : alcoholList.keySet()) {
            if ((alcoholList.get(d) > min) && (alcoholList.get(d) <= max)) {
                dates.add(CalendarDay.from(d));
            }
        }
        return dates;
    }

    /**
     * Gets the days where the units drunk were more than the the min value
     *
     * @param min : the min units drunk
     * @return : the list of all the days within the range
     */
    private List<CalendarDay> getDays(int min) {
        ArrayList<CalendarDay> dates = new ArrayList<CalendarDay>();
        for (Date d : alcoholList.keySet()) {
            if ((alcoholList.get(d) > min)) {
                dates.add(CalendarDay.from(d));
            }
        }
        return dates;
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
}
