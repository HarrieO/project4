package nl.mprog.BrickSlide10196129.brickslide.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import nl.mprog.BrickSlide10196129.brickslide.app.database.HighscoreDatabaseHelper.HighscoreReaderContract.FeedEntry;

/**
 * Highscore Database holds up to 30 highscores.
 * Created by hroosterhuis on 17/02/14.
 */
public class HighscoreDatabase {

    private HighscoreDatabaseHelper mDbHelper = null;
    private Context context;

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

    public void put(int puzzle_id, int moves) {
        SQLiteDatabase db = load().getWritableDatabase();

        if(remove(puzzle_id,moves)) {
            ContentValues values = new ContentValues();
            values.put(FeedEntry.COLUMN_NAME_PUZZLE_ID, puzzle_id);
            values.put(FeedEntry.COLUMN_NAME_MOVES, moves);

            long newRowId = db.insert(FeedEntry.TABLE_NAME, null, values);
        }

    }

    /**
     * Returns true if newMoves is smaller than the previous smallest moves
     * @param puzzle_id
     * @param newMoves
     * @return
     */
    public boolean remove(int puzzle_id, int newMoves){
        SQLiteDatabase db = load().getReadableDatabase();

        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_PUZZLE_ID,
                FeedEntry.COLUMN_NAME_MOVES
        };

        String whereClause = FeedEntry.COLUMN_NAME_PUZZLE_ID + "=?";

        String[] whereArgs = new String[]{ String.valueOf(puzzle_id) };

        Cursor c = db.query(FeedEntry.TABLE_NAME, projection, whereClause, whereArgs, null, null, null);


        db = load().getWritableDatabase();

        String[] id = new String[1];
        boolean best = true ;

        while(c.moveToNext()){
            int score = c.getInt(c.getColumnIndex(FeedEntry.COLUMN_NAME_MOVES));
            if((newMoves != -1 && score > newMoves) || score == -1 ) {
                id[0] = String.valueOf(c.getInt(c.getColumnIndex(FeedEntry._ID)));
                db.delete(FeedEntry.TABLE_NAME, FeedEntry._ID + " LIKE ?", id);
            } else
                best = false ;
        }

        return best ;
    }

    public boolean hasScore(int puzzle_id){
        SQLiteDatabase db = load().getReadableDatabase();

        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_PUZZLE_ID,
                FeedEntry.COLUMN_NAME_MOVES
        };

        String whereClause = FeedEntry.COLUMN_NAME_PUZZLE_ID + "=?";

        String[] whereArgs = new String[]{ String.valueOf(puzzle_id) };

        Cursor c = db.query(FeedEntry.TABLE_NAME, projection, whereClause, whereArgs, null, null, null);
        return c.getCount() > 0 ;
    }

    public int get(int puzzle_id) {
        SQLiteDatabase db = load().getReadableDatabase();

        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_PUZZLE_ID,
                FeedEntry.COLUMN_NAME_MOVES
        };

        String whereClause = FeedEntry.COLUMN_NAME_PUZZLE_ID + "=?";

        String[] whereArgs = new String[]{ String.valueOf(puzzle_id) };

        Cursor c = db.query(FeedEntry.TABLE_NAME, projection, whereClause, whereArgs, null, null, null);
        c.moveToFirst();
        return c.getInt(c.getColumnIndex(FeedEntry.COLUMN_NAME_MOVES));
    }

}
