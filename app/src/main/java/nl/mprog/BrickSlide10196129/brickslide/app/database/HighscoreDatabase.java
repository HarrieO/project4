package nl.mprog.BrickSlide10196129.brickslide.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import nl.mprog.apps.Hangman10196129.database.HighscoreDatabaseHelper.HighscoreReaderContract.FeedEntry;

/**
 * Highscore Database holds up to 30 highscores.
 * Created by hroosterhuis on 17/02/14.
 */
public class HighscoreDatabase {

    private HighscoreDatabaseHelper mDbHelper = null;
    private Context context;
    private final static int limit = 30;

    public HighscoreDatabase(Context context) {
        this.context = context;
    }

    /**
     * Load method so that the database is only loaded when necessary.
     */
    private HighscoreDatabaseHelper load() {
        if (mDbHelper == null)
            mDbHelper = new HighscoreDatabaseHelper(context);
        return mDbHelper;
    }

    public void put(String word, int incorrect, int score) {
        SQLiteDatabase db = load().getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_WORD, word);
        values.put(FeedEntry.COLUMN_NAME_INCORRECT, incorrect);
        values.put(FeedEntry.COLUMN_NAME_SCORE, score);

        long newRowId = db.insert(FeedEntry.TABLE_NAME, null, values);
        if(count() > limit)
            truncate();
    }

    public void truncate(){
        SQLiteDatabase db = load().getReadableDatabase();

        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_SCORE
        };

        String sortOrder =
                FeedEntry.COLUMN_NAME_SCORE + " DESC" ;

        Cursor c = db.query(FeedEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);

        c.moveToFirst();
        c.move(limit-1);

        db = load().getWritableDatabase();

        String[] id = new String[1];
        for(int i = 0 ; i < c.getCount()-limit ; i++){
            c.moveToNext();
            id[0] = String.valueOf(c.getInt(c.getColumnIndex(FeedEntry._ID)));
            db.delete(FeedEntry.TABLE_NAME, FeedEntry._ID + " LIKE ?", id);
        }




    }

    public HighscoreCursor get() {
        SQLiteDatabase db = load().getReadableDatabase();

        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_WORD,
                FeedEntry.COLUMN_NAME_INCORRECT,
                FeedEntry.COLUMN_NAME_SCORE
        };

        String sortOrder =
                FeedEntry.COLUMN_NAME_SCORE + " DESC";

        Cursor c = db.query(FeedEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
        return new HighscoreCursor(c);
    }

    public long count() {
        return DatabaseUtils.queryNumEntries(load().getReadableDatabase(),
                HighscoreDatabaseHelper.HighscoreReaderContract.FeedEntry.TABLE_NAME);
    }

    public static class HighscoreCursor {
        Cursor cursor;

        public HighscoreCursor(Cursor cursor) {
            this.cursor = cursor;
            this.cursor.moveToFirst();
        }

        public String getWord() {
            return cursor.getString(cursor.getColumnIndex(FeedEntry.COLUMN_NAME_WORD));
        }

        public int getIncorrect() {
            return cursor.getInt(cursor.getColumnIndex(FeedEntry.COLUMN_NAME_INCORRECT));
        }

        public int getScore() {
            return cursor.getInt(cursor.getColumnIndex(FeedEntry.COLUMN_NAME_SCORE));
        }

        public void next() {
            cursor.moveToNext();
        }

        public void first() {
            cursor.moveToFirst();
        }

        public int count() {
            return cursor.getCount();
        }

        public boolean hasNext() {
            return !cursor.isLast();
        }
    }


}
