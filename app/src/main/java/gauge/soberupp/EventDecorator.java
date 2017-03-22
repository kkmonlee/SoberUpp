package gauge.soberupp;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {

    private final HashSet<CalendarDay> dates;
    private Drawable highlightDrawable;

    public EventDecorator(int color, Collection<CalendarDay> dates) {
        this.dates = new HashSet<>(dates);
        highlightDrawable = new ColorDrawable(color);
    }

    /**
     * Checks if the day should be decorated
     *
     * @param day : day to be tested
     * @return : if the day is included in the set
     */
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    /**
     * Colors the background of the sober diary page
     *
     * @param view : view of the DayViewFacade
     */
    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(highlightDrawable);
    }
}