package gauge.soberupp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Calendar currentDay = Calendar.getInstance();
        currentDay.set(currentDay.get(Calendar.YEAR), currentDay.get(Calendar.MONTH), currentDay.get(Calendar.DAY_OF_MONTH), 0,0,0);

        Calendar chosenDay = Calendar.getInstance();
        chosenDay.set(year, month, day, 0,0,0);

        if(currentDay.compareTo(chosenDay) >= 0){
            TextView tv1= (TextView) getActivity().findViewById(R.id.setDate);
            tv1.setText(view.getDayOfMonth() + "-" + (view.getMonth()+1) + "-" + view.getYear());
        } else {
            TextView tv1= (TextView) getActivity().findViewById(R.id.setDate);
            tv1.setText("Date in future");
        }
    }
}
