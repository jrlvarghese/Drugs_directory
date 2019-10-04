package com.example.android.drugs_directory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.drugs_directory.data.DrugsContract.DrugsEntry;


public class DrugsCursorAdapter extends CursorAdapter
{
    // Default constructor
    public DrugsCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }

    // Creating a new view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.drugs_list_item, parent, false);
    }

    // Making a bind view method that will bind the listview to the cursor adapter
    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        // For generic name
        TextView genericNameTextView = (TextView)view.findViewById(R.id.generic_name);
        int genericNameColumnIndex = cursor.getColumnIndex(DrugsEntry.COLUMN_GENERIC_NAME);
        String genericName = cursor.getString(genericNameColumnIndex);
        genericNameTextView.setText(genericName);

        // For dosage
        TextView dosageTextView = (TextView)view.findViewById(R.id.dosage);
        int dosageColumnIndex = cursor.getColumnIndex(DrugsEntry.COLUMN_DOSAGE);
        int dosage = cursor.getInt(dosageColumnIndex);
        if(dosage>0){
            dosageTextView.setText(dosage + " mg/kg");
        }
        else{
            dosageTextView.setText("_");
        }


        // For brand name
        TextView brandNameTextView = (TextView)view.findViewById(R.id.brand_name);
        int brandNameColumnIndex = cursor.getColumnIndex(DrugsEntry.COLUMN_BRAND_NAME);
        String brandName = cursor.getString(brandNameColumnIndex);

        // Based on the drug category add Tab. / Syr. / Inj. / Inh.
        int categoryColumnIndex = cursor.getColumnIndex(DrugsEntry.COLUMN_CATEGORY_FORM);
        int drugsCategory = cursor.getInt(categoryColumnIndex);
        switch(drugsCategory)
        {
            case DrugsEntry.TABLET:
                brandName = "Tab. " + brandName;
                break;
            case DrugsEntry.SYRUP:
                brandName = "Syr. " + brandName;
                break;
            case DrugsEntry.INJECTION:
                brandName = "Inj. " + brandName;
                break;
            case DrugsEntry.INHALATION:
                brandName = "Inh. " + brandName;
                break;
            case DrugsEntry.CAPSULE:
                brandName = "Cap. " + brandName;
                break;
        }
        brandNameTextView.setText(brandName);

        // For available dosagae
        TextView avl_dosageTextView = (TextView)view.findViewById(R.id.available_dose);
        int avl_doseColumnIndex = cursor.getColumnIndex(DrugsEntry.COLUMN_AVAIL_DOSE);
        String avl_dose = cursor.getString(avl_doseColumnIndex);
        avl_dosageTextView.setText(avl_dose + " mg");

    }

}
