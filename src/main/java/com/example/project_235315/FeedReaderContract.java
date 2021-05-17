package com.example.project_235315;

import android.provider.BaseColumns;

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_DESC = "description";
        public static final String COLUMN_NAME_IS_DONE = "isDone";
        public static final String COLUMN_NAME_IS_DUE_DATE = "dueDate";
    }

}

