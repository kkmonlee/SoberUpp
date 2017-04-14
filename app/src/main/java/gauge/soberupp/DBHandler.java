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

    // Database name
    public static final String DATABASE_NAME = "AlcoholInfo";
    // Database version
    private static final int DATABASE_VERSION = 1;
    // Table names
    private static final String TABLE_ALCOHOLS = "alcohols";
    private static final String TABLE_GOALS = "goals";
    // Alcohols table columns names
    private static final String KEY_GOAL = "goal";
    private static final String KEY_ID = "id";
    private static final String KEY_DAY = "day";
    private static final String KEY_MONTH = "month";
    private static final String KEY_YEAR = "year";
    private static final String KEY_TYPE = "type";
    private static final String KEY_VOLUME = "volume";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_ABV = "abv";
    private static final String KEY_COMMENT = "comment";

    /**
     * Constructor needed to initialise the database
     *
     * @param context normally should be used as <code>DBHandler db = new DBHandler(this)</code>
     *                in another class
     */
    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Implements method in SQLiteOpenHelper, see there for details
     *
     * @param db SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Date is cursor.getString(1)
        // Units is cursor.getDouble(2);
        String CREATE_ALCOHOL_TABLE = "CREATE TABLE " + TABLE_ALCOHOLS + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " + KEY_DAY + " INTEGER, " +
                KEY_MONTH + " INTEGER, " + KEY_YEAR + " INTEGER, " +
                KEY_TYPE + " TEXT, " + KEY_VOLUME + " REAL, " +
                KEY_QUANTITY + " REAL, " + KEY_ABV + " REAL, " +
                KEY_COMMENT + " TEXT" + ")";
        // Creates the Goals table
        String CREATE_GOAL_TABLE = "CREATE TABLE " + TABLE_GOALS + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " + KEY_GOAL + " INTEGER, " +
                KEY_DAY + " INTEGER, " + KEY_MONTH + " INTEGER, " + KEY_YEAR +
                " INTEGER" + ")";

        db.execSQL(CREATE_ALCOHOL_TABLE);
        db.execSQL(CREATE_GOAL_TABLE);
    }

    /**
     * Implements method in SQLiteOpenHelper, see there for details
     * Removes table definition from TABLE_ALCOHOLS if it already exists, then invokes onCreate()
     * to create the table again.
     *
     * @param db         SQLiteDatabase
     * @param oldVersion int
     * @param newVersion int
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALCOHOLS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
        // Create table again
        onCreate(db);
    }

    /**
     * Adds a new goal to the goals column
     *
     * @param goal
     * @param date
     */
    public void addGoal(int goal, String date) {
        String[] split = date.split("-");


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_GOAL, goal);
        values.put(KEY_DAY, split[0]);
        values.put(KEY_MONTH, split[1]);
        values.put(KEY_YEAR, split[2]);

        db.insert(TABLE_GOALS, null, values);
        db.close();
    }

    /**
     * Adds fields of an Alcohol object into the table
     *
     * @param alcohol the Alcohol object whose fields are to be added
     */
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
        values.put(KEY_COMMENT, alcohol.getComment());

        // Insert row
        db.insert(TABLE_ALCOHOLS, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Gets the units per week
     *
     * @return int, amount of units per week
     */
    public int getGoal(int i) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_GOALS, new String[]{KEY_ID, KEY_GOAL}, KEY_ID + "=?",
                new String[]{String.valueOf(i)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        assert cursor != null;
        int goal = cursor.getInt(1);
        cursor.close();

        return goal;
    }

    /**
     * Get fields of an Alcohol object by ID
     *
     * @param id int, is the PRIMARY INTEGER KEY in SQL
     * @return creates an Alcohol object from the retrieved fields and returns it
     */
    public Alcohol getAlcohol(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ALCOHOLS, new String[]{
                        KEY_ID, KEY_DAY, KEY_MONTH, KEY_YEAR, KEY_TYPE, KEY_VOLUME, KEY_QUANTITY, KEY_ABV, KEY_COMMENT
                        // 0         1       2           3         4         5           6           7           8
                }, KEY_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        assert cursor != null;
        AlcoholType alcoholType = getCorrectType(cursor);

        String date = cursor.getInt(1) + "-" + cursor.getInt(2) + "-" + cursor.getInt(3);

        Alcohol alcohol = new Alcohol(Integer.parseInt(cursor.getString(0)), date, alcoholType, cursor.getDouble(5), cursor.getDouble(6), cursor.getDouble(7), cursor.getString(8));
        cursor.close();
        return alcohol;
    }

    public List<Integer> getAllGoals() {
        List<Integer> goalList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_GOALS + " ORDER BY " + KEY_DAY +
                "," + KEY_MONTH + "," + KEY_YEAR + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                goalList.add(cursor.getInt(1));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return goalList;
    }

    /**
     * Sorts all Alcohol object fields from the table by date and creates an object with those
     * fields.
     *
     * @return List<Alcohol>, an ArrayList of Alcohol objects
     */
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
                AlcoholType alcoholType = getCorrectType(cursor);

                Alcohol alcohol = new Alcohol();

                String date = cursor.getInt(1) + "-" + cursor.getInt(2) + "-" + cursor.getInt(3);

                alcohol.setId(Integer.parseInt(cursor.getString(0)));
                alcohol.setDate(date);
                alcohol.setAlcoholType(alcoholType);
                alcohol.setVolume(cursor.getDouble(5));
                alcohol.setQuantity(cursor.getDouble(6));
                alcohol.setAbv(cursor.getDouble(7));
                alcohol.setComment(cursor.getString(8));
                alcohol.calculateUnits();

                // Adding Alcohol to list
                alcoholList.add(alcohol);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return Alcohol list
        return alcoholList;
    }

    /**
     * Gets the total number of Alcohol object entries in the table
     *
     * @return int, number of Alcohol objects
     */
    public int getAlcoholCount() {
        String countQuery = "SELECT * FROM " + TABLE_ALCOHOLS;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

    /**
     * Updates the goal value to another value
     *
     * @param newGoal int, new goal value
     * @return int, 1 if successful, 0 otherwise
     */
    public int updateGoal(int i, int newGoal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_GOAL, newGoal);

        return db.update(TABLE_GOALS, values, KEY_ID + " = ?", new String[]{String.valueOf(i)});
    }

    /**
     * Changes the fields of the Alcohol object.
     * Alcohol object is found in the table by getId()
     *
     * @param alcohol the Alcohol object whose fields are to be updated in the table
     * @return int, 1 if successful, 0 otherwise
     */
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
        values.put(KEY_COMMENT, alcohol.getComment());

        // Updating row
        return db.update(TABLE_ALCOHOLS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(alcohol.getId())});
    }

    public void deleteGoal(int i) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALCOHOLS, KEY_ID + " = ?",
                new String[]{String.valueOf(i)});
        db.close();
    }

    /**
     * Deletes an Alcohol object from the table
     * Finds the Alcohol object by getId()
     *
     * @param alcohol the Alcohol object whose fields are to be deleted from the table
     */
    public void deleteAlcohol(Alcohol alcohol) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALCOHOLS, KEY_ID + " = ?",
                new String[]{String.valueOf(alcohol.getId())});
        db.close();
    }

    /**
     * Clears everything from TABLE_ALCOHOLS
     */
    public void deleteAll() {
        String deleteQuery = "DELETE FROM " + TABLE_ALCOHOLS;
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(deleteQuery);
        db.execSQL("VACUUM");
        db.close();
    }

    /**
     * Gets the correct AlcoholType enumeration from the Cursor
     * At the moment, AlcoholType is stored in the 5th column, therefore getString(4)
     *
     * @param cursor SQLiteDatabase Cursor
     * @return AlcoholType enumeration, the correct type
     */
    private AlcoholType getCorrectType(Cursor cursor) {
        AlcoholType alcoholType = null;
        if (cursor.getString(4).equals("Beer")) {
            alcoholType = AlcoholType.BEER;
        } else if (cursor.getString(4).equals("Cider")) {
            alcoholType = AlcoholType.CIDER;
        } else if (cursor.getString(4).equals("Wine")) {
            alcoholType = AlcoholType.WINE;
        } else if (cursor.getString(4).equals("Spirits")) {
            alcoholType = AlcoholType.SPIRITS;
        } else if (cursor.getString(4).equals("Other")) {
            alcoholType = AlcoholType.OTHER;
        }

        return alcoholType;
    }
}
