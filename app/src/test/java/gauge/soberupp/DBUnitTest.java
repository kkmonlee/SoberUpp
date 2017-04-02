package gauge.soberupp;
import android.content.Context;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DBUnitTest {
    @Mock
    Context mockContext;

    @Test
    public void readListFromSQL() {
        DBHandler db = new DBHandler(mockContext);

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

        assertThat(alcoholsToTest, is(actualAlcohols));
    }
}