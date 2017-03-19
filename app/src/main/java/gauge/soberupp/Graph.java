package gauge.soberupp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

        DBHandler db = new DBHandler(this);
        alcohols = db.getAllAlcohols();
        // Adds each days entry to a treemap
        TreeMap<Date, Double> alcoholList = new TreeMap<Date, Double>();
        for (Alcohol alcohol : alcohols) {
            Calendar date = Calendar.getInstance();
            date.set(Integer.parseInt(alcohol.getYYYY()), Integer.parseInt(alcohol.getMM())-1, Integer.parseInt(alcohol.getDD()), 0,0,0);
            Date d = date.getTime();
            System.out.println(d);
            if(alcoholList.containsKey(d)){
                alcoholList.put(d, alcoholList.get(d) + alcohol.getUnits());
            } else {
                alcoholList.put(d, alcohol.getUnits());
            }
        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {});
        // Adds the data to the graph
        for(Date D : alcoholList.keySet()){
            series.appendData(new DataPoint(D, alcoholList.get(D)), true, alcoholList.size());
        }

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Date day = new Date();
                day.setTime((long)dataPoint.getX());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String date = sdf.format(day);
                Toast.makeText(Graph.this,date + " Units : " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        if(!alcoholList.isEmpty()) {
            // set manual Y bounds
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(Collections.max(alcoholList.values())+1);

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
        TextView dateTo= (TextView) findViewById(R.id.dateToText);
        TextView dateFrom= (TextView) findViewById(R.id.dateFromText);
        dateTo.setText(day + "-" + month + "-" + year);
        dateFrom.setText((day-1) + "-" + month + "-" + year);




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

    /**
     * Shows the date picker when the button is pressed
     * @param v : the view of the button
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle page = new Bundle();
        // Gives the right bundle depending on whether it was picked
        if(v.getId() == R.id.dateFrom) {
            page.putString("page", "GraphFrom");
        } else if(v.getId() == R.id.dateTo){
            page.putString("page", "GraphTo");
        }
        newFragment.setArguments(page);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void filterData(View view){
        TextView dateFrom = (TextView) findViewById(R.id.dateFromText);
        TextView dateTo = (TextView) findViewById(R.id.dateToText);
        TextView errorText = (TextView) findViewById(R.id.errorText);
        if(dateFrom.getText().equals("Date in future")|| dateTo.getText().equals("Date in future")){
            errorText.setText("One or more dates are in the future");
        } else {
            String[] dateFromSplit = dateFrom.getText().toString().split("-");
            String[] dateToSplit = dateTo.getText().toString().split("-");
            Calendar dateFromDay = Calendar.getInstance();
            dateFromDay.set(Integer.valueOf(dateFromSplit[2]), Integer.valueOf(dateFromSplit[1])-1, Integer.valueOf(dateFromSplit[0]), 0,0,0);
            Calendar dateToDay = Calendar.getInstance();
            dateToDay.set(Integer.valueOf(dateToSplit[2]), Integer.valueOf(dateToSplit[1])-1, Integer.valueOf(dateToSplit[0]), 0,0,0);
            if(dateToDay.compareTo(dateFromDay) > 0){
                filterGraph(dateFromDay, dateToDay);
                errorText.setText("Data Filtered");
            } else {
                errorText.setText("<html>Date From is greater or equal than the Date to</html>");
            }
        }
    }

    private void filterGraph(Calendar dateFrom, Calendar dateTo) {
        // Makes inequality inclusive on both sides
        dateTo.set(dateTo.get(Calendar.YEAR), dateTo.get(Calendar.MONTH), dateTo.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
        graph.removeAllSeries();
        TreeMap<Date, Double> alcoholList = new TreeMap<Date, Double>();
        for (Alcohol alcohol : alcohols) {
            Calendar date = Calendar.getInstance();
            date.set(Integer.parseInt(alcohol.getYYYY()), Integer.parseInt(alcohol.getMM()) - 1, Integer.parseInt(alcohol.getDD()), 0, 0, 0);
            Date d = date.getTime();
            // dateFrom <= date <= dateTo
            if ((date.compareTo(dateFrom) >= 0) && ((dateTo.compareTo(date) >= 0)||(dateTo.equals(date)))) {
                System.out.println(date);
                if (alcoholList.containsKey(d)) {
                    alcoholList.put(d, alcoholList.get(d) + alcohol.getUnits());
                } else {
                    alcoholList.put(d, alcohol.getUnits());
                }
            }
        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{});
        // Adds the data to the graph
        for (Date D : alcoholList.keySet()) {
            series.appendData(new DataPoint(D, alcoholList.get(D)), true, alcoholList.size());
        }
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Date day = new Date();
                day.setTime((long)dataPoint.getX());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String date = sdf.format(day);
                Toast.makeText(Graph.this,date + " Units : " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });



        if(!alcoholList.isEmpty()) {
            // set manual Y bounds
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(Collections.max(alcoholList.values())+5);

            // set manual X bounds
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(alcoholList.firstKey().getTime());
            graph.getViewport().setMaxX(alcoholList.lastKey().getTime());
        }
        graph.addSeries(series);
    }

}
