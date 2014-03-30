package nl.mprog.BrickSlide10196129.brickslide.app.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by hroosterhuis on 17/02/14.
 */
public class HighscoreDatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "BrickSlideHighScores.db";

    public HighscoreDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HighscoreReaderContract.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(HighscoreReaderContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static final class HighscoreReaderContract {
        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.
        public HighscoreReaderContract() {}

        /* Inner class that defines the table contents */
        public static abstract class FeedEntry implements BaseColumns {
            public static final String TABLE_NAME = "highscores";
            public static final String COLUMN_NAME_PUZZLE_ID = "puzzle_id";
            public static final String COLUMN_NAME_MOVES = "moves";
        }

        public static final String INT_TYPE =  " INTEGER";
        public static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + HighscoreReaderContract.FeedEntry.TABLE_NAME + " (" +
                        FeedEntry._ID + " INTEGER PRIMARY KEY," +
                        FeedEntry.COLUMN_NAME_PUZZLE_ID + INT_TYPE + COMMA_SEP +
                        FeedEntry.COLUMN_NAME_MOVES     + INT_TYPE  +
                        " )";

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + HighscoreReaderContract.FeedEntry.TABLE_NAME;
    }
}
