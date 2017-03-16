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
    private static final String KEY_ID          = "id";
    private static final String KEY_DATE        = "date";
    private static final String KEY_TYPE        = "type";
    private static final String KEY_VOLUME      = "volume";
    private static final String KEY_QUANTITY    = "quantity";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Date is cursor.getString(1)
        // Units is cursor.getDouble(2);
        String CREATE_ALCOHOL_TABLE = "CREATE TABLE " + TABLE_ALCOHOLS + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " + KEY_DATE + " TEXT,"
                + KEY_TYPE + " TEXT, " + KEY_VOLUME + " REAL, "
                + KEY_QUANTITY + " REAL" + ")";
/*
        String sql = "CREATE TABLE alcohols (" +
                " id integer PRIMARY KEY," +
                " date text NOT NULL," +
                " type text NOT NULL," +
                " volume real," +
                " quantity real" +
                ")";
        db.execSQL(sql);*/
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

        values.put(KEY_DATE, alcohol.getDate());
        values.put(KEY_TYPE, alcohol.getAlcoholType().getName());
        values.put(KEY_VOLUME, alcohol.getVolume());
        values.put(KEY_QUANTITY, alcohol.getQuantity());

        // Insert row
        db.insert(TABLE_ALCOHOLS, null, values);
        db.close(); // Closing database connection
    }

    // Getting one Alcohol
    public Alcohol getAlcohol(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ALCOHOLS, new String[] {
                KEY_ID, KEY_DATE, KEY_TYPE, KEY_VOLUME, KEY_QUANTITY
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
        if (cursor.getString(2).equals("Beer")) {
            alcoholType = AlcoholType.BEER;
        } else if (cursor.getString(2).equals("Cider")) {
            alcoholType = AlcoholType.CIDER;
        } else if (cursor.getString(2).equals("Wine")) {
            alcoholType = AlcoholType.WINE;
        } else if (cursor.getString(2).equals("Spirits")) {
            alcoholType = AlcoholType.SPIRITS;
        }
        Alcohol alcohol = new Alcohol(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                alcoholType, cursor.getDouble(3), cursor.getDouble(4));
        cursor.close();
        return alcohol;
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
                AlcoholType alcoholType = null;
                if (cursor.getString(2).equals("Beer")) {
                    alcoholType = AlcoholType.BEER;
                } else if (cursor.getString(2).equals("Cider")) {
                    alcoholType = AlcoholType.CIDER;
                } else if (cursor.getString(2).equals("Wine")) {
                    alcoholType = AlcoholType.WINE;
                } else if (cursor.getString(2).equals("Spirits")) {
                    alcoholType = AlcoholType.SPIRITS;
                }
                Alcohol alcohol = new Alcohol();

                alcohol.setId(Integer.parseInt(cursor.getString(0)));
                alcohol.setDate(cursor.getString(1));
                alcohol.setAlcoholType(alcoholType);
                alcohol.setVolume(cursor.getDouble(3));
                alcohol.setQuantity(cursor.getDouble(4));
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

        values.put(KEY_DATE, alcohol.getDate());
        values.put(KEY_TYPE, alcohol.getAlcoholType().getName());
        values.put(KEY_VOLUME, alcohol.getVolume());
        values.put(KEY_QUANTITY, alcohol.getQuantity());

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
