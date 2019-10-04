package com.example.android.drugs_directory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class DrugsContract
{
    // To prevent someone from accidentally instantiating the contract class,
    // give an empty constructor
    private DrugsContract(){
        // Empty constructor for the class
    }

    // String constants for drugs provider class
    public final static String CONTENT_AUTHORITY = "com.example.android.drugs_directory";
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public final static String PATH_DRUGS = "drugs";

    public static final class DrugsEntry implements BaseColumns
    {
        // Name for the database table for drugs
        public final static String TABLE_NAME = "drugs";

        // Unique id number for the drugs
        public final static String _ID = BaseColumns._ID;
        // Column name for the generic name of the drugs
        public final static String COLUMN_GENERIC_NAME = "generic";
        // Column name for the category, (tab(1), syr(2), inj(3), inh(4))
        public final static String COLUMN_CATEGORY_FORM = "category_form";
        // Column name for dosage of the drugs
        public final static String COLUMN_DOSAGE = "dosage";
        // column title for the brand name
        public final static String COLUMN_BRAND_NAME = "brand_name";
        // Column title name for available dosage
        public final static String COLUMN_AVAIL_DOSE = "avail_dose";
        //************************************************************

        // Possible values for the drug cateogory
        public final static int UNKNOWN_CATEGORY = 0;
        public final static int TABLET = 1;
        public final static int SYRUP = 2;
        public final static int INJECTION = 3;
        public final static int INHALATION = 4;
        public final static int CAPSULE = 5;

        // Create a string that contains the SQL statement for creating a SQL table
        public final static String SQL_CREATE_DRUGS_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_GENERIC_NAME + " TEXT NOT NULL, "
                + COLUMN_CATEGORY_FORM + " INTEGER NOT NULL, "
                + COLUMN_DOSAGE + " INTEGER, "
                + COLUMN_BRAND_NAME + " TEXT, "
                + COLUMN_AVAIL_DOSE + " INTEGER);";

        // String constants for sort order using generic name
        public final static String GENERIC_DESCENDING = COLUMN_GENERIC_NAME + " DESC";
        public final static String GENERIC_ASCENDING = COLUMN_GENERIC_NAME + " ASC";

        // Create a string that contains the SQL statement for deleting the SQL table
        public final static String SQL_DELETE_DRUGS_TABLE = "DROP TABLE IF EXIST " + DrugsEntry.TABLE_NAME;
        // Final uri to communicate using the content provider
        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DRUGS);

        // For the getType method in the DrugProvider class
        public final static String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DRUGS;
        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DRUGS;
    }
}