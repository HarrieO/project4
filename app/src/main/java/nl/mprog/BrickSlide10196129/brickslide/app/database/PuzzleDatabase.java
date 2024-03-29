package nl.mprog.BrickSlide10196129.brickslide.app.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import RushHourSolver.Puzzle;
import nl.mprog.BrickSlide10196129.brickslide.app.database.PuzzleDatabaseHelper.WordReaderContract.FeedEntry ;


/**
 * Created by hroosterhuis on 18/02/14.
 */
public class PuzzleDatabase {

    private PuzzleDatabaseHelper mDbHelper = null;

    public PuzzleDatabase(Context context) {
        mDbHelper = new PuzzleDatabaseHelper(context);
    }

    public void close(){
        mDbHelper.close();
    }


    public PuzzleCursor getCursor() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_NAME,
                FeedEntry.COLUMN_NAME_MINIMUM,
                FeedEntry.COLUMN_NAME_PUZZLE
        };
        PuzzleCursor pc =  new PuzzleCursor(db.query(FeedEntry.TABLE_NAME, projection, null, null, null, null, null));

        return  pc ;
    }

    public static class PuzzleCursor {
        Cursor cursor;

        public PuzzleCursor(Cursor cursor) {
            this.cursor = cursor;
            this.cursor.moveToFirst();
        }

        public Puzzle get() {
            int id          = cursor.getInt(cursor.getColumnIndex(FeedEntry._ID));
            String name     = cursor.getString(cursor.getColumnIndex(FeedEntry.COLUMN_NAME_NAME));
            int minimum     = cursor.getInt(cursor.getColumnIndex(FeedEntry.COLUMN_NAME_MINIMUM));
            String state   = cursor.getString(cursor.getColumnIndex(FeedEntry.COLUMN_NAME_PUZZLE));
            return new Puzzle(id,name,minimum,state);
        }

        private int id(){
            return cursor.getInt(cursor.getColumnIndex(FeedEntry._ID));
        }

        public Puzzle next(){
            cursor.moveToNext();
            if(cursor.isAfterLast())
                cursor.moveToFirst();
            return get();
        }

        public Puzzle get(int id){
            cursor.moveToFirst();
            while(!cursor.isAfterLast() && id() != id)
                cursor.moveToNext();
            if(cursor.isAfterLast())
                return null ;
            return get();
        }

        public boolean hasNext() {
            return !cursor.isLast();
        }

    }
}
