package com.example.android.myinventoryx2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.myinventoryx2.data.Contract;


/**
 * Created by Person on 26/08/2018.
 */

public class Adapter extends CursorAdapter {


    public Adapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return rootView;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ImageView itemImageView = view.findViewById(R.id.item_image);
        TextView itemNameView = view.findViewById(R.id.item_name_view);
        TextView itemSupplierNameView = view.findViewById(R.id.item_supplier_name_view);
        TextView itemSupplierMailView = view.findViewById(R.id.item_supplier_mail_view);
        TextView itemQuantityView = view.findViewById(R.id.item_quantity_view);
        TextView itemPriceView = view.findViewById(R.id.item_price_view);

        String itemImageUri = cursor.getString(cursor.getColumnIndex(Contract.InventoryEntry.COLUMN_ITEM_IMAGE_URI));
        String itemName = cursor.getString(cursor.getColumnIndex(Contract.InventoryEntry.COLUMN_ITEM_NAME));
        String supplierName = cursor.getString(cursor.getColumnIndex(Contract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME));
        final String supplierEmail = cursor.getString(cursor.getColumnIndex(Contract.InventoryEntry.COLUMN_ITEM_SUPPLIER_MAIL));
        final Integer quantity = cursor.getInt(cursor.getColumnIndex(Contract.InventoryEntry.COLUMN_ITEM_QUANTITY));
        Double price = cursor.getDouble(cursor.getColumnIndex(Contract.InventoryEntry.COLUMN_ITEM_PRICE));

        if (itemImageUri != null) {
            itemImageView.setImageURI(Uri.parse(itemImageUri));
        }

        itemNameView.setText(itemName);
        itemSupplierNameView.setText(supplierName);
        itemSupplierMailView.setText(supplierEmail);
        itemQuantityView.setText(String.valueOf(quantity));
        itemPriceView.setText(String.valueOf(price));

        ImageView sellImage = view.findViewById(R.id.item_sell_image);

        final Uri currentItemUri = ContentUris.withAppendedId(Contract.InventoryEntry.INVENTORY_ENTRY_CONTENT_URI,
                cursor.getInt(cursor.getColumnIndex(Contract.InventoryEntry._ID)));

        sellImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, values);
                values.remove("_id");
                values.put(Contract.InventoryEntry.COLUMN_ITEM_QUANTITY, quantity - 1);
                context.getContentResolver().update(currentItemUri, values, null, null);
            }
        });
    }

}
