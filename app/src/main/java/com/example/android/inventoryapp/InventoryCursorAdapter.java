package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.database.WarehouseContract.ProductsEntry;

class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context) {
        super(context, null, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView productNameTxt = view.findViewById(R.id.product_name);
        TextView quantityTxt = view.findViewById(R.id.quantity);
        TextView supplierNumberTxt = view.findViewById(R.id.supplier_number);
        TextView priceTxt = view.findViewById(R.id.price);

        String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductsEntry.COLUMN_PRODUCT));
        String quantity = cursor.getString(cursor.getColumnIndexOrThrow(ProductsEntry.COLUMN_QUANTITY));
        String supplierPhone = cursor.getString(cursor.getColumnIndexOrThrow(ProductsEntry.COLUMN_SUPPLIER_PHONE));
        String price = cursor.getString(cursor.getColumnIndexOrThrow(ProductsEntry.COLUMN_PRICE));

        if (TextUtils.isEmpty(price)) {
            price = context.getString(R.string.unknown_price);
        }

        productNameTxt.setText(productName);
        quantityTxt.setText(quantity);
        supplierNumberTxt.setText("+20" + supplierPhone);
        priceTxt.setText("$" + price);
    }
}