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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class AddData extends AppCompatActivity {
    DatePickerDialog datePickerDialog;
    private ArrayList<Alcohol> alcohols = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        Intent intent = getIntent();

        // Sets the text box to todays date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        TextView tv1= (TextView) findViewById(R.id.setDate);
        tv1.setText(day + "-" + month + "-" + year);

    }

    public void getData(View view){
        final TextView selectedDate =  (TextView) findViewById(R.id.setDate);
        final String date = selectedDate.getText().toString();
        final EditText unitsDrank = (EditText) findViewById(R.id.unitsInput);
        final double units = Double.parseDouble(unitsDrank.getText().toString());

        // Clears the input data from the text boxes
        unitsDrank.setText("");
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        selectedDate.setText(day + "-" + month + "-" + year);

        Alcohol alcohol = new Alcohol(units, date);
        alcohols.add(alcohol);

        objectToString(alcohol);
        Snackbar.make(view, Integer.toString(alcohols.size()), Snackbar.LENGTH_LONG).setAction("Action", null).show();


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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile(){
        FileInputStream fis;
        try {
            fis = openFileInput("data.txt");
            StringBuffer fileContent = new StringBuffer("");

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

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}
