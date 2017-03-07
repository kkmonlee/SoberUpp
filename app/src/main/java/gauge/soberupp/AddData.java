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
        Button submit = (Button) findViewById(R.id.submit);
        final EditText selectedDate =  (EditText) findViewById(R.id.setDate);
        final String date = selectedDate.getText().toString();
        final EditText unitsDrank = (EditText) findViewById(R.id.unitsInput);
        final double units = Integer.parseInt(unitsDrank.getText().toString());

        // doesn't work but you get the idea
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alcohol alcohol = new Alcohol(units, date);
                alcohols.add(alcohol);
                Snackbar snackbar = Snackbar.make(v, alcohol.getDate(), 9000);
                snackbar.show();
            }
        });

        /*System.out.println(date);
        System.out.println(units);*/
    }
}
