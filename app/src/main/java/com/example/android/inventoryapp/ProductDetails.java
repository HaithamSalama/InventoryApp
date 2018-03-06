package com.example.android.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.database.WarehouseContract.ProductsEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCTS_LOADER = 0;
    private Uri mCurrentProductUri;
    private int quantity;
    private String callNumber;
    
    @BindView(R.id.product_name_details)
    TextView productNameDetails;
    @BindView(R.id.price_details)
    TextView priceDetails;
    @BindView(R.id.quantity_details)
    TextView quantityDetails;
    @BindView(R.id.supplier_details)
    TextView supplierDetails;
    @BindView(R.id.phone_details)
    TextView phoneDetails;
    @BindView(R.id.call_button)
    Button callButton;
    @BindView(R.id.sell_button)
    Button sellButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        getLoaderManager().initLoader(PRODUCTS_LOADER, null, this);

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sell();
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:" + callNumber));
                if (ActivityCompat.checkSelfPermission(ProductDetails.this,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(phoneIntent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductsEntry._ID,
                ProductsEntry.COLUMN_PRODUCT,
                ProductsEntry.COLUMN_PRICE,
                ProductsEntry.COLUMN_SUPPLIER_NAME,
                ProductsEntry.COLUMN_SUPPLIER_PHONE,
                ProductsEntry.COLUMN_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
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
            quantity = cursor.getInt(quantitytColumnIndex);

            productNameDetails.setText(productName);
            priceDetails.setText(price);
            quantityDetails.setText(Integer.toString(quantity));
            callNumber = "+20" + Integer.toString(supplierNum);
            phoneDetails.setText(callNumber);
            supplierDetails.setText(Integer.toString(supplierName));
        }
    }

    private void sell() {
        if (quantity > 0) {
            quantity--;
            if (quantity == 1) {
                Toast.makeText(this, R.string.last_item, Toast.LENGTH_SHORT).show();
            }
            quantityDetails.setText(Integer.toString(quantity));
            int productQuantity = Integer.parseInt(quantityDetails.getText().toString().trim());
            ContentValues values = new ContentValues();
            values.put(ProductsEntry.COLUMN_QUANTITY, productQuantity);
            getContentResolver().update(mCurrentProductUri, values, null, null);
        } else if (quantity == 0) {
            Toast.makeText(this, R.string.out_of_stock, Toast.LENGTH_SHORT).show();
            getContentResolver().delete(mCurrentProductUri, null, null);
            finish();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productNameDetails.setText("");
        priceDetails.setText("");
        quantityDetails.setText("");
        phoneDetails.setText("");
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

}