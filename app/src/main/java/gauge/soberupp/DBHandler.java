package gauge.soberupp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aa on 15/03/17.
 */

public class DBHandler extends SQLiteOpenHelper {

    // Database version
    private static final int DATABASE_VERSION = 1;
    // Database name
    private static final String DATABASE_NAME   = "AlcoholInfo";
    // Alcohols table name
    private static final String TABLE_ALCOHOLS  = "alcohols";
    // Alcohols table columns names
    private static final String KEY_ID          = "id";
    private static final String KEY_DAY         = "day";
    private static final String KEY_MONTH       = "month";
    private static final String KEY_YEAR        = "year";
    private static final String KEY_TYPE        = "type";
    private static final String KEY_VOLUME      = "volume";
    private static final String KEY_QUANTITY    = "quantity";
    private static final String KEY_ABV         = "abv";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Date is cursor.getString(1)
        // Units is cursor.getDouble(2);
        String CREATE_ALCOHOL_TABLE = "CREATE TABLE " + TABLE_ALCOHOLS + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " + KEY_DAY + " INTEGER," +
                KEY_MONTH + " INTEGER," + KEY_YEAR + " INTEGER," +
                KEY_TYPE + " TEXT, " + KEY_VOLUME + " REAL, " +
                KEY_QUANTITY + " REAL," + KEY_ABV + " REAL" + ")";

        db.execSQL(CREATE_ALCOHOL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALCOHOLS);
        // Create table again
        onCreate(db);
    }

    // Inserting a new Alcohol in SQLite Database
    public void addAlcohol(Alcohol alcohol) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DAY, Integer.parseInt(alcohol.getDD()));
        values.put(KEY_MONTH, Integer.parseInt(alcohol.getMM()));
        values.put(KEY_YEAR, Integer.parseInt(alcohol.getYYYY()));
        values.put(KEY_TYPE, alcohol.getAlcoholType().getName());
        values.put(KEY_VOLUME, alcohol.getVolume());
        values.put(KEY_QUANTITY, alcohol.getQuantity());
        values.put(KEY_ABV, alcohol.getAbv());

        // Insert row
        db.insert(TABLE_ALCOHOLS, null, values);
        db.close(); // Closing database connection
    }

    // Getting one Alcohol
    public Alcohol getAlcohol(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ALCOHOLS, new String[] {
                KEY_ID, KEY_DAY, KEY_MONTH, KEY_YEAR, KEY_TYPE, KEY_VOLUME, KEY_QUANTITY, KEY_ABV
               // 0         1       2           3         4         5           6           7
        }, KEY_ID + "=?",
                new String[] {
                        String.valueOf(id)
                }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        assert cursor != null;

        /*return new Alcohol(Integer.parseInt(cursor.getString(0)),
                cursor.getDouble(2), cursor.getString(1));
        */
        AlcoholType alcoholType = null;
        if (cursor.getString(4).equals("Beer")) {
            alcoholType = AlcoholType.BEER;
        } else if (cursor.getString(4).equals("Cider")) {
            alcoholType = AlcoholType.CIDER;
        } else if (cursor.getString(4).equals("Wine")) {
            alcoholType = AlcoholType.WINE;
        } else if (cursor.getString(4).equals("Spirits")) {
            alcoholType = AlcoholType.SPIRITS;
        }

        String date = cursor.getInt(1) + "-" + cursor.getInt(2) + "-" + cursor.getInt(3);

        Alcohol alcohol = new Alcohol(Integer.parseInt(cursor.getString(0)), date, alcoholType, cursor.getDouble(5), cursor.getDouble(6), cursor.getDouble(7));
        cursor.close();
        return alcohol;
    }

    // Getting all Alcohols
    public List<Alcohol> getAllAlcohols() {
        List<Alcohol> alcoholList = new ArrayList<>();
        // Select all query
        String selectQuery = "SELECT * FROM " + TABLE_ALCOHOLS + " ORDER BY " + KEY_DAY +
                "," + KEY_MONTH + "," + KEY_YEAR + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AlcoholType alcoholType = null;
                if (cursor.getString(4).equals("Beer")) {
                    alcoholType = AlcoholType.BEER;
                } else if (cursor.getString(4).equals("Cider")) {
                    alcoholType = AlcoholType.CIDER;
                } else if (cursor.getString(4).equals("Wine")) {
                    alcoholType = AlcoholType.WINE;
                } else if (cursor.getString(4).equals("Spirits")) {
                    alcoholType = AlcoholType.SPIRITS;
                }
                Alcohol alcohol = new Alcohol();

                String date = cursor.getInt(1) + "-" + cursor.getInt(2) + "-" + cursor.getInt(3);

                alcohol.setId(Integer.parseInt(cursor.getString(0)));
                alcohol.setDate(date);
                alcohol.setAlcoholType(alcoholType);
                alcohol.setVolume(cursor.getDouble(5));
                alcohol.setQuantity(cursor.getDouble(6));
                alcohol.setAbv(cursor.getDouble(7));
                alcohol.calculateUnits();

                // Adding Alcohol to list
                alcoholList.add(alcohol);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return Alcohol list
        return alcoholList;
    }

    // Get total number of Alcohols in database
    public int getAlcoholCount() {
        String countQuery = "SELECT * FROM " + TABLE_ALCOHOLS;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

    // To update an Alcohol record
    public int updateAlcohol(Alcohol alcohol) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DAY, Integer.parseInt(alcohol.getDD()));
        values.put(KEY_MONTH, Integer.parseInt(alcohol.getMM()));
        values.put(KEY_YEAR, Integer.parseInt(alcohol.getYYYY()));
        values.put(KEY_TYPE, alcohol.getAlcoholType().getName());
        values.put(KEY_VOLUME, alcohol.getVolume());
        values.put(KEY_QUANTITY, alcohol.getQuantity());
        values.put(KEY_ABV, alcohol.getAbv());

        // Updating row
        return db.update(TABLE_ALCOHOLS, values, KEY_ID + " = ?",
                new String[] {String.valueOf(alcohol.getId())});
    }

    // Delete an Alcohol record
    public void deleteAlcohol(Alcohol alcohol) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALCOHOLS, KEY_ID + " = ?",
                new String[] {String.valueOf(alcohol.getId())});
        db.close();
    }

    public void deleteAll() {
        String deleteQuery = "DELETE FROM " + TABLE_ALCOHOLS;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.execSQL("VACUUM");
        db.close();
    }

}
