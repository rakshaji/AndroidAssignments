package com.raksha.assignment.assignment3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Raksha on 3/13/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.toString();
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.raksha.assignment.assignment3/databases/";
    private static String DB_NAME = "assignment3.sqlite";
    private final Context context;
    private SQLiteDatabase sqLiteDatabase;
    private static DatabaseHelper mInstance;

    public static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;

        try {
            createDataBase();
        } catch (IOException ioe) {
            Log.e(TAG, "Unable to create database", ioe);
        }

        try {
            openDataBase();
        } catch (SQLException sqle) {
            Log.d(TAG, "Problem in opening database", sqle);
        }
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {
            //By calling this method an empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, "Error copying database", e);
            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.e(TAG, "Database does't exist yet.", e);
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring bytestream.
     */
    private void copyDataBase() throws IOException {
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        //Open the database
        String fullDatabasePath = DB_PATH + DB_NAME;
        sqLiteDatabase = SQLiteDatabase.openDatabase(fullDatabasePath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (sqLiteDatabase != null)
            sqLiteDatabase.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertInstructorInfo(InstructorInfo instructorInfo) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FirstName", instructorInfo.getFirstName());
        values.put("LastName", instructorInfo.getLastName());
        values.put("InstructorId", instructorInfo.getInstructorId());
        database.insert("InstructorInfo", null, values);
    }

    public void insertInstructorInfoIfNotExist(InstructorInfo instructorInfo) {
        if(isInstructorExist(String.valueOf(instructorInfo.getInstructorId()))){
            return;
        }

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FirstName", instructorInfo.getFirstName());
        values.put("LastName", instructorInfo.getLastName());
        values.put("InstructorId", instructorInfo.getInstructorId());
        database.insert("InstructorInfo", null, values);
    }

    public void insertUserReviews(UserReviews ratingAndReviews) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Comment", ratingAndReviews.getComment());
        values.put("Date", ratingAndReviews.getDate());
        values.put("InstructorId", ratingAndReviews.getInstructorId());
        database.insert("UserReviews", null, values);
    }

    public int updateInstructorInfo(InstructorInfo instructorInfo) {
        int rows = 0;
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("FirstName", instructorInfo.getFirstName());
            values.put("LastName", instructorInfo.getLastName());
            values.put("Office", instructorInfo.getOffice());
            values.put("Email", instructorInfo.getEmail());
            values.put("Phone", instructorInfo.getPhone());
            values.put("AverageRating", instructorInfo.getAverageRating());
            values.put("TotalReviews", instructorInfo.getTotalReviews());
            rows = database.update("InstructorInfo", values, "InstructorId = ?",
                    new String[]{String.valueOf(instructorInfo.getInstructorId())});
        } catch (SQLiteConstraintException e){
            Log.e(TAG, "instructorInfo : "+instructorInfo.toString(), e);
        }
        return rows;
    }

    public void deleteInstructorInfo(String id) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM InstructorInfo WHERE InstructorId = " + id ;
        database.execSQL(deleteQuery);
    }

    public void deleteAllReviews(String id) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM UserReviews where InstructorId = " + id;
        database.execSQL(deleteQuery);
    }

    public ArrayList<InstructorInfo> getAllInstructorInfo() {
        ArrayList<InstructorInfo> instructorList;
        instructorList = new ArrayList<>();
        String selectQuery = "SELECT * FROM InstructorInfo";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                InstructorInfo instructorInfo = new InstructorInfo();
                instructorInfo.setInstructorId(cursor.getInt(0));
                instructorInfo.setFirstName(cursor.getString(1));
                instructorInfo.setLastName(cursor.getString(2));
                instructorInfo.setOffice(cursor.getString(3));
                instructorInfo.setEmail(cursor.getString(4));
                instructorInfo.setPhone(cursor.getString(5));
                instructorList.add(instructorInfo);
            }
            while (cursor.moveToNext());
        }
        return instructorList;
    }

    public ArrayList<UserReviews> getAllReviews() {
        ArrayList<UserReviews> reviewsArrayList = new ArrayList<>();
        String selectQuery = "SELECT * FROM UserReviews";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                UserReviews userReviews = new UserReviews();
                userReviews.setInstructorId(cursor.getInt(0));
                userReviews.setComment(cursor.getString(1));
                userReviews.setDate(cursor.getString(2));
                userReviews.setRatingId(cursor.getInt(3));
                reviewsArrayList.add(userReviews);
            }
            while (cursor.moveToNext());
        }
        return reviewsArrayList;
    }

    public InstructorInfo getInstructorInfo(String id) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM InstructorInfo where InstructorId =" + id ;
        Cursor cursor = database.rawQuery(selectQuery, null);
        InstructorInfo instructorInfo = new InstructorInfo();
        if (cursor.moveToFirst()) {
            do {
                instructorInfo.setInstructorId(cursor.getInt(0));
                instructorInfo.setFirstName(cursor.getString(1));
                instructorInfo.setLastName(cursor.getString(2));
                instructorInfo.setOffice(cursor.getString(3));
                instructorInfo.setEmail(cursor.getString(4));
                instructorInfo.setPhone(cursor.getString(5));
                instructorInfo.setAverageRating(cursor.getString(6));
                instructorInfo.setTotalReviews(cursor.getInt(7));
            } while (cursor.moveToNext());
        }
        return instructorInfo;
    }

    public boolean isInstructorExist(String id) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM InstructorInfo WHERE InstructorId =" + id ;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }
}