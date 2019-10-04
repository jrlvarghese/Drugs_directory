package com.example.android.drugs_directory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.drugs_directory.data.DrugsContract.DrugsEntry;

public class EditDrugActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{

    // Global variable for the spinner
    Spinner myDrugsCategorySpinner;
    // integer variable to store the selected drugs category
    private int myDrugsCategory = 0;
    // Variable for the uri
    private Uri myCurrentDrugUri;

    private int EXISTING_DRUG_LOADER = 0;

    // Variables for getting the input from the edit fields
    private EditText genericEditText;
    private EditText brandNameEditText;
    private EditText dosageEditText;
    private EditText availableDoseEditText;

    // To know the changes in the editable fields of the drug
    private boolean myDrugHasChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drug);
        // use getIntent() and getData() method to get the associated URI
        Intent intent = getIntent();
        // Get the uri from the intent; in case of intent from the floating action bar data is null
        // if the intent is from the click on list view intent will contain a data
        myCurrentDrugUri = intent.getData();

        if(myCurrentDrugUri == null){
            setTitle("Add Drug");
            // As it's adding a new drug should not show the delete option
            invalidateOptionsMenu();
        }
        else{
            setTitle("Edit drug");
            getLoaderManager().initLoader(EXISTING_DRUG_LOADER, null, this);
        }




        // Find all relevant views that we will need to read user input from
        genericEditText = (EditText)findViewById(R.id.edit_generic_name);
        brandNameEditText = (EditText)findViewById(R.id.edit_brand_name);
        dosageEditText = findViewById(R.id.edit_dosage);
        availableDoseEditText = findViewById(R.id.edit_strength_1);

        // Attach the touch listener with the editable fields
        genericEditText.setOnTouchListener(mTouchListener);
        brandNameEditText.setOnTouchListener(mTouchListener);
        dosageEditText.setOnTouchListener(mTouchListener);
        availableDoseEditText.setOnTouchListener(mTouchListener);

        myDrugsCategorySpinner = (Spinner)findViewById(R.id.drugs_category_spinner);
        setupSpinner();
        
    }

    private void setupSpinner()
    {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter drugSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_drug_category, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        drugSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        myDrugsCategorySpinner.setAdapter(drugSpinnerAdapter);

        // Set the integer mSelected to the constant values
        myDrugsCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.tablet_category))) {
                        myDrugsCategory = DrugsEntry.TABLET;
                    } else if (selection.equals(getString(R.string.syrup_category))) {
                        myDrugsCategory = DrugsEntry.SYRUP;
                    } else if(selection.equals(getString(R.string.injection_category))){
                        myDrugsCategory = DrugsEntry.INJECTION;
                    }else if(selection.equals(getString(R.string.inhalation_category))){
                        myDrugsCategory = DrugsEntry.INHALATION;
                    }else if(selection.equals(getString(R.string.capsule_category))){
                        myDrugsCategory = DrugsEntry.CAPSULE;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                myDrugsCategory = DrugsEntry.UNKNOWN_CATEGORY;
            }
        });
    }


    // Create the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit_drugs, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (myCurrentDrugUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId())
        {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                saveDrug();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the drug hasn't changed, continue with navigating up to parent activity
                // Which is the ViewDrugsActivity
                if(!myDrugHasChanged){
                    NavUtils.navigateUpFromSameTask(EditDrugActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that changes should be discarded
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditDrugActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void saveDrug()
    {
        String genericString = genericEditText.getText().toString().trim();
        String brandString = brandNameEditText.getText().toString().trim();
        String dosageString = dosageEditText.getText().toString().trim();
        String availDoseString = availableDoseEditText.getText().toString().trim();


        // Sanity check
        if((myCurrentDrugUri == null)&&
                TextUtils.isEmpty(genericString)&&TextUtils.isEmpty(brandString)&&
                TextUtils.isEmpty(dosageString)&&TextUtils.isEmpty(availDoseString)){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DrugsEntry.COLUMN_GENERIC_NAME, genericString);
        values.put(DrugsEntry.COLUMN_BRAND_NAME, brandString);

        int dosage = 0;
        if(!TextUtils.isEmpty(dosageString)){
            dosage = Integer.parseInt(dosageString);
        }
        values.put(DrugsEntry.COLUMN_DOSAGE, dosage);
        values.put(DrugsEntry.COLUMN_CATEGORY_FORM, myDrugsCategory);
        values.put(DrugsEntry.COLUMN_AVAIL_DOSE, availDoseString);

        if(myCurrentDrugUri == null){
            /* INSERT THE DATA USING THE CONTENT PROVIDER CLASS*/
            Uri uri = getContentResolver().insert(DrugsEntry.CONTENT_URI, values);
            // Give a toast message
            if(uri == null)
                Toast.makeText(this, "Failed to insert data!!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Successfully inserted data.", Toast.LENGTH_LONG).show();
        }
        else{
            int rowId = getContentResolver().update(myCurrentDrugUri, values, null, null);
            // Make toast messages
            if(rowId != 0)
                Toast.makeText(this, "Sucessfully updated drug.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Failed updating the drug!!", Toast.LENGTH_LONG).show();
        }
    }

    // FOLLOWING THREE METHODS ARE IMPLEMENTED FROM THE CLASS LoaderManager
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                DrugsEntry._ID,
                DrugsEntry.COLUMN_GENERIC_NAME,
                DrugsEntry.COLUMN_BRAND_NAME,
                DrugsEntry.COLUMN_DOSAGE,
                DrugsEntry.COLUMN_CATEGORY_FORM,
                DrugsEntry.COLUMN_AVAIL_DOSE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                myCurrentDrugUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int genericColumnIndex = cursor.getColumnIndex(DrugsEntry.COLUMN_GENERIC_NAME);
            int brandColumnIndex = cursor.getColumnIndex(DrugsEntry.COLUMN_BRAND_NAME);
            int categoryColumnIndex = cursor.getColumnIndex(DrugsEntry.COLUMN_CATEGORY_FORM);
            int dosageColumnIndex = cursor.getColumnIndex(DrugsEntry.COLUMN_DOSAGE);
            int availStrengthColumnIndex = cursor.getColumnIndex(DrugsEntry.COLUMN_AVAIL_DOSE);

            // Extract out the value from the Cursor for the given column index
            String genericName = cursor.getString(genericColumnIndex);
            String brandName = cursor.getString(brandColumnIndex);
            int dosage = cursor.getInt(dosageColumnIndex);
            int drugsCategory = cursor.getInt(categoryColumnIndex);
            String availStrength = cursor.getString(availStrengthColumnIndex);

            // Update the views on the screen with the values from the database
            genericEditText.setText(genericName);
            brandNameEditText.setText(brandName);
            dosageEditText.setText(Integer.toString(dosage));
            availableDoseEditText.setText(availStrength);

            // Drugs category is a dropdown spinner, so mapt the constant value from the database
            // into one of the dropdown options
            // then call set selection() so that option is displayed on screen as the current selection
            switch(drugsCategory)
            {
                case DrugsEntry.TABLET:
                    myDrugsCategorySpinner.setSelection(DrugsEntry.TABLET);
                    break;
                case DrugsEntry.SYRUP:
                    myDrugsCategorySpinner.setSelection(DrugsEntry.SYRUP);
                    break;
                case DrugsEntry.INJECTION:
                    myDrugsCategorySpinner.setSelection(DrugsEntry.INJECTION);
                    break;
                case DrugsEntry.INHALATION:
                    myDrugsCategorySpinner.setSelection(DrugsEntry.INHALATION);
                    break;
                case DrugsEntry.CAPSULE:
                    myDrugsCategorySpinner.setSelection(DrugsEntry.CAPSULE);
                    break;
                case DrugsEntry.UNKNOWN_CATEGORY:
                    myDrugsCategorySpinner.setSelection(DrugsEntry.UNKNOWN_CATEGORY);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        genericEditText.setText("");
        brandNameEditText.setText("");
        dosageEditText.setText("");
        availableDoseEditText.setText("");
        myDrugsCategorySpinner.setSelection(DrugsEntry.UNKNOWN_CATEGORY);
    }
    // ################### END OF METHODS FROM THE LOADER MANAGER CLASS ###########################

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mPetHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            myDrugHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!myDrugHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }



    // Method to delete individual drug
    private void deleteDrug()
    {
        if(myCurrentDrugUri != null){
            int rowId = getContentResolver().delete(myCurrentDrugUri, null, null);
            if(rowId == 0)
                Toast.makeText(this, "Unable to delete drug", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Deleted!!", Toast.LENGTH_LONG).show();
        }

    }

    // Delete confirmation dialog
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_drug_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteDrug();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}