package gauge.soberupp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class Graph extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Alcohol> alcohols;
    private GraphView graph;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the XML file for the layout
        setContentView(R.layout.activity_graph);
        Intent intent = getIntent();
        // Sets the title of the page
        setTitle("Graphs");

        // Sets up the graph
        graph = (GraphView) findViewById(R.id.graph);

        //Gets the list of alcohols entries from the db
        db = new DBHandler(this);
        alcohols = db.getAllAlcohols();

        // Adds each days entry to a treemap
        TreeMap<Date, Double> alcoholList = new TreeMap<Date, Double>();
        for (Alcohol alcohol : alcohols) {
            Calendar date = Calendar.getInstance();
            date.set(Integer.parseInt(alcohol.getYYYY()), Integer.parseInt(alcohol.getMM()) - 1, Integer.parseInt(alcohol.getDD()), 0, 0, 0);
            Date d = date.getTime();
            System.out.println(d);
            if (alcoholList.containsKey(d)) {
                alcoholList.put(d, alcoholList.get(d) + alcohol.getUnits());
            } else {
                alcoholList.put(d, alcohol.getUnits());
            }
        }

        // Initialises the bar graph
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{});
        // Adds the data to the graph
        for (Date D : alcoholList.keySet()) {
            series.appendData(new DataPoint(D, alcoholList.get(D)), true, alcoholList.size());
        }

        // Sets up an listener to show the date and units drunk on the selected day
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Date day = new Date();
                day.setTime((long) dataPoint.getX());
                String date = sdf.format(day);
                Toast.makeText(Graph.this, date + " Units : " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
        if (!alcoholList.isEmpty()) {
            // set manual Y bounds
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(Collections.max(alcoholList.values()) + 1);

            // set manual X bounds
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(alcoholList.firstKey().getTime());
            graph.getViewport().setMaxX(alcoholList.lastKey().getTime());
        }

        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        graph.addSeries(series);


        // Sets the text boxes to todays date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        TextView dateTo = (TextView) findViewById(R.id.dateToText);
        TextView dateFrom = (TextView) findViewById(R.id.dateFromText);
        dateTo.setText(day + "-" + month + "-" + year);
        dateFrom.setText((day - 1) + "-" + month + "-" + year);


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
     * Shows the date picker when the button is pressed
     *
     * @param v : the view of the button
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle page = new Bundle();
        // Gives the right bundle depending on whether it was picked
        if (v.getId() == R.id.dateFrom) {
            page.putString("page", "GraphFrom");
        } else if (v.getId() == R.id.dateTo) {
            page.putString("page", "GraphTo");
        }
        newFragment.setArguments(page);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Gets the dates from the screen, error checks them and sends then into the filer Graph method
     *
     * @param view : the view of the button
     */
    public void filterData(View view) {
        // Gets the text from the screen
        TextView dateFrom = (TextView) findViewById(R.id.dateFromText);
        TextView dateTo = (TextView) findViewById(R.id.dateToText);
        TextView errorText = (TextView) findViewById(R.id.errorText);
        if (dateFrom.getText().equals("Date in future") || dateTo.getText().equals("Date in future")) {
            errorText.setText("One or more dates are in the future");
        } else {
            // Checks to see if date from is less than date to
            String[] dateFromSplit = dateFrom.getText().toString().split("-");
            String[] dateToSplit = dateTo.getText().toString().split("-");
            Calendar dateFromDay = Calendar.getInstance();
            dateFromDay.set(Integer.valueOf(dateFromSplit[2]), Integer.valueOf(dateFromSplit[1]) - 1, Integer.valueOf(dateFromSplit[0]), 0, 0, 0);
            Calendar dateToDay = Calendar.getInstance();
            dateToDay.set(Integer.valueOf(dateToSplit[2]), Integer.valueOf(dateToSplit[1]) - 1, Integer.valueOf(dateToSplit[0]), 0, 0, 0);
            if (dateToDay.compareTo(dateFromDay) > 0) {
                filterGraph(dateFromDay, dateToDay);
            } else {
                errorText.setText("Date From is greater or equal than the Date to");
            }
        }
    }

    /**
     * Filters the data series with the selected range
     *
     * @param dateFrom : the date the filtered data starts
     * @param dateTo   : the last date of the filtering
     */
    private void filterGraph(Calendar dateFrom, Calendar dateTo) {
        TextView errorText = (TextView) findViewById(R.id.errorText);
        Button filterData = (Button) findViewById(R.id.SubmitButton);
        graph.removeAllSeries();

        // Checks if the data has been filtered to unfilter it
        if (!errorText.getText().toString().equals("Data Filtered")) {
            // Makes inequality inclusive on both sides
            dateTo.set(dateTo.get(Calendar.YEAR), dateTo.get(Calendar.MONTH), dateTo.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
            // Removes the previous series from the graph
            TreeMap<Date, Double> alcoholList = new TreeMap<Date, Double>();
            // Iterates through the alcohols to see if they are in the range
            for (Alcohol alcohol : alcohols) {
                Calendar date = Calendar.getInstance();
                date.set(Integer.parseInt(alcohol.getYYYY()), Integer.parseInt(alcohol.getMM()) - 1, Integer.parseInt(alcohol.getDD()), 0, 0, 0);
                Date d = date.getTime();
                // dateFrom <= date <= dateTo
                if ((date.compareTo(dateFrom) >= 0) && ((dateTo.compareTo(date) >= 0) || (dateTo.equals(date)))) {
                    if (alcoholList.containsKey(d)) {
                        alcoholList.put(d, alcoholList.get(d) + alcohol.getUnits());
                    } else {
                        alcoholList.put(d, alcohol.getUnits());
                    }
                }
            }
            // Adds all the points to the graph
            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{});
            // Adds the data to the graph
            for (Date D : alcoholList.keySet()) {
                series.appendData(new DataPoint(D, alcoholList.get(D)), true, alcoholList.size());
            }

            // Adds a listener to the graph to display the data entry when you tap the graph
            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Date day = new Date();
                    day.setTime((long) dataPoint.getX());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String date = sdf.format(day);
                    Toast.makeText(Graph.this, date + " Units : " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
                }
            });


            if (!alcoholList.isEmpty()) {
                // set manual Y bounds
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setMinY(0);
                graph.getViewport().setMaxY(Collections.max(alcoholList.values()) + 5);

                // set manual X bounds
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(alcoholList.firstKey().getTime());
                graph.getViewport().setMaxX(alcoholList.lastKey().getTime());
            }
            graph.addSeries(series);
            errorText.setText("Data Filtered");
            filterData.setText("Unfilter Data");
        } else {
            // Adds each days entry to a treemap
            TreeMap<Date, Double> alcoholList = new TreeMap<Date, Double>();
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

            // Initialises the bar graph
            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{});
            // Adds the data to the graph
            for (Date D : alcoholList.keySet()) {
                series.appendData(new DataPoint(D, alcoholList.get(D)), true, alcoholList.size());
            }

            // Sets up an listener to show the date and units drunk on the selected day
            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Date day = new Date();
                    day.setTime((long) dataPoint.getX());
                    String date = sdf.format(day);
                    Toast.makeText(Graph.this, date + " Units : " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
                }
            });

            // set date label formatter
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
            graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
            if (!alcoholList.isEmpty()) {
                // set manual Y bounds
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setMinY(0);
                graph.getViewport().setMaxY(Collections.max(alcoholList.values()) + 1);

                // set manual X bounds
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(alcoholList.firstKey().getTime());
                graph.getViewport().setMaxX(alcoholList.lastKey().getTime());
            }
            graph.addSeries(series);
            errorText.setText("Data Unfiltered");
            filterData.setText("Filter Data");

        }
    }

}
