package gauge.soberupp;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddData extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Runs when the page is created
     * @param savedInstanceState : last instance of the page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Adds the xml file for layout
        setContentView(R.layout.activity_add_data);
        Intent intent = getIntent();
        // Sets the title of the page
        setTitle("Input Data");

        // Sets the text box to todays date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        TextView tv1= (TextView) findViewById(R.id.setDate);
        tv1.setText(day + "-" + month + "-" + year);

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

        final Spinner drinkType = (Spinner) findViewById(R.id.DrinkType);
        setDrinkSpinner(drinkType);
        drinkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setVolumeSpinner(drinkType.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });

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

    private DBHandler db = new DBHandler(this);
    private ArrayList<Alcohol> alcohols = new ArrayList<>();
    // This id correlates to Alcohol.id
    private int id  = 1;

    private void setDrinkSpinner(Spinner spin){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Drinks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
    }

    private void setVolumeSpinner(String drinkType){
        Spinner volume = (Spinner) findViewById(R.id.volume);
        EditText abv = (EditText) findViewById(R.id.ABVInput);
        ArrayAdapter<CharSequence> adapter;
        switch (drinkType) {
            case "Beer":
            case "Cider":
                abv.setText("4.5");
                adapter = ArrayAdapter.createFromResource(this, R.array.VolumeBeerCider, android.R.layout.simple_spinner_item);
                break;
            case "Wine":
                abv.setText("12");
                adapter = ArrayAdapter.createFromResource(this, R.array.VolumeWine, android.R.layout.simple_spinner_item);
                break;
            case "Spirits":
                abv.setText("37.5");
                adapter = ArrayAdapter.createFromResource(this, R.array.VolumeSpirits, android.R.layout.simple_spinner_item);
                break;
            default:
                abv.setText("4");
                adapter = ArrayAdapter.createFromResource(this, R.array.VolumeAlcopops, android.R.layout.simple_spinner_item);
                break;
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        volume.setAdapter(adapter);
    }

    public void getData(View view) {

        // Check if no view has focus:
        //http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
        TextView selectedDate = (TextView) findViewById(R.id.setDate);
        //Need to store
        String date = selectedDate.getText().toString();

        EditText abv = (EditText) findViewById(R.id.ABVInput);
        //Need to store
        String abvOfDrink = abv.getText().toString();

        EditText quantity = (EditText) findViewById(R.id.numberDrunk);
        // Need to store
        String quantityDrunk = quantity.getText().toString();

        Spinner drinkTypeSpinner = (Spinner) findViewById(R.id.DrinkType);
        // Need to store
        String drinkType = drinkTypeSpinner.getSelectedItem().toString();

        Spinner drinkVolumeSpinner = (Spinner) findViewById(R.id.volume);
        // Need to store
        String drinkVolume = drinkVolumeSpinner.getSelectedItem().toString();

        String[] volumeSplit = drinkVolume.split(" ");

        AlcoholType alcoholType = null;
        switch (drinkType) {
            case "Beer":
                alcoholType = AlcoholType.BEER;
                break;
            case "Cider":
                alcoholType = AlcoholType.CIDER;
                break;
            case "Wine":
                alcoholType = AlcoholType.WINE;
                break;
            case "Spirits":
                alcoholType = AlcoholType.SPIRITS;
                break;
        }
        assert alcoholType != null;
        alcoholType.setAbv(Double.valueOf(abvOfDrink));

        Alcohol alcohol = new Alcohol(id, date, alcoholType,
                Double.valueOf(volumeSplit[volumeSplit.length - 1].substring(0, volumeSplit[1].length() - 2)),
                Double.valueOf(quantityDrunk));

        db.addAlcohol(alcohol);
        alcohols.add(alcohol);
        Snackbar.make(view, "Alcohol Entry has been added", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        id++;
    }

    /**
     * This is executed when the "Read" button is pressed
     *
     * Creates a FileInputStream and opens data.txt.
     * Since FileInputStream reads by byte, we create a byte[] buffer of size 1024
     * The content of the file is then appended into a new string
     *
     * To access the file data, use fileContent.toString().
     * @param view View object required for button
     */
    public void readFile(View view) {
        printData(db.getAllAlcohols());
    }

    /**
     * Parses the data from readFile()'s fileContent.toString()
     */
    private void printData(List<Alcohol> alcoholList) {
        final TextView readData = (TextView) findViewById(R.id.printData);
        String log = "";
        for (Alcohol alcohol : alcoholList) {
            log += "id: " + alcohol.getId() + ", Date: " + alcohol.getDate() +
                    ", Type: " + alcohol.getAlcoholType().getName() + ", Volume: " +
                    alcohol.getVolume() + ", Quantity: " + alcohol.getQuantity() + "Units: " + alcohol.getUnits() +  "\n";
        }
        readData.setText(log);
        /*// Splits the string into newlines "\\r?\\n" is used so it's compatible with UNIX and Windows
        String[] newlines = data.split("\\r?\\n");
        // This ArrayList is for each new line of data
        ArrayList<String> singleData = new ArrayList<>();
        // The splitted data is then added onto this ArrayList
        for (int i = 0; i < newlines.length; i++) {
            singleData.add(i, newlines[i]);
        }
        // Number of days the person has entered data for
        int numDays = singleData.size();
        // Number of units the person has consumed alcohol
        double totalUnits = 0;

        // This will hold the dates and units from the parsed string from data.txt
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Double> units = new ArrayList<>();

        // Each string in singleData is then split by a comma.
        // Example: 15-09-2001, 12
        // The first bit is the date and the second one is units
        for (String s : singleData) {
            String[] temp = s.split(",");
            // The date is then added to the ArrayList
            dates.add(temp[0]);
            // Units are also added
            // Using Double.valueOf to make sure no numbers with leading zeroes are entered
            units.add(Double.valueOf(temp[1]));
        }

        // Count total units
        for (Double d : units) {
            totalUnits += d;
        }

        // And print...
        final TextView readData = (TextView) findViewById(R.id.printData);
        String dataToPrint = "";
        readData.setText(dataToPrint);
        if (numDays == 1) {
            dataToPrint += ("You have drank " + totalUnits + " units over the span of one day.");
            dataToPrint += "\n";
        } else {
            dataToPrint += ("You have drank " + totalUnits + " units over the span of " + numDays + " entries.");
            dataToPrint += "\n";
        }

        // More details for the user
        for (int i = 0; i < numDays; i++) {
            dataToPrint += ("You've had " + units.get(i) + " units on the " + dates.get(i) + ".");
            dataToPrint += "\n";
        }
        readData.setMovementMethod(new ScrollingMovementMethod());

        readData.setText(dataToPrint);*/
    }

    /**
     * Shows the date picker when the button is pressed
     * @param v : the view of the button
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}
