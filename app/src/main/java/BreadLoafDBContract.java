import android.provider.BaseColumns;

public final class BreadLoafDBContract {

    private BreadLoafDBContract() {
    }

    public static class Expenses implements BaseColumns {
        public static final String TABLE_NAME = "expenses";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_DATE = "date";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_AMOUNT + " TEXT, " +
                COLUMN_DATE + " INTEGER" + ")";
    }
}
