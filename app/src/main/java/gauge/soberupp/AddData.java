package gauge.soberupp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        final EditText selectedDate =  (EditText) findViewById(R.id.setDate);
        final String date = selectedDate.getText().toString();
        final EditText unitsDrank = (EditText) findViewById(R.id.unitsInput);
        final double units = Double.parseDouble(unitsDrank.getText().toString());

        // Clears the input data from the text boxes
        unitsDrank.setText("");
        selectedDate.setText("");

        Alcohol alcohol = new Alcohol(units, date);
        alcohols.add(alcohol);
        Snackbar.make(view, "Alcohol", Snackbar.LENGTH_LONG).setAction("Action", null).show();


        /*System.out.println(date);
        System.out.println(units);*/
    }
}
