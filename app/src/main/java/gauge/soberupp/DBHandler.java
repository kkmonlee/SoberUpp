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
    private static final String DATABASE_NAME = "AlcoholInfo";
    // Alcohols table name
    private static final String TABLE_ALCOHOLS = "alcohols";
    // Alcohols table columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_UNITS = "units";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Date is cursor.getString(1)
        // Units is cursor.getDouble(2);
        String CREATE_ALCOHOL_TABLE = "CREATE TABLE " + TABLE_ALCOHOLS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT,"
                + KEY_UNITS + " REAL" + ")";
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

        values.put(KEY_DATE, alcohol.getDate()); // Alcohol date
        values.put(KEY_UNITS, alcohol.getUnits()); // Alcohol units

        // Insert row
        db.insert(TABLE_ALCOHOLS, null, values);
        db.close(); // Closing database connection
    }

    // Getting one Alcohol
    public Alcohol getAlcohol(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ALCOHOLS, new String[] {
                KEY_ID, KEY_DATE, KEY_UNITS
        }, KEY_ID + "=?",
                new String[] {
                        String.valueOf(id)
                }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        assert cursor != null;

        return new Alcohol(Integer.parseInt(cursor.getString(0)),
                cursor.getDouble(2), cursor.getString(1));
    }

    // Getting all Alcohols
    public List<Alcohol> getAllAlcohols() {
        List<Alcohol> alcoholList = new ArrayList<>();
        // Select all query
        String selectQuery = "SELECT * FROM " + TABLE_ALCOHOLS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Alcohol alcohol = new Alcohol();
                alcohol.setId(Integer.parseInt(cursor.getString(0)));
                alcohol.setDate(cursor.getString(1));
                alcohol.setUnits(cursor.getDouble(2));
                // Adding Alcohol to list
                alcoholList.add(alcohol);
            } while (cursor.moveToNext());
        }
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

        values.put(KEY_DATE, alcohol.getDate());
        values.put(KEY_UNITS, alcohol.getUnits());

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

}
