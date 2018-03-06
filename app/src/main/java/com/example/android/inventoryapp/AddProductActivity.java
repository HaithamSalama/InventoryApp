package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
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

import com.example.android.inventoryapp.database.WarehouseContract.ProductsEntry;

import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AddProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;

    private EditText mProductName;
    private EditText mPrice;
    private EditText mQuantity;
    private EditText mPhoneNum;
    private Spinner mSupplierSpinner;

    private int mSupplier = ProductsEntry.SUPPLIER_G4;

    private boolean mProductHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mProductHasChanged boolean to true.
     */
    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edite_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mProductName = findViewById(R.id.edit_Product_name);
        mPrice = findViewById(R.id.edit_price);
        mQuantity = findViewById(R.id.edit_quantity);
        mPhoneNum = findViewById(R.id.edit_phone);
        mSupplierSpinner = findViewById(R.id.spinner_supplier);

        mProductName.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);
        mPhoneNum.setOnTouchListener(mTouchListener);
        mSupplierSpinner.setOnTouchListener(mTouchListener);

        setUpSpinner();
    }

    private void setUpSpinner() {
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);

        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mSupplierSpinner.setAdapter(supplierSpinnerAdapter);

        mSupplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.g4))) {
                        mSupplier = ProductsEntry.SUPPLIER_G4; // G4
                    } else if (selection.equals(getString(R.string.philipse))) {
                        mSupplier = ProductsEntry.SUPPLIER_PHILIPSE; // Philipse
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplier = ProductsEntry.SUPPLIER_G4;
            }
        });
    }

    private void insertProductData() {
        String productName = mProductName.getText().toString().trim();
        String productPrice = mPrice.getText().toString().trim();
        String productQuantity = mQuantity.getText().toString().trim();
        String supplierPhone = mPhoneNum.getText().toString().trim();
        int intPrice;
        int intQuantity;
        long longPhone;
        if (Objects.equals(productName, "") || Objects.equals(productPrice, "") || Objects.equals(productQuantity, "") ||
                Objects.equals(supplierPhone, "")) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();

            return;
        } else {
            intPrice = Integer.parseInt(productPrice);
            intQuantity = Integer.parseInt(productQuantity);
            longPhone = Long.parseLong(supplierPhone);
        }

        ContentValues values = new ContentValues();
        values.put(ProductsEntry.COLUMN_PRODUCT, productName);
        values.put(ProductsEntry.COLUMN_PRICE, intPrice);
        values.put(ProductsEntry.COLUMN_QUANTITY, intQuantity);
        values.put(ProductsEntry.COLUMN_SUPPLIER_NAME, mSupplier);
        values.put(ProductsEntry.COLUMN_SUPPLIER_PHONE, longPhone);

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductsEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                String petData = "Name: " + productName + " from " + mSupplier +
                        "\t" + intPrice + " Phone: " + longPhone;
                Toast.makeText(this, getString(R.string.editor_insert_product_successful) + "\t" + petData,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.product_updated),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        if (mCurrentProductUri == null) {
//            MenuItem menuItem = menu.findItem(R.id.action_delete);
//            menuItem.setVisible(false);
//        }
//        return true;
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                insertProductData();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddProductActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(AddProductActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductsEntry._ID,
                ProductsEntry.COLUMN_PRODUCT,
                ProductsEntry.COLUMN_PRICE,
                ProductsEntry.COLUMN_SUPPLIER_NAME,
                ProductsEntry.COLUMN_SUPPLIER_PHONE,
                ProductsEntry.COLUMN_QUANTITY};

        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int productColumnIndex = cursor.getColumnIndex(ProductsEntry.COLUMN_PRODUCT);
            int priceColumnIndex = cursor.getColumnIndex(ProductsEntry.COLUMN_PRICE);
            int supllierNameColumnIndex = cursor.getColumnIndex(ProductsEntry.COLUMN_SUPPLIER_NAME);
            int supllierNumColumnIndex = cursor.getColumnIndex(ProductsEntry.COLUMN_SUPPLIER_PHONE);
            int quantitytColumnIndex = cursor.getColumnIndex(ProductsEntry.COLUMN_QUANTITY);

            String productName = cursor.getString(productColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int supplierName = cursor.getInt(supllierNameColumnIndex);
            int supplierNum = cursor.getInt(supllierNumColumnIndex);
            int quantity = cursor.getInt(quantitytColumnIndex);

            mProductName.setText(productName);
            mPrice.setText(price);
            mQuantity.setText(Integer.toString(quantity));
            mPhoneNum.setText(Integer.toString(supplierNum));

            switch (supplierName) {
                case ProductsEntry.SUPPLIER_PHILIPSE:
                    mSupplierSpinner.setSelection(0);
                    break;
                case ProductsEntry.SUPPLIER_G4:
                    mSupplierSpinner.setSelection(1);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductName.setText("");
        mPrice.setText("");
        mQuantity.setText("");
        mPhoneNum.setText("");
        mSupplierSpinner.setSelection(0);
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}