package nl.mprog.BrickSlide10196129.brickslide.app.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;


/**
 * Word database to access all words stored in the game.
 * Created by hroosterhuis on 18/02/14.
 */
public class WordDatabase {

    private WordDatabaseHelper mDbHelper = null;

    public WordDatabase(Context context) {
        mDbHelper = new WordDatabaseHelper(context);
    }

    /**
     * Returns the lenght of the longest word in the database.
     * @return
     */
    public int maxLength(){
        Cursor c = mDbHelper.getReadableDatabase().rawQuery(
                "SELECT MAX(" + FeedEntry.COLUMN_NAME_LENGTH + ") as max FROM " + FeedEntry.TABLE_NAME, null
        );
        c.moveToFirst();

        return c.getInt(c.getColumnIndex("max"));
    }

    /**
     * Returns a random word of given length.
     * @param length
     * @return
     */
    public String get(int length) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_WORD,
                FeedEntry.COLUMN_NAME_LENGTH
        };

        String whereClause = FeedEntry.COLUMN_NAME_LENGTH + " =  ?";
        String[] whereArgs = {
                String.valueOf(length)
        };

        Cursor c = db.query(FeedEntry.TABLE_NAME, projection, whereClause, whereArgs, null, null, null);
        c.moveToFirst();
        c.move((int)Math.floor(c.getCount()*Math.random()));
        return c.getString(c.getColumnIndex(FeedEntry.COLUMN_NAME_WORD));
    }

    /**
     * Returns a cursor used for Evil algorithm,words found are like state but unlike given arraylist
     */
    public Cursor getCursor(String state, ArrayList<String> impossibilities) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_WORD,
                FeedEntry.COLUMN_NAME_LENGTH
        };

        String whereClause = FeedEntry.COLUMN_NAME_LENGTH + " =  ? AND " + FeedEntry.COLUMN_NAME_WORD +
                " LIKE ?" ;

        int incorrectLength = impossibilities.size();
        String[] whereArgs = new String[2 + incorrectLength];
        whereArgs[0] = String.valueOf(state.length());
        whereArgs[1] = state;
        for(int i = 0;i < incorrectLength;i++){
            whereClause = whereClause + " AND " + FeedEntry.COLUMN_NAME_WORD  + " NOT LIKE ?";
            whereArgs[2 + i] = impossibilities.get(i) ;
        }
        return db.query(FeedEntry.TABLE_NAME, projection, whereClause, whereArgs, null, null, null);

    }

    public WordCursor getWordCursor(String state, ArrayList<String> impossibilities) {
        return new WordCursor(getCursor(state, impossibilities));
    }

    public static class WordCursor {
        Cursor cursor;

        public WordCursor(Cursor cursor) {
            this.cursor = cursor;
            this.cursor.moveToFirst();
        }

        public String getWord() {
            return cursor.getString(cursor.getColumnIndex(FeedEntry.COLUMN_NAME_WORD));
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
