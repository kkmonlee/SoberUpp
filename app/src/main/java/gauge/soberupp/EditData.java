package gauge.soberupp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static gauge.soberupp.R.array.VolumeBeerCider;

public class EditData extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Alcohol> alcohols;
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);
        setTitle("Edit Data");
        // Gets the list of alcohol objects from the DB
        db = new DBHandler(this);
        alcohols = db.getAllAlcohols();
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

        //Set the drink spinner to have an onclick
        final Spinner drinkType = (Spinner) findViewById(R.id.drinkSpinner);
        // Sets the dropdown list for the drink spinner
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

        //Set the entry selection spinner to the right record
        final Spinner entrySelect = (Spinner) findViewById(R.id.entrySelect);
        // Sets the dropdown list for the entry spinner
        setEntrySpinner(entrySelect);
        // Adds an onclick listener
        entrySelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setData(entrySelect.getSelectedItemId());
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
     * Sets up the menu
     *
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
     *
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
     * Sets the entry spinner to the records of the DB
     *
     * @param spin
     */
    private void setEntrySpinner(Spinner spin) {
        List<String> entries = new ArrayList<String>();
        // Iterates through the alcohol objects and adds it to the dropdown list
        for (Alcohol alcohol : alcohols) {
            entries.add(alcohol.getDate() + " - " + alcohol.getQuantity() + "x " + alcohol.getVolume()
                    + "ml of " + alcohol.getAbv() + "%" + alcohol.getAlcoholType().getName());
        }

        // Creating adapter for spinner and adds list to the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, entries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
    }

    /**
     * When an entry is selected, it prepopulates the fields to the datas records
     *
     * @param entrySelected : the id of the alcohol object of the entry selected
     */
    private void setData(long entrySelected) {
        Alcohol alcoholSelected = alcohols.get((int) entrySelected);
        //Gets the elements needed to be set
        TextView dateSelected = (TextView) findViewById(R.id.dateSelected);
        Spinner drink = (Spinner) findViewById(R.id.drinkSpinner);
        Spinner volume = (Spinner) findViewById(R.id.sizeSpinner);
        EditText abv = (EditText) findViewById(R.id.ABVText);
        EditText numberDrunk = (EditText) findViewById(R.id.numberDrunkText);
        EditText comments = (EditText) findViewById(R.id.CommentsText);

        // Sets all the fields to the appropiate data
        dateSelected.setText(alcoholSelected.getDate());
        drink.setSelection(((ArrayAdapter) drink.getAdapter()).getPosition(alcoholSelected.getAlcoholType().getName()));
        setVolumeSpinner(alcoholSelected.getAlcoholType().getName());
        abv.setText(String.valueOf(alcoholSelected.getAbv()));
        numberDrunk.setText(String.valueOf(alcoholSelected.getQuantity()));
        comments.setText(alcoholSelected.getComment());
    }

    /**
     * Sets the drink spinner to be the list of alcohols
     *
     * @param spin : the spinner needed
     */
    private void setDrinkSpinner(Spinner spin) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Drinks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
    }

    /**
     * Sets the volume spinner to be the appropiate list
     *
     * @param drinkType : the drink type selected
     */
    private void setVolumeSpinner(String drinkType) {
        Spinner volume = (Spinner) findViewById(R.id.sizeSpinner);
        EditText abv = (EditText) findViewById(R.id.ABVText);
        ArrayAdapter<CharSequence> adapter;
        // Selects the list depending on the drink type
        switch (drinkType) {
            case "Beer":
            case "Cider":
                abv.setText("4.5");
                adapter = ArrayAdapter.createFromResource(this, VolumeBeerCider, android.R.layout.simple_spinner_item);
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

        //Sets the volume to be the selected volume of the data record selected
        String[] volumes;
        Spinner dataEntry = (Spinner) findViewById(R.id.entrySelect);
        Alcohol alcoholSelected = alcohols.get((int) dataEntry.getSelectedItemId());
        if (alcoholSelected.getAlcoholType().getName().equals(drinkType)) {
            switch (alcoholSelected.getAlcoholType().getName()) {
                case "Beer":
                case "Cider":
                    volumes = getResources().getStringArray(R.array.VolumeBeerCider);
                    break;
                case "Wine":
                    volumes = getResources().getStringArray(R.array.VolumeWine);
                    break;
                case "Spirits":
                    volumes = getResources().getStringArray(R.array.VolumeSpirits);
                    break;
                case "Other":
                    volumes = getResources().getStringArray(R.array.VolumeOthers);
                default:
                    volumes = getResources().getStringArray(R.array.VolumeOthers);
                    break;
            }

            // Sets the volume spinner to the correct record
            String volumeSelected = null;
            for (String s : volumes) {
                String[] volumeSplit = s.split(" ");
                if (Double.valueOf(volumeSplit[volumeSplit.length - 1].substring(0, volumeSplit[1].length() - 2)) == alcoholSelected.getVolume()) {
                    volumeSelected = s;
                }
            }
            volume.setSelection(((ArrayAdapter) volume.getAdapter()).getPosition(volumeSelected));
        }
    }

    /**
     * Updates the record of the alcohol when the button is pressed
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

        // Gets the objects of the entry items
        TextView dateSelected = (TextView) findViewById(R.id.dateSelected);
        Spinner drink = (Spinner) findViewById(R.id.drinkSpinner);
        Spinner volume = (Spinner) findViewById(R.id.sizeSpinner);
        EditText abv = (EditText) findViewById(R.id.ABVText);
        EditText numberDrunk = (EditText) findViewById(R.id.numberDrunkText);
        EditText comments = (EditText) findViewById(R.id.CommentsText);

        // The updated data of the alcohol object
        String date = dateSelected.getText().toString();
        String abvOfDrink = abv.getText().toString();
        String quantityDrunk = numberDrunk.getText().toString();
        String drinkType = drink.getSelectedItem().toString();
        String drinkVolume = volume.getSelectedItem().toString();
        String commentsInput = comments.getText().toString();

        String[] volumeSplit = drinkVolume.split(" ");

        TextView message = (TextView) findViewById(R.id.errorMessage);

        //Error checking for the entry
        if (abvOfDrink.trim().isEmpty()) {
            message.setText("ABV of drink is empty");
        } else if (quantityDrunk.trim().isEmpty()) {
            message.setText("Enter how many drinks you have drunk");
        } else if (Double.parseDouble(abvOfDrink) >= 90.0) {
            message.setText("ABV too high");
        } else if (Double.parseDouble(quantityDrunk) >= 30.0) {
            message.setText("You have drunk too many");
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

            Spinner entrySelect = (Spinner) findViewById(R.id.entrySelect);
            Alcohol alcoholSelected = alcohols.get((int) entrySelect.getSelectedItemId());

            // Creates the alcohol object with the data provided
            Alcohol alcohol = new Alcohol(alcoholSelected.getId(), date, alcoholType,
                    Double.valueOf(volumeSplit[volumeSplit.length - 1].substring(0, volumeSplit[1].length() - 2)),
                    Double.valueOf(quantityDrunk), Double.valueOf(abvOfDrink), commentsInput);

            // Updates the old alcohol entry with the new one
            db.updateAlcohol(alcohol);
            Snackbar.make(view, "Alcohol Entry has been updated", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            numberDrunk.setText("");
            comments.setText("");
            alcohols = db.getAllAlcohols();
            setEntrySpinner(entrySelect);

        }
    }

    public void removeEntry(View view) {
        //Gets the id of the alcohol entry to be removed
        Spinner entrySelect = (Spinner) findViewById(R.id.entrySelect);
        Alcohol alcoholSelected = alcohols.get((int) entrySelect.getSelectedItemId());
        // Deletes it from the db
        db.deleteAlcohol(alcoholSelected);
        Snackbar.make(view, "Alcohol Entry has been deleted", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        alcohols = db.getAllAlcohols();
        setEntrySpinner(entrySelect);
    }


}


