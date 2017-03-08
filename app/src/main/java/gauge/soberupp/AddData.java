package gauge.soberupp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AddData extends AppCompatActivity {
    DatePickerDialog datePickerDialog;
    private ArrayList<Alcohol> alcohols = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        Intent intent = getIntent();
    }

    public void getData(View view){
        final TextView selectedDate =  (TextView) findViewById(R.id.setDate);
        final String date = selectedDate.getText().toString();
        final EditText unitsDrank = (EditText) findViewById(R.id.unitsInput);
        final double units = Double.parseDouble(unitsDrank.getText().toString());

        // Clears the input data from the text boxes
        unitsDrank.setText("");
        selectedDate.setText("");

        Alcohol alcohol = new Alcohol(units, date);
        alcohols.add(alcohol);

        objectToString(alcohol);
        //Snackbar.make(view, Integer.toString(alcohols.size()), Snackbar.LENGTH_LONG).setAction("Action", null).show();


        /*System.out.println(date);
        System.out.println(units);*/
    }

    private void objectToString(Alcohol a) {
        try {
            String filename = "data.txt";
            String string = a.getDate() + "," + a.getUnits() + "\n";
            FileOutputStream outputStream;

            outputStream = openFileOutput(filename, Context.MODE_PRIVATE); // TODO: Make sure this can be read
            outputStream.write(string.getBytes());
            System.out.println("File path => " + AddData.this.getFilesDir().getAbsolutePath());
            outputStream.close();
            System.out.println("HELLO");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}
