package gauge.soberupp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DBInstrumentedTest {
    private Context appContext = null;

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("gauge.soberupp", appContext.getPackageName());
    }

    @Test
    public void readListFromSQL() {
        DBHandler db = new DBHandler(appContext);

        Alcohol al1 = new Alcohol(1, "02-04-2017", AlcoholType.BEER, 500, 2, 5.3, "");
        Alcohol al2 = new Alcohol(2, "01-04-2017", AlcoholType.SPIRITS, 25, 3, 60, "Absolute Vodka");
        Alcohol al3 = new Alcohol(3, "29-03-2017", AlcoholType.WINE, 500, 1, 20, "");

        db.addAlcohol(al1);
        db.addAlcohol(al2);
        db.addAlcohol(al3);

        List<Alcohol> alcoholsToTest = db.getAllAlcohols();

        List<Alcohol> actualAlcohols = new ArrayList<>();
        actualAlcohols.add(al1);
        actualAlcohols.add(al2);
        actualAlcohols.add(al3);

        assertEquals(alcoholsToTest, actualAlcohols);
    }
}
