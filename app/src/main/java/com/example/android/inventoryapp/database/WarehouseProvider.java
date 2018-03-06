package com.example.android.inventoryapp.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryapp.database.WarehouseContract.ProductsEntry;

public class WarehouseProvider extends ContentProvider {

    private static final String LOG_TAG = WarehouseProvider.class.getSimpleName();
    private WarehouseDpHelper warehouseDbHelper;


    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    private static final UriMatcher baseUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        baseUriMatcher.addURI(WarehouseContract.CONTENT_AUTHORITY, WarehouseContract.PATH_PRODUCTS, PRODUCTS);
        baseUriMatcher.addURI(WarehouseContract.CONTENT_AUTHORITY, WarehouseContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        warehouseDbHelper = new WarehouseDpHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase base = warehouseDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = baseUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = base.query(ProductsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = base.query(ProductsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = baseUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String name = values.getAsString(ProductsEntry.COLUMN_PRODUCT);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer supplierName = values.getAsInteger(ProductsEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null || !ProductsEntry.isValidSupplier(supplierName)) {
            throw new IllegalArgumentException("Product requires valid Supplier Name");
        }

        Integer price = values.getAsInteger(ProductsEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product requires valid Price");
        }

        SQLiteDatabase base = warehouseDbHelper.getWritableDatabase();
        long id = base.insert(ProductsEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = baseUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductsEntry.COLUMN_PRODUCT)) {
            String name = values.getAsString(ProductsEntry.COLUMN_PRODUCT);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if (values.containsKey(ProductsEntry.COLUMN_SUPPLIER_NAME)) {
            Integer supplierName = values.getAsInteger(ProductsEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null || !ProductsEntry.isValidSupplier(supplierName)) {
                throw new IllegalArgumentException("Product requires valid Supplier Name");
            }
        }

        if (values.containsKey(ProductsEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(ProductsEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Product requires valid Price");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = warehouseDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ProductsEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = warehouseDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = baseUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(ProductsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(ProductsEntry.TABLE_NAME, selection, selectionArgs);

                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     * define data types used
     **/

    @Override
    public String getType(Uri uri) {
        final int match = baseUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductsEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
