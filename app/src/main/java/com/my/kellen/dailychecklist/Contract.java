package com.my.kellen.dailychecklist;

import android.provider.BaseColumns;

class Contract {

    static final class Entry implements BaseColumns{
        static final String TABLE_NAME = "checklist";
        static final String COLUMN_NAME = "guestName";
        static final String COLUMN_DATE = "date";
        static final String COLUMN_COMPLETE = "complete";
        static final String COLUMN_REPEAT = "repeat";
    }
}
