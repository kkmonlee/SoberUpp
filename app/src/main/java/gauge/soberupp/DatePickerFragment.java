package gauge.soberupp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private String page;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        page = getArguments().getString("page");

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Calendar chosenDay = Calendar.getInstance();
        chosenDay.set(year, month, day, 0, 0, 0);
        Calendar currentDay = Calendar.getInstance();
        currentDay.set(currentDay.get(Calendar.YEAR), currentDay.get(Calendar.MONTH), currentDay.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        // Checks to see if the date is in the future and sets the text boxes accordingly
        if (page.equals("AddData")) {
            TextView tv1 = (TextView) getActivity().findViewById(R.id.setDate);
            if (currentDay.compareTo(chosenDay) >= 0) {
                tv1.setText(view.getDayOfMonth() + "-" + (view.getMonth() + 1) + "-" + view.getYear());
            } else {
                tv1.setText("Date in future");
            }
        } else if (page.equals("GraphFrom")) {
            TextView tv1 = (TextView) getActivity().findViewById(R.id.dateFromText);
            if (currentDay.compareTo(chosenDay) >= 0) {
                tv1.setText(view.getDayOfMonth() + "-" + (view.getMonth() + 1) + "-" + view.getYear());
            } else {
                tv1.setText("Date in future");
            }
        } else if (page.equals("GraphTo")) {
            TextView tv1 = (TextView) getActivity().findViewById(R.id.dateToText);
            if (currentDay.compareTo(chosenDay) >= 0) {
                tv1.setText(view.getDayOfMonth() + "-" + (view.getMonth() + 1) + "-" + view.getYear());
            } else {
                tv1.setText("Date in future");
            }
        }
    }
}
