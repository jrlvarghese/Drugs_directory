package com.example.android.drugs_directory;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.android.drugs_directory.data.DrugsContract.DrugsEntry;

import static com.example.android.drugs_directory.data.DrugsContract.DrugsEntry.TABLET;

public class ViewDrugsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    // Instantiate a cursorAdapter class
    DrugsCursorAdapter myCursorAdapter;

    // Integer constant which will be used by the LoaderClass to define what to load
    private static final int DRUGS_LOADER = 0;

    private String mySortOrder = DrugsEntry.GENERIC_ASCENDING;

    //    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drugs);

        // Get the access to database in this context
        //myDbHelper = new DrugsDbHelper(this);



        // Setup a floating action button in view drugs activity
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewDrugsActivity.this, EditDrugActivity.class);
                startActivity(intent);
            }
        });

        ListView drugsListView = findViewById(R.id.drugsList);

        // Set empty view if the list is empty
        View emptyView = findViewById(R.id.empty_view);
        drugsListView.setEmptyView(emptyView);

        // Instantiate the DrugsCursorAdapter which later connected to the listView
        myCursorAdapter = new DrugsCursorAdapter(this, null);
        drugsListView.setAdapter(myCursorAdapter);

        // Set click listener for editing a particular drug
        drugsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                // Set an intent to the editor activity
                Intent intent = new Intent(ViewDrugsActivity.this, EditDrugActivity.class);
                // Create an uri based on the selected drug that will pass id of the row
                Uri currentPetUri = ContentUris.withAppendedId(DrugsEntry.CONTENT_URI, id);
                // Attach the uri to the intent
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        // Start the loader manager
        getLoaderManager().initLoader(DRUGS_LOADER, null, this);
    }

    // Create menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_drugs_catalog, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//        SearchView searchView = searchItem()
        searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()) );
        return true;
    }

    // Decide what to do once the menu options are clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
//            case android.R.id.home:
//                mDrawerLayout.openDrawer(GravityCompat.START);
//                return true;
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDrug();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteAllConfirmationDialog();
                return true;
//            case R.id.action_sort_ascending:
//                return true;
//            case R.id.action_sort_descending:
//                return true;
            case R.id.action_view_tablets:
                sendIntentViewByCategory(DrugsEntry.TABLET);
                return true;
            case R.id.action_view_syrup:
                sendIntentViewByCategory(DrugsEntry.SYRUP);
                return true;
            case R.id.action_view_injection:
                sendIntentViewByCategory(DrugsEntry.INJECTION);
                return true;
            case R.id.action_view_inhalation:
                sendIntentViewByCategory(DrugsEntry.INHALATION);
                return true;
            case R.id.action_view_capsule:
                sendIntentViewByCategory(DrugsEntry.CAPSULE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String[] projection = {
                DrugsEntry._ID,
                DrugsEntry.COLUMN_GENERIC_NAME,
                DrugsEntry.COLUMN_CATEGORY_FORM,
                DrugsEntry.COLUMN_DOSAGE,
                DrugsEntry.COLUMN_BRAND_NAME,
                DrugsEntry.COLUMN_AVAIL_DOSE};

        //String selection = DrugsEntry.COLUMN_CATEGORY_FORM + "=?";
        //String[] selectionArgs = {"2"};
        return new CursorLoader(this,
                DrugsEntry.CONTENT_URI,
                projection,
                null,
                null,
                mySortOrder);
    }

    // After database loading finished swap the cursor with new one
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        myCursorAdapter.swapCursor(data);
    }

    // Once loader resets make all cursor null
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        myCursorAdapter.swapCursor(null);
    }


    //********************************* CUSTOM METHODS *********************************
    // method to insert drugs
    public void insertDrug()
    {
        ContentValues values = new ContentValues();
        values.put(DrugsEntry.COLUMN_GENERIC_NAME, "PARACETAMOL");
        values.put(DrugsEntry.COLUMN_CATEGORY_FORM, TABLET);
        values.put(DrugsEntry.COLUMN_DOSAGE, 15);
        values.put(DrugsEntry.COLUMN_BRAND_NAME, "DOLO");
        values.put(DrugsEntry.COLUMN_AVAIL_DOSE, "650");

        Uri uri = getContentResolver().insert(DrugsEntry.CONTENT_URI, values);
        if(uri == null)
            Toast.makeText(this,"Failed to insert data!!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Successfully inserted data!!", Toast.LENGTH_SHORT).show();
    }

    // method to delete all the drugs
    public void deleteAllDrugs()
    {
        int rowsDeleted = getContentResolver().delete(DrugsEntry.CONTENT_URI, null, null);
        if(rowsDeleted == 0)
            Toast.makeText(this, "Failed to delete all pets!!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Deleted " + rowsDeleted + " drugs.", Toast.LENGTH_SHORT).show();
    }

    // Method for showing delete confirmation message

    private void showDeleteAllConfirmationDialog()
    {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the message to the builder
        builder.setMessage(R.string.delete_all_dialog_message);

        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteAllDrugs();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "cancel button, so dismiss the dialog
                // and continue within the view pet activity
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void sendIntentViewByCategory(int category)
    {
        Intent viewByCategory = new Intent(this, ViewByCategoryActivity.class);
        viewByCategory.putExtra("selection_s", DrugsEntry.COLUMN_CATEGORY_FORM + "=?");
        viewByCategory.putExtra("selectionArgs_s", Integer.toString(category));
        startActivity(viewByCategory);
    }
}

