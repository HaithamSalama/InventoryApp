package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.database.WarehouseContract.ProductsEntry;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private InventoryCursorAdapter inventoryCursorAdapter;
    private static final int PRODUCTS_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("Database: create", "done");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });
        ListView productsListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        productsListView.setEmptyView(emptyView);

        inventoryCursorAdapter = new InventoryCursorAdapter(this);
        productsListView.setAdapter(inventoryCursorAdapter);
        getLoaderManager().initLoader(PRODUCTS_LOADER, null, this);
        productsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductsEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
                return false;
            }
        });
        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, ProductDetails.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductsEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });
    }


    private void insertFakeData() {
        ContentValues values = new ContentValues();
        values.put(ProductsEntry.COLUMN_PRODUCT, "Fake");
        values.put(ProductsEntry.COLUMN_PRICE, 0);
        values.put(ProductsEntry.COLUMN_QUANTITY, 1);
        values.put(ProductsEntry.COLUMN_SUPPLIER_NAME, ProductsEntry.SUPPLIER_PHILIPSE);
        values.put(ProductsEntry.COLUMN_SUPPLIER_PHONE, "01000000000");

        Uri fakeResolverData = getContentResolver().insert(ProductsEntry.CONTENT_URI, values);
        Log.v("Database: adding data", "New row ID" + fakeResolverData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_default_data:
                insertFakeData();
                return true;
            case R.id.action_delete_all_entries:

                deleteTableConfirm();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductsEntry._ID,
                ProductsEntry.COLUMN_PRODUCT,
                ProductsEntry.COLUMN_PRICE,
                ProductsEntry.COLUMN_SUPPLIER_NAME,
                ProductsEntry.COLUMN_SUPPLIER_PHONE,
                ProductsEntry.COLUMN_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                ProductsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        inventoryCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        inventoryCursorAdapter.swapCursor(null);
    }

    private void deleteTableConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteInventoryTable();
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

    private void deleteInventoryTable() {
        int rowsDeleted = getContentResolver().delete(ProductsEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
        Toast.makeText(this, R.string.erased_data, Toast.LENGTH_SHORT).show();
    }
}