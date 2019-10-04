package com.example.android.drugs_directory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.example.android.drugs_directory.data.DrugsContract;
import com.example.android.drugs_directory.data.DrugsContract.DrugsEntry;

import static android.view.View.GONE;

public class ViewByCategoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{

    // Global string variables for selection and selection args
    private String mySelection;
    private String[] mySelectionArgs = {""};
    private String mySortOrder = DrugsEntry.GENERIC_ASCENDING;

    // Instantiate a cursorAdapter class
    DrugsCursorAdapter myCursorAdapter;

    // Integer constant which will be used by the LoaderClass to define what to load
    private static final int DRUGS_LOADER = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_by_category);
        // Make an up button in the action bar
        // initially when i used getActionBar() it was not working
        // there is no need of adding menu inflator method or oncreate options menu for this
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // When viewing by category of the drug disable the add drug icon (in the floating action bar)
        // as we are using the same view for viewing the action
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setVisibility(GONE);

        // Get the intent using getIntent method
        Intent intent = getIntent();
        // Get extras attached with the intents
        // this is for getting the selection
        mySelection = intent.getExtras().getString("selection_s");
        // for getting the selection args
        mySelectionArgs[0] = intent.getExtras().getString("selectionArgs_s");

        // Get the integer value of the selectionArgs which represents the category value
        int category = Integer.parseInt(mySelectionArgs[0]);
        switch(category)
        {
            case DrugsEntry.TABLET:
                setTitle(R.string.tablet_category);
                break;
            case DrugsEntry.SYRUP:
                setTitle(R.string.syrup_category);
                break;
            case DrugsEntry.INJECTION:
                setTitle(R.string.injection_category);
                break;
            case DrugsEntry.INHALATION:
                setTitle(R.string.inhalation_category);
                break;
            case DrugsEntry.CAPSULE:
                setTitle(R.string.capsule_category);
                break;
            default:
                setTitle("Drugs Directory");
                break;
        }

//        Log.v("ViewByCategoryActivity",mySelection + mySelectionArgs[0]);

        // Find the list view that have to be populated
        ListView drugsListView = findViewById(R.id.drugsList);

        // Get the empty view and set the image when it's empty
        View emptyView = findViewById(R.id.empty_view);
        drugsListView.setEmptyView(emptyView);

        // Instantiate the DrugsCursorAdapter which later connected to the listView
        myCursorAdapter = new DrugsCursorAdapter(this, null);

        // Set the cursor adapter to the list view
        drugsListView.setAdapter(myCursorAdapter);

        // Start the loader manager
        getLoaderManager().initLoader(DRUGS_LOADER, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String[] projection = {
                DrugsContract.DrugsEntry._ID,
                DrugsContract.DrugsEntry.COLUMN_GENERIC_NAME,
                DrugsContract.DrugsEntry.COLUMN_CATEGORY_FORM,
                DrugsContract.DrugsEntry.COLUMN_DOSAGE,
                DrugsContract.DrugsEntry.COLUMN_BRAND_NAME,
                DrugsContract.DrugsEntry.COLUMN_AVAIL_DOSE};

        //String selection = DrugsEntry.COLUMN_CATEGORY_FORM + "=?";
        //String[] selectionArgs = {"2"};
        return new CursorLoader(this,
                DrugsEntry.CONTENT_URI,
                projection,
                mySelection,
                mySelectionArgs,
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

}
