package gauge.soberupp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddData extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener {

    private DBHandler db = new DBHandler(this);
    private ArrayList<Alcohol> alcohols = new ArrayList<>();
    // This id correlates to Alcohol.id
    private int id = 1;

    /**
     * Runs when the page is created
     *
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
        Button datePicker = (Button) findViewById(R.id.chooseDate);
        datePicker.setText(day + "-" + month + "-" + year);

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

        // Sets the spinner and selects the on selected item action
        final Spinner drinkType = (Spinner) findViewById(R.id.DrinkType);
        setDrinkSpinner(drinkType);
        drinkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setVolumeSpinner(drinkType.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Sets the spinner and selects the on selected item action
        final Spinner drinkVolume = (Spinner) findViewById(R.id.volume);
        drinkVolume.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setCustomVolume(drinkVolume.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
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
     * Sets the drink spinner to the array
     *
     * @param spin : the drink spinner
     */
    private void setDrinkSpinner(Spinner spin) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Drinks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
    }

    /**
     * Sets the volume spinner depending on what drink was selected
     *
     * @param drinkType : the drink type selected
     */
    private void setVolumeSpinner(String drinkType) {
        Spinner volume = (Spinner) findViewById(R.id.volume);
        EditText abv = (EditText) findViewById(R.id.ABVInput);
        ArrayAdapter<CharSequence> adapter;
        switch (drinkType) {
            case "Beer":
                abv.setText("4.5");
                adapter = ArrayAdapter.createFromResource(this, R.array.VolumeBeer, android.R.layout.simple_spinner_item);
                break;
            case "Cider":
                abv.setText("4.5");
                adapter = ArrayAdapter.createFromResource(this, R.array.VolumeCider, android.R.layout.simple_spinner_item);
                break;
            case "Wine":
                abv.setText("12");
                adapter = ArrayAdapter.createFromResource(this, R.array.VolumeWine, android.R.layout.simple_spinner_item);
                break;
            case "Spirits":
                abv.setText("37.5");
                adapter = ArrayAdapter.createFromResource(this, R.array.VolumeSpirits, android.R.layout.simple_spinner_item);
                break;
            case "Other":
                abv.setText("5");
                adapter = ArrayAdapter.createFromResource(this, R.array.VolumeOthers, android.R.layout.simple_spinner_item);
            default:
                abv.setText("4");
                adapter = ArrayAdapter.createFromResource(this, R.array.VolumeOthers, android.R.layout.simple_spinner_item);
                break;
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        volume.setAdapter(adapter);
    }

    /**
     * Shows the custom volume row if other is selected
     *
     * @param volumeSelected : the volume amount selected
     */
    private void setCustomVolume(String volumeSelected) {
        TableRow customVolume = (TableRow) findViewById(R.id.customMl);
        customVolume.setVisibility(View.INVISIBLE);
        if (volumeSelected.equals("Other")) {
            customVolume.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Adds the data to the database when the button is selected
     *
     * @param view : the view of the button
     */
    public void getData(View view) {

        // Check if no view has focus / Hide Keyboard:
        //http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }

        Button selectedDate = (Button) findViewById(R.id.chooseDate);
        String date = selectedDate.getText().toString();

        EditText abv = (EditText) findViewById(R.id.ABVInput);
        String abvOfDrink = abv.getText().toString();

        EditText quantity = (EditText) findViewById(R.id.numberDrunk);
        String quantityDrunk = quantity.getText().toString();

        Spinner drinkTypeSpinner = (Spinner) findViewById(R.id.DrinkType);
        String drinkType = drinkTypeSpinner.getSelectedItem().toString();

        Spinner drinkVolumeSpinner = (Spinner) findViewById(R.id.volume);
        String drinkVolume = drinkVolumeSpinner.getSelectedItem().toString();
        if (drinkVolume.equals("Other")) {
            EditText volumeEditText = (EditText) findViewById(R.id.customVolume);
            drinkVolume = volumeEditText.getText().toString() + "ml";
        }

        EditText comments = (EditText) findViewById(R.id.commentsInput);
        String commentsInput = comments.getText().toString();

        String[] volumeSplit = drinkVolume.split(" ");

        TextView message = (TextView) findViewById(R.id.message);

        //Error checking for the entry
        if (date.equals("Date in future") || date.equals("Choose Date")) {
            message.setText("Enter valid date");
        } else if (abvOfDrink.trim().isEmpty()) {
            message.setText("ABV of drink is empty");
        } else if (quantityDrunk.trim().isEmpty()) {
            message.setText("Enter how many drinks you have drunk");
        } else if (Double.parseDouble(abvOfDrink) >= 90.0) {
            message.setText("ABV too high");
        } else if (drinkVolume.isEmpty() || drinkVolume.equals("0ml")) {
            message.setText("Set Valid Volume");
        } else {
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
                case "Other":
                    alcoholType = AlcoholType.OTHER;
                    break;
            }
            assert alcoholType != null;

            Alcohol alcohol = new Alcohol(id, date, alcoholType,
                    Double.valueOf(volumeSplit[volumeSplit.length - 1].substring(0, volumeSplit[volumeSplit.length - 1].length() - 2)),
                    Double.valueOf(quantityDrunk), Double.valueOf(abvOfDrink), commentsInput);
            callPopup(alcohol);
            quantity.setText("");
            comments.setText("");
            message.setText("");
        }
    }

    /**
     * Sets the popup to confirm addition
     *
     * @param alcohol : the alcohol element to be added
     */
    private void callPopup(Alcohol alcohol) {
        final Alcohol alcoholToBeVerified = alcohol;
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

        // Sets the text to show whats being added
        TextView popUp = (TextView) popupView.findViewById(R.id.popupText);
        if (alcoholToBeVerified.getVolume() >= 30) {
            popUp.setText("Entry to be added - Nuber drunk seems to be high\n" + "Date: " + alcohol.getDate() +
                    ", Type: " + alcohol.getAlcoholType().getName() + ", Volume: " +
                    alcohol.getVolume() + ", Quantity: " + alcohol.getQuantity() +
                    "Units: " + alcohol.getUnits() + "\nComment: " + alcohol.getComment());
        } else {
            popUp.setText("Entry to be added\n" + "Date: " + alcohol.getDate() +
                    ", Type: " + alcohol.getAlcoholType().getName() + ", Volume: " +
                    alcohol.getVolume() + ", Quantity: " + alcohol.getQuantity() +
                    "Units: " + alcohol.getUnits() + "\nComment: " + alcohol.getComment());
        }

        // Decides what happens on the button clicks
        ((Button) popupView.findViewById(R.id.cancel))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        popupWindow.dismiss();
                        Snackbar.make(getCurrentFocus(), "Entry not added", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });
        ((Button) popupView.findViewById(R.id.confirm))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        popupWindow.dismiss();
                        db.addAlcohol(alcoholToBeVerified);
                        alcohols.add(alcoholToBeVerified);
                        id++;
                        Snackbar.make(getCurrentFocus(), "Entry added", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });
    }


    /**
     * This is executed when the "Read" button is pressed
     * <p>
     * Creates a FileInputStream and opens data.txt.
     * Since FileInputStream reads by byte, we create a byte[] buffer of size 1024
     * The content of the file is then appended into a new string
     * <p>
     * To access the file data, use fileContent.toString().
     *
     * @param view View object required for button
     */
    public void readFile(View view) {
        printData(db.getAllAlcohols());
    }

    /**
     * Parses the data from readFile()'s fileContent.toString()
     */
    private void printData(List<Alcohol> alcoholList) {
        // Check if no view has focus / Hide Keyboard:
        //http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }

        final TextView readData = (TextView) findViewById(R.id.printData);
        String log = "";
        for (Alcohol alcohol : alcoholList) {
            log += "Date: " + alcohol.getDate() +
                    ", Type: " + alcohol.getAlcoholType().getName() + ", Volume: " +
                    alcohol.getVolume() + ", Quantity: " + alcohol.getQuantity() +
                    "Units: " + alcohol.getUnits() + "\nComment: " + alcohol.getComment() +
                    "\n";
        }
        readData.setMovementMethod(new ScrollingMovementMethod());
        readData.setText(log);
    }

    /**
     * Shows the date picker when the button is pressed
     *
     * @param v : the view of the button
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle page = new Bundle();
        page.putString("page", "AddData");
        newFragment.setArguments(page);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}
