package gauge.soberupp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AddData extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        Intent intent = getIntent();
        setTitle("Input Data");

        // Sets the text box to todays date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        TextView tv1= (TextView) findViewById(R.id.setDate);
        tv1.setText(day + "-" + month + "-" + year);

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
    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return super.onNavigationItemSelected(item);
    }

    private ArrayList<Alcohol> alcohols = new ArrayList<>();

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
            if(unitsDrankInput <= 100) {
                Alcohol alcohol = new Alcohol(unitsDrankInput, date);
                alcohols.add(alcohol);
                objectToString(alcohol);
                Snackbar.make(view, Integer.toString(alcohols.size()), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {
                Snackbar.make(view, "Too many units inputted", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else {
            Snackbar.make(view, "Select a date", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        // Clears the input data from the text boxes
        unitsDrank.setText("");
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        selectedDate.setText(day + "-" + month + "-" + year);



        /*System.out.println(date);
        System.out.println(units);*/
    }

    private void objectToString(Alcohol a) {
        try {
            String filename = "data.txt";
            String string = a.getDate() + "," + a.getUnits() + "\n";
            FileOutputStream outputStream;

            outputStream = openFileOutput(filename, Context.MODE_APPEND); // TODO: Make sure this can be read
            outputStream.write(string.getBytes());
            System.out.println("File path => " + AddData.this.getFilesDir().getAbsolutePath());
            outputStream.close();
            System.out.println("HELLO");
            readFile();
            System.out.println("dataList =>");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile() {
        FileInputStream fis;
        try {
            fis = openFileInput("data.txt");
            StringBuilder fileContent = new StringBuilder("");

            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, n));
            }
            System.out.println("fileContent => " + fileContent.toString());
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void printData(ArrayList<Alcohol> alcohols) {
        double totalUnits = 0;
        int numDays = alcohols.size();

        for (Alcohol a : alcohols) {
            totalUnits += a.getUnits();
        }

        if (numDays == 1)
            System.out.println("You have drank " + totalUnits + " units over the span of one day.");
        else
            System.out.println("You have drank " + totalUnits + " units over the span of " + numDays + " days.");

        // Print it to the user
        for (Alcohol a : alcohols) {
            // Click here to view details...
            System.out.println("You've had " + a.getUnits() + " units on the " + a.getDD() + " of " + a.getMM() + ", " + a.getYYYY());
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}
