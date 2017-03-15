package gauge.soberupp;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

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

    private ArrayList<Alcohol> alcohols = new ArrayList<>();
    // This id correlates to Alcohol.id
    private int id  = 1;

    public void getData(View view) {

        // Check if no view has focus:
        //http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }

        final TextView selectedDate = (TextView) findViewById(R.id.setDate);
        final String date = selectedDate.getText().toString();
        final EditText unitsDrank = (EditText) findViewById(R.id.unitsInput);
        final String units = unitsDrank.getText().toString();
        double unitsDrankInput = 0;
        if (!units.isEmpty() && !date.contentEquals("Date in future")){
            unitsDrankInput = Double.parseDouble(units);
            if(unitsDrankInput <= 50) {
                Alcohol alcohol = new Alcohol(id, unitsDrankInput, date);
                alcohols.add(alcohol);
                id++;
                writeFile(alcohol);
                Snackbar.make(view, Integer.toString(alcohols.size()), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {
                Snackbar.make(view, "Too many units inputted", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else {
            if(units.isEmpty())
                Snackbar.make(view, "Add how many Units Drunk", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            else
                Snackbar.make(view, "Select a date", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        // Clears the input data from the text boxes
        unitsDrank.setText("");
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        selectedDate.setText(day + "-" + month + "-" + year);



        /*System.out.println(date);
        System.out.println(units);*/
    }

    /**
     * Creates a file called data.txt in /data/user/0/gauge.soberupp/
     * Writes an Alcohol object's date and units in a line with format "date, units" every line
     * To find file path, AddData.this.getFilesDir().getAbsolutePath();
     *
     * NOTE: Writes object data in bytes
     * @param a the Alcohol object holding date of consumption and units
     */
    private void writeFile(Alcohol a) {
        try {
            String filename = "data.txt";
            String string = a.getDate() + "," + a.getUnits() + "\n";
            FileOutputStream outputStream;

            // MODE_APPEND makes sure data.txt is not overwritten every time writeFile() is called
            // To overwrite, use MODE_PRIVATE
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        FileInputStream fis;
        try {
            fis = openFileInput("data.txt");
            StringBuilder fileContent = new StringBuilder("");

            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, n));
            }
            /*System.out.println("fileContent => " + fileContent.toString());*/
            if (!fileContent.toString().isEmpty()) {
                printData(fileContent.toString());
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses the data from readFile()'s fileContent.toString()
     * @param data the string containing the data
     */
    private void printData(String data) {
        // Splits the string into newlines "\\r?\\n" is used so it's compatible with UNIX and Windows
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

        readData.setText(dataToPrint);
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
