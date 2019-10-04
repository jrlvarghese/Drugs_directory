package com.example.android.drugs_directory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.drugs_directory.data.DrugsContract.DrugsEntry;

public class DrugsProvider extends ContentProvider
{
    // Get an instance of the DrugsDbHelper class within the provider
    private DrugsDbHelper myDbHelper;

    // Integer constants for assigning values for particular uri
    private final static int FULL_TABLE = 100;
    private final static int SINGLE_ROW = 200;
    //private final static int TABLETS = 300;

    // Make a global variable for type UriMatcher
    private final static UriMatcher myUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        // Add the default values, so that uri matcher can later recognise the same
        myUriMatcher.addURI(DrugsContract.CONTENT_AUTHORITY, DrugsContract.PATH_DRUGS, FULL_TABLE);
        myUriMatcher.addURI(DrugsContract.CONTENT_AUTHORITY, DrugsContract.PATH_DRUGS + "/#", SINGLE_ROW);
        //myUriMatcher.addURI(DrugsContract.CONTENT_AUTHORITY, DrugsContract.PATH_DRUGS + "/#", TABLETS);
    }

    // Initialise the provider class and database helper class
    public boolean onCreate()
    {
        // Initialise the DrugsDbHelper class
        myDbHelper = new DrugsDbHelper(getContext());
        return true;
    }

    // Perform the query for the given uri
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        // Get an instance of readable database as query method will return a cursor
        SQLiteDatabase drugsDb = myDbHelper.getReadableDatabase();
        // Instantiating a cursor
        Cursor cursor = null;

        // Variable for storing the uri matcher result
        final int match = myUriMatcher.match(uri);
        switch(match)
        {
            case FULL_TABLE:
                cursor = drugsDb.query(DrugsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SINGLE_ROW:
                selection = DrugsEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = drugsDb.query(DrugsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
//            case TABLETS:
//                selection = DrugsEntry.COLUMN_CATEGORY_FORM + "=?";
//                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
            default:
                throw new IllegalArgumentException("Cannot query this unknown uri: " + uri);
        }
        // Set the notification Uri on the cursor
        // So we know what content URI the cursor was created for
        // If the data at this URI changes, then we know we need to update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    // Insert method inside the DrugsProvider to insert data with the given ContentValues
    @Override
    public Uri insert(Uri uri, ContentValues contentValues)
    {
        // Match the uri and decide what to do
        final int match = myUriMatcher.match(uri);
        switch(match)
        {
            case FULL_TABLE:
                // For inserting a new row complete table is required and there is no other option
                // to insert data into a particular row
                return insertDrug(uri, contentValues);
            default:
                throw new IllegalArgumentException("Cannot insert using unknown uri: " + uri);
        }
    }

    // Update method withing the provider class
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs)
    {
        final int match = myUriMatcher.match(uri);
        switch(match)
        {
            case FULL_TABLE:
                return updateDrug(uri, contentValues, selection, selectionArgs);
            case SINGLE_ROW:
                selection = DrugsEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateDrug(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot upadate using unknown uri: " + uri);
        }
    }

    // Delete the data at the given selection and selection arguments
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        // Get access to  a writable database
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        final int match = myUriMatcher.match(uri);
        switch(match)
        {
            case FULL_TABLE:// Deletes the entire table
                return db.delete(DrugsEntry.TABLE_NAME, selection, selectionArgs);
            case SINGLE_ROW:    // Deletes single entry
                selection = DrugsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowsDeleted = db.delete(DrugsEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted!=0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for: " + uri);
        }
    }

    // Returns the MIME type of the uri
    @Override
    public String getType(Uri uri)
    {
        final int match = myUriMatcher.match(uri);
        switch (match)
        {
            case FULL_TABLE:
                return DrugsEntry.CONTENT_LIST_TYPE;
            case SINGLE_ROW:
                return DrugsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    //**********************************************************************************
    // CUSTOM METHODS USED ABOVE
    // defining the insertDrug method
    public Uri insertDrug(Uri uri, ContentValues contentValues)
    {
        // Sanity check for the incoming data
        // For the generic name of the drug which is stored as a string
        String genericName = contentValues.getAsString(DrugsEntry.COLUMN_GENERIC_NAME);
        if(genericName==null)
            throw new IllegalArgumentException("Drug requires a generic name!!");

        // For the category of the drugs
        Integer drugCategory = contentValues.getAsInteger(DrugsEntry.COLUMN_CATEGORY_FORM);
        if(drugCategory == null)
            throw new IllegalArgumentException("There should be a drug category!!");

        // For drugs dosage which should be an integer
        Integer drugDosage = contentValues.getAsInteger(DrugsEntry.COLUMN_DOSAGE);
        if(drugDosage == null)
            throw new IllegalArgumentException("Dosage cannot have this value!!");

        String availDose = contentValues.getAsString(DrugsEntry.COLUMN_AVAIL_DOSE);
        if(availDose == null)
            throw new IllegalArgumentException("Avail dosage cannot have this value!!");


        // For the brand name
        String brandName = contentValues.getAsString(DrugsEntry.COLUMN_BRAND_NAME);
        if(brandName==null)
            throw new IllegalArgumentException("Brand name should be a string.");

        // Get access to a writable database using the method
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        // Using the insert method insert the data
        long newRowId = db.insert(DrugsEntry.TABLE_NAME, null, contentValues);
        if(newRowId == -1)
            return null;
        else{
            // Notify that there is a change in the database
            getContext().getContentResolver().notifyChange(uri,null);
            // Return the uri with appended id of the new row
            return ContentUris.withAppendedId(uri, newRowId);
        }
    }

    // updateDrug method used inside the update method
    public int updateDrug(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs)
    {
        if(contentValues.containsKey(DrugsEntry.COLUMN_GENERIC_NAME)){
            String genericName = contentValues.getAsString(DrugsEntry.COLUMN_GENERIC_NAME);
            if(genericName == null)
                throw new IllegalArgumentException("Requires a generic name!");
        }

        if(contentValues.containsKey(DrugsEntry.COLUMN_CATEGORY_FORM)){
            Integer category = contentValues.getAsInteger(DrugsEntry.COLUMN_CATEGORY_FORM);
            if(category == null)
                throw new IllegalArgumentException("Drug requires a category");
        }

        // If there is no values to update return and should not access the database
        if(contentValues.size() == 0 ){
            return 0;
        }
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(DrugsEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        // Give a notification that the row have been updated so that the loader can reload the data
        if(rowsUpdated!=0)
            getContext().getContentResolver().notifyChange(uri, null);
        // Finally return the total number of rows updated
        return rowsUpdated;
    }


}
