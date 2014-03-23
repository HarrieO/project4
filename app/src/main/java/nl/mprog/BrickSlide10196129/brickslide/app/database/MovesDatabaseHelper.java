package nl.mprog.BrickSlide10196129.brickslide.app.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by hroosterhuis on 17/02/14.
 */
public class MovesDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BrickSlide.db";

    public MovesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MovesReaderContract.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MovesReaderContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }



    public static final class MovesReaderContract {
        public MovesReaderContract() {}

        public static abstract class FeedEntry implements BaseColumns {
            public static final String TABLE_NAME = "moves";
            public static final String COLUMN_NAME_MOVES = "moves";
        }

        public static final String TEXT_TYPE = " TEXT";
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + MovesReaderContract.FeedEntry.TABLE_NAME + " (" +
                        FeedEntry._ID + " INTEGER PRIMARY KEY," +
                        FeedEntry.COLUMN_NAME_MOVES      + TEXT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + MovesReaderContract.FeedEntry.TABLE_NAME;

        public static final String SQL_TRUNCATE_ENTRIES = "TRUNCATE TABLE " + MovesReaderContract.FeedEntry.TABLE_NAME;

    }
}
