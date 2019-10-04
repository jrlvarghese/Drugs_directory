package com.example.android.drugs_directory;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.drugs_directory.data.DrugsContract.DrugsEntry;


public class SearchableActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    // Instantiate a cursorAdapter class
    DrugsCursorAdapter myCursorAdapter;

    // Integer constant which will be used by the LoaderClass to define what to load
    private static final int DRUGS_LOADER = 0;

    private String mySortOrder = DrugsEntry.GENERIC_ASCENDING;

    private String query;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        setTitle(R.string.search_activity_label);

//        Intent intent = getIntent();
//        String query = intent.getStringExtra(SearchManager.QUERY);
//        showResults(query);
        handleIntent(getIntent());

        // Start the loader manager
        getLoaderManager().initLoader(DRUGS_LOADER, null, this);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }

    private void showResults(String query)
    {
        // Find the list view using the method findViewById
        ListView searchResultListView = findViewById(R.id.search_result_list);
        // Set empty view if the list is empty
        View emptyView = findViewById(R.id.empty_view);
        searchResultListView.setEmptyView(emptyView);
        TextView emptyResultView = findViewById(R.id.empty_search_term);
        emptyResultView.setText(query);

        // Instantiate the DrugsCursorAdapter which later connected to the listView
        myCursorAdapter = new DrugsCursorAdapter(this, null);
        // Attach the cursor adapter to the listView
        searchResultListView.setAdapter(myCursorAdapter);

    }


    // METHODS FOR THE LoaderManager class, following methods are abstract methods
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

        // In selection give the name of column where the match have to be found
        String selection = DrugsEntry.COLUMN_GENERIC_NAME + "=?";
        String[] selectionArgs = {query};
        return new CursorLoader(this,
                DrugsEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
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
