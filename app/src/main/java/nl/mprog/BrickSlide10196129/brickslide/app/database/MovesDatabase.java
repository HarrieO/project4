package nl.mprog.BrickSlide10196129.brickslide.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import nl.mprog.BrickSlide10196129.brickslide.app.database.MovesDatabaseHelper.MovesReaderContract.FeedEntry ;
import nl.mprog.BrickSlide10196129.brickslide.app.database.MovesDatabaseHelper.MovesReaderContract;

public class MovesDatabase {

    private MovesDatabaseHelper mDbHelper = null;
    private Context context;
    private final static int limit = 30;

    public MovesDatabase(Context context) {
        this.context = context;
    }

    /**
     * Load method so that the database is only loaded when necessary.
     */
    private MovesDatabaseHelper load() {
        if (mDbHelper == null)
            mDbHelper = new MovesDatabaseHelper(context);
        return mDbHelper;
    }

    public void close(){
        mDbHelper.close();
    }

    public void empty(){
        SQLiteDatabase db = load().getWritableDatabase();
        db.execSQL(MovesReaderContract.SQL_DELETE_ENTRIES);
        db.execSQL(MovesReaderContract.SQL_CREATE_ENTRIES);
    }

    public void put(String moves) {
        SQLiteDatabase db = load().getWritableDatabase();

        empty();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_MOVES, moves);

        db.insert(FeedEntry.TABLE_NAME, null, values);
    }

    public boolean movesSaved() {
        SQLiteDatabase db = load().getReadableDatabase();

        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_MOVES
        };


        Cursor c = db.query(FeedEntry.TABLE_NAME, projection, null, null, null, null, null);

        return c.getCount() > 0;
    }


    public String get() {
        SQLiteDatabase db = load().getReadableDatabase();

        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_MOVES
        };


        Cursor c = db.query(FeedEntry.TABLE_NAME, projection, null, null, null, null, null);
        c.moveToFirst();

        return c.getString(c.getColumnIndex(FeedEntry.COLUMN_NAME_MOVES));
    }




}
