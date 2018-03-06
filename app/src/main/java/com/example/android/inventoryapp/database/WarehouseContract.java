package com.example.android.inventoryapp.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class WarehouseContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "goods";

    private WarehouseContract() {
    }

    public static class ProductsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String TABLE_NAME = "goods";
        public static final String COLUMN_PRODUCT = "product";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "sup_name";
        public static final String COLUMN_SUPPLIER_PHONE = "sup_phone";


        public static final int SUPPLIER_PHILIPSE = 0;
        public static final int SUPPLIER_G4 = 1;

        public static boolean isValidSupplier(Integer supplierName) {
            if (supplierName == SUPPLIER_G4 || supplierName == SUPPLIER_PHILIPSE) {
                return true;
            }
            return false;
        }
    }
}
