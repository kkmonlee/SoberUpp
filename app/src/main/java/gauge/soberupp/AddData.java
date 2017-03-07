package gauge.soberupp;

import android.app.DatePickerDialog;
import android.content.Intent;
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
        String date = selectedDate.getText().toString();
        final EditText unitsDrank = (EditText) findViewById(R.id.unitsInput);
        double Units = Integer.parseInt(unitsDrank.getText().toString());

        Alcohol alcohol = new Alcohol(Units, date);
        alcohols.add(alcohol);

        /*System.out.println(date);
        System.out.println(Units);*/
    }
}
