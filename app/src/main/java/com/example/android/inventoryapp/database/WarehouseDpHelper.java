package com.example.android.inventoryapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.inventoryapp.database.WarehouseContract.ProductsEntry;

class WarehouseDpHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = WarehouseDpHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 2;

    public WarehouseDpHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase base) {
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + ProductsEntry.TABLE_NAME + " ("
                + ProductsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductsEntry.COLUMN_PRODUCT + " TEXT NOT NULL, "
                + ProductsEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + ProductsEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductsEntry.COLUMN_SUPPLIER_NAME + " INTEGER NOT NULL, "
                + ProductsEntry.COLUMN_SUPPLIER_PHONE + " INTEGER NOT NULL);";

        Log.v(LOG_TAG, SQL_CREATE_PETS_TABLE);
        base.execSQL(SQL_CREATE_PETS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase base, int i, int i1) {

    }
}
