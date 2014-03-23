package nl.mprog.BrickSlide10196129.brickslide.app.database;

import android.content.Context;
import android.provider.BaseColumns;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class PuzzleDatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "puzzles.db";
    private static final int DATABASE_VERSION = 1;

    public PuzzleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final class WordReaderContract {
        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.
        public WordReaderContract() {}

        /* Inner class that defines the table contents */
        public static abstract class FeedEntry implements BaseColumns {
            public static final String TABLE_NAME           = "puzzles" ;
            public static final String COLUMN_NAME_NAME     = "name"    ;
            public static final String COLUMN_NAME_MINIMUM  = "minimum" ;
            public static final String COLUMN_NAME_PUZZLE   = "puzzle"  ;
        }

    }

}
